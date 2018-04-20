package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import car.superfun.game.actor.DelayedButtonActor;
import car.superfun.game.actor.LoadingActor;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class LoadingScreen extends State {
    private Stage stage;

    public LoadingScreen(){
        this.stage = new Stage(new ScreenViewport());

        DelayedButtonActor backButton = new DelayedButtonActor("menu-buttons/backWhite.png");
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(MainMenu.stage);
                GameStateManager.getInstance().pop();
                return true;
            }
        });

        backButton.setPosition(stage.getWidth()/50,(stage.getHeight()-backButton.getHeight())-(stage.getHeight()/30));
        LoadingActor loadingActor = new LoadingActor("loading.png");
        loadingActor.setPosition((stage.getWidth()/2)-(loadingActor.getWidth()/2), (stage.getHeight()/2)-(loadingActor.getHeight()/2));
        stage.addActor(backButton);
        stage.addActor(loadingActor);

        Gdx.input.setInputProcessor(stage);
    }

    public void handleInput() {
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}