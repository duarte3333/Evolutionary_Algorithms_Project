package src;
import java.util.*;

class Population {
    private List<Individual> individuals;
    private int maxPopulation;

    public Population(int _maxPopulation, double comfortThreshold) {
        this.individuals = new ArrayList<>();
        this.maxPopulation = _maxPopulation;
    }

    public void addIndividual(Individual individual) {
        if (individuals.size() < maxPopulation) {
            individuals.add(individual);
        } else {
            // Handle overpopulation and epidemics
            handleEpidemic();
        }
    }

    public void handleEpidemic() {
        // Sort individuals by comfort and keep the best ones
        // Comparator is a interface in JAVA
        individuals.sort(Comparator.comparingDouble(Individual::getComfort)); // Sort individuals in ascending order of comfort
        List<Individual> survivors = individuals.subList(0, 5);
        individuals = new ArrayList<>(survivors);
        // Allow other individuals to survive based on their comfort
        for (int i = 5; i < individuals.size(); i++) {
            //Math.random() returns a random number between 0.0 and 1.0
            if (Math.random() < ((2.0 / 3) * individuals.get(i).getComfort())) {
                survivors.add(individuals.get(i));
            }
        }
        individuals = survivors;
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