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
        individuals.sort(Comparator.comparingInt(Allocation::getMaxTime));
        List<Allocation> survivors = individuals.subList(0, 5);
        individuals = new ArrayList<>(survivors);
        // Allow other individuals to survive based on their comfort
        for (int i = 5; i < individuals.size(); i++) {
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
