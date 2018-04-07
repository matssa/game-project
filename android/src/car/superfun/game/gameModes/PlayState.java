package car.superfun.game.gameModes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import car.superfun.game.CarControls.CarController;
import car.superfun.game.CarSuperFun;
import car.superfun.game.TrackBuilder;
import car.superfun.game.physicalObjects.BasicContactListener;
import car.superfun.game.physicalObjects.LocalCar;


public class PlayState extends GameMode{

    public static final int GOAL_ENTITY = 0b0100;
    public static final int CHECKPOINT_ENTITY = 0b1000;

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private CarController carController;
    private LocalCar localCar;

    public PlayState() {
        super();

        world.setContactListener(new BasicContactListener());

        carController = new CarController();

        tiledMap = new TmxMapLoader().load("tiled_maps/simpleMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        TrackBuilder.buildWalls(tiledMap, CarSuperFun.PIXELS_TO_METERS, world);
        TrackBuilder.buildGoalLine(tiledMap, CarSuperFun.PIXELS_TO_METERS, world);

        // TODO: implement some way to save starting position together with the map
        // (1600, 11000) is an appropriate starting place in simpleMap
        localCar = new LocalCar(new Vector2(1600, 11000), carController, world);
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