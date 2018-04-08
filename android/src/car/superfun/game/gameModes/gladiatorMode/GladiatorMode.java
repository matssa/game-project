package car.superfun.game.gameModes.gladiatorMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import car.superfun.game.car.CarController;
import car.superfun.game.GlobalVariables;
import car.superfun.game.TrackBuilder;
import car.superfun.game.gameModes.GameMode;


public class GladiatorMode extends GameMode {

    // Filters
    public static final short DEATH_ENTITY = 0x0032;

    // Music and sounds
    public static final Sound dustWallCrash = Gdx.audio.newSound(Gdx.files.internal("sounds/crash_in_dirt_wall.ogg"));

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private CarController carController;
    private LocalGladiatorCar localCar;
    private int score;

    public GladiatorMode() {
        super();

        score = 5;
        carController = new CarController();
        localCar = new LocalGladiatorCar(new Vector2(6000, 6000), carController, world, score);
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
        carController.update();
        localCar.update(dt);
        camera.position.set(localCar.getPosition(), 0);
        camera.position.set(localCar.getPosition().add(localCar.getVelocity().scl(10f)), 0);
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
        carController.render(sb);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void endGame() {
        // TODO: Implement a proper way to exit the game
    }
}
