package car.superfun.game.gameModes.raceMode;

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


public class OpponentCar {

    private Sprite sprite;
    private Body body;
    private float frameRotation;


    public OpponentCar(Vector2 position, World world) {
        sprite = new Sprite(new Texture("racing-pack/PNG/Cars/car_red_5.png"));
        frameRotation = 0;
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

        fixtureDef.filter.categoryBits = GlobalVariables.OPPONENT_ENTITY;
        fixtureDef.filter.maskBits = GlobalVariables.ALL_ENTITIES;
        fixtureDef.restitution = 0.2f;

        body.createFixture(fixtureDef).setUserData(this);

        shape.dispose();
    }

    public void update(float dt) {
        sprite.setPosition((body.getTransform().getPosition().x * CarSuperFun.PIXELS_TO_METERS) - sprite.getWidth()/2 ,
                (body.getTransform().getPosition().y * CarSuperFun.PIXELS_TO_METERS) - sprite.getHeight()/2 );
        sprite.setRotation((float)Math.toDegrees(body.getAngle()));
    }

    public void render(SpriteBatch sb) {

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
        // TODO: Get the cars position from GGS
        return new Vector2(sprite.getX(), sprite.getY());
    }

    public void setPosition(Vector2 position, Float angle) {
        body.setTransform(position, angle);
    }

    public Body getBody() {
        return body;
    }

    public float getFrameRotation() {
        // TODO: GET frame rotation from GGS
        return frameRotation;
    }
}
