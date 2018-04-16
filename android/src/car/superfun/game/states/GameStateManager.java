package car.superfun.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

import car.superfun.game.menus.MainMenu;


/**
 * This class handles the different states the game can be in, that is menu, game, game over etc...
 */
public class GameStateManager {


    //define gameStateManger, sets it to null in case its not used
    private static GameStateManager gameStateManager = null;

    //Stack containing the states in use
    private Stack<State> states = new Stack<State>();;

    //Changed the contructor to private so that the only way to init the class is from the class
    //itself
    private GameStateManager(){
    }

    //Returnes the one and only instance of the class
    public static GameStateManager getInstance() {
        if(gameStateManager == null) {
            gameStateManager = new GameStateManager();
        }
        return gameStateManager;
    }

    /**
     * Add a state to the stack
     * @param state
     */
    public void push(State state)
    {
        states.push(state);
        Gdx.graphics.requestRendering();
    }

    /**
     * Remove top state from stack
     */
    public void pop(){
        dispose();
        states.pop();
        Gdx.graphics.requestRendering();
    }

    /**
     * Sets new state
     * Removes last state and sets the given state
     * @param state
     */
    public void set(State state) {
        dispose();
        states.pop();
        states.push(state);
        Gdx.graphics.requestRendering();
    }   

    /**
     * updates the states
     * @param dt
     */
    public void update(float dt) {
        if(!states.empty()) {
            states.peek().update(dt);
        }
    }

    /**
     * Calls the render function for the state at the top of the stack
     * @param sb
     */
    public void render(SpriteBatch sb) {
        if(!states.empty()) {
            states.peek().render(sb);
        }
    }

    /**
     * Disposes for a given state
     */
    public void dispose() {
        if(!states.empty()) {
            states.peek().dispose();
        }
    }

    public boolean isInMainMenu() { return (states.peek() instanceof MainMenu); }
}
