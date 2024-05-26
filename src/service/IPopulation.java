package src.service;

import src.model.Individual;
import java.util.List;

public interface IPopulation {
    void addIndividual(Individual individual);
    void handleEpidemic();
    List<Individual> getIndividuals();
    Individual getBestIndividual();
    int getMaxPopulation();
}
