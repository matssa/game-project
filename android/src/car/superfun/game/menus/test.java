package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import car.superfun.game.actor.ButtonActor;
import car.superfun.game.states.State;

public class test extends State {
    private Texture background;
    private Stage stage;
    private ButtonActor hostButton, joinButton, settingsButton;
    private Table table;

    public test() {
        stage = new Stage(new ScreenViewport());
        background = new Texture("background.png");

        table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center|Align.top);

        table.setPosition(0,Gdx.graphics.getHeight());

        settingsButton = new ButtonActor(new Sprite(new Texture("menu-buttons/settings.png")));
        settingsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Debug", "Settings");
                //GameStateManager.getInstance().push(new GameBrowser());
                return true;
            }
        });

        joinButton = new ButtonActor(new Sprite(new Texture("menu-buttons/join.png")));
        joinButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Debug", "Join");
                //GameStateManager.getInstance().push(new GameBrowser());
                return true;
            }
        });

        hostButton = new ButtonActor(new Sprite(new Texture("menu-buttons/host.png")));
        hostButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Debug", "Host");
                //GameStateManager.getInstance().push(new HostMenu());
                return true;
            }
        });

        table.add(settingsButton).expandX().top().right().padBottom(120);
        table.row();
        table.add(joinButton).padBottom(120);
        table.row();
        table.add(hostButton);

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void handleInput() { }

    @Override
    public void update(float dt) { }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(background, 0, 0, stage.getWidth(), stage.getHeight());
        sb.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
    }
}
