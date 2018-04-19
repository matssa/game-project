package car.superfun.game.car;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.GlobalVariables;

public class OpponentCar extends Car {


    private Vector2 receivedPosition;
    private Vector2 receivedVelocity;
    private float receivedAngle;
    private int receivedTimeDiff;
    private int receivedTimestamp;

    private boolean doUpdate;

    GlobalVariables.AvgLogger posDiffLogger;

    public OpponentCar(Vector2 position, OpponentCarController opponentCarController, World world, String texturePath) {
        super(position,
                new Sprite(new Texture(texturePath)),
                opponentCarController,
                world,
                GlobalVariables.OPPONENT_ENTITY);
        opponentCarController.setControlledCar(this);
        doUpdate = false;
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

    public void setRender(boolean render){
        super.render = render;
    }
}
