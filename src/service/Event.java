package src.service;
import src.model.Individual;
import src.main.Main;

public abstract class Event {
    private double time;

    public Event(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public abstract void execute(Individual individual, double currentTime, Main main);
}
