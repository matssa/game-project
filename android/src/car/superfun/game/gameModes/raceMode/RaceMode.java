package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;

import car.superfun.game.GlobalVariables;
import car.superfun.game.GoogleGameServices;
import car.superfun.game.TrackBuilder;
import car.superfun.game.UserDataCreater;
import car.superfun.game.car.LocalCarController;
import car.superfun.game.car.OpponentCar;
import car.superfun.game.car.OpponentCarController;
import car.superfun.game.gameModes.GameMode;
import car.superfun.game.gameModes.SetStartPositionCallback;

public class RaceMode extends GameMode {

    public static final int GOAL_ENTITY = 0b1 << 8;
    public static final int CHECKPOINT_ENTITY = 0b1 << 9;
    public static final int TEST_ENTITY = 0b1 << 10;

    private final static String MAP_PATH = "tiled_maps/simpleMap.tmx";

    private Array<OpponentCar> opponentCars = new Array<>();
    private LocalRaceCar localRaceCar;

    private int amountOfCheckpoints;

    public RaceMode(GoogleGameServices googleGameServices, boolean singlePlayer) {
        super(MAP_PATH, googleGameServices, singlePlayer);

        world.setContactListener(new RaceContactListener());

        // Set up the map
        setUpMap();

        // Sets the positions for the different cars
        setStartPositions(startPositionCallback);

        // Enables testing mode
        if (GlobalVariables.TESTING_MODE) {
            FixtureDef testDef = new FixtureDef();
            testDef.filter.categoryBits = TEST_ENTITY;
            testDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
            testDef.isSensor = true;
            TrackBuilder.buildLayer(tiledMap, world, "test", testDef);
        }
        readyToStart();
    }

    private void setUpMap() {
        // Set the normal walls
        FixtureDef wallDef = new FixtureDef();
        wallDef.filter.categoryBits = GlobalVariables.WALL_ENTITY;
        wallDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY | GlobalVariables.OPPONENT_ENTITY;

        TrackBuilder.buildLayer(tiledMap, world, "walls", wallDef);

        // Set the goal line
        FixtureDef goalDef = new FixtureDef();
        goalDef.filter.categoryBits = GOAL_ENTITY;
        goalDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
        goalDef.isSensor = true;

        TrackBuilder.buildLayer(tiledMap, world, "goal_line", goalDef);

        // Set the checkpoints around the map
        FixtureDef checkpointDef = new FixtureDef();
        checkpointDef.filter.categoryBits = CHECKPOINT_ENTITY;
        checkpointDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY;
        checkpointDef.isSensor = true;

        // returnes the amount of checkpoints for the given map
        amountOfCheckpoints = TrackBuilder.buildLayerWithUserData(tiledMap, world, "checkpoints", checkpointDef, new checkpointUserData()).size;

        // Get starting positions for map
        getStartPositions("starting_points");
    }

    private SetStartPositionCallback startPositionCallback = new SetStartPositionCallback() {
        @Override
        public void addOpponentCar(Vector2 position, OpponentCarController opponentCarController) {
            opponentCars.add(new OpponentCar(position, opponentCarController, world));
        }

        @Override
        public void addLocalCar(Vector2 position, LocalCarController localCarController) {
            localRaceCar = new LocalRaceCar(position, localCarController, world, amountOfCheckpoints);
        }
    };


    private class checkpointUserData implements UserDataCreater {
        private int id;

        public checkpointUserData() {
            id = 0;
        }

        public Object getUserData() {
            return id++;
        }
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
        localRaceCar.update(dt);
        world.step(dt, 2, 1); // Using deltaTime

        camera.position.set(localRaceCar.getSpritePosition(), 0);
        camera.position.set(localRaceCar.getSpritePosition().add(localRaceCar.getVelocity().scl(10f)), 0);
        camera.up.set(localRaceCar.getDirectionVector(), 0);

        localCarController.update();
        if (!singlePlayer) {
            googleGameServices.broadcast(
                    localRaceCar.getVelocity(),
                    localRaceCar.getBodyPosition(),
                    localRaceCar.getAngle(),
                    localCarController.getForward(),
                    localCarController.getRotation());

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

