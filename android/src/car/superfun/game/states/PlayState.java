package car.superfun.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import car.superfun.game.CarControls.CarController;
import car.superfun.game.gameModes.GameMode;
import car.superfun.game.physicalObjects.LocalCar;


public class PlayState extends GameMode{

    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    private CarController carController;
    private LocalCar localCar;

    public PlayState() {
        super();

        carController = new CarController();
        localCar = new LocalCar(new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2), carController);
        tiledMap = new TmxMapLoader().load("tiled_maps/simpleMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    @Override
    public void handleInput() {
    
    }

    @Override
    public void update(float dt) {
        carController.update();
        localCar.update(dt);
        camera.position.set(localCar.getPosition().add(localCar.getVelocity().scl(0.12f)), 0);
        camera.rotate(-localCar.getFrameRotation());
    }

    // Renders objects that had a static position in the gameworld. Is called by superclass
    @Override
    public void renderWithCamera(SpriteBatch sb, OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        localCar.render(sb);
    }

    // Renders objects that have a static position on the screen. Is called by superclass
    @Override
    public void renderHud(SpriteBatch sb) {
        carController.render(sb);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void endGame() {
        // TODO: Implement a proper way to exit the game
    }
}
