package car.superfun.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;


public final class GlobalVariables {

    // Common Filters
    public static final short ALL_ENTITIES = -1;  // -1 means all ones, all entities

    public static final short PLAYER_ENTITY = 0b0001;
    public static final short WALL_ENTITY = 0b0010;
    public static final short OPPONENT_ENTITY = 0b0100;

    // For converting between coordinate systems
    public static final float PIXELS_TO_METERS = 100f;

    public static void logVector(Vector2 vector, String tag) {
        Gdx.app.log(tag, "(" + vector.x + ", " + vector.y + ")");
    }

    public static void logVector(Vector2 vector) {
        logVector(vector, "Vector log: ");
    }

    public static final boolean TESTING_MODE = true;

    public static int worldStepCounter = 0;
    public static int 

    // Game mode specific filters are made from 0b 0000 0001 0000 0000 and up.
    // I.e. leave the first 8 entity bits for globals
}
