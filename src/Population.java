package src;
import java.util.*;

public class Population {
    private List<Allocation> individuals;
    private int maxIndividuals;
    private double comfortThreshold;

    public Population(int maxIndividuals, double comfortThreshold) {
        this.maxIndividuals = maxIndividuals;
        this.comfortThreshold = comfortThreshold;
        individuals = new ArrayList<>();
    }

    public void addIndividual(Allocation allocation) {
        if (individuals.size() < maxIndividuals) {
            individuals.add(allocation);
        } else {
            // Handle overpopulation and epidemics
            handleEpidemic();
        }
    }

    private void handleEpidemic() {
        // Sort individuals by comfort and keep the best ones
        // Comparator is a interface in JAVA
        individuals.sort(Comparator.comparingInt(Allocation::getMaxTime)); // Sort individuals in ascending order of maxTime
        List<Allocation> survivors = individuals.subList(0, 5);
        individuals = new ArrayList<>(survivors);
        // Allow other individuals to survive based on their comfort
        for (int i = 5; i < individuals.size(); i++) {
            //Math.random() returns a random number between 0.0 and 1.0
            // 1 - comfortThreshold is the probability of an individual not surviving
            // Math.log(1 - comfortThreshold) is the probability of an individual not surviving in logarithmic scale
            // 2/3 * (1 - Math.log(1 - comfortThreshold)) is the probability of an individual surviving in logarithmic scale
            // Why Logarithmic Scale: By applying Math.log(1 - comfortThreshold), the transformation allows for finer control over the probability curve, making survival rates more sensitive to changes in comfort at different levels of comfort.
            // Adjusting with 2.0 / 3: This scales the result to fit the desired range for probabilities. Without this factor, the survival rates might be too high or too low.
            if (Math.random() < 2.0 / 3 * (1 - Math.log(1 - comfortThreshold))) {
                survivors.add(individuals.get(i));
            }
        }
        individuals = survivors;
    }

    public List<Allocation> getIndividuals() {
        return individuals;
    }

    public Allocation getBestIndividual() {
        return individuals.stream().min(Comparator.comparingInt(Allocation::getMaxTime)).orElse(null);
    }
}
