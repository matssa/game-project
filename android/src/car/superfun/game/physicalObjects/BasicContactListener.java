package car.superfun.game.physicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import car.superfun.game.TrackBuilder;

/**
 * Created by kristian on 07.04.18.
 */

public class BasicContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int bothCategoryBits = (fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits);

        // stop here if not one and only one LocalCar is involved
        if (((fixtureA.getFilterData().categoryBits ^ fixtureB.getFilterData().categoryBits) & TrackBuilder.USER_ENTITY) != TrackBuilder.USER_ENTITY) {
            return;
        }

        // Check if at least one of the fixtures is a goal
        if ((bothCategoryBits & TrackBuilder.GOAL_ENTITY) == TrackBuilder.GOAL_ENTITY) {
            beginGoalContact(contact);
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
}
