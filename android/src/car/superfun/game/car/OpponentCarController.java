package car.superfun.game.car;

import com.badlogic.gdx.math.Vector2;

public class OpponentCarController implements CarController {
    public float forward;
    public float rotation;
    public String partisipentID;
    private OpponentCar controlledCar;

    public OpponentCarController() {
        forward = 0;
        rotation = 0;
    }

    public void setControlledCar(OpponentCar controlledCar, String partisipentID) {

        this.controlledCar = controlledCar;
        this.partisipentID = partisipentID;
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
    public float getForward() {
        return forward;
    }

    @Override
    public float getRotation() {
        return rotation;
    }
}
