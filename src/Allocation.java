package src;
import java.util.*;

public class Allocation {
    //alocation is a list of patrols and the systems they are assigned to

    //Example:
    //patrol1 -> [system1, system2, system3]
    //patrol2 -> [system4, system5, system6]
    //patrol3 -> [system7, system8, system9]
    private Map<Patrol, List<PlanetarySystem>> allocation;
    private int maxTime;

    public Allocation(Map<Patrol, List<PlanetarySystem>> allocation) {
        this.allocation = allocation;
        calculateMaxTime();
    }

    private void calculateMaxTime() {
        maxTime = 0;
        for (Map.Entry<Patrol, List<PlanetarySystem>> entry : allocation.entrySet()) { //for each patrol
            int totalTime = 0;
            for (PlanetarySystem system : entry.getValue()) {
                totalTime += system.getTimeForPatrol(entry.getKey().getId());
            }
            maxTime = Math.max(maxTime, totalTime);
        }
    }

    public int getMaxTime() {
        return maxTime;
    }

    public Map<Patrol, List<PlanetarySystem>> getAllocation() {
        return allocation;
    }
}
