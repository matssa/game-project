package car.superfun.game.car;

public class OpponentCarController implements CarController {
    public float forward;
    public float rotation;

    public OpponentCarController() {
        forward = 0;
        rotation = 0;
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

    @Override
    public float getForward() {
        return forward;
    }

    @Override
    public float getRotation() {
        return rotation;
    }
}
