package car.superfun.game.gameModes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import car.superfun.game.states.State;

/**
 * Created by kristian on 14.03.18.
 */

public abstract class GameMode extends State {

    //Renders objects that have a static position on the screen
    public abstract void renderHud(SpriteBatch sb);

    public abstract void endGame();
}
