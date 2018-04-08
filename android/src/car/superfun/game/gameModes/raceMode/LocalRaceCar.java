package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.car.CarController;
import car.superfun.game.car.LocalCar;

public class LocalRaceCar extends LocalCar {
    int[] checkpoints;

        public LocalRaceCar(Vector2 position, CarController carController, World world, int[] checkpoints) {
            super(position, carController, world);
            this.checkpoints = checkpoints;
    }
}
