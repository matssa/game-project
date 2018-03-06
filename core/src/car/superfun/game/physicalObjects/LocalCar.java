package car.superfun.game.physicalObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import car.superfun.game.observerPattern.Observer;
import car.superfun.game.observerPattern.Subject;

/**
 * Created by kristian on 06.03.18.
 */

public class LocalCar extends PhysicalObject implements Observer {
    private int maxSpeed;
    private int acceleration;

    public LocalCar(Vector2 position, Sprite sprite){
        super(position, sprite, new Vector2(0,0));
        maxSpeed = 100;
        acceleration = 5;
    }

    public LocalCar(Vector2 position) {
        this(position, new Sprite(new Texture("som")));
    }

    @Override
    public void notifyOfChange() {

    }

    @Override
    public void subscribeTo(Subject subject) {

    }
}
