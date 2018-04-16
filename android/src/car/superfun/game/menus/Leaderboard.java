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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

import car.superfun.game.actor.ButtonActor;
import car.superfun.game.states.GameStateManager;
import car.superfun.game.states.State;

public class Leaderboard extends State {
    // Singleton
    private static Leaderboard leaderboard = null;

    private Leaderboard(){
    }

    public static Leaderboard getInstance() {
        if(leaderboard == null) {
            leaderboard = new Leaderboard();
        }
        return leaderboard;
    }

    // Rest of the owl
    private Texture background;
    private Stage stage;
    private ArrayList<ArrayList<String>> playerList;
    private Table table;
    private boolean isPositive;
    //private ArrayList<String> placement = ArrayList<>

    public void initialize(){
        this.stage = new Stage(new ScreenViewport());
        background = new Texture("background.png");

        table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center|Align.top);
        table.setPosition(0, stage.getHeight());
        //table.setDebug(true);
        fillTable();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void handleInput() {

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

    public void fillTable(){
        Skin headerSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        headerSkin.getFont("default-font").getData().setScale(6f,6f);
        skin.getFont("default-font").getData().setScale(4f,4f);

        ButtonActor backButton = new ButtonActor(new Sprite(new Texture("menu-buttons/back.png")));
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //GameStateManager.getInstance().pop();
                //Gdx.input.setInputProcessor(MainMenu.stage);
                updateTable("haxor", 999999);
                return true;
            }
        });

        table.add(backButton).expandX().left().colspan(3).padBottom(-60);
        table.row();
        table.add(new Label("Leaderboard", headerSkin)).center().colspan(3);
        table.row();
        table.add(new Label("Position", skin));
        table.add(new Label("Nickname", skin));
        table.add(new Label("Score", skin));
        if(!playerList.isEmpty()){
            int pos = 1;
            if(!isPositive){
                for(int i = playerList.size()-1; i>= 0; i--){
                    table.row();
                    table.add(new Label(Integer.toString(pos)+".", skin)).padRight(80);
                    table.add(new Label(playerList.get(i).get(0), skin)).padRight(80);
                    table.add(new Label(playerList.get(i).get(1), skin));
                    pos += 1;
                }
            }else{
                for(ArrayList<String> player : playerList){
                    table.row();
                    table.add(new Label(Integer.toString(pos)+".", skin)).padRight(150);
                    table.add(new Label(player.get(0), skin)).padRight(150);
                    table.add(new Label(player.get(1), skin));
                    pos += 1;
                }
            }
        }

        stage.addActor(table);
    }

    public void setBool(boolean isPositive){
        this.isPositive = isPositive;
    }

    public void updateTable(String player, int score){
        table.clearChildren();
        placePlayer(player, score);
        fillTable();
    }

    public void placePlayer(String player, int score){
        if(playerList == null){
            playerList = new ArrayList<>();
        }
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add(player);
        String time = msToString(score);
        tempList.add(time);
        playerList.add(tempList);
    }

    private String msToString(int ms){
        String milliseconds = Integer.toString(ms%1000);
        while(milliseconds.length() < 3){
            milliseconds = "0" + milliseconds;
        }
        String seconds = Integer.toString((ms/1000)%60);
        while(seconds.length() < 2){
            seconds = "0" + seconds;
        }
        int minutes = (ms/(1000*60))%60;
        String time;
        if(!(minutes == 0)){
            time = minutes + ":" + seconds + ":" + milliseconds;
        }else{
            time = seconds + ":" + milliseconds;
        }
        return time;
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}
