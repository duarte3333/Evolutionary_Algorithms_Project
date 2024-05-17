import java.util.*;

public class EvolutionaryAlgorithm {
    private Population population;
    private List<Patrol> patrols;
    private List<PlanetarySystem> systems;
    private int maxGenerations;
    private int maxTime;
    private double comfortThreshold; // Define comfortThreshold

    public EvolutionaryAlgorithm(List<Patrol> patrols, List<PlanetarySystem> systems, int maxGenerations, int maxTime, double comfortThreshold) {
        this.patrols = patrols;
        this.systems = systems;
        this.maxGenerations = maxGenerations;
        this.maxTime = maxTime;
        this.comfortThreshold = comfortThreshold; // Initialize comfortThreshold
        population = new Population(100, comfortThreshold); // Pass comfortThreshold to Population
    }

    public void run() {
        generateInitialPopulation();
        for (int i = 0; i < maxGenerations; i++) {
            evolvePopulation();
            if (population.getBestIndividual().getMaxTime() <= maxTime) {
                break;
            }
        }
        displayBestSolution();
    }

    private void generateInitialPopulation() {
        for (int i = 0; i < 100; i++) {
            Map<Patrol, List<PlanetarySystem>> allocation = new HashMap<>();
            for (Patrol patrol : patrols) {
                allocation.put(patrol, new ArrayList<>());
            }
            for (PlanetarySystem system : systems) {
                Patrol randomPatrol = patrols.get(new Random().nextInt(patrols.size()));
                allocation.get(randomPatrol).add(system);
            }
            population.addIndividual(new Allocation(allocation));
        }
    }

    private void evolvePopulation() {
        List<Allocation> newIndividuals = new ArrayList<>();
        for (Allocation individual : population.getIndividuals()) {
            // Apply mutations
            Allocation mutated = mutateIndividual(individual);
            newIndividuals.add(mutated);
            // Apply reproduction
            Allocation reproduced = reproduceIndividual(individual);
            newIndividuals.add(reproduced);
        }
        for (Allocation newIndividual : newIndividuals) {
            population.addIndividual(newIndividual);
        }
    }

    private Allocation mutateIndividual(Allocation individual) {
        // Mutate the allocation
        Map<Patrol, List<PlanetarySystem>> allocation = new HashMap<>(individual.getAllocation());
        Patrol randomPatrol = patrols.get(new Random().nextInt(patrols.size()));
        List<PlanetarySystem> systems = allocation.get(randomPatrol);
        if (!systems.isEmpty()) {
            PlanetarySystem system = systems.remove(new Random().nextInt(systems.size()));
            Patrol newPatrol = patrols.get(new Random().nextInt(patrols.size()));
            allocation.get(newPatrol).add(system);
        }
        return new Allocation(allocation);
    }

    private Allocation reproduceIndividual(Allocation individual) {
        // Reproduce the allocation
        Map<Patrol, List<PlanetarySystem>> allocation = new HashMap<>(individual.getAllocation());
        int numberOfSystemsToRemove = (int) Math.floor((1 - (1 - Math.log(1 - comfortThreshold))) * systems.size());
        for (int i = 0; i < numberOfSystemsToRemove; i++) {
            Patrol randomPatrol = patrols.get(new Random().nextInt(patrols.size()));
            List<PlanetarySystem> systems = allocation.get(randomPatrol);
            if (!systems.isEmpty()) {
                PlanetarySystem system = systems.remove(new Random().nextInt(systems.size()));
                Patrol newPatrol = patrols.get(new Random().nextInt(patrols.size()));
                allocation.get(newPatrol).add(system);
            }
        }
        return new Allocation(allocation);
    }

    private void displayBestSolution() {
        Allocation best = population.getBestIndividual();
        System.out.println("Best allocation with max time: " + best.getMaxTime());
        for (Map.Entry<Patrol, List<PlanetarySystem>> entry : best.getAllocation().entrySet()) {
            System.out.println("Patrol " + entry.getKey().getId() + ": " + entry.getValue().stream().map(PlanetarySystem::getId).toList());
        }
    }

    public static void main(String[] args) {
        List<Patrol> patrols = List.of(new Patrol(0), new Patrol(1), new Patrol(2));
        List<PlanetarySystem> systems = List.of(
            new PlanetarySystem(0, new int[]{1, 2, 3}),
            new PlanetarySystem(1, new int[]{2, 2, 3}),
            new PlanetarySystem(2, new int[]{1, 2, 3}),
            new PlanetarySystem(3, new int[]{1, 2, 3}),
            new PlanetarySystem(4, new int[]{2, 2, 3}),
            new PlanetarySystem(5, new int[]{1, 2, 3})
        );
        EvolutionaryAlgorithm algorithm = new EvolutionaryAlgorithm(patrols, systems, 1000, 4, 0.1);
        algorithm.run();
    }
}
