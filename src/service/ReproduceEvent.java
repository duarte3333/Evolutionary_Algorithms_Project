package src.service;

import src.main.Main;
import src.model.Individual;
import src.model.PlanetarySystem;
import src.model.Patrol;

import java.util.*;

public class ReproduceEvent extends Event {
    private Random random;

    public ReproduceEvent(double time, Random random) {
        super(time);
        this.random = random;
    }

    @Override
    public void execute(Individual individual, double currentTime, Main main) {
        Map<Patrol, List<PlanetarySystem>> newAllocation = new HashMap<>(individual.getAllocation());
        int numberOfSystemsToRemove = (int) Math.floor((1 - individual.getComfort()) * main.getSystems().size());
        
        List<PlanetarySystem> systems_to_remove = new ArrayList<>();
        List<PlanetarySystem> tmp_system = new ArrayList<>(main.getSystems());
        for (int i = 0; i < numberOfSystemsToRemove; i++) {
            int randomIndex = random.nextInt(tmp_system.size());
            systems_to_remove.add(tmp_system.get(randomIndex));
            tmp_system.remove(randomIndex);
        }

        for (Patrol patrol : newAllocation.keySet()) {
            newAllocation.get(patrol).removeAll(systems_to_remove);
        }

        for (PlanetarySystem system : systems_to_remove) {
            Patrol randomPatrol = main.getPatrols().get(random.nextInt(main.getPatrols().size()));
            newAllocation.get(randomPatrol).add(system);
        }

        Individual newIndividual = new Individual(newAllocation);
        main.getPopulation().addIndividual(newIndividual);
        main.setNextEvent(currentTime, newIndividual);
        main.setNextEvent(currentTime, individual);
    }
}
