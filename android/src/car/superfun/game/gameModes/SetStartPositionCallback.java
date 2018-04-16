package car.superfun.game.gameModes;


import com.badlogic.gdx.math.Vector2;

import car.superfun.game.car.LocalCarController;
import car.superfun.game.car.OpponentCarController;

public interface SetStartPositionCallback {

    public abstract void addOpponentCar(Vector2 position, OpponentCarController opponentCarController);
    public abstract void addLocalCar(Vector2 position, LocalCarController localCarController);

}
