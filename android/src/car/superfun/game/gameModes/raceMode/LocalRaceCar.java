package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Arrays;

import car.superfun.game.car.LocalCarController;
import car.superfun.game.car.LocalCar;

public class LocalRaceCar extends LocalCar {
    boolean[] passedCheckpoints;
    int completedRounds;

    public LocalRaceCar(Vector2 position, LocalCarController localCarController, World world, int amountOfCheckpoints) {
        super(position, localCarController, world);
        passedCheckpoints = new boolean[amountOfCheckpoints];
        Arrays.fill(passedCheckpoints, Boolean.FALSE);
        completedRounds = 0;
    }

    public void passCheckpoint(int checkpointId) {
        passedCheckpoints[checkpointId] = true;
    }

    public void passGoal() {
        if (allCheckpointsPassed()) {
            completedRounds++;
            Gdx.app.log("GOAL PASSED!", "" + completedRounds + " rounds passed");
            Arrays.fill(passedCheckpoints, Boolean.FALSE);
        }
    }

    private boolean allCheckpointsPassed() {
        for (boolean checkpointPassed : passedCheckpoints) {
            if (!checkpointPassed) {
                return false;
            }
        }
        return true;
    }
}
