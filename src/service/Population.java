package src.service;

import src.model.Individual;
import src.model.Patrol;
import src.model.PlanetarySystem;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing a population of individuals.
 */
public class Population{
    private List<Individual> individuals;
    private int maxPopulation;
    public static Population instance;

    /**
     * Constructs a Population with the given maximum population size.
     *
     * @param _maxPopulation The maximum population size.
     */
    public Population(int _maxPopulation) {
        this.individuals = new ArrayList<>();
        this.maxPopulation = _maxPopulation;
    }

    /**
     * Returns the instance of the Population class.
     *
     * @param _maxPopulation The maximum population size.
     * @return The instance of the Population class.
     */
    public static Population getInstance(int _maxPopulation) {
        if (instance == null) {
            instance = new Population(_maxPopulation);
        }
        return instance;
    }

    /**
     * Adds an individual to the population.
     *
     * @param individual The individual to add.
     */
    public void addIndividual(Individual individual) {
        individuals.add(individual);
    }

    /**
     * Handles an epidemic by keeping the best individuals and allowing others to survive based on their comfort.
     */
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
    }
    /**
     * Gets the list of individuals in the population.
     *
     * @return The list of individuals.
     */
    public List<Individual> getIndividuals() {
        return individuals;
    }

    /**
     * Finds and returns the best individual in the population based on comfort level.
     * 
     * @return The best individual.
     */
    public Individual getBestIndividual() {
        return individuals.stream()
                          .max(Comparator.comparingDouble(Individual::getComfort))
                          .orElse(null);
    }

    /**
     * Gets a list of candidate distributions from the population.
     * 
     * @param count The number of candidate distributions to retrieve.
     * @return A list of candidate distributions.
     */
    public List<Individual> getCandidateDistributions(int count) {
        Set<Map<Patrol, List<PlanetarySystem>>> seenAllocations = new HashSet<>();

        return individuals.stream()
                            .sorted(Comparator.comparingDouble(Individual::getComfort).reversed())
                            .filter(individual -> seenAllocations.add(individual.getAllocation()))
                            .limit(count)
                            .collect(Collectors.toList());
    }
    
    /**
     * Gets the maximum population size allowed.
     * 
     * @return The maximum population size.
     */
    public int getMaxPopulation() {
        return maxPopulation;
    }
}