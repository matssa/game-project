package car.superfun.game.physicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.CarControls.CarController;
import car.superfun.game.CarSuperFun;
import car.superfun.game.observerPattern.Observer;
import car.superfun.game.observerPattern.Subject;

import static java.lang.Math.abs;

/**
 * Created by kristian on 06.03.18.
 */

public class LocalCar implements Observer {
//    private int maxSpeed;
    private float acceleration;
    private float steering;
    private float grip;

    private CarController carController;

    private Vector2 direction;
    private float frameRotation;

    Body body;
    Sprite sprite;

    private float normalFriction;

    public LocalCar(Vector2 position, Sprite sprite, CarController carController, World world){
//        super(position, sprite, new Vector2(0,0));

        this.sprite = sprite;
        this.sprite.setPosition(position.x, position.y);
        Gdx.app.log("inital sprite rotation: ", "" + this.sprite.getRotation());

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / CarSuperFun.PIXELS_TO_METERS,
                (sprite.getY() + sprite.getHeight() / 2) / CarSuperFun.PIXELS_TO_METERS);
        bodyDef.allowSleep = false;
        bodyDef.angularDamping = 0.8f;
        bodyDef.linearDamping = 0.4f;

//        bodyDef.angularDamping

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.getWidth() / 2) / CarSuperFun.PIXELS_TO_METERS, (sprite.getHeight() / 2) / CarSuperFun.PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        body.createFixture(fixtureDef);
        shape.dispose();

//        maxSpeed = 2500;
        acceleration = 15.0f;
        steering = 3.3f;
        grip = 7;

        this.carController = carController;
        direction = new Vector2(0, 1);
        frameRotation = 0;

//        normalFriction = friction;
    }

    public LocalCar(Vector2 position, CarController carController, World world) {
        this(position,
                new Sprite(new Texture("racing-pack/PNG/Cars/car_red_5.png")),
                carController,
                world);
    }

    //    @Override
    public void update(float dt) {
//        body.applyTorque(carController.rotation * steering, true);
        frameRotation = carController.rotation * steering;
//        double angle = (body.getAngle() % (2 * Math.PI));
//        angle = (angle + 2 * Math.PI) % (2 * Math.PI);
//        Gdx.app.log("angle:    ", "" + angle);
//        Gdx.app.log("SpriteAngle:    ", "" + sprite.getRotation());
        Vector2 direction = new Vector2(0,1).rotateRad(body.getAngle());
//        Gdx.app.log("direction: ", "(" + direction.x + ", " + direction.y + ")");
//        Gdx.app.log("f len", "" + direction.len());
//        body.applyForceToCenter(body.getLinearVelocity().cpy().nor().scl(carController.forward), true);


//        super.update(dt);



        float traction = abs(body.getLinearVelocity().dot(direction.cpy().rotate(90 + (float) Math.toDegrees(frameRotation))));
        float sidewaysVelocityDampening = (body.getLinearVelocity().len() > 0.1) ? (abs(body.getLinearVelocity().dot(direction) / body.getLinearVelocity().len()) / 4) + 0.75f : 1;
//        Gdx.app.log("sideways velocity dampening", ": " + sidewaysVelocityDampening);

//        Gdx.app.log("traction: ", "" + traction);
//
//        float traction = abs(this.getVelocity().dot(this.getDirection().rotate(90 + frameRotation)));
        if (traction < grip) {
            body.setLinearVelocity(body.getLinearVelocity().rotate(frameRotation).scl(sidewaysVelocityDampening));
        } else {
            frameRotation = frameRotation * (float) (Math.exp(grip / traction) / Math.exp(traction / grip));
//            Gdx.app.log("frameRotation: ", "" + frameRotation);
            body.setLinearVelocity(body.getLinearVelocity().rotate(frameRotation / 4).scl(sidewaysVelocityDampening));
//            velocity.rotate(frameRotation * 0.15f);
//            Gdx.app.log("lost grip", "" + traction);
//            friction = 2f * normalFriction;
        }
//
//        direction.rotate(frameRotation);
//        sprite.rotate(frameRotation);
//        if (velocity.len() < maxSpeed) {
//            Vector2 addedVel = new Vector2(direction).scl(acceleration * carController.forward * dt);
//            velocity.add(addedVel);
//        }
        body.applyForceToCenter(direction.scl(carController.forward * acceleration), true);
        body.setAngularVelocity(frameRotation);
    }

    public void render(SpriteBatch sb) {
        sprite.setPosition((body.getPosition().x * CarSuperFun.PIXELS_TO_METERS) - sprite.getWidth()/2 ,
                (body.getPosition().y * CarSuperFun.PIXELS_TO_METERS) - sprite.getHeight()/2 );
        sprite.setRotation((float)Math.toDegrees(body.getAngle()));
        sb.begin();
        sb.draw(sprite,
                sprite.getX(),
                sprite.getY(),
                sprite.getOriginX(),
                sprite.getOriginY(),
                sprite.getWidth(),
                sprite.getHeight(),
                sprite.getScaleX(),
                sprite.getScaleY(),
                sprite.getRotation());
        sb.end();
    }

    @Override
    public void notifyOfChange() {
        //TODO: Is this really needed at all?
    }

    public Vector2 getVelocity() {
//        return velocity.cpy();
        Gdx.app.log("localCar velocity: ", "(" + body.getLinearVelocity().x + ", " + body.getLinearVelocity().y + ")");
        return body.getLinearVelocity();
    }

    public Vector2 getPosition() {
//        return position.cpy();
//        Gdx.app.log("localCar position: ", "(" + body.getPosition().x + ", " + body.getPosition().y + ")");
        return new Vector2(sprite.getX(), sprite.getY());
    }

    @Override
    public void subscribeTo(Subject subject) {
        carController = (CarController) subject;
    }

    public Vector2 getDirection() { return direction.cpy(); }

    public float getFrameRotation() {
        return frameRotation;
    }
}
