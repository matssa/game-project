package car.superfun.game;

import car.superfun.game.menus.MainMenu;
import car.superfun.game.states.GameStateManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class CarSuperFun extends ApplicationAdapter {

    public static final String TITLE = "Car Superfun xD";
    private GameStateManager gsm;
    private SpriteBatch batch;


    /**
     * Sets up the app
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        gsm = GameStateManager.getInstance();

        //sets the color to black
        Gdx.gl.glClearColor(0, 0, 0, 1);

        gsm.push(new MainMenu(gsm));
    }

    /**
     * Renders the scene
     * by updating and rendering the current state given by the gsm
     */
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render(batch);
    }

    /**
     * get gsm to dispose
     */
    @Override
    public void dispose() {
        gsm.dispose();
        batch.dispose();
    }
}
