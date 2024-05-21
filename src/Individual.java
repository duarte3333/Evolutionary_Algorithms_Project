package src;
import java.util.*;

public class Individual {
    //an individual is a possible solution
    //an alocation is a list of patrols and the systems they are assigned to

    //Example:
    //patrol1 -> [system1, system2, system3]
    //patrol2 -> [system4, system5, system6]
    //patrol3 -> [system7, system8, system9]
    private Map<Patrol, List<PlanetarySystem>> allocation;
    private double comfort;
    private double time;
    private Event event;
    private boolean action;

    public Individual(Map<Patrol, List<PlanetarySystem>> allocation) {
        this.allocation = allocation;
        this.comfort = calculateComfort();
        this.action = false;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    private double calculateComfort() {
        //Calculate t_min which is the time that the patrol with the least time takes to complete its route
        double tmin = 0;
        for (PlanetarySystem system : allocation.values().iterator().next()) {
            int minTime = Integer.MAX_VALUE;
            for (Patrol patrol : allocation.keySet()) {
                minTime = Math.min(minTime, system.getTimeForPatrol(patrol.getId()));
            }
            tmin += minTime;
        }
        tmin /= allocation.keySet().size();

        // Calculate tz 
        double tz = 0;
        for (Patrol patrol : allocation.keySet()) {
            for (PlanetarySystem system : allocation.get(patrol)) {
                tz += system.getTimeForPatrol(patrol.getId());
            }
        }
        return tmin / tz;
    }

    public Map<Patrol, List<PlanetarySystem>> getAllocation() {
        return allocation;
    }
    
    public void setAllocation(Map<Patrol, List<PlanetarySystem>> allocation) {
        this.allocation = allocation;
    }

    public double getComfort() {
        return comfort;
    }
}
