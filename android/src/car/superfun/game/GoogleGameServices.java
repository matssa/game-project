package car.superfun.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import car.superfun.game.car.OpponentCarController;

public interface GoogleGameServices {

    void broadcastState(Vector2 velocity, Vector2 position, float angle, float forward, float rotation);
    void broadcastScore(int score, boolean isPositive);

    Array<OpponentCarController> getOpponentCarControllers();

    boolean isSignedIn();
    void signOut();
    void startSignInIntent();

    void startQuickGame(NewState newState);
    void leaveRoom();
    void readyToStart();
    boolean gameStarted();

    long getStartTime();

}
