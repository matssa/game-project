package car.superfun.game.car;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.CarSuperFun;
import car.superfun.game.GlobalVariables;

import static java.lang.Math.abs;

public class LocalCar {
    private float acceleration;
    private float steering;
    private float grip;

    private CarController carController;

    private float frameRotation;

    protected Body body;
    protected Sprite sprite;

    public LocalCar(Vector2 position, Sprite sprite, CarController carController, World world){

        this.sprite = sprite;
        this.sprite.setPosition(position.x, position.y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / CarSuperFun.PIXELS_TO_METERS,
                (sprite.getY() + sprite.getHeight() / 2) / CarSuperFun.PIXELS_TO_METERS);
        bodyDef.allowSleep = false;
        bodyDef.angularDamping = 0.9f;
        bodyDef.linearDamping = 0.5f;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.getWidth() / 2) / CarSuperFun.PIXELS_TO_METERS, (sprite.getHeight() / 2) / CarSuperFun.PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        fixtureDef.filter.categoryBits = GlobalVariables.PLAYER_ENTITY;
        fixtureDef.filter.maskBits = GlobalVariables.ALL_ENTITIES;
        fixtureDef.restitution = 0.2f;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();

        acceleration = 850.0f;
        steering = 175.0f;
        grip = 10;

        this.carController = carController;
        frameRotation = 0;
    }

    public LocalCar(Vector2 position, CarController carController, World world) {
        this(position,
                new Sprite(new Texture("racing-pack/PNG/Cars/car_red_5.png")),
                carController,
                world);
    }

    //    @Override
    public void update(float dt) {
        frameRotation = carController.rotation * steering * dt;
        Vector2 direction = this.getDirectionVector();

        float traction = abs(body.getLinearVelocity().dot(direction.cpy().rotate(90 + 45 * carController.rotation)));
        float sidewaysVelocityDampening = (body.getLinearVelocity().len() > 0.1) ? (abs(body.getLinearVelocity().dot(direction) / body.getLinearVelocity().len()) / 4) + 0.75f : 1;

        if (traction < grip) {
            body.setLinearVelocity(body.getLinearVelocity().rotate(frameRotation).scl(sidewaysVelocityDampening));
        } else {
            float velocityRotator = frameRotation * (float) (Math.exp(grip / traction) / Math.exp(traction / grip));

            frameRotation = frameRotation * (float) (Math.log(grip) / Math.log(traction));

            body.setLinearVelocity(body.getLinearVelocity().rotate(velocityRotator).scl(sidewaysVelocityDampening));
        }

        body.applyForceToCenter(direction.scl(carController.forward * acceleration * dt), true);
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

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
    }

    public Vector2 getBodyPosition() {
        return new Vector2(body.getPosition().x, body.getPosition().y);
    }

    public Vector2 getDirectionVector() { return new Vector2(0,1).rotateRad(body.getAngle()); }

    public Body getBody() { return body; }

    public float getDirectionFloat() { return body.getAngle(); }
    public float getFrameRotation() {
        return frameRotation;
    }
}
