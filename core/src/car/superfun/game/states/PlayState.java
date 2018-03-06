package car.superfun.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by pcipc on 06.03.2018.
 */

public class PlayState extends State{

    private OrthographicCamera camera;

    public PlayState(OrthographicCamera  camera) {
        this.camera = camera;
        Sprite test = new Sprite(new Texture())
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}
