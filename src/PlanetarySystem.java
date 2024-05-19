package src;
public class PlanetarySystem {
    private int id;
    private int[] patrolTimes; // Time required by each patrol to pacify this system

    public PlanetarySystem(int id, int[] patrolTimes) {
        this.id = id;
        this.patrolTimes = patrolTimes;
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
