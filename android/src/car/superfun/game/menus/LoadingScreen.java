package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

/**
 * Created by pcipc on 14.04.2018.
 */

public class LoadingScreen extends State {

    private Texture backButton;


    public LoadingScreen(){

        backButton = new Texture("menu-buttons/back.png");
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()) {
            if (isOnBack()) {
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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.draw(backButton, 120, 890);
        sb.end();
    }

    @Override
    public void dispose() {
        backButton.dispose();
    }

    private boolean isOnBack(){
        Circle textureBounds = new Circle(120+backButton.getWidth()/2, (Gdx.graphics.getHeight() - 890)-backButton.getHeight()/2, backButton.getWidth()/2);
        if(textureBounds.contains(Gdx.input.getX(), Gdx.input.getY())){
            return true;
        }else{
            return false;
        }
    }
}
