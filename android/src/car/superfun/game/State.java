package car.superfun.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class State {

    protected State() {
    }

    //Handles input from user
    public abstract void handleInput();

    //Updates the state
    public abstract void update(float dt);

    //Renders the objects in this state
    public abstract void render(SpriteBatch sb);

    //dispose
    public abstract void dispose();
}
