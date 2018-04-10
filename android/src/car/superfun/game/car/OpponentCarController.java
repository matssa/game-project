package car.superfun.game.car;

import com.badlogic.gdx.math.Vector2;

public class OpponentCarController implements CarController {
    public float forward;
    public float rotation;
    private OpponentCar controlledCar;

    public OpponentCarController() {
        forward = 0;
        rotation = 0;
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

    public void setCarTransform(float x, float y, float angle) {
        controlledCar.setTransform(x, y, angle);
    }

    public void setCarVelocity(Vector2 velocity) {
        controlledCar.getBody().setLinearVelocity(velocity);
    }

    public OpponentCar getControlledCar() {
        return controlledCar;
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
