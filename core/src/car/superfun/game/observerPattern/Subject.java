package car.superfun.game.observerPattern;

import java.util.ArrayList;

/**
 * Created by kristian on 01.02.18.
 */

public abstract class Subject {
    private ArrayList<Observer> observers;

    public Subject() {
        observers = new ArrayList<Observer>();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
        observer.subscribeTo(this);
    }

    protected void notifyObservers() {
        for (Observer observer : observers) {
            observer.notifyOfChange();
        }
    }
}
