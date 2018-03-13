package car.superfun.game.physicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    private CarController carController;

    private Vector2 direction;

    public LocalCar(Vector2 position, Sprite sprite, CarController carController){
        super(position, sprite, new Vector2(0,0));
        maxSpeed = 1500;
        acceleration = 1000.0f;
        this.carController = carController;
        direction = new Vector2(0, 1);
    }

    public LocalCar(Vector2 position, CarController carController) {
        this(position, new Sprite(new Texture("green_car.png")), carController);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (velocity.len() < maxSpeed) {
            Vector2 addedVel = new Vector2(direction).scl(acceleration * carController.forward * dt);
//            Gdx.app.log("added velocity.x", "" + addedVel.x);
//            Gdx.app.log("added velocity.y", "" + addedVel.y);

            velocity.add(addedVel);
        }
    }

    @Override
    public void notifyOfChange() {
        //TODO: Is this really needed at all?
    }

    @Override
    public void subscribeTo(Subject subject) {
        carController = (CarController) subject;
    }
}
