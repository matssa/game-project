package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.Car.CarController;
import car.superfun.game.Car.LocalCar;

public class LocalRaceCar extends LocalCar {
    public LocalRaceCar(Vector2 position, CarController carController, World world) {
        super(position, carController, world);

    }
}
