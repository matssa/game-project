package car.superfun.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

/**
 * Created by gustav on 06.03.18.
 */

public class Track {
    private Texture map;
    private ArrayList<Object> cars;

    public Track(Texture map, ArrayList<Object> cars) {
        this.map = map;
        this.cars = cars;
    }

    //Todo: Make instantiation of TrackTiles

}
