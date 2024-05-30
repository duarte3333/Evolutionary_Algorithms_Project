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

    public List<Individual> getCandidateDistributions(int count) {
    // Use a set to keep track of allocations we've already seen
    Set<Map<Patrol, List<PlanetarySystem>>> seenAllocations = new HashSet<>();

    // Stream, sort, filter, and collect the top 'count' individuals with unique allocations
    return individuals.stream()
                        .sorted(Comparator.comparingDouble(Individual::getComfort).reversed())
                        .filter(individual -> seenAllocations.add(individual.getAllocation()))
                        .limit(count)
                        .collect(Collectors.toList());
    }

    public int getMaxPopulation() {
        return maxPopulation;
    }
}