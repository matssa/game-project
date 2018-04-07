package car.superfun.game.gameModes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import car.superfun.game.CarControls.CarController;
import car.superfun.game.TrackBuilder;
import car.superfun.game.physicalObjects.LocalGladiatorCar;

import static com.badlogic.gdx.Gdx.app;

/**
 * Created by matss on 21-Mar-18.
 */

public class GladiatorMode extends GameMode {

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    Sound dustWallCrash;
    Sound carSound;

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
        TrackBuilder.buildWalls(tiledMap, 100f, world);
        TrackBuilder.buildDeathZone(tiledMap, 100f, world);
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
        carSound.dispose();
    }

    @Override
    public void endGame() {
        // TODO: Implement a proper way to exit the game
    }
}
