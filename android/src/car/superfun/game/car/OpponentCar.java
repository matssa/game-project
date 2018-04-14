package car.superfun.game.car;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.instacart.library.truetime.TrueTime;

import car.superfun.game.CarSuperFun;
import car.superfun.game.GlobalVariables;

public class OpponentCar extends Car {


    private Vector2 receivedPosition;
    private Vector2 receivedVelocity;
    private float receivedAngle;
    private int receivedTimeDiff;
    private int receivedTimestamp;

    private boolean doUpdate;
//    private boolean lostLastPackage;

    GlobalVariables.AvgLogger posDiffLogger;

    public OpponentCar(Vector2 position, OpponentCarController opponentCarController, World world) {
        super(position,
                new Sprite(new Texture("racing-pack/PNG/Cars/car_red_5.png")),
                opponentCarController,
                world,
                GlobalVariables.OPPONENT_ENTITY);
        opponentCarController.setControlledCar(this);
        doUpdate = false;
//        lostLastPackage = false;
//        posDiffLogger = new AvgLogger(10, "Average position difference", 0.001f);
    }

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    public void update(float dt){
        if (doUpdate) {
            doUpdate = false;
            updateState(receivedPosition, receivedAngle, receivedVelocity, receivedTimeDiff, receivedTimestamp);
        }
        super.update(dt);
    }

    private void updateState(Vector2 position, float angle, Vector2 velocity, int timeDiff, int timestamp) {
        Vector2 travelledDistance = velocity.cpy().scl((0.5f * carController.getForward() + 1f) * 5 * (float) timeDiff / 10000f);
        Vector2 updatedPosition = position.cpy().add(travelledDistance);
        Vector2 positionDifference = updatedPosition.cpy().sub(body.getPosition());

        Vector2 newPosition;
        if (positionDifference.len() < 2) {
            newPosition = body.getPosition().cpy().add(positionDifference.scl(0.18f));
        } else {
            newPosition = updatedPosition;
        }

        float newAngle;
        float angleDifference = body.getAngle() - angle;
        if (Math.abs(angleDifference) < 1) {
            newAngle = body.getAngle() - 0.18f * angleDifference;
        } else {
            newAngle = angle;
        }

//        Gdx.app.log("their timestamp", "" + timestamp);
//        Gdx.app.log("time right now", "" + (int) (TrueTime.now().getTime() % 2147483648L));
//        GlobalVariables.logVector(position, "received position");
//        GlobalVariables.logVector(newPosition, "new position");

        body.setTransform(newPosition, newAngle);
        body.setLinearVelocity(velocity);
    }

    // Timestamp is for logging purposes only
    // TODO remove timestamp when finished using it
    public void setMovement(Vector2 position, float angle, Vector2 velocity, int timeDiff, int timestamp) {
        receivedPosition = position;
        receivedVelocity = velocity;
        receivedAngle = angle;
        receivedTimeDiff = timeDiff;
        receivedTimestamp = timestamp;

        doUpdate = true;
    }
}