package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.instacart.library.truetime.TrueTime;

import car.superfun.game.GlobalVariables;
import car.superfun.game.GoogleGameServices;
import car.superfun.game.TrackBuilder;
import car.superfun.game.UserDataCreater;
import car.superfun.game.car.LocalCarController;
import car.superfun.game.car.OpponentCar;
import car.superfun.game.car.OpponentCarController;
import car.superfun.game.gameModes.GameMode;
import car.superfun.game.menus.Leaderboard;
import car.superfun.game.states.GameStateManager;

public class RaceMode extends GameMode {

    public static final int GOAL_ENTITY = 0b1 << 8;
    public static final int CHECKPOINT_ENTITY = 0b1 << 9;
    public static final int TEST_ENTITY = 0b1 << 10;


    // Path to map
    private final static String MAP_PATH = "tiled_maps/simpleMap.tmx";

    // Car textures
    private final static String[] TEXTURE_PATHS = new String[]{
            "racing-pack/PNG/Cars/car_black_5.png",
            "racing-pack/PNG/Cars/car_blue_5.png",
            "racing-pack/PNG/Cars/car_red_5.png",
            "racing-pack/PNG/Cars/car_green_5.png",
    };
    // Used to set different color to different players cars
    private int texturePathIndex = 0;

    // List of opponentCars
    private Array<OpponentCar> opponentCars = new Array<>();

    // The car controlled by this phones owner
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
        if (!singlePlayer) {
            googleGameServices.readyToStart();
        }
    }

    /**
     * Load and set up tiledMap
     */
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

        // returns the amount of checkpoints for the given map
        amountOfCheckpoints = TrackBuilder.buildLayerWithUserData(tiledMap, world, "checkpoints", checkpointDef, new checkpointUserData()).size;

        // Get starting positions for map
        getStartPositions("starting_points");
    }

    /**
     * Callback used to create the opponent cars and the localCar.
     */
    private SetStartPositionCallback startPositionCallback = new SetStartPositionCallback() {
        @Override
        public void addOpponentCar(Vector2 position, OpponentCarController opponentCarController) {
            opponentCars.add(new OpponentCar(position, opponentCarController, world, TEXTURE_PATHS[texturePathIndex]));
            incrementTexturePath();
        }

        @Override
        public void addLocalCar(Vector2 position, LocalCarController localCarController, GameMode thisGameMode) {
            localRaceCar = new LocalRaceCar(position, localCarController, world, (RaceMode) thisGameMode, amountOfCheckpoints, TEXTURE_PATHS[texturePathIndex]);
            incrementTexturePath();
        }
    };

    private void incrementTexturePath() {
        if (texturePathIndex < TEXTURE_PATHS.length) {
            texturePathIndex++;
        }
    }

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
    public void update(float dt) {
        camera.position.set(localRaceCar.getSpritePosition(), 0);
        camera.position.set(localRaceCar.getSpritePosition().add(localRaceCar.getVelocity().scl(10f)), 0);
        camera.up.set(localRaceCar.getDirectionVector(), 0);

        if (!googleGameServices.gameStarted() && !singlePlayer) {
            return;
        }
        for (OpponentCar car : opponentCars) {
            car.update(dt);
        }
        localRaceCar.update(dt);
        world.step(dt, 2, 1); // Using deltaTime


        localCarController.update();
        if (!GlobalVariables.SINGLE_PLAYER) {
            googleGameServices.broadcastState(
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
        int timeSinceStart = (int) ((TrueTime.now().getTime() - googleGameServices.getStartTime()) % 2147483648L);
        if (singlePlayer) {
            Gdx.app.log("You won!", "" + timeSinceStart + " milliseconds used");
            return;
        }
        Leaderboard.getInstance().newPlayerScore(googleGameServices.getLocalParticipant().getDisplayName(), timeSinceStart);
        googleGameServices.broadcastScore(timeSinceStart);
        GameStateManager.getInstance().set(Leaderboard.getInstance().initialize(scoreFormatter, false));
    }

    private Leaderboard.ScoreFormatter scoreFormatter = new Leaderboard.ScoreFormatter() {

        @Override
        public String formatScore(int ms) {
            String milliseconds = Integer.toString(ms%1000);
            while(milliseconds.length() < 3){
                milliseconds = "0" + milliseconds;
            }
            String seconds = Integer.toString((ms/1000)%60);
            while(seconds.length() < 2){
                seconds = "0" + seconds;
            }
            int minutes = (ms/(1000*60))%60;
            String time;
            if(!(minutes == 0)){
                time = minutes + ":" + seconds + ":" + milliseconds;
            }else{
                time = seconds + ":" + milliseconds;
            }
            return time;
        }
    };
}

