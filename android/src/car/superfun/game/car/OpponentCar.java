package car.superfun.game.car;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.CarSuperFun;
import car.superfun.game.GlobalVariables;

public class OpponentCar extends Car {

    boolean doUpdate;
    Vector2 newPosition;
    Vector2 newVelocity;
    float newAngle;
    Vector2 sentPosition;
    int timestamp;

    public OpponentCar(Vector2 position, OpponentCarController opponentCarController, World world) {
        super(position,
                new Sprite(new Texture("racing-pack/PNG/Cars/car_red_5.png")),
                opponentCarController,
                world,
                GlobalVariables.OPPONENT_ENTITY);
        opponentCarController.setControlledCar(this);
        doUpdate = false;
    }

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    public void update(float dt){
        super.update(dt);
        if (doUpdate) {
            doUpdate = false;
            body.setTransform(newPosition, newAngle);
            body.setLinearVelocity(newVelocity);
        }
    }

    // Timestamp is for logging purposes only
    // TODO remove timestamp when finished using it
    public void setMovement(Vector2 position, float angle, Vector2 velocity, int timeDiff, int timestamp) {
        Vector2 travelledDistance = velocity.cpy().scl((0.5f * carController.getForward() + 1f) * 15 * (float) timeDiff / 10000f);
        Vector2 updatedPosition = position.cpy().add(travelledDistance);
        Vector2 positionDifference = updatedPosition.cpy().sub(body.getPosition());
        if (positionDifference.len() < 2) {
            newPosition = body.getPosition().cpy().add(positionDifference.scl(0.15f));
        } else {
            newPosition = updatedPosition;
        }
        float angleDifference = body.getAngle() - angle;
        if (Math.abs(angleDifference) < 1) {
            newAngle = body.getAngle() - 0.15f * angleDifference;
        } else {
            newAngle = angle;
        }
        newVelocity = velocity;
        doUpdate = true;
        this.timestamp = timestamp;
    }
}
