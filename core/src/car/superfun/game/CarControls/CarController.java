package car.superfun.game.CarControls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import car.superfun.game.observerPattern.Subject;

/**
 * Created by kristian on 06.03.18.
 */

public class CarController extends Subject {
    public float slider1Position;
    public float slider2Position;

    public float forward;
    public float rotation;

    private Texture slider1Texture;
    private Texture slider2Texture;

    private Texture knob1Texture;
    private Texture knob2Texture;

    private int knobRadius = 50;
    private int sliderWidth = 120;

    public CarController() {
        super();
        slider1Position = Gdx.graphics.getHeight() / 2;
        slider2Position = Gdx.graphics.getHeight() / 2;

        slider1Texture = new Texture("slider.png");
        slider2Texture = new Texture("slider.png");

        knob1Texture = new Texture("slider_knob.png");
        knob2Texture = new Texture("slider_knob.png");
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

            forward = slider1Position + slider2Position - Gdx.graphics.getHeight();

            notifyObservers();
        }
    }

    public void render(SpriteBatch sb) {
        sb.begin();

        sb.draw(slider1Texture, 0, 0, slider1Texture.getWidth(), Gdx.graphics.getHeight());
        sb.draw(slider2Texture, Gdx.graphics.getWidth() - sliderWidth, 0, slider1Texture.getWidth(), Gdx.graphics.getHeight());

        sb.draw(knob1Texture, (sliderWidth / 2) - knobRadius, slider1Position);
        sb.draw(knob2Texture, Gdx.graphics.getWidth() - (sliderWidth / 2) - knobRadius, slider2Position);

        sb.end();
    }

}
