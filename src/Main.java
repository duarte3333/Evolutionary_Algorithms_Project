package src;

import java.util.*;

public class Main {
    private Population population;
    private List<Patrol> patrols;
    private List<PlanetarySystem> systems;
    private int tau;
    private double comfortThreshold;
    private int inicial_population;


    public Main(List<Patrol> patrols, List<PlanetarySystem> systems, 
        int tau, double comfortThreshold, 
        int inicial_population, int max_population) {
        this.patrols = patrols;
        this.systems = systems;
        this.tau = tau;
        this.comfortThreshold = comfortThreshold;
        population = new Population(max_population, comfortThreshold);
    }

    public void run() {
        generateInitialPopulation();

    }

    private void generateInitialPopulation() {
        for (int i = 0; i < this.inicial_population; i++) {
            Map<Patrol, List<PlanetarySystem>> allocation = new HashMap<>();
            for (Patrol patrol : patrols) {
                allocation.put(patrol, new ArrayList<>());
            }
            for (PlanetarySystem system : systems) {
                Patrol randomPatrol = patrols.get(new Random().nextInt(patrols.size()));
                allocation.get(randomPatrol).add(system);
            }
            population.addIndividual(new Individual(allocation));
        }
    }


    

    //java -jar project.jar      -r     n m  τ    ν νmax μ ρ δ
    //java -jar MyJarProject.jar -r     3 6  1000 4 0.1  1 1 1
    public static void main(String[] args) {
        Parser parser = new Parser(args);

        int n = parser.getN(); // Number of patrols
        int m = parser.getM(); // Number of systems
        int tau = parser.getTau(); // Final instant of evolution (> 0);
        int nu = parser.getNu(); // Initial population
        int nuMax = parser.getNuMax(); // Maximum population
        double mu = parser.getMu(); // Mutation rate
        double rho = parser.getRho(); // Reproduction rate
        double delta = parser.getDelta(); // Comfort threshold

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

        List<Patrol> patrols = new ArrayList<>(n);
        for (int i = 0; i < parser.getN(); i++) {
            patrols.add(new Patrol(i));
        }

        List<PlanetarySystem> systems = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            systems.add(new PlanetarySystem(i, sintetic_C[i % sintetic_C.length]));
        }

        Main algorithm = new Main(patrols, systems, tau, delta, nu, nuMax) ;
        algorithm.run();
    }
}
