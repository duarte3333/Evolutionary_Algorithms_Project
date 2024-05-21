package src;
import java.util.*;

public class Event {
    private EventType type;
    enum EventType {
        DEATH,
        MUTATE,
        REPRODUCE,
        //0 - D ; 1 - M ; 2 - R
    }
    Event(int type) {
        switch (type) {
            case 0:
                this.type = EventType.DEATH;
                break;
            case 1:
                this.type = EventType.MUTATE;
                break;
            case 2:
                this.type = EventType.REPRODUCE;
                break;
        }
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }
}
