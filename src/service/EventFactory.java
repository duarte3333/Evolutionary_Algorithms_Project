package src.service;
import src.model.Individual;
import src.model.Patrol;
import src.model.PlanetarySystem;
import java.util.*;

public class EventFactory {
    public static Event createEvent(EventType type, Individual individual, 
                        double currentTime, List<PlanetarySystem> systems, 
                        List<Patrol> patrols, double reproductionRate, 
                        double mutationRate, double deathRate, Population population) {
        switch (type) {
            case REPRODUCE:
                return new ReproductionEvent(individual, currentTime, systems, patrols, reproductionRate, mutationRate, deathRate, population);
            case MUTATE:
                return new MutationEvent(individual, currentTime, systems, patrols, reproductionRate, mutationRate, deathRate, population);
            case DEATH:
                return new DeathEvent(individual, currentTime, systems, patrols, reproductionRate, mutationRate, deathRate, population);
            default:
                throw new IllegalArgumentException("Unknown event type");
        }
    }
}

