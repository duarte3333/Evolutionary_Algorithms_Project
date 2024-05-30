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

    /**
     * Constructs an Individual with the given allocation and tmin.
     *
     * @param allocation The allocation of planetary systems to patrols.
     * @param tmin The minimum patrol time.
     */
    public Individual(Map<Patrol, List<PlanetarySystem>> allocation, double tmin) {
        this.allocation = allocation;
        this.tmin = tmin;
        System.out.println("Tmin: " + tmin);
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
     * Gets the comfort of the individual.
     *
     * @return The comfort of the individual.
     */
    public double getComfort() {
        return comfort;
    }

    /**
     * Calculates the comfort of the individual.
     *
     * @return The comfort of the individual.
     */
    private double calculateComfort() {
        double tz = 0;
        for (Patrol patrol : allocation.keySet()) {
            double patrolTime = 0;
            for (PlanetarySystem system : allocation.get(patrol)) {
                patrolTime += system.getTimeForPatrol(patrol.getId());
            }
            tz = Math.max(tz, patrolTime);
        }
        return tmin / tz;
    }
}
