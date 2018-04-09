package car.superfun.game.gameModes.gladiatorMode;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.car.LocalCarController;
import car.superfun.game.car.LocalCar;

import static java.lang.Math.abs;


public class LocalGladiatorCar extends LocalCar {

    private int score;
    

    public LocalGladiatorCar(Vector2 position, LocalCarController localCarController, World world, Integer score){
        super(position, localCarController, world);
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
