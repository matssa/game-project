package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import car.superfun.game.AndroidLauncher;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class LoginMenu extends State{

    private Texture background;
    private Sprite loginButton;
    private AndroidLauncher androidLauncher;


    public LoginMenu(AndroidLauncher androidLauncher){
        this.androidLauncher = androidLauncher;
        background = new Texture("background.png");
        loginButton = new Sprite(new Texture("menu-buttons/google.png"));
        loginButton.setPosition(Gdx.graphics.getWidth()/2 - loginButton.getWidth()/2, Gdx.graphics.getHeight()/2 - loginButton.getHeight()/2);
    }


    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()) {
            if (loginButton.getBoundingRectangle().contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                androidLauncher.startSignInIntent();
            }
            tryNextWindow();

        }
    }

    private void tryNextWindow() {
        System.out.println(androidLauncher.isSignedIn());
        if(androidLauncher.isSignedIn()) {
            GameStateManager.getInstance().push(new MainMenu(androidLauncher));
        }
    }

    @Override
    public void update(float dt) {
        tryNextWindow();
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sb.draw(loginButton, loginButton.getX(), loginButton.getY());
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        loginButton.getTexture().dispose();
    }
}
