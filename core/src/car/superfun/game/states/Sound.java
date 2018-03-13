package car.superfun.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by gustav on 13.03.18.
 */

public class Sound extends State{

    private Music music;

    public Sound(String soundFile) {
        music = Gdx.audio.newMusic(Gdx.files.internal("soundFile"));
        music.setLooping(true);
        music.play();
        music.setVolume(0.5f);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        music.setVolume(dt);
    }

    public void stopMusic() {
        music.stop();
    }

    public void startMusic() {
        music.play();
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void renderHud(SpriteBatch sb) {

    }

    @Override
    public void dispose() {
        music.dispose();
    }
}
