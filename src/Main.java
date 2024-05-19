package src;

import java.util.*;

public class Main {
    private Population population;
    private List<Patrol> patrols;
    private List<PlanetarySystem> systems;
    private int maxGenerations;
    private int maxTime;
    private double comfortThreshold;

    public Main(List<Patrol> patrols, List<PlanetarySystem> systems, int maxGenerations, int maxTime, double comfortThreshold) {
        this.patrols = patrols;
        this.systems = systems;
        this.maxGenerations = maxGenerations;
        this.maxTime = maxTime;
        this.comfortThreshold = comfortThreshold;
        population = new Population(100, comfortThreshold);
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
            Allocation mutated = mutateIndividual(individual);
            newIndividuals.add(mutated);
            Allocation reproduced = reproduceIndividual(individual);
            newIndividuals.add(reproduced);
        }
        for (Allocation newIndividual : newIndividuals) {
            population.addIndividual(newIndividual);
        }
    }

    private Allocation mutateIndividual(Allocation individual) {
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
    //java -jar project.jar -r      n m   τ  ν νmax μ ρ δ
    //java -jar MyJarProject.jar -r 3 6 1000 4 0.1 1 1 1
    public static void main(String[] args) {
        Parser parser = new Parser(args);

        System.out.println("n: " + parser.getN());
        // List of patrols
        int[][] sintetic_C = new int[][]{
            {1, 2, 3},
            {2, 2, 3},
            {1, 2, 3},
            {1, 2, 3},
            {2, 2, 3},
            {1, 2, 3}
        };

        List<Patrol> patrols = new ArrayList<>(parser.getN());
        for (int i = 0; i < parser.getN(); i++) {
            patrols.add(new Patrol(i));
        }

        List<PlanetarySystem> systems = new ArrayList<>(parser.getM());
        for (int i = 0; i < parser.getM(); i++) {
            systems.add(new PlanetarySystem(i, sintetic_C[i % sintetic_C.length]));
        }

        Main algorithm = new Main(patrols, systems, 1000, 4, 0.1);
        algorithm.run();
    }
}
