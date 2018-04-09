package car.superfun.game.gameModes.gladiatorMode;

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

import car.superfun.game.car.LocalCarController;
import car.superfun.game.GlobalVariables;
import car.superfun.game.TrackBuilder;
import car.superfun.game.gameModes.GameMode;


public class GladiatorMode extends GameMode {

    // Filters
    static final short DEATH_ENTITY = 0x0032;

    // Music and sounds
    private final Sound dustWallCrash;
    private final Music gladiatorSong;

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private LocalCarController localCarController;
    private LocalGladiatorCar localCar;
    private int score;

    public GladiatorMode() {
        super();

        gladiatorSong = Gdx.audio.newMusic(Gdx.files.internal("sounds/gladiatorMode.ogg"));
        dustWallCrash = Gdx.audio.newSound(Gdx.files.internal("sounds/crash_in_dirt_wall.ogg"));

        gladiatorSong.setLooping(true);
        gladiatorSong.setVolume(0.5f);
        gladiatorSong.play();

        score = 5;
        localCarController = new LocalCarController();
        localCar = new LocalGladiatorCar(this, new Vector2(6000, 6000), localCarController, world, score, dustWallCrash);

        tiledMap = new TmxMapLoader().load("tiled_maps/gladiator.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        world.setContactListener(new GladiatorContactListener());

        FixtureDef wallDef = new FixtureDef();
        wallDef.filter.categoryBits = GlobalVariables.WALL_ENTITY;
        wallDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;

        TrackBuilder.buildLayer(tiledMap, world, "walls", wallDef);

        FixtureDef deathZoneDef = new FixtureDef();
        deathZoneDef.restitution = 2f;
        deathZoneDef.filter.categoryBits = GladiatorMode.DEATH_ENTITY;
        deathZoneDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;

        TrackBuilder.buildLayer(tiledMap, world, "dirt_barrier", deathZoneDef);
    }

    @Override
    public void handleInput() {

    }


    @Override
    public void update(float dt) {
        world.step(1f/60f, 6, 2);
        localCarController.update();
        localCar.update(dt);
        camera.position.set(localCar.getSpritePosition(), 0);
        camera.position.set(localCar.getSpritePosition().add(localCar.getVelocity().scl(10f)), 0);
        camera.up.set(localCar.getDirectionVector(), 0);
    }

    // Renders objects that had a static position in the gameworld. Is called by superclass
    @Override
    public void renderWithCamera(SpriteBatch sb, OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        localCar.render(sb);

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
