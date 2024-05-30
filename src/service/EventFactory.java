
package src.service;

import src.model.Individual;
import java.util.Random;

/**
 * Factory class to create events.
 */
public class EventFactory {
    private double reproductionRate;
    private double mutationRate;
    private double deathRate;
    private Random random;

    /**
     * Constructs an EventFactory with the given parameters.
     *
     * @param reproductionRate The reproduction rate.
     * @param mutationRate The mutation rate.
     * @param deathRate The death rate.
     * @param random The Random instance for generating random values.
     */
    public EventFactory(double reproductionRate, double mutationRate, double deathRate, Random random) {
        this.reproductionRate = reproductionRate;
        this.mutationRate = mutationRate;
        this.deathRate = deathRate;
        this.random = random;
    }

    /**
     * Creates an event for the given individual.
     *
     * @param individual The individual for which to create an event.
     * @return The created event.
     */
    public Event createEvent(Individual individual) {
        double comfort = individual.getComfort();
        double deathRate = (1 - Math.log(1 - comfort)) * this.deathRate;
        double mutationRate = (1 - Math.log(comfort)) * this.mutationRate;
        double reproductionRate = (1 - Math.log(comfort)) * this.reproductionRate;

        double Tdeath = -deathRate * Math.log(1 - random.nextDouble());
        double Tmutation = -mutationRate * Math.log(1 - random.nextDouble());
        double Treproduction = -reproductionRate * Math.log(1 - random.nextDouble());

        if (Tmutation < Tdeath && Tmutation < Treproduction) {
            return new MutateEvent(Tmutation, random);
        } else if (Treproduction < Tdeath && Treproduction < Tmutation) {
            return new ReproduceEvent(Treproduction, random);
        } else {
            return new DeathEvent(Tdeath);
        }
    }
}
