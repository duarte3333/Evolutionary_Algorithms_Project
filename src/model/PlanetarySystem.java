package src.model;

import java.util.Arrays;

public class PlanetarySystem {
    private int id;
    private int[] patrolTimes; // Time required by each patrol to pacify this system

    public PlanetarySystem(int id, int[] patrolTimes) {
        this.id = id;
        this.patrolTimes = patrolTimes;
    }

        // Copy constructor for deep copy
    public PlanetarySystem(PlanetarySystem other) {
        this.id = other.id;
        this.patrolTimes = Arrays.copyOf(other.patrolTimes, other.patrolTimes.length);
    }

    public int getId() {
        return id;
    }

    public int getTimeForPatrol(int patrolId) {
        return patrolTimes[patrolId];
    }
    
    @Override
    public String toString() {
        return "System " + id;
    }

}
