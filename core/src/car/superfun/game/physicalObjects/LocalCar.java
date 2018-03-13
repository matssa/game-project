package car.superfun.game.physicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

import car.superfun.game.CarControls.CarController;
import car.superfun.game.observerPattern.Observer;
import car.superfun.game.observerPattern.Subject;

/**
 * Created by kristian on 06.03.18.
 */

public class LocalCar extends PhysicalObject implements Observer {
    private int maxSpeed;
    private float acceleration;
    private float steering;

    private CarController carController;

    private Vector2 direction;

    public LocalCar(Vector2 position, Sprite sprite, CarController carController){
        super(position, sprite, new Vector2(0,0));

        maxSpeed = 1500;
        acceleration = 1000.0f;
        steering = 2.0f;

        this.carController = carController;
        direction = new Vector2(0, 1);
    }

    public LocalCar(Vector2 position, CarController carController) {
        this(position, new Sprite(new Texture("racing-pack/PNG/Cars/car_green_5.png")), carController);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        float rotation = carController.rotation * steering;

        direction.rotate(rotation);
        sprite.rotate(rotation);
        velocity.rotate(rotation);

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
        return velocity;
    }

    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void subscribeTo(Subject subject) {
        carController = (CarController) subject;
    }
}
