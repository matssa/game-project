package car.superfun.game.physicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import car.superfun.game.CarControls.CarController;
import car.superfun.game.observerPattern.Observer;
import car.superfun.game.observerPattern.Subject;

import static java.lang.Math.abs;

/**
 * Created by kristian on 06.03.18.
 */

public class LocalCar extends PhysicalObject implements Observer {
    private int maxSpeed;
    private float acceleration;
    private float steering;
    private float grip;

    private CarController carController;

    private Vector2 direction;
    private float frameRotation;

    private float normalFriction;
    private boolean lostGrip;

    public LocalCar(Vector2 position, Sprite sprite, CarController carController){
        super(position, sprite, new Vector2(0,0));

        maxSpeed = 2500;
        acceleration = 1500.0f;
        steering = 3.3f;
        grip = 100;

        this.carController = carController;
        direction = new Vector2(0, 1);
        frameRotation = 0;

        normalFriction = friction;
        lostGrip = false;
    }

    public LocalCar(Vector2 position, CarController carController) {
        this(position, new Sprite(new Texture("racing-pack/PNG/Cars/car_green_5.png")), carController);
    }

    @Override
    public void update(float dt) {
//        if (lostGrip) {
//        } else {
//        }
        super.update(dt);

        frameRotation = carController.rotation * steering;


        float traction = abs(this.getVelocity().dot(this.getDirection().rotate(90 + frameRotation)));
        if (traction < grip) {
//            lostGrip = false;
            friction = normalFriction;
            velocity.rotate(frameRotation);
        } else {
            frameRotation = frameRotation / 2;
            velocity.rotate(frameRotation * 0.15f);
            Gdx.app.log("lost grip", "" + traction);
            friction = 2f * normalFriction;
        }

        direction.rotate(frameRotation);
        sprite.rotate(frameRotation);
        if (velocity.len() < maxSpeed) {
            Vector2 addedVel = new Vector2(direction).scl(acceleration * carController.forward * dt);
            velocity.add(addedVel);
        }
    }

    @Override
    public void notifyOfChange() {
        //TODO: Is this really needed at all?
    }

    public Vector2 getVelocity() {
        return velocity.cpy();
    }

    public Vector2 getPosition() {
        return position.cpy();
    }

    @Override
    public void subscribeTo(Subject subject) {
        carController = (CarController) subject;
    }

    public Vector2 getDirection() { return direction.cpy(); }

    public float getFrameRotation() {
        return frameRotation;
    }
}
