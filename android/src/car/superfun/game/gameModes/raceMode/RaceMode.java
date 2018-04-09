package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import car.superfun.game.car.CarController;
import car.superfun.game.GlobalVariables;
import car.superfun.game.TrackBuilder;
import car.superfun.game.UserDataCreater;
import car.superfun.game.gameModes.GameMode;
import car.superfun.game.car.LocalCar;

import com.badlogic.gdx.utils.Array;

public class RaceMode extends GameMode {

    public static final int GOAL_ENTITY = 0b0100;
    public static final int CHECKPOINT_ENTITY = 0b1000;

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private CarController carController;
    private LocalRaceCar localRaceCar;
    private LocalRaceCar localRaceCar2;
    private OpponentCar opponentCar;

    private class checkpointUserData implements UserDataCreater {
        private int id;

        public checkpointUserData() {
            id = 0;
        }

        public Object getUserData() {
            return id++;
        }
    }

    public RaceMode() {
        super();

        world.setContactListener(new RaceContactListener());

        carController = new CarController();

        tiledMap = new TmxMapLoader().load("tiled_maps/simpleMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        FixtureDef wallDef = new FixtureDef();
        wallDef.filter.categoryBits = GlobalVariables.WALL_ENTITY;
        wallDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;

        TrackBuilder.buildLayer(tiledMap, world, "walls", wallDef);

        FixtureDef goalDef = new FixtureDef();
        goalDef.filter.categoryBits = GOAL_ENTITY;
        goalDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
        goalDef.isSensor = true;

        TrackBuilder.buildLayer(tiledMap, world, "goal_line", goalDef);

        FixtureDef checkpointDef = new FixtureDef();
        checkpointDef.filter.categoryBits = CHECKPOINT_ENTITY;
        checkpointDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
        checkpointDef.isSensor = true;

        Array<Body> bodies = TrackBuilder.buildLayerWithUserData(tiledMap, world, "checkpoints", checkpointDef, new checkpointUserData());

        // TODO: implement some way to save starting position together with the map
        // (1600, 11000) is an appropriate starting place in simpleMap
        localRaceCar = new LocalRaceCar(new Vector2(1600, 11000), carController, world, bodies.size);

//        Array<OpponentCar> opponentCars = new Array<OpponentCar>();
        opponentCar = new OpponentCar(new Vector2(1500, 11000), world);
    }

    @Override
    public void handleInput() {
    
    }

    @Override
    public void update(float dt) {
        world.step(1f/60f, 6, 2);
        carController.update();
        localRaceCar.update(dt);
        if (carController.middle) {
            Vector2 carPos = localRaceCar.getBody().getTransform().getPosition();
            Gdx.app.log("localCar position: ", "(" + carPos.x + ", " + carPos.y + ")");
            opponentCar.setPosition(new Vector2(carPos.x - 5, carPos.y), 0f);
            Vector2 oppPos = opponentCar.getBody().getTransform().getPosition();
            Gdx.app.log("opponentCar position: ", "(" + oppPos.x + ", " + oppPos.y + ")");
        }
        opponentCar.update(dt);
        camera.position.set(localRaceCar.getPosition(), 0);
        camera.position.set(localRaceCar.getPosition().add(localRaceCar.getVelocity().scl(10f)), 0);
        camera.up.set(localRaceCar.getDirectionVector(), 0);
    }

    // Renders objects that had a static position in the gameworld. Is called by superclass
    @Override
    public void renderWithCamera(SpriteBatch sb, OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        localRaceCar.render(sb);
        opponentCar.render(sb);
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