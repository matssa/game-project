package car.superfun.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import car.superfun.game.car.OpponentCarController;

public interface GoogleGameServices {

    public abstract void broadcast(Vector2 velocity, Vector2 position, float angle, float forward, float rotation);
    public abstract Array<OpponentCarController> getOpponentCarControllers();

    public abstract boolean isSignedIn();
    public abstract void signOut();
    public abstract void startSignInIntent();

    public abstract void startQuickGame(NewState newState);
    public abstract void leaveRoom();
    public abstract void readyToStart();
    public abstract boolean gameStarted();


}
