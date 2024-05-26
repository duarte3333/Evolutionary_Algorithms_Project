package src.model;

public class Patrol {
    private int id;

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
