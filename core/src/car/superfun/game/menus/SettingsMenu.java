package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

/**
 * Created by Jonas on 06.03.2018.
 */

public class SettingsMenu extends State {
    private Texture background, back;

    public SettingsMenu(){
        background = new Texture("pink_car.png");
        back = new Texture("cogwheel.png");
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()) {
            //Gdx.app.log("X", Integer.toString(Gdx.input.getX()));
            //Gdx.app.log("Y", Integer.toString(Gdx.input.getY()));
            if (isOnBack()) {
                Gdx.app.log("Touched", "heyo");
                GameStateManager.getInstance().pop();
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sb.draw(back, 1600, 890);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        back.dispose();
    }

    public boolean isOnBack(){
        Circle textureBounds = new Circle(1600+back.getWidth()/2, (Gdx.graphics.getHeight() - 890)-back.getHeight()/2, back.getWidth()/2);
        if(textureBounds.contains(Gdx.input.getX(), Gdx.input.getY())){
            return true;
        }else{
            return false;
        }
    }
}
