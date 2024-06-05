package src.model;

import src.service.*;
import java.util.*;

/**
 * Class representing an individual in the population.
 */
public class Individual {
    private Map<Patrol, List<PlanetarySystem>> allocation;
    private Event event;
    private double time;
    private double comfort;
    private double tmin;
    private int policing_time;

    /**
     * Constructs an Individual with the given allocation and tmin.
     *
     * @param allocation The allocation of planetary systems to patrols.
     * @param tmin The minimum patrol time.
     */
    public Individual(Map<Patrol, List<PlanetarySystem>> allocation, double tmin) {
        this.allocation = allocation;
        this.tmin = tmin;
        this.policing_time = calculatePolicingTime();
        this.comfort = calculateComfort();
    }

    /**
     * Gets the allocation of planetary systems to patrols.
     *
     * @return The allocation map.
     */
    public Map<Patrol, List<PlanetarySystem>> getAllocation() {
        return allocation;
    }

    /**
     * Gets the event associated with the individual.
     *
     * @return The event.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the event for the individual.
     *
     * @param event The event to set.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Gets the time of the next event for the individual.
     *
     * @return The time of the next event.
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the time of the next event for the individual.
     *
     * @param time The time to set.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the policing time of the individual.
     *
     * @return The policing time of the individual.
     */
    public double getPolicingTime() {
        return policing_time;
    }

    /**
     * Set policing time of the individual.
     *
     * @param policing_time The time to policy the system.
     */
    public void setPolicingTime(int policing_time) {
        this.policing_time =  policing_time;
    }


    /**
     * Gets the comfort of the individual.
     *
     * @return The comfort of the individual.
     */
    public double getComfort() {
        return comfort;
    }

    /**
     * Set confort of the individual.
     *
     */
    public void setComfort(double comfort) {
        this.comfort =  comfort;
    }

    /**
     * Calculates the time it takes the individual to patrol the empire.
     *
     * @return The time to patrol the empire of the individual.
     */
    public int calculatePolicingTime() {
        int tz = 0;
        for (Patrol patrol : allocation.keySet()) {
            int patrolTime = 0;
            for (PlanetarySystem system : allocation.get(patrol)) {
                patrolTime += system.getTimeForPatrol(patrol.getId());
            }
            tz = Math.max(tz, patrolTime);
        }
        return tz;
    }

    /**
     * Creates a deep copy of this individual.
     *
     * @return A deep copy of this individual.
     */
    public Individual deepCopy() {
        Map<Patrol, List<PlanetarySystem>> allocationCopy = new HashMap<>();
        for (Map.Entry<Patrol, List<PlanetarySystem>> patrol : allocation.entrySet()) {
            Patrol patrolCopy = patrol.getKey();
            List<PlanetarySystem> systemsCopy = new ArrayList<>(patrol.getValue());
            allocationCopy.put(patrolCopy, systemsCopy);
        }
        Individual copy = new Individual(allocationCopy, tmin);

        return copy;
    }


    /**
     * Calculates the comfort of the individual.
     *
     * @return The comfort of the individual.
     */
    public double calculateComfort() {
        int tz = policing_time;
        return tmin / tz;
    }
}
