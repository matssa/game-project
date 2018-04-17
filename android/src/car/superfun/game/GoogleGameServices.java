package car.superfun.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.games.multiplayer.Participant;

import java.util.ArrayList;

import car.superfun.game.car.CarController;

public interface GoogleGameServices {

    void broadcast(Vector2 velocity, Vector2 position, float angle, float forward, float rotation);
    Array<CarController> getOpponentCarControllers();

    boolean isSignedIn();
    void signOut();
    void startSignInIntent();

    void startQuickGame(NewState newState);
    void leaveRoom();
    void readyToStart();
    boolean gameStarted();
    ArrayList<Participant> getParticipants();
    String getMyID();


    Participant getLocalParticipant();
}
