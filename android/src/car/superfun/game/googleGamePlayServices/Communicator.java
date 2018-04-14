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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import car.superfun.game.AndroidLauncher;
import car.superfun.game.car.OpponentCarController;


public class Communicator {

    final static String TAG = "CarSuperFun";

    private RealTimeMultiplayerClient realTimeMultiplayerClient = null;

    private AndroidLauncher androidLauncher;

    private SetUpGame setUpGame;
    int lastTimestamp;

    // Participants who sent us their final score.
    Set<String> finishedParticipants = new HashSet<>();

    public Map<String, OpponentCarController> participantCarControllers = new HashMap<>();


    public Communicator(AndroidLauncher androidLauncher, SetUpGame setUpGame) {
        this.setUpGame = setUpGame;
        this.androidLauncher = androidLauncher;


    }

    public void putParticipantController(String id, OpponentCarController controller){
        participantCarControllers.put(id, controller);
    }

    public Array<OpponentCarController> getOpponentCarControllers() {
        Array<OpponentCarController> opponentCarControllers = new Array<OpponentCarController>(participantCarControllers.size());
        for (Map.Entry<String, OpponentCarController> entry : participantCarControllers.entrySet()) {
            opponentCarControllers.add(entry.getValue());
        }
        return opponentCarControllers;
    }


    // Called when we receive a real-time message from the network.
    // Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
    // indicating
    // whether it's a final or interim score. The second byte is the score.
    // There is also the
    // 'S' message, which indicates that the game should start.
    public OnRealTimeMessageReceivedListener messageReceivedHandler = new OnRealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
//            String sender = realTimeMessage.getSenderParticipantId();

            ByteBuffer buffer = ByteBuffer.wrap(realTimeMessage.getMessageData());
            if (buffer.getChar(0) == 'U' || buffer.getChar(0) == 'F') {

                int timestamp = buffer.getInt(22);
                if (timestamp < lastTimestamp) {
                    return;
                }
                lastTimestamp = timestamp;
                int timeDiff = Math.abs(((int) (System.currentTimeMillis() % 2147483648L)) - timestamp);

                Vector2 velocity = new Vector2(buffer.getFloat(2), buffer.getFloat(6));

                float x = buffer.getFloat(10);
                float y = buffer.getFloat(14);
                float angle = buffer.getFloat(18);


                OpponentCarController opponentCarController = participantCarControllers.get(realTimeMessage.getSenderParticipantId());

                if (opponentCarController.hasControlledCar()) {
                    opponentCarController.getControlledCar().setMovement(x, y, angle, velocity, timeDiff);
                } else {
                    Gdx.app.log("opponentCarController missing car", "id: " + realTimeMessage.getSenderParticipantId());
                }

                // if it's a final score, mark this participant as having finished
                // the game
                if (buffer.getChar(0) == 'F') {
                    finishedParticipants.add(realTimeMessage.getSenderParticipantId());
                }
            } else {
                Gdx.app.log("unknown byte buffer content", "length: " + buffer.array().length);
                for (byte b : buffer.array()) {
                    Gdx.app.log("byte: ", "" + b);
                }
            }
        }
    };

    public void broadcast(boolean finalScore, int score, Vector2 velocity, Vector2 position, float angle) {

        ByteBuffer messageBuffer = ByteBuffer.allocate(26);

        // First byte in message indicates whether it's a final score or not
//        messageBuffer.putChar(finalScore ? 'F' : 'U');
        messageBuffer.putChar(0, 'U'); // TODO: put 'F' if final score, and send score

        messageBuffer.putFloat(2, velocity.x);
        messageBuffer.putFloat(6, velocity.y);

        messageBuffer.putFloat(10, position.x);
        messageBuffer.putFloat(14, position.y);
        messageBuffer.putFloat(18, angle);

        messageBuffer.putInt(22, (int) (System.currentTimeMillis() % 2147483648L));

        // Send to every other participant.
        for (Participant p : setUpGame.participants) {
            if (p.getParticipantId().equals(setUpGame.myId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            System.out.println("print pos");
            if (realTimeMultiplayerClient == null) {
                this.realTimeMultiplayerClient = setUpGame.realTimeMultiplayerClient;
            }else {
                if (finalScore) {
                    // final score notification must be sent via reliable message
                    realTimeMultiplayerClient.sendReliableMessage(messageBuffer.array(),
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
                } else {
                    realTimeMultiplayerClient.sendUnreliableMessage(messageBuffer.array(), setUpGame.roomId,
                            p.getParticipantId());
                }
            }
        }
    }



}
