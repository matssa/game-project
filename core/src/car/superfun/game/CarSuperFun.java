package car.superfun.game;

import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.PlayState;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class CarSuperFun extends ApplicationAdapter {
    private GameStateManager gsm;
    private SpriteBatch batch;
    private SpriteBatch hud;
    private OrthographicCamera camera;

    /**
     * Sets up the app
     */
    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.update();

        batch = new SpriteBatch();
        hud = new SpriteBatch();

        gsm = GameStateManager.getInstance();

        //sets the color to black
        Gdx.gl.glClearColor(0, 0, 1, 1);

        // Starts the game in playstate
        gsm.push(new PlayState(camera));
    }

    /**
     * Renders the scene
     * by updating and rendering the current state given by the gsm
     */
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.update(Gdx.graphics.getDeltaTime());

        // Update the camera position
        camera.update();

        // render based on the camera position
        batch.setProjectionMatrix(camera.combined);

        // render the bach
        gsm.render(batch);

        //render controller
        gsm.renderHud(hud);
    }

    /**
     * get gsm to dispose
     */
    @Override
    public void dispose() {
        gsm.dispose();
    }
}
