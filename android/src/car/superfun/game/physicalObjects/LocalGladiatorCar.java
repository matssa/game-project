package car.superfun.game.physicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.CarControls.CarController;
import car.superfun.game.CarSuperFun;
import car.superfun.game.GlobalVariables;
import car.superfun.game.gameModes.GladiatorMode;
import car.superfun.game.observerPattern.Observer;
import car.superfun.game.observerPattern.Subject;

import static java.lang.Math.abs;


public class LocalGladiatorCar extends LocalCar{

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
        getRebound();
    }

    public void hitByCar() {
        // TODO: Let the cars crash and bounce.
    }


    public void getRebound() {
        if (body.getLinearVelocity().x < 0) {
            body.setLinearVelocity(20f, body.getLinearVelocity().y);
        } else {
            body.setLinearVelocity(-20f, body.getLinearVelocity().y);
        }

        if (body.getLinearVelocity().y < 0) {
            body.setLinearVelocity(body.getLinearVelocity().x, 20f);
        } else {
            body.setLinearVelocity(body.getLinearVelocity().x, -20f);
        }
    }


    public int getScore() {
        return score;
    }
}
