package car.superfun.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;


public final class GlobalVariables {

    // Common Filters
    public static final int ALL_ENTITIES = -1;  // -1 means all ones, all entities

    public static final int PLAYER_ENTITY = 0b0001;
    public static final int WALL_ENTITY = 0b0010;

    // Game mode specific filters are made from 0b 0000 0001 0000 0000 and up.
    // I.e. leave the first 8 entity bits for globals
}
