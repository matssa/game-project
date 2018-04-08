package car.superfun.game.gameModes.gladiatorMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.Car.CarController;
import car.superfun.game.Car.LocalCar;

import static java.lang.Math.abs;


public class LocalGladiatorCar extends LocalCar {

    private int score;

    private void log(String string) {
        Gdx.app.log("log: ", string);
    }

    public LocalGladiatorCar(Vector2 position, CarController carController, World world, Integer score){
        super(position, carController, world);
        this.score = score;
    }

    public void hitDeathWalls() {
        score -= 1;
        GladiatorMode.dustWallCrash.play(0.8f);
    }

    public void hitByCar() {
        // TODO: Let the cars crash and bounce.
    }


    public int getScore() {
        return score;
    }
}
