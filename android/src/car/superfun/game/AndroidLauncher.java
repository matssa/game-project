package car.superfun.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.GamesActivityResultCodes;

import car.superfun.game.car.OpponentCarController;
import car.superfun.game.googleGamePlayServices.Communicator;
import car.superfun.game.googleGamePlayServices.SetUpGame;
import car.superfun.game.googleGamePlayServices.SignIn;

public class AndroidLauncher extends AndroidApplication {

    final static String TAG = "CarSuperFun";

    public SetUpGame setUpGame;
    public SignIn signIn;
    public Communicator communicator;

    private CarSuperFun carSuperFun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        this.carSuperFun = new CarSuperFun(googleGameServices);
        initialize(carSuperFun, config);

        this.setUpGame = new SetUpGame(this);
        this.signIn = new SignIn(this, setUpGame, carSuperFun);
        this.communicator = setUpGame.getCommunicator();

        signIn.setSignInClient();
    }


    @Override
    protected void onResume() {
        super.onResume();
        signIn.signInSilently();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == signIn.SIGN_IN_ID) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                Log.d(TAG, "Login success");
                carSuperFun.createNewState(NewState.LOGIN_MENU);
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        } else if (requestCode == setUpGame.RC_WAITING_ROOM) {
            if (resultCode == Activity.RESULT_OK) {
                setUpGame.waitingRoomReady();
                carSuperFun.createNewState(NewState.RACE_MODE);
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                setUpGame.leaveRoom();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setUpGame.leaveRoom();
            }
        }
    }

    public GoogleGameServices googleGameServices = new GoogleGameServices() {
        @Override
        public void broadcast(boolean finalScore, int score, Vector2 velocity, Vector2 position, float angle) {
            communicator.broadcast(finalScore, score, velocity, position, angle);
        }

        @Override
        public Array<OpponentCarController> getOpponentCarControllers() {
            return communicator.getOpponentCarControllers();
        }

        @Override
        public boolean isSignedIn() {
            return signIn.isSignedIn();
        }

        @Override
        public void signOut() {
            signIn.signOut();
        }

        @Override
        public void startSignInIntent() {
            signIn.startSignInIntent();
        }

        @Override
        public void startQuickGame() {
            setUpGame.startQuickGame();
        }

        @Override
        public void leaveRoom() {
            setUpGame.leaveRoom();
        }
    };

}


