package car.superfun.game.physicalObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by gustav on 06.03.18.
 */

public class TrackTile extends GeneralObject{

    public TrackTile(Vector2 position, Sprite sprite, float rotate){
        super(position, sprite);
        this.getSprite().rotate(rotate);
    }

}
