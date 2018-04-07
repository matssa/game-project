package car.superfun.game.physicalObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by kristian on 06.03.18.
 */

public abstract class GeneralObject {
    public Vector2 position;
    protected Sprite sprite;
    // private Shape2D collider; // TODO: try using sprites first.

    public GeneralObject(Vector2 position, Sprite sprite) {
        this.position = position;
        this.sprite = sprite;
    }

    public void render(SpriteBatch sb) {
        sb.begin();
        sprite.draw(sb);
        sb.end();
    }

    public Sprite getSprite() {
        return this.sprite;
    }
}
