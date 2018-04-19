package car.superfun.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import car.superfun.game.actor.ButtonActor;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;


public class SettingsMenu extends State {
    private Texture background;
    private Stage stage;

    public SettingsMenu(){
        background = new Texture("background.png");

        this.stage = new Stage(new ScreenViewport());

        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center|Align.top);

        table.setPosition(0,Gdx.graphics.getHeight());

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        skin.getFont("default-font").getData().setScale(4f,4f);

        ButtonActor backButton = new ButtonActor("menu-buttons/back.png");
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                GameStateManager.getInstance().pop();
                Gdx.input.setInputProcessor(MainMenu.stage);
                return true;
            }
        });

        table.add(backButton).expandX().left().colspan(2).padBottom(-60);
        table.row();
        table.add(new Label("Settings", skin)).center().colspan(2);
        table.row();
        table.add(new Label("Music volume", skin)).right().padRight(80);
        table.add(new Slider(0f, 1f, 0.1f, false, skin)).left().width(stage.getWidth()/6);



        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(float dt) {
    }

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
