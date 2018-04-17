package car.superfun.game.car;

import android.support.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;
import com.google.android.gms.games.multiplayer.Participant;

public class OpponentCarController implements CarController {

    public float forward;
    public float rotation;
    private Participant participent;
    private OpponentCar controlledCar;

    public OpponentCarController(Participant participent) {
        forward = 0;
        rotation = 0;
        this.participent = participent;
    }

    public void setControlledCar(OpponentCar controlledCar) {
        this.controlledCar = controlledCar;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setForwardAndRotation(float forward, float rotation) {
        this.forward = forward;
        this.rotation = rotation;
    }

    public OpponentCar getControlledCar() {
        return controlledCar;
    }

    public void setCarMovement(Vector2 position, float angle, Vector2 velocity, int timeDiff, int timestamp) {
        controlledCar.setMovement(position, angle, velocity, timeDiff, timestamp);
    }

    public boolean hasControlledCar() {
        return (controlledCar != null);
    }

    @Override
    public Participant getParticipant() {
        return participent;
    }

    @Override
    public float getForward() {
        return forward;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public int compareTo(@NonNull CarController carController) {
        return participent.getParticipantId().compareTo(carController.getParticipant().getParticipantId());
    }

}
