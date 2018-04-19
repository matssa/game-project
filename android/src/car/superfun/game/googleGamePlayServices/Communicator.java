package car.superfun.game.googleGamePlayServices;

import android.support.annotation.NonNull;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.instacart.library.truetime.TrueTime;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import car.superfun.game.AndroidLauncher;
import car.superfun.game.GlobalVariables;
import car.superfun.game.car.OpponentCarController;
import car.superfun.game.menus.Leaderboard;


public class Communicator {

    final static String TAG = "Communicator";

    private AndroidLauncher androidLauncher;

    private SetUpGame setUpGame;

    public Map<String, OpponentCarController> participantCarControllers = new HashMap<>();

    private Map<String, Integer> lastTimestamps = new HashMap<>();
    private int readyParticipants = 0;
    private long myReadyTime = 0L;

    public long startTime = 0;
    public boolean gameStarted = false;


    public Communicator(AndroidLauncher androidLauncher, SetUpGame setUpGame) {
        this.setUpGame = setUpGame;
        this.androidLauncher = androidLauncher;
    }

    public void putParticipantController(String id, OpponentCarController controller) {
        participantCarControllers.put(id, controller);
    }

    public void clearParticipantCarController() {
        participantCarControllers.clear();
    }

    public void initiateTimestampMap() {
        for (OpponentCarController opponent : participantCarControllers.values()) {
            lastTimestamps.put(opponent.getParticipant().getParticipantId(), new Integer(0));
        }
    }

    public Array<OpponentCarController> getOpponentCarControllers() {
        Array<OpponentCarController> opponentCarControllers = new Array<>(participantCarControllers.size());
        for (Map.Entry<String, OpponentCarController> entry : participantCarControllers.entrySet()) {
            if (!entry.getKey().equals(setUpGame.myId)) {
                opponentCarControllers.add(entry.getValue());
            }
        }
        return opponentCarControllers;
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

    public void broadcastScore(int score) {
        ByteBuffer messageBuffer = ByteBuffer.allocate(6);
        messageBuffer.putChar(0, 'F'); // F for finished
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

        // Send to all the other participants.
        for (Participant p : setUpGame.participants) {
            if (p.getParticipantId().equals(setUpGame.myId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            // Using UDP so that we don't waste time resending time critical packages
            setUpGame.realTimeMultiplayerClient.sendUnreliableMessage(messageBuffer.array(), setUpGame.roomId, p.getParticipantId());
        }
    }

    private void broadcastReliableMessage(byte[] byteArray) {
        // Send to every other participant.
        for (Participant p : setUpGame.participants) {
            if (p.getParticipantId().equals(setUpGame.myId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            setUpGame.realTimeMultiplayerClient.sendReliableMessage(byteArray,
                    setUpGame.roomId, p.getParticipantId(), new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
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

    private void handleFinalScoreMessage(ByteBuffer buffer, String senderId) {
        int score = buffer.getInt(2);
        String senderName = participantCarControllers.get(senderId).getParticipant().getDisplayName();
        Leaderboard.getInstance().newPlayerScore(senderName, score);
    }

    private void handleStateMessage(ByteBuffer buffer, String senderId) {
        OpponentCarController opponentCarController = (OpponentCarController) participantCarControllers.get(senderId);
        if (!opponentCarController.hasControlledCar()) {
            Gdx.app.error("opponentCarController missing car", "id: " + senderId);
            return;
        }

        int packageTimestamp = buffer.getInt(2);
        if (packageTimestamp < lastTimestamps.get(senderId)) {
            Gdx.app.log("package loss", "out of order package arrived. timestamp: " + packageTimestamp);
            return; // Dropping outdated message
        }
        lastTimestamps.put(senderId, new Integer(packageTimestamp));
        int timeDiff = Math.abs(((int) (TrueTime.now().getTime() % 2147483648L)) - packageTimestamp);

        Vector2 velocity = new Vector2(buffer.getFloat(6), buffer.getFloat(10));
        Vector2 position = new Vector2(buffer.getFloat(14), buffer.getFloat(18));

        float angle = buffer.getFloat(22);
        float forward = buffer.getFloat(26);
        float rotation = buffer.getFloat(30);

        opponentCarController.setForwardAndRotation(forward, rotation);
        opponentCarController.setCarMovement(position, angle, velocity, timeDiff, packageTimestamp);
    }


    public void readyToStart(boolean isSinglePlayer) {
        if (isSinglePlayer) {
            gameStarted = true;
            return;
        }
        ByteBuffer messageBuffer = ByteBuffer.allocate(10);
        messageBuffer.putChar(0, 'R');

        myReadyTime = TrueTime.now().getTime() + GlobalVariables.START_DELAY_MS;
        messageBuffer.putLong(2, myReadyTime);

        newParticipantReady(myReadyTime);

        broadcastReliableMessage(messageBuffer.array());
    }

    private void newParticipantReady(long newStartTime) {
        readyParticipants++;
        if (newStartTime > this.startTime) {
            this.startTime = newStartTime;
        }
        if (readyParticipants == setUpGame.participants.size()) {
            Gdx.app.log("all participants have joined", "starting game in " + (startTime - TrueTime.now().getTime()) + " ms");
            startGame();
//            startRenderService();
        }
    }

    private void startGame() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Gdx.app.log("GAME STARTED", "GO GO GO!!!");
                gameStarted = true;
            }
        }, startTime - TrueTime.now().getTime());
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

    public void updateParticipants(List<String> peersWhoLeft) {
        for (String id : peersWhoLeft) {
            OpponentCarController controller = (OpponentCarController) participantCarControllers.get(id);
            controller.getControlledCar().setRender(false);
        }
    }

}
