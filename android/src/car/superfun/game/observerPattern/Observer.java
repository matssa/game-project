package car.superfun.game.observerPattern;

/**
 * Created by kristian on 01.02.18.
 */

public interface Observer {

    // This is the "update()"-method, but did not call it "update", because that was already taken
    void notifyOfChange();

    void subscribeTo(Subject subject);
}
