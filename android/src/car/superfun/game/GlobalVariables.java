package car.superfun.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by gustav on 07.04.18.
 */

public final class GlobalVariables {

    // Common Filters
    public static final short MASK_PLAYER = -1;               // Crash with all
    public static final short PLAYER_ENTITY = 0x0001;
    public static final short WALL_ENTITY = 0x0002;

    // Game mode spesific filters
    public static final short DEATH_ENTITY = 0x0032;

    // Sounds and music
    public static final Sound dustWallCrash = Gdx.audio.newSound(Gdx.files.internal("sounds/crash_in_dirt_wall.ogg"));
}
