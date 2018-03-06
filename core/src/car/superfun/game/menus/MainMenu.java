package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import car.superfun.game.CarSuperFun;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

/**
 * Created by Jonas on 06.03.2018.
 */

public class MainMenu extends State {
    private Texture background, button, settings;

    public MainMenu(){
        background = new Texture("background.png");
        settings = new Texture("cogwheel.png");
    }

    @Override
    public void handleInput() {
        /*if(Gdx.input.justTouched()){
        }*/
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0);
        sb.draw(settings, 100, 100);
        sb.end();
    }

    @Override
    public void dispose() {
        settings.dispose();
    }
}
