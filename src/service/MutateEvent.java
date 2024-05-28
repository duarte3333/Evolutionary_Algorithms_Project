package src.service;

import src.main.Main;
import src.model.Individual;
import src.model.Patrol;
import src.model.PlanetarySystem;

import java.util.List;
import java.util.Random;

public class MutateEvent extends Event {
    private Random random;

    public MutateEvent(double time, Random random) {
        super(time);
        this.random = random;
    }

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
        }
        main.setNextEvent(currentTime, individual);
    }
}
