package src.service;

import src.main.Main;
import src.model.Individual;

public class DeathEvent extends Event {

    public DeathEvent(double time) {
        super(time);
    }

    @Override
    public void execute(Individual individual, double currentTime, Main main) {
        main.getPopulation().getIndividuals().remove(individual);
    }
}
