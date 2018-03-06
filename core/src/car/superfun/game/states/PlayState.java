package car.superfun.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class PlayState extends State{

    private Sprite map;

    private OrthographicCamera camera;

    public PlayState(OrthographicCamera  camera) {
        this.camera = camera;

        // init test map
        map = new Sprite(new Texture("really_the_first_track.png"));
        map.setPosition(0, 0);
    }

    @Override
    public void handleInput() {
    
    }

    @Override
    public void update(float dt) {
        Vector2 position = new Vector2(0,0);
        camera.translate(position);

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        map.draw(sb);
        sb.end();
    }

    @Override
    public void dispose() {

    }
}
