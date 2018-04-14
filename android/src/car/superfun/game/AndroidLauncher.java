package car.superfun.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.instacart.library.truetime.TrueTime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import car.superfun.game.car.OpponentCarController;

public class AndroidLauncher extends AndroidApplication {


    final static String TAG = "CarSuperFun";

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

    final static int RC_WAITING_ROOM = 10002;

    // Client used to sign in with Google APIs
    private GoogleSignInClient googleSignInClient = null;

    // Client used to interact with the real time multiplayer system.
    private RealTimeMultiplayerClient realTimeMultiplayerClient = null;

    private RoomConfig joinedRoomConfig = null;

    private String roomId;

    // The participants in the currently active game
    ArrayList<Participant> participants = null;

    // My participant ID in the currently active game
    String myId = null;

    private CarSuperFun carSuperFun;

    private int lastTimestamp = 0;
    private int readyParticipants = 0;
    private long startTime = 0;
    private long myReadyTime = 0L;
    public boolean gameStarted = false;

    Map<String, OpponentCarController> participantCarControllers = null;

    // Participants who sent us their final score.
    Set<String> finishedParticipants = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        this.carSuperFun =  new CarSuperFun(this);
        initialize(carSuperFun, config);
        participantCarControllers = new HashMap<>();

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        ClockSynchronizer clockSync = new ClockSynchronizer();
        clockSync.start();
        Gdx.graphics.setContinuousRendering(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult()");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }  else if (requestCode == RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Starting game (waiting room returned OK).");
               // GameStateManager.getInstance().push(new RaceMode(this, false));

                for(Participant participant : participants) {
                    if (participant.getParticipantId().equals(myId)) {
                        continue;
                    }
                    OpponentCarController opponentCarController = new OpponentCarController();
                    participantCarControllers.put(participant.getParticipantId(), opponentCarController);
                }

            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player indicated that they want to leave the room
                leaveRoom();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance). In our game,
                // this means leaving the room too. In more elaborate games, this could mean
                // something else (like minimizing the waiting room UI).
                leaveRoom();
            }
        }
    }

    public void startSignInIntent() {
        Log.d(TAG, "startSignInIntent()");
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    public void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // at this point, the user is signed out.
                        Log.d(TAG, "signOut(): success");
                    }
                });
    }

    /**
     * Try to sign in without displaying dialogs to the user.
     * <p>
     * If the user has already signed in previously, it will not show dialog.
     */
    public void signInSilently() {
        Log.d(TAG, "signInSilently()");

        googleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                        }
                    }
                });
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    public void startQuickGame() {
        readyParticipants = 0;
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 2, 0);

        // build the room config:
        joinedRoomConfig =
                RoomConfig.builder(roomUpdateCallback)
                        .setOnMessageReceivedListener(messageReceivedHandler)
                        .setRoomStatusUpdateCallback(roomStatusCallbackHandler)
                        .setAutoMatchCriteria(autoMatchCriteria)
                        .build();

        // prevent screen from sleeping during handshake
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // create room:
        Games.getRealTimeMultiplayerClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .create(joinedRoomConfig);
    }


    private RoomUpdateCallback roomUpdateCallback = new RoomUpdateCallback() {

        // Called when room has been created
        @Override
        public void onRoomCreated(int statusCode, Room room) {
            Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
                showGameError();
                return;
            }

            // save room ID so we can leave cleanly before the game starts.
            roomId = room.getRoomId();

            // show the waiting room UI
            showWaitingRoom(room);
            gameStarted = false;
        }

        // Called when room is fully connected.
        @Override
        public void onRoomConnected(int statusCode, Room room) {
            Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                showGameError();
                return;
            }
            updateRoom(room);
        }

        @Override
        public void onJoinedRoom(int statusCode, Room room) {
            Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
            if (statusCode != GamesCallbackStatusCodes.OK) {
                Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
                showGameError();
                return;
            }

            // show the waiting room UI
            showWaitingRoom(room);
        }

        // Called when we've successfully left the room (this happens a result of voluntarily leaving
        // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
        @Override
        public void onLeftRoom(int statusCode, @NonNull String roomId) {
            // we have left the room; return to main screen.
            Log.d(TAG, "onLeftRoom, code " + statusCode);
            getMenuScreen();
        }
    };

    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).create();

        getMenuScreen();
    }

     /*
     * CALLBACKS SECTION. This section shows how we implement the several games
     * API callbacks.
     */

    private String playerId;

    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    GoogleSignInAccount signedInAccount = null;

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        if (signedInAccount != googleSignInAccount) {

            signedInAccount = googleSignInAccount;

            // update the clients
            realTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, googleSignInAccount);

            // get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
            playersClient.getCurrentPlayer()
                    .addOnSuccessListener(new OnSuccessListener<Player>() {
                        @Override
                        public void onSuccess(Player player) {
                            playerId = player.getPlayerId();

                            getMenuScreen();
                        }
                    })
                    .addOnFailureListener(createFailureListener("There was a problem getting the player id!"));
        }

    }


    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(e, string);
            }
        };
    }

    private RoomStatusUpdateCallback roomStatusCallbackHandler = new RoomStatusUpdateCallback() {
        // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
        // is connected yet).
        @Override
        public void onConnectedToRoom(Room room) {
            Log.d(TAG, "onConnectedToRoom.");

            //get participants and my ID:
            participants = room.getParticipants();
            myId = room.getParticipantId(playerId);

            // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
            if (roomId == null) {
                roomId = room.getRoomId();
            }

            // print out the list of participants (for debug purposes)
            Log.d(TAG, "Room ID: " + roomId);
            Log.d(TAG, "My ID " + myId);
            Log.d(TAG, "<< CONNECTED TO ROOM>>");

            // Make sure to start the game with lastTimestamp = 0
            lastTimestamp = 0;
        }

        // Called when we get disconnected from the room. We return to the main screen.
        @Override
        public void onDisconnectedFromRoom(Room room) {
            roomId = null;
            joinedRoomConfig = null;
            showGameError();
        }


        // We treat most of the room update callbacks in the same way: we update our list of
        // participants and update the display. In a real game we would also have to check if that
        // change requires some action like removing the corresponding player avatar from the screen,
        // etc.
        @Override
        public void onPeerDeclined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerInvitedToRoom(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onP2PDisconnected(@NonNull String participant) {
        }

        @Override
        public void onP2PConnected(@NonNull String participant) {
        }

        @Override
        public void onPeerJoined(Room room, @NonNull List<String> arg1) {
            updateRoom(room);
        }

        @Override
        public void onPeerLeft(Room room, @NonNull List<String> peersWhoLeft) {
            updateRoom(room);
        }

        @Override
        public void onRoomAutoMatching(Room room) {
            updateRoom(room);
        }

        @Override
        public void onRoomConnecting(Room room) {
            updateRoom(room);
        }

        @Override
        public void onPeersConnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(Room room, @NonNull List<String> peers) {
            updateRoom(room);
        }
    };

    // Leave the room.
    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        stopKeepingScreenOn();
        if (roomId != null) {
            realTimeMultiplayerClient.leave(joinedRoomConfig, roomId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            roomId = null;
                            joinedRoomConfig = null;
                        }
                    });

        }
        getMenuScreen();
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void getMenuScreen() {
        // TODO show menu screen here
        Log.d(TAG, "Show menu screen");
    }

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        realTimeMultiplayerClient.getWaitingRoomIntent(room, MIN_PLAYERS)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        // show waiting room UI
                        startActivityForResult(intent, RC_WAITING_ROOM);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem getting the waiting room!"));
    }

    private void updateRoom(Room room) {
        // TODO show waiting room
        Log.d(TAG, "Do some stuff");
    }

    private void handleException(Exception exception, String details) {
        int status = 0;

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        String errorString = null;
        switch (status) {
            case GamesCallbackStatusCodes.OK:
                break;
            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                errorString = getString(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                errorString = getString(R.string.match_error_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                errorString = getString(R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                errorString = getString(R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                errorString = getString(R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                errorString = getString(R.string.match_error_locally_modified);
                break;
            default:
                errorString = getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status));
                break;
        }

        if (errorString == null) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);

        new AlertDialog.Builder(AndroidLauncher.this)
                .setTitle("Error")
                .setMessage(message + "\n" + errorString)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    private void handleInterimScoreMessage(ByteBuffer buffer, String senderId) {
    }

    private void handleFinalScoreMessage(ByteBuffer buffer, String senderId) {
        finishedParticipants.add(senderId);
    }

    private void handleStateMessage(ByteBuffer buffer, String senderId) {
        OpponentCarController opponentCarController = participantCarControllers.get(senderId);
        if (!opponentCarController.hasControlledCar()) {
            Gdx.app.error("opponentCarController missing car", "id: " + senderId);
            return;
        }

        int packageTimestamp = buffer.getInt(2);
        if (packageTimestamp < lastTimestamp) {
            Gdx.app.log("package loss", "out of order package arrived. timestamp: " + packageTimestamp);
            return; // Dropping outdated message
        }
        lastTimestamp = packageTimestamp;
        int timeDiff = Math.abs(((int) (TrueTime.now().getTime() % 2147483648L)) - packageTimestamp);

        Vector2 velocity = new Vector2(buffer.getFloat(6), buffer.getFloat(10));
        Vector2 position = new Vector2(buffer.getFloat(14), buffer.getFloat(18));

        float angle = buffer.getFloat(22);
        float forward = buffer.getFloat(26);
        float rotation = buffer.getFloat(30);

        opponentCarController.setForwardAndRotation(forward, rotation);
        opponentCarController.setCarMovement(position, angle, velocity, timeDiff, packageTimestamp);
    }

    // Called when we receive a real-time message from the network.
    OnRealTimeMessageReceivedListener messageReceivedHandler = new OnRealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
            String senderId = realTimeMessage.getSenderParticipantId();
            ByteBuffer buffer = ByteBuffer.wrap(realTimeMessage.getMessageData());
            switch (buffer.getChar(0)) {
                case ('S'): {
                    handleStateMessage(buffer, senderId);
                    break;
                }
                case ('I'): {
                    handleInterimScoreMessage(buffer, senderId);
                    break;
                }
                case ('F'): {
                    handleFinalScoreMessage(buffer, senderId);
                    break;
                }
                case ('R'): { // a readyMessage
                    newParticipantReady(buffer.getLong(2));
                    break;
                }
                default: {
                    Gdx.app.error("unknown byte buffer content", "length: " + buffer.array().length);
                    for (byte b : buffer.array()) {
                        Gdx.app.error("byte: ", "" + b);
                    }
                }
            }
        }
    };

    private void broadcastReliableMessage(byte[] byteArray) {
        // Send to every other participant.
        for (Participant p : participants) {
            if (p.getParticipantId().equals(myId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            realTimeMultiplayerClient.sendReliableMessage(byteArray,
                    roomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                        @Override
                        public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
                            Log.d(TAG, "RealTime message sent");
                            Log.d(TAG, "  statusCode: " + statusCode);
                            Log.d(TAG, "  tokenId: " + tokenId);
                            Log.d(TAG, "  recipientParticipantId: " + recipientParticipantId);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer tokenId) {
                            Log.d(TAG, "Created a reliable message with tokenId: " + tokenId);
                        }
                    });
        }
    }

    public void broadcastScore(boolean finalScore, int score) {
        ByteBuffer messageBuffer = ByteBuffer.allocate(6);
        if (finalScore) {
            messageBuffer.putChar(0, 'I'); // I for interim
        } else {
            messageBuffer.putChar(0, 'F'); // F for final
        }
        messageBuffer.putInt(2, score);
        broadcastReliableMessage(messageBuffer.array());
    }

    public void broadcastState(Vector2 velocity, Vector2 position, float angle, float forward, float rotation) {

        ByteBuffer messageBuffer = ByteBuffer.allocate(34);

        messageBuffer.putChar(0, 'S'); // S for state

        int timestamp = (int) (TrueTime.now().getTime() % 2147483648L);
        messageBuffer.putInt(2, timestamp);

        messageBuffer.putFloat(6, velocity.x);
        messageBuffer.putFloat(10, velocity.y);

        messageBuffer.putFloat(14, position.x);
        messageBuffer.putFloat(18, position.y);
        messageBuffer.putFloat(22, angle);

        messageBuffer.putFloat(26, forward);
        messageBuffer.putFloat(30, rotation);

//        Gdx.app.log("my timestamp", "" + timestamp);
//        GlobalVariables.logVector(position, "My position");

        // Send to all the other participants.
            for (Participant p : participants) {
                if (p.getParticipantId().equals(myId)) {
                    continue;
                }
                if (p.getStatus() != Participant.STATUS_JOINED) {
                    continue;
                }
                // Using UDP so that we don't waste time resending time critical packages
                realTimeMultiplayerClient.sendUnreliableMessage(messageBuffer.array(), roomId, p.getParticipantId());
            }
    }

    public Array<OpponentCarController> getOpponentCarControllers() {
        Array<OpponentCarController> opponentCarControllers = new Array<OpponentCarController>(participantCarControllers.size());
        for (Map.Entry<String, OpponentCarController> entry : participantCarControllers.entrySet()) {
            opponentCarControllers.add(entry.getValue());
        }
        return opponentCarControllers;
    }

    private class ClockSynchronizer extends Thread { // TODO make this more reliable
        public void run() {
            if (!TrueTime.isInitialized()) {
                Gdx.app.log("TrueTime", "start of run");
                try {
                    TrueTime.build()
                            .withNtpHost("time.google.com")
                            .withLoggingEnabled(false)
                            .withConnectionTimeout(3_1428)
                            .initialize();
                } catch (IOException ex) {
                    Gdx.app.log("IOException from TrueTime:", ex.getMessage());
                    ex.printStackTrace();
                }
                if (!TrueTime.isInitialized()) {
                    Gdx.app.error("TrueTime", "True time is not initialized");
                } else {
                    Gdx.app.log("TrueTime", "True time now initialized! :D");
                }
            } else {
                Gdx.app.log("TrueTime", "True time all ready initialized! :D");
            }
        }
    }

    public void readyToStart() {
        ByteBuffer messageBuffer = ByteBuffer.allocate(10);
        messageBuffer.putChar(0, 'R');

        myReadyTime = TrueTime.now().getTime() + 3000;
        messageBuffer.putLong(2, myReadyTime);

        newParticipantReady(myReadyTime);

        broadcastReliableMessage(messageBuffer.array());
    }

    private void newParticipantReady(long newStartTime) {
        readyParticipants++;
        if (newStartTime > this.startTime) {
            this.startTime = newStartTime;
        }
        if (readyParticipants == participants.size()) {
//            startRenderService();
            gameStarted = true;
        }
    }

    // Seems like it's best to make libGDX do the rendering by itself for now
    // TODO: Test whether this does give a smoother simulation
    private void startRenderService() {
            Runnable renderRequester = new Runnable() {
                @Override
                public void run() {
                    Gdx.graphics.requestRendering();
                }
            };
            ScheduledExecutorService renderService = Executors.newSingleThreadScheduledExecutor();
            renderService.scheduleAtFixedRate(renderRequester,
                    startTime - TrueTime.now().getTime(),
                    25,
                    TimeUnit.MILLISECONDS);
    }
}


