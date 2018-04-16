package car.superfun.game.gameModes.gladiatorMode;

import android.provider.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;

import car.superfun.game.GoogleGameServices;
import car.superfun.game.car.LocalCarController;
import car.superfun.game.GlobalVariables;
import car.superfun.game.TrackBuilder;
import car.superfun.game.car.OpponentCar;
import car.superfun.game.car.OpponentCarController;
import car.superfun.game.gameModes.GameMode;


public class GladiatorMode extends GameMode {

    // Filters
    static final short DEATH_ENTITY = 0b1 << 8;
    static final short BOOST_ZONE = 0b1 << 9;

    // Music and sounds
    private final Sound dustWallCrash;
    private final Music gladiatorSong;

    private GoogleGameServices googleGameServices;

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private Array<OpponentCar> opponentCars;

    private LocalCarController localCarController;
    private LocalGladiatorCar localCar;
    private int score;
    private float boost;
    private boolean singlePlayer;


    public GladiatorMode(GoogleGameServices googleGameServices, boolean singlePlayer) {
        super();

        this.singlePlayer = singlePlayer;
        this.googleGameServices = googleGameServices;


        // Audio
        gladiatorSong = Gdx.audio.newMusic(Gdx.files.internal("sounds/gladiatorMode.ogg"));
        dustWallCrash = Gdx.audio.newSound(Gdx.files.internal("sounds/crash_in_dirt_wall.ogg"));

        gladiatorSong.setLooping(true);
        gladiatorSong.setVolume(GlobalVariables.MUSIC_VOLUME);
        gladiatorSong.play();

        score = 5;
        boost = 10;
        localCarController = new LocalCarController();

        // Set the map
        tiledMap = new TmxMapLoader().load("tiled_maps/gladiator.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        world.setContactListener(new GladiatorContactListener());

        // Set the normal walls
        FixtureDef wallDef = new FixtureDef();
        wallDef.filter.categoryBits = GlobalVariables.WALL_ENTITY;
        wallDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
        TrackBuilder.buildLayer(tiledMap, world, "walls", wallDef);

        // Set the death walls
        FixtureDef deathZoneDef = new FixtureDef();
        deathZoneDef.restitution = 2f;
        deathZoneDef.filter.categoryBits = GladiatorMode.DEATH_ENTITY;
        deathZoneDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
        TrackBuilder.buildLayer(tiledMap, world, "dirt_barrier", deathZoneDef);

        // Set the boost charging zone
        FixtureDef boostZone = new FixtureDef();
        boostZone.isSensor = true;
        boostZone.filter.categoryBits = BOOST_ZONE;
        boostZone.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
        TrackBuilder.buildLayer(tiledMap, world, "boost_zone", boostZone);


        int startX = 1900;

        Array<OpponentCarController> opponentCarControllers = googleGameServices.getOpponentCarControllers();

        opponentCars = new Array<OpponentCar>();

        for (OpponentCarController opponentCarController : opponentCarControllers) {
            opponentCars.add(new OpponentCar(new Vector2(startX, 11000), opponentCarController, world));
            startX -= 100;
        }

        setLocalRaceCar(new Vector2());

        googleGameServices.readyToStart();
    }

    // Google Game Service sets the opponent cars
    public void setOpponentCars(Array<Vector2> carPositions, Array<OpponentCarController> opponentCarControllers) {
        if (carPositions.size != opponentCarControllers.size) {
            Gdx.app.log("ERROR: carPositions.size != opponentCarControllers.size", "cp: " + carPositions.size + ", occ: " + opponentCarControllers.size);
            return;
        }
        opponentCars = new Array<OpponentCar>();
        for (int i = 0; i < carPositions.size; i++) {
            opponentCars.add(new OpponentCar(carPositions.get(i), opponentCarControllers.get(i), world));
        }
    }

    // Google game service sets the local car
    public void setLocalRaceCar(Vector2 position) {
        // TODO: implement some way to save starting position together with the map
        // Position is set to 6000, 6000 for now.
        localCar = new LocalGladiatorCar(this, new Vector2(6000, 6000), localCarController, world, score, dustWallCrash);
    }

    @Override
    public void handleInput() {

    }


    @Override
    public void update(float dt) {
        if (!googleGameServices.gameStarted() && !singlePlayer) {
            return;
        }
        for (OpponentCar car : opponentCars) {
            car.update(dt);
        }
        localCar.update(dt);

        world.step(dt, 2, 1); // Using deltaTime

        camera.position.set(localCar.getSpritePosition(), 0);
        camera.position.set(localCar.getSpritePosition().add(localCar.getVelocity().scl(10f)), 0);
        camera.up.set(localCar.getDirectionVector(), 0);

        localCarController.update();
        if (!singlePlayer) {
            googleGameServices.broadcast(
                    localCar.getVelocity(),
                    localCar.getBodyPosition(),
                    localCar.getAngle(),
                    localCarController.getForward(),
                    localCarController.getRotation());
        }
    }

    // Renders objects that had a static position in the gameworld. Is called by superclass
    @Override
    public void renderWithCamera(SpriteBatch sb, OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        localCar.render(sb);
        for (OpponentCar car : opponentCars) {
            car.render(sb);
        }
    }

    // Renders objects that have a static position on the screen. Is called by superclass
    @Override
    public void renderHud(SpriteBatch sb) {
        localCarController.render(sb);
    }

    @Override
    public void dispose() {
        gladiatorSong.stop();
        gladiatorSong.dispose();
    }

    @Override
    public void endGame() {
        // TODO: send data to leaderboard
        this.dispose();
    }
}
