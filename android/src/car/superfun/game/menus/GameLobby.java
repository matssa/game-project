package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import car.superfun.game.states.State;


public class GameLobby extends State{


    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void setInputProcessor() {
        Gdx.input.setInputProcessor(null);
    }
}
