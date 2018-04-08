package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import car.superfun.game.GlobalVariables;
import car.superfun.game.gameModes.raceMode.RaceMode;
import car.superfun.game.physicalObjects.LocalCar;

/**
 * Created by kristian on 07.04.18.
 */

public class RaceContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int bothCategoryBits = (fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits);

        // stop here if not one and only one LocalCar is involved
        if (((fixtureA.getFilterData().categoryBits ^ fixtureB.getFilterData().categoryBits) & GlobalVariables.PLAYER_ENTITY) != GlobalVariables.PLAYER_ENTITY) {
            return;
        }

        // Check if at least one of the fixtures is a goal
        if ((bothCategoryBits & RaceMode.GOAL_ENTITY) == RaceMode.GOAL_ENTITY) {
            beginGoalContact(contact);
        }

        // Check if at least one of the fixtures is a checkpoint
        if ((bothCategoryBits & RaceMode.CHECKPOINT_ENTITY) == RaceMode.CHECKPOINT_ENTITY) {
            beginCheckpointContact(contact);
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void beginGoalContact(Contact contact) {
        Gdx.app.log("New contact", "goal contact");
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        LocalCar localCar = (fixtureA.getUserData() instanceof LocalCar) ? (LocalCar) fixtureA.getUserData() : (LocalCar) fixtureB.getUserData();

//        Gdx.app.log("Entity A:", "" + fixtureA.getFilterData().categoryBits);
//        Gdx.app.log("Entity B:", "" + fixtureB.getFilterData().categoryBits);
    }

    private void beginCheckpointContact(Contact contact) {
        Gdx.app.log("New contact", "checkpoint contact");
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        LocalCar localCar = (fixtureA.getUserData() instanceof LocalCar) ? (LocalCar) fixtureA.getUserData() : (LocalCar) fixtureB.getUserData();
        int checkpointId = (fixtureA.getUserData() instanceof LocalCar) ? (int) fixtureB.getUserData() : (int) fixtureA.getUserData();

        Gdx.app.log("checkpoint id", "" + checkpointId);
//        Gdx.app.log("Entity A:", "" + fixtureA.getFilterData().categoryBits);
//        Gdx.app.log("Entity B:", "" + fixtureB.getFilterData().categoryBits);
    }
}
