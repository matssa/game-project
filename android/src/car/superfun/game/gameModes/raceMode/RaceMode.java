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
import com.badlogic.gdx.utils.Array;

import java.util.Timer;
import java.util.TimerTask;

import car.superfun.game.AndroidLauncher;
import car.superfun.game.GlobalVariables;
import car.superfun.game.TrackBuilder;
import car.superfun.game.UserDataCreater;
import car.superfun.game.car.LocalCarController;
import car.superfun.game.car.OpponentCar;
import car.superfun.game.car.OpponentCarController;
import car.superfun.game.gameModes.GameMode;

public class RaceMode extends GameMode {

    public static final int GOAL_ENTITY = 0b1 << 8;
    public static final int CHECKPOINT_ENTITY = 0b1 << 9;
    public static final int TEST_ENTITY = 0b1 << 10;

    private AndroidLauncher androidLauncher;

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private LocalCarController localCarController;
    private LocalRaceCar localRaceCar;
    private Array<OpponentCar> opponentCars;
    private int amountOfCheckpoints;
    private boolean singlePlayer;

    private class checkpointUserData implements UserDataCreater {
        private int id;


        public checkpointUserData() {
            id = 0;
        }

        public Object getUserData() {
            return id++;
        }
    }

    public RaceMode(AndroidLauncher androidLauncher, boolean singlePlayer) {
        super();
        this.singlePlayer = singlePlayer;
        this.androidLauncher = androidLauncher;
        world.setContactListener(new RaceContactListener());

        localCarController = new LocalCarController();

        tiledMap = new TmxMapLoader().load("tiled_maps/simpleMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        FixtureDef wallDef = new FixtureDef();
        wallDef.filter.categoryBits = GlobalVariables.WALL_ENTITY;
        wallDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY | GlobalVariables.OPPONENT_ENTITY;

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

        amountOfCheckpoints = TrackBuilder.buildLayerWithUserData(tiledMap, world, "checkpoints", checkpointDef, new checkpointUserData()).size;

        localRaceCar = new LocalRaceCar(new Vector2(2000, 11000), localCarController, world, amountOfCheckpoints);

        int startX = 1900;

        Array<OpponentCarController> opponentCarControllers = androidLauncher.getOpponentCarControllers();

        opponentCars = new Array<OpponentCar>();
        for (OpponentCarController oppCC : opponentCarControllers) {
            opponentCars.add(new OpponentCar(new Vector2(startX, 11000), oppCC, world));
            startX -= 100;
        }

        if (GlobalVariables.TESTING_MODE) {
            FixtureDef testDef = new FixtureDef();
            testDef.filter.categoryBits = TEST_ENTITY;
            testDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
            testDef.isSensor = true;
            TrackBuilder.buildLayer(tiledMap, world, "test", testDef);
        }

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
        // (1600, 11000) is an appropriate starting place in simpleMap
        localRaceCar = new LocalRaceCar(new Vector2(1600, 11000), localCarController, world, amountOfCheckpoints);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        world.step(1f/60f, 2, 1); // Low precision high efficiency
//        world.step(1f/60f, 6, 2); // High precision low efficiency
        localCarController.update();
        localRaceCar.update(dt);
        camera.position.set(localRaceCar.getSpritePosition(), 0);
        camera.position.set(localRaceCar.getSpritePosition().add(localRaceCar.getVelocity().scl(10f)), 0);

        for (OpponentCar car : opponentCars) {
            car.update(dt);
        }

        camera.up.set(localRaceCar.getDirectionVector(), 0);
        if(!singlePlayer){
            Vector2 position = localRaceCar.getBody().getTransform().getPosition();
            float angle = localRaceCar.getDirectionFloat();
            androidLauncher.broadcast(false, 0, localRaceCar.getVelocity(), position, angle);
        }
    }

    // Renders objects that had a static position in the gameworld. Is called by superclass
    @Override
    public void renderWithCamera(SpriteBatch sb, OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        localRaceCar.render(sb);
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
    }

    @Override
    public void endGame() {
        // TODO: Implement a proper way to exit the game
    }
}
