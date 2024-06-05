package src.service;

import src.main.Main;
import src.model.Individual;
import src.model.Patrol;
import src.model.PlanetarySystem;

import java.util.List;
import java.util.Random;

/**
 * Class representing a mutate event.
 */
public class MutateEvent extends Event {
    private Random random;

    /**
     * Constructs a MutateEvent with the given time and Random instance.
     *
     * @param time The time at which the event occurs.
     * @param random The Random instance for generating random values.
     */
    public MutateEvent(double time, Random random) {
        super(time);
        this.random = random;
    }

    /**
     * Executes the event on the given individual.
     *
     * @param individual The individual on which to execute the event.
     * @param currentTime The current time.
     * @param main The Main instance.
     */
    @Override
    public void execute(Individual individual, double currentTime, Main main) {
        List<Patrol> patrols = main.getPatrols();
        int randomPatrolIndex = random.nextInt(patrols.size());
        Patrol randomPatrol = patrols.get(randomPatrolIndex);
        List<PlanetarySystem> systemsRandomPatrol = individual.getAllocation().get(randomPatrol);

        if (!systemsRandomPatrol.isEmpty()) {
            PlanetarySystem system = individual.getAllocation().get(randomPatrol).remove(random.nextInt(systemsRandomPatrol.size()));

            int newPatrolIndex = random.nextInt(patrols.size());
            while (newPatrolIndex == randomPatrolIndex) {
                newPatrolIndex = random.nextInt(patrols.size());
            }
            Patrol newPatrol = patrols.get(newPatrolIndex);

            individual.getAllocation().get(newPatrol).add(system);

            int new_policing_time = individual.calculatePolicingTime();
            individual.setPolicingTime(new_policing_time);

            double new_comfort = individual.calculateComfort();
            individual.setComfort(new_comfort);
        }

        main.setNextEvent(currentTime, individual);

    }
}
