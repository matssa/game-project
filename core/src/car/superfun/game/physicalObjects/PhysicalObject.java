package car.superfun.game.physicalObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by kristian on 06.03.18.
 */

public abstract class PhysicalObject extends GeneralObject {
    public Vector2 velocity;
    public int weight;

    private float friction;

    public PhysicalObject(Vector2 position, Sprite sprite, Vector2 velocity, int weight, float friction) {
        super(position, sprite);
        this.velocity = velocity;
        this.weight = weight;
        this.friction = friction;
    }

    public PhysicalObject(Vector2 position, Sprite sprite, Vector2 velocity) {
        this(position, sprite, velocity, 1, 0.3f);
    }

    public void update(float dt) {
        velocity.scl(dt);
        position.add(velocity.x, velocity.y);
        velocity.scl(1/dt);

        velocity.scl(1 - friction * dt);
        if (velocity.isZero(50f)) {
            velocity.set(0,0);
        }
    }

    // TODO: create onHit dependent on weight
    public void onHit(PhysicalObject otherObject) {
        Vector2 otherPosition = otherObject.position;
        Vector2 otherVelocity = otherObject.velocity;

        Vector2 diff = new Vector2(position);
        diff.sub(otherPosition);
        Vector2 n = new Vector2(diff.nor());

        float ownSpeed = velocity.dot(n);
        float otherSpeed = otherVelocity.dot(n);

        velocity.sub(n.scl(ownSpeed - otherSpeed));
    }

}
