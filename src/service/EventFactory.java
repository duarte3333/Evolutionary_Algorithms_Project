package src.service;
import src.model.Individual;

public class EventFactory {
    public static Event createEvent(EventType type, Individual individual, double currentTime) {
        switch (type) {
            case REPRODUCE:
                return new ReproductionEvent(individual, currentTime);
            case MUTATE:
                return new MutationEvent(individual, currentTime);
            case DEATH:
                return new DeathEvent(individual, currentTime);
            default:
                throw new IllegalArgumentException("Unknown event type");
        }
    }
}

