package src.service;

import src.model.Individual;
import java.util.*;

public class Population{
    private List<Individual> individuals;
    private int maxPopulation;
    public static Population instance;

    public Population(int _maxPopulation) {
        this.individuals = new ArrayList<>();
        this.maxPopulation = _maxPopulation;
    }

    public static Population getInstance(int _maxPopulation) {
        if (instance == null) {
            instance = new Population(_maxPopulation);
        }
        return instance;
    }

    public void addIndividual(Individual individual) {
        individuals.add(individual);
    }

    public void handleEpidemic() {
        // Sort individuals by comfort and keep the best ones
        // Comparator is a interface in JAVA
        individuals.sort(Comparator.comparingDouble(Individual::getComfort)); // Sort individuals in ascending order of comfort
        List<Individual> survivors = individuals.subList(0, 5);
        // Allow other individuals to survive based on their comfort
        for (int i = 5; i < this.individuals.size(); i++) {
            //Math.random() returns a random number between 0.0 and 1.0
            if (Math.random() < ((2.0 / 3) * this.individuals.get(i).getComfort())) {
                survivors.add(this.individuals.get(i));
            }
        }
        this.individuals = survivors;
        System.out.println("Number of survivors: " + individuals.size());
    }

    public List<Individual> getIndividuals() {
        return individuals;
    }

    public Individual getBestIndividual() {
        // a stream is a sequence of elements supporting sequential and parallel aggregate operations
        // orElse() returns the value if present, otherwise returns the default value
        return individuals.stream()
                          .max(Comparator.comparingDouble(Individual::getComfort))
                          .orElse(null);
    }

    public int getMaxPopulation() {
        return maxPopulation;
    }
}