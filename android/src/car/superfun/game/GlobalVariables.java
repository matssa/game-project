package car.superfun.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;


public final class GlobalVariables {

    // Common Filters
    public static final short MASK_PLAYER = -1;               // -1 = Crash with all
    public static final short PLAYER_ENTITY = 0b0001;
    public static final short WALL_ENTITY = 0b0010;

    // Game mode spesific filters are made from 0b 0000 0001 0000 0000 and up.
    // I.e. leave the first 8 entity bits for globals
}
