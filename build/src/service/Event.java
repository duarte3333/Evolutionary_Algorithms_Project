package src.service;
import src.model.Individual;
import src.main.Main;

/**
 * Abstract class representing an event.
 */
public abstract class Event {
    private double time;

    /**
     * Constructs an event with the given time.
     *
     * @param time The time at which the event occurs.
     */
    public Event(double time) {
        this.time = time;
    }

    /**
     * Returns the time at which the event occurs.
     *
     * @return The time at which the event occurs.
     */
    public double getTime() {
        return time;
    }

    /**
     * Executes the event on the given individual.
     *
     * @param individual The individual on which to execute the event.
     * @param currentTime The current time.
     * @param main The Main instance.
     */
    public abstract void execute(Individual individual, double currentTime, Main main);
}
