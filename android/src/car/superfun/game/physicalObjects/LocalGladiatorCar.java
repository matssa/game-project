package car.superfun.game.physicalObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import car.superfun.game.CarControls.CarController;
import car.superfun.game.CarSuperFun;
import car.superfun.game.gameModes.GladiatorMode;
import car.superfun.game.observerPattern.Observer;
import car.superfun.game.observerPattern.Subject;

import static java.lang.Math.abs;


public class LocalGladiatorCar implements Observer {
    //    private int maxSpeed;
    private float acceleration;
    private float steering;
    private float grip;
//    private BitmapFont font;
    private Sound dustWallCrash;
//    Sound carSound;

    private final short PLAYER_ENTITY;
    private final short DEATH_ENTITY;
    private final short WALL_ENTITY;
    private final short MASK_PLAYER;

    private CarController carController;

    private float frameRotation;

    Body body;
    Sprite sprite;

    private float normalFriction;
    private boolean lostGrip;
    private int score;

    private void log(String string) {
        Gdx.app.log("log: ", string);
    }
    private Vector2 spawnPoint;

    public LocalGladiatorCar(Vector2 position, Sprite sprite, CarController carController, World world, Integer score, Sound dustWallCrash, Sound carSound){
//        super(position, sprite, new Vector2(0,0));

        // Filters. TODO: Put filters in global constants class.
        PLAYER_ENTITY = 0x0001;
        WALL_ENTITY = 0x0002;
        DEATH_ENTITY = 0x0004;

        // Player will crash with all
        MASK_PLAYER = -1;


//        font = new BitmapFont();
//        font.setColor(Color.BLACK);
//        font.getData().setScale(5, 5);

        this.score = score;
        this.sprite = sprite;
        this.sprite.setPosition(position.x, position.y);
        this.dustWallCrash = dustWallCrash;
//        this.carSound = carSound;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) / CarSuperFun.PIXELS_TO_METERS,
                (sprite.getY() + sprite.getHeight() / 2) / CarSuperFun.PIXELS_TO_METERS);
        bodyDef.allowSleep = false;
        bodyDef.angularDamping = 0.9f;
        bodyDef.linearDamping = 0.5f;

        spawnPoint = bodyDef.position.cpy();

//        bodyDef.angularDamping

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox((sprite.getWidth() / 2) / CarSuperFun.PIXELS_TO_METERS, (sprite.getHeight() / 2) / CarSuperFun.PIXELS_TO_METERS);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = PLAYER_ENTITY;
        fixtureDef.filter.maskBits = MASK_PLAYER;

        body.createFixture(fixtureDef).setUserData(this);
        shape.dispose();

//        maxSpeed = 2500;
        acceleration = 4500.0f;
        steering = 200.0f;
        grip = 10;

        this.carController = carController;
        frameRotation = 0;

//        normalFriction = friction;
        lostGrip = false;
    }

    public LocalGladiatorCar(Vector2 position, CarController carController, World world, Integer score, Sound dustWallCrash, Sound carSound) {
        this(position,
                new Sprite(new Texture("black_car.png")),
                carController,
                world,
                score,
                dustWallCrash,
                carSound);
    }

    public void hitDeathWalls() {
//        carSound.pause();
        score -= 1;
        dustWallCrash.play(0.8f);
        Gdx.app.log("yolo", "swag");
        Gdx.app.log("score = ", String.valueOf(score));
        Gdx.app.log("body x vel", String.valueOf(body.getLinearVelocity().x));
        Gdx.app.log("body y vel", String.valueOf(body.getLinearVelocity().y));
        getRebound();
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

    public float getSpeedForSound() {
        float x = Math.round(getVelocity().x * getVelocity().x);
        float y = Math.round(getVelocity().y * getVelocity().y);
        return (float) Math.sqrt(x + y);
    }


    //    @Override
    public void update(float dt) {
//        carSound.play((getSpeedForSound() / 40) + 0.3f);
        frameRotation = carController.rotation * steering * dt;
        Vector2 direction = this.getDirectionVector();

        float traction = abs(body.getLinearVelocity().dot(direction.cpy().rotate(90 + 45 * carController.rotation)));
        float sidewaysVelocityDampening = (body.getLinearVelocity().len() > 0.1) ? (abs(body.getLinearVelocity().dot(direction) / body.getLinearVelocity().len()) / 4) + 0.75f : 1;

//        Gdx.app.log("sideways velocity dampening", ": " + sidewaysVelocityDampening);

        if (traction < grip) {
            body.setLinearVelocity(body.getLinearVelocity().rotate(frameRotation).scl(sidewaysVelocityDampening));
//            if (lostGrip) {
//                lostGrip = false;
//                log("--- grip regained ---");
//            }
        } else {
            float velocityRotator = frameRotation * (float) (Math.exp(grip / traction) / Math.exp(traction / grip));
            frameRotation = frameRotation * (grip / traction);
//            Gdx.app.log("traction: ", "" + traction);
//            Gdx.app.log("frameRotation: ", "" + frameRotation);
            body.setLinearVelocity(body.getLinearVelocity().rotate(velocityRotator).scl(sidewaysVelocityDampening));
//            velocity.rotate(frameRotation * 0.15f);
//            Gdx.app.log("lost grip", "" + traction);
//            friction = 2f * normalFriction;
            lostGrip = true;
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
//        font.draw(sb, String.valueOf(score), 500,500);
        sb.end();
    }

    @Override
    public void notifyOfChange() {
        //TODO: Is this really needed at all?
    }

    public Vector2 getVelocity() {
//        return velocity.cpy();
//        Gdx.app.log("localCar velocity: ", "(" + body.getLinearVelocity().x + ", " + body.getLinearVelocity().y + ")");
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

    public Vector2 getDirectionVector() { return new Vector2(0,1).rotateRad(body.getAngle()); }
    public float getDirectionFloat() { return body.getAngle(); }

    public float getFrameRotation() {
        return frameRotation;
    }
}
