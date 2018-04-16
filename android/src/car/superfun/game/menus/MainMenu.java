package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import car.superfun.game.AndroidLauncher;
import car.superfun.game.GoogleGameServices;
import car.superfun.game.gameModes.gladiatorMode.GladiatorMode;
import car.superfun.game.gameModes.raceMode.RaceMode;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class MainMenu extends State {
    private Texture background, hostButton, joinButton, settings, extraSettings;
    private GoogleGameServices googleGameServices;

    public MainMenu(GoogleGameServices googleGameServices){
        this.googleGameServices = googleGameServices;
        background = new Texture("background.png");
        hostButton = new Texture("menu-buttons/host.png");
        joinButton = new Texture("menu-buttons/join.png");
        settings = new Texture("menu-buttons/settings.png");
        extraSettings = new Texture(("menu-buttons/settings.png"));
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            if(isOnSettings()){
                GameStateManager.getInstance().push(new SettingsMenu());
                googleGameServices.signOut();
                //GameStateManager.getInstance().push(new GladiatorMode());
            }
            if(isOnJoin()){
                googleGameServices.startQuickGame();
                GameStateManager.getInstance().push(new GameBrowser());
            }
            if(isOnHost()){
//              GameStateManager.getInstance().push(new HostMenu());
                // starting PlayState instead, so that we can test the game
                RaceMode race = new RaceMode(googleGameServices, false);

//                race.setLocalRaceCar(new Vector2(1600, 11000));
//                Array<Vector2> opponentCars = new Array<Vector2>();
//                opponentCars.add(new Vector2(1600, 11050));
//                race.setOpponentCars(opponentCars);

                GameStateManager.getInstance().push(race);
            }
            if(isOnExtraSettings()){
                GameStateManager.getInstance().push(new GladiatorMode());
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
        sb.draw(extraSettings, 1400, 890);
        sb.end();
    }

    @Override
    public void dispose() {
        settings.dispose();
        extraSettings.dispose();
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

    public boolean isOnExtraSettings(){
        Circle textureBounds = new Circle(1400+settings.getWidth()/2, (Gdx.graphics.getHeight() - 890)-settings.getHeight()/2, settings.getWidth()/2);
        if(textureBounds.contains(Gdx.input.getX(), Gdx.input.getY())){
            return true;
        }else{
            return false;
        }
    }
}
