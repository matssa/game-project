package car.superfun.game.gameModes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;

import car.superfun.game.GoogleGameServices;
import car.superfun.game.TrackBuilder;
import car.superfun.game.car.CarController;
import car.superfun.game.car.LocalCarController;
import car.superfun.game.car.OpponentCarController;
import car.superfun.game.states.State;


public abstract class GameMode extends State {

    protected OrthographicCamera camera;
    protected SpriteBatch camBatch;

    protected Array<Vector2> startPositions;

    protected LocalCarController localCarController;

    protected boolean singlePlayer = true;
    protected World world;

    protected TiledMap tiledMap;
    protected TiledMapRenderer tiledMapRenderer;

    protected GoogleGameServices googleGameServices;

    protected GameMode(String mapPath, GoogleGameServices googleGameServices, boolean singlePlayer) {
        this.singlePlayer = singlePlayer;

        tiledMap = new TmxMapLoader().load(mapPath);
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = camera.zoom * 1.4f;
        camera.update();

        camBatch = new SpriteBatch();

        localCarController = new LocalCarController(googleGameServices.getLocalParticipant());

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        world = new World(new Vector2(0, 0), true);

        this.googleGameServices = googleGameServices;

        startPositions = TrackBuilder.getPoints(tiledMap, "starting_points");
    }

    protected void setStartPositions(SetStartPositionCallback callback) {
        ArrayList<CarController> carControllers = new ArrayList<>();
        for (CarController carController : googleGameServices.getOpponentCarControllers()) {
            carControllers.add(carController);
        }

        carControllers.add(localCarController);
        Collections.sort(carControllers);

        for (int i = 0; i < carControllers.size(); i++) {
            if (carControllers.get(i) instanceof OpponentCarController) {
                callback.addOpponentCar(startPositions.get(i), (OpponentCarController) carControllers.get(i));
            } else if (carControllers.get(i) instanceof LocalCarController) {
                callback.addLocalCar(startPositions.get(i), localCarController, this);
            }
        }
    }

    protected void readyToStart() {
        googleGameServices.readyToStart();
    }

    protected void getStartPositions(String layer) {
        startPositions = TrackBuilder.getPoints(tiledMap, layer);
    }

    public void render(SpriteBatch sb) {
        // Update the camera position
        camera.update();

        // render based on the camera position
        camBatch.setProjectionMatrix(camera.combined);
        this.renderWithCamera(camBatch, camera);
        this.renderHud(sb);
    }

    protected abstract void renderWithCamera(SpriteBatch sb, OrthographicCamera camera);

    protected abstract void renderHud(SpriteBatch sb);

    public abstract void endGame();

    public interface SetStartPositionCallback {
        void addOpponentCar(Vector2 position, OpponentCarController opponentCarController);
        void addLocalCar(Vector2 position, LocalCarController localCarController, GameMode thisGameMode);
    }
}
