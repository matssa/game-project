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

//    private int posLarger = 0;
//    private int posSmaller = 0;

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

    // Set the position of the car using values from libGDX coordinate system
    public void setPositionAndAngle(int xCoordinate, int yCoordinate, float angle) {
        float x = (xCoordinate + sprite.getWidth() / 2) / GlobalVariables.PIXELS_TO_METERS;
        float y = (yCoordinate + sprite.getHeight() / 2) / GlobalVariables.PIXELS_TO_METERS;
        body.setTransform(x, y, angle);
    }

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    public void update(float dt){
        super.update(dt);
        if (doUpdate) {
            doUpdate = false;
//            if(body.getPosition().x > newPosition.x) {
//                Gdx.app.log("" + timestamp + "@ OpponentCar:", "oldX: " + body.getPosition().x + ", newX: " + newPosition.x + ", sentX: " + sentPosition.x);
//            }
            body.setTransform(newPosition, newAngle);
            body.setLinearVelocity(newVelocity);
        }
    }

    public void setMovement(float xPos, float yPos, float angle, Vector2 velocity, int timeDiff, int timestamp) {
        sentPosition = new Vector2(xPos, yPos);
        Vector2 travelledDistance = velocity.cpy().scl((0.5f * carController.getForward() + 1f) * 10 * (float) timeDiff / 10000f);
        newPosition = sentPosition.cpy().add(travelledDistance);
        newAngle = angle;
        newVelocity = velocity;
        doUpdate = true;
        GlobalVariables.opponentCarSetMovementCounter++;
        Gdx.app.log("opponentCar", "setMovement");
        this.timestamp = timestamp;
//        Gdx.app.log("setMovement", "new position: (" + newPosition.x + ", " + newPosition.y + "), new angle: " + angle);
//        if (body.getPosition().x > newPosition.x) {
////            Gdx.app.log("LAG!", "New position behind current");
////            GlobalVariables.logVector(body.getPosition(), "current position");
////            GlobalVariables.logVector(newPosition, "new position");
//            posSmaller++;
//        } else {
//            posLarger++;
//        }

    }

//    public void logPos() {
//        Gdx.app.log("Number of times updated position was smaller than current position", "" + posSmaller);
//        Gdx.app.log("Number of times updated position was larger than current position", "" + posLarger);
//    }
}
