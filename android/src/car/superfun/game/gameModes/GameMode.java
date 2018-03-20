package car.superfun.game.gameModes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import car.superfun.game.State;

/**
 * Created by kristian on 14.03.18.
 */

public abstract class GameMode extends State {

    protected OrthographicCamera camera;
    protected SpriteBatch camBatch;

    protected GameMode() {
        super();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.update();

        camBatch = new SpriteBatch();
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
