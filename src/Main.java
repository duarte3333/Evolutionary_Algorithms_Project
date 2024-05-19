package src;

import java.util.*;

public class Main {
    private Population population;
    private List<Patrol> patrols;
    private List<PlanetarySystem> systems;
    private int tau;
    private double comfortThreshold;
    private double mutationRate;
    private int initialPopulation;
    private double reproductionRate;
    private Random random = new Random();

    public Main(List<Patrol> patrols, List<PlanetarySystem> systems, 
                int tau, double comfortThreshold, 
                int initialPopulation, int maxPopulation,
                double mutationRate, double reproductionRate) {
        this.patrols = patrols;
        this.systems = systems;
        this.tau = tau;
        this.comfortThreshold = comfortThreshold;
        this.initialPopulation = initialPopulation;
        this.mutationRate = mutationRate;
        this.reproductionRate = reproductionRate;
        population = new Population(maxPopulation, comfortThreshold);
    }

    public void run() {
        generateInitialPopulation();
        int events = 0;
        int epidemics = 0;
        double time = 0;

        int observationInterval = Math.max(1, tau / 20);
        while (time < tau && !population.getIndividuals().isEmpty()) {
            double nextEventTime = getNextEventTime();
            if (time + nextEventTime > tau) break;

            time += nextEventTime;
            events++;

            performRandomEvent();

            if (population.getIndividuals().size() > population.getMaxPopulation()) {
                population.handleEpidemic();
                epidemics++;
            }
            if ((events % observationInterval) == 0 || time >= tau) {
                outputObservation(time, events, epidemics);
            }
        }

        outputFinalObservation(time, events, epidemics);
    }

    private void generateInitialPopulation() {
        for (int i = 0; i < this.initialPopulation; i++) {
            Map<Patrol, List<PlanetarySystem>> allocation = new HashMap<>();
            // Generate a list of systems for each patrol
            for (Patrol patrol : this.patrols) { 
                allocation.put(patrol, new ArrayList<>());
            }
            for (PlanetarySystem system : this.systems) {
                Patrol randomPatrol = patrols.get(random.nextInt(patrols.size()));
                allocation.get(randomPatrol).add(system);
            }
            population.addIndividual(new Individual(allocation));
        }
    }

    private double getNextEventTime() {
        Individual bestIndividual = population.getBestIndividual();
        double comfort = bestIndividual.getComfort();
        return -Math.log(random.nextDouble()) / (1 - Math.log(comfort));
    }

    private void performRandomEvent() {
        double eventType = random.nextDouble(); // Random number between 0 and 1
        if (eventType < mutationRate) { // mutationRate = 0.1
            mutate(selectIndividual());
        } else if (eventType < mutationRate + reproductionRate) { // reproductionRate = 0.7 + 0.1
            reproduce(selectIndividual());
        } else {
            death(selectIndividual());
        }
    }

    private void mutate(Individual individual) {
        Map<Patrol, List<PlanetarySystem>> allocation = new HashMap<>(individual.getAllocation());
        // Select a random patrol
        Patrol randomPatrol = patrols.get(random.nextInt(patrols.size()));
        List<PlanetarySystem> systems = allocation.get(randomPatrol);
        
        if (!systems.isEmpty()) {
            // Select a random system from the chosen patrol
            PlanetarySystem system = systems.remove(random.nextInt(systems.size()));
            
            // Select a new patrol to move the system to
            Patrol newPatrol = patrols.get(random.nextInt(patrols.size()));
            allocation.get(newPatrol).add(system);
        }
        individual.setAllocation(allocation);
    }

    private void reproduce(Individual individual) {
        Map<Patrol, List<PlanetarySystem>> allocation = new HashMap<>(individual.getAllocation());
        int numberOfSystemsToRemove = (int) Math.floor((1 - (1 - Math.log(1 - comfortThreshold))) * systems.size());
    
        for (int i = 0; i < numberOfSystemsToRemove; i++) {
            // Select a random patrol
            Patrol randomPatrol = patrols.get(random.nextInt(patrols.size()));
            List<PlanetarySystem> systems = allocation.get(randomPatrol);
    
            if (!systems.isEmpty()) {
                // Select a random system from the chosen patrol
                PlanetarySystem system = systems.remove(random.nextInt(systems.size()));
                
                // Select a new patrol to move the system to
                Patrol newPatrol = patrols.get(random.nextInt(patrols.size()));
                allocation.get(newPatrol).add(system);
            }
        }
        population.addIndividual(new Individual(allocation));
    }

    private void death(Individual individual) {
        population.getIndividuals().remove(individual);
    }

    private Individual selectIndividual() {
        return population.getIndividuals().get(random.nextInt(population.getIndividuals().size()));
    }

    private void outputObservation(double time, int events, int epidemics) {
        Individual bestIndividual = population.getBestIndividual();
        System.out.println("Present instant: " + time);
        System.out.println("Number of realized events: " + events);
        System.out.println("Population size: " + population.getIndividuals().size());
        System.out.println("Number of epidemics: " + epidemics);
        System.out.println("Best distribution of the patrols: " + formatAllocation(bestIndividual.getAllocation()));
        System.out.println("Empire policing time: " + (1 / bestIndividual.getComfort()));
        System.out.println("Comfort: " + bestIndividual.getComfort());

        // Output other candidate distributions if available
        int numberOfCandidates = Math.min(5, population.getIndividuals().size());
        for (int i = 1; i < numberOfCandidates; i++) {
            Individual individual = population.getIndividuals().get(i);
            System.out.println("Other candidate distribution " + i + ": " + formatAllocation(individual.getAllocation()));
            System.out.println("Empire policing time: " + (1 / individual.getComfort()));
            System.out.println("Comfort: " + individual.getComfort());
        }
        System.out.println();
    }
    
    private void outputFinalObservation(double time, int events, int epidemics) {
        Individual bestIndividual = population.getBestIndividual();
        System.out.println("Final instant: " + time);
        System.out.println("Total events: " + events);
        System.out.println("Final population size: " + population.getIndividuals().size());
        System.out.println("Total number of epidemics: " + epidemics);
        System.out.println("Best distribution of the patrols: " + formatAllocation(bestIndividual.getAllocation()));
        System.out.println("Empire policing time: " + (1 / bestIndividual.getComfort()));
        System.out.println("Final comfort: " + bestIndividual.getComfort());
        System.out.println();
    }
    
    private String formatAllocation(Map<Patrol, List<PlanetarySystem>> allocation) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean firstPatrol = true;
        for (Map.Entry<Patrol, List<PlanetarySystem>> entry : allocation.entrySet()) {
            if (!firstPatrol) {
                sb.append(", ");
            }
            firstPatrol = false;
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
        }
        sb.append("}");
        return sb.toString();
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

        Main algorithm = new Main(patrols, systems, tau, delta, nu, nuMax, mu, rho);
        algorithm.run();
    }
}
