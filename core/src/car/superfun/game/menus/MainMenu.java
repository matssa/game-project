package car.superfun.game.menus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

import car.superfun.game.CarSuperFun;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

/**
 * Created by Jonas on 06.03.2018.
 */

public class MainMenu extends State {
    private Texture background, hostButton, joinButton, settings;
    private Music menu_music;

    public MainMenu(){
        background = new Texture("background.png");
        hostButton = new Texture("menu-buttons/host.png");
        joinButton = new Texture("menu-buttons/join.png");
        settings = new Texture("menu-buttons/settings.png");
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            if(isOnSettings()){
                GameStateManager.getInstance().push(new SettingsMenu());
            }
            if(isOnJoin()){
                GameStateManager.getInstance().push(new GameBrowser());
            }
            if(isOnHost()){
                GameStateManager.getInstance().push(new HostMenu());
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sb.draw(joinButton, Gdx.graphics.getWidth()/2-joinButton.getWidth()/2, Gdx.graphics.getHeight()/2-(joinButton.getHeight()/2)+150);
        sb.draw(hostButton, Gdx.graphics.getWidth()/2-hostButton.getWidth()/2, Gdx.graphics.getHeight()/2-(hostButton.getHeight()/2)-150);
        sb.draw(settings, 1600, 890);
        sb.end();
    }

    @Override
    public void dispose() {
        settings.dispose();
        hostButton.dispose();
        joinButton.dispose();
        background.dispose();
    }

    public boolean isOnJoin(){
        Rectangle textureBounds = new Rectangle((Gdx.graphics.getWidth()/2-joinButton.getWidth()/2), (Gdx.graphics.getHeight()/2+(joinButton.getHeight()/2)-350), (joinButton.getWidth()), joinButton.getHeight());
        if(textureBounds.contains(Gdx.input.getX(), Gdx.input.getY())){
            return true;
        }else{
            return false;
        }
    }

    public boolean isOnHost(){
        Rectangle textureBounds = new Rectangle((Gdx.graphics.getWidth()/2-hostButton.getWidth()/2), (Gdx.graphics.getHeight()/2+(hostButton.getHeight()/2)-50), (hostButton.getWidth()), hostButton.getHeight());
        if(textureBounds.contains(Gdx.input.getX(), Gdx.input.getY())){
            return true;
        }else{
            return false;
        }
    }

    public boolean isOnSettings(){
        Circle textureBounds = new Circle(1600+settings.getWidth()/2, (Gdx.graphics.getHeight() - 890)-settings.getHeight()/2, settings.getWidth()/2);
        if(textureBounds.contains(Gdx.input.getX(), Gdx.input.getY())){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void renderHud(SpriteBatch sb) {

    }
}
