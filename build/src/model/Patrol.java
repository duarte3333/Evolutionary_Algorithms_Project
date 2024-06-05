package src.model;

/**
 * Class representing a patrol.
 */
public class Patrol {
    private int id;

    /**
     * Constructs a Patrol with the given id.
     *
     * @param id The id of the patrol.
     */
    public Patrol(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Patrol " + id;
    }
}
