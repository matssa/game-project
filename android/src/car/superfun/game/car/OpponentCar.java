package car.superfun.game.car;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.CarSuperFun;
import car.superfun.game.GlobalVariables;

public class OpponentCar extends Car {

    public OpponentCar(Vector2 position, OpponentCarController opponentCarController, World world) {
        super(position,
                new Sprite(new Texture("racing-pack/PNG/Cars/car_red_5.png")),
                opponentCarController,
                world,
                GlobalVariables.OPPONENT_ENTITY);
        opponentCarController.setControlledCar(this);
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

    public void setMovement(float xPos, float yPos, float angle, Vector2 velocity, int timeDiff) {
        Vector2 sentPosition = new Vector2(xPos, yPos);
        Vector2 travelledDistance = velocity.cpy().scl((float) timeDiff / 10000f);
        body.setTransform(sentPosition.cpy().add(travelledDistance), angle);
        body.setLinearVelocity(velocity);
    }
}
