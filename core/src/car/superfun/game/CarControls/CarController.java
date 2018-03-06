package car.superfun.game.CarControls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import car.superfun.game.observerPattern.Subject;

/**
 * Created by kristian on 06.03.18.
 */

public class CarController extends Subject {
    public float slider1Position;
    public float slider2Position;

    public CarController() {
        super();
        slider1Position = Gdx.graphics.getHeight() / 2;
        slider2Position = Gdx.graphics.getHeight() / 2;
    }

    public void update() {
        Vector2 justTouched = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        if(Gdx.input.justTouched()) {
            if(justTouched.x < Gdx.graphics.getWidth() / 8) {
                slider1Position = justTouched.y;
            }
            if(justTouched.x > 7 * Gdx.graphics.getWidth() / 8) {
                slider2Position = justTouched.y;
            }
            notifyObservers();
        }
    }

}
