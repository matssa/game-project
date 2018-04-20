package car.superfun.game.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TouchableAction;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;

public class DelayedButtonActor extends Actor {
    private Sprite sprite;

    /*
     * The DelayedButtonActor is a button which initially is invisible (and uninteractable).
     * After x amount of seconds, the button appears.
     */

    public DelayedButtonActor(String path){
        this.sprite = new Sprite(new Texture(path));
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());

        VisibleAction setInvisible = new VisibleAction();
        setInvisible.setVisible(false);

        TouchableAction setUntouchable = new TouchableAction();
        setUntouchable.setTouchable(Touchable.disabled);

        DelayAction delay = new DelayAction(5f);

        VisibleAction setVisible = new VisibleAction();
        setVisible.setVisible(true);

        TouchableAction setTouchable = new TouchableAction();
        setTouchable.setTouchable(Touchable.enabled);

        SequenceAction actionSequence = new SequenceAction(setInvisible, setUntouchable, delay, setVisible, setTouchable);

        this.addAction(actionSequence);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(sprite, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
