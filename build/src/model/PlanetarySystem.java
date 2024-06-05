package src.model;

import java.util.Arrays;

/**
 * Class representing a planetary system.
 */
public class PlanetarySystem {
    private int id;
    private int[] patrolTimes; // Time required by each patrol to pacify this system

    /**
     * Constructs a PlanetarySystem with the given id and patrol times.
     *
     * @param id The id of the planetary system.
     * @param patrolTimes The time required by each patrol to pacify this system.
     */
    public PlanetarySystem(int id, int[] patrolTimes) {
        this.id = id;
        this.patrolTimes = patrolTimes;
    }

    /**
     * Copy constructor for deep copy.
     *
     * @param other The PlanetarySystem to copy.
     */
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
    
    /**
     * Decorates the toString method to return the id of the planetary system when called.
     */
    @Override
    public String toString() {
        return "System " + id;
    }

}
