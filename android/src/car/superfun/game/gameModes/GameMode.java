package car.superfun.game.gameModes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.states.State;


public abstract class GameMode extends State {

    protected OrthographicCamera camera;
    protected SpriteBatch camBatch;
    protected World world;

    protected GameMode() {
        super();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = camera.zoom * 1.4f;
        camera.update();

        camBatch = new SpriteBatch();

        world = new World(new Vector2(0,0), true);


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
}
