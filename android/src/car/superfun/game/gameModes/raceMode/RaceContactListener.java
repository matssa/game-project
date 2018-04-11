package car.superfun.game.gameModes.raceMode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import car.superfun.game.GlobalVariables;

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
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int bothCategoryBits = (fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits);

        // stop here if not one and only one LocalCar is involved
        if (((fixtureA.getFilterData().categoryBits ^ fixtureB.getFilterData().categoryBits) & GlobalVariables.PLAYER_ENTITY) != GlobalVariables.PLAYER_ENTITY) {
            return;
        }

        if ((bothCategoryBits & RaceMode.TEST_ENTITY) == RaceMode.TEST_ENTITY) {
            LocalRaceCar localRaceCar = (fixtureA.getUserData() instanceof LocalRaceCar) ? (LocalRaceCar) fixtureA.getUserData() : (LocalRaceCar) fixtureB.getUserData();
            localRaceCar.rotateBy((float) (-Math.PI / 2));
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void beginGoalContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        LocalRaceCar localRaceCar = (fixtureA.getUserData() instanceof LocalRaceCar) ? (LocalRaceCar) fixtureA.getUserData() : (LocalRaceCar) fixtureB.getUserData();

        localRaceCar.passGoal();
    }

    private void beginCheckpointContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        LocalRaceCar localRaceCar = (fixtureA.getUserData() instanceof LocalRaceCar) ? (LocalRaceCar) fixtureA.getUserData() : (LocalRaceCar) fixtureB.getUserData();
        int checkpointId = (fixtureA.getUserData() instanceof LocalRaceCar) ? (int) fixtureB.getBody().getUserData() : (int) fixtureA.getBody().getUserData();

        localRaceCar.passCheckpoint(checkpointId);
    }
}
