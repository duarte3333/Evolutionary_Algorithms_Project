import java.util.*;

public class Allocation {
    private Map<Patrol, List<PlanetarySystem>> allocation;
    private int maxTime;

    public Allocation(Map<Patrol, List<PlanetarySystem>> allocation) {
        this.allocation = allocation;
        calculateMaxTime();
    }

    private void calculateMaxTime() {
        maxTime = 0;
        for (Map.Entry<Patrol, List<PlanetarySystem>> entry : allocation.entrySet()) {
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
