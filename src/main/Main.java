package src.main;

import src.model.*;
import src.service.*;
import src.util.Parser;
import java.util.*;

/**
 * The main class for running the simulation.
 */
public class Main {
    public  static Main instance;
    private Population population;
    private List<Patrol> patrols;
    private List<PlanetarySystem> systems;
    private int tau;
    private int initialPopulation;
    private double deathRate;
    private double mutationRate;
    private double reproductionRate;
    private Random random = new Random();

    public Main(List<Patrol> patrols, List<PlanetarySystem> systems, 
                int tau, int initialPopulation, int maxPopulation,
                double deathRate, double mutationRate, double reproductionRate) {
        this.patrols = patrols;
        this.systems = systems;
        this.tau = tau;
        this.initialPopulation = initialPopulation;
        this.deathRate = deathRate;
        this.mutationRate = mutationRate;
        this.reproductionRate = reproductionRate;
        population = Population.getInstance(maxPopulation);
    }


    /**
     * Returns the singleton instance of the Main class.
     *
     * @param patrols List of patrols.
     * @param systems List of planetary systems.
     * @param tau The final instant of evolution.
     * @param initialPopulation Initial population size.
     * @param maxPopulation Maximum population size.
     * @param deathRate Death rate.
     * @param mutationRate Mutation rate.
     * @param reproductionRate Reproduction rate.
     * @return The singleton instance of the Main class.
     */
    public static Main getInstance(List<Patrol> patrols, List<PlanetarySystem> systems, 
                                   int tau, int initialPopulation, int maxPopulation,
                                   double deathRate, double mutationRate, double reproductionRate) {
        if (instance == null) {
            instance = new Main(patrols, systems, tau, initialPopulation, maxPopulation, deathRate, mutationRate, reproductionRate);
        }
        return instance;
    }

    /**
     * Generates the initial population for the simulation.
     */
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


    /**
     * Sets the next event for the given individual.
     *
     * @param currentTime The current time in the simulation.
     * @param individual The individual for which to set the next event.
     */
    public void setNextEvent(double currentTime, Individual individual) {
        EventFactory eventFactory = new EventFactory(reproductionRate, mutationRate, deathRate, random);
        Event event = eventFactory.createEvent(individual);
        individual.setEvent(event);
        individual.setTime(event.getTime() + currentTime);
    }

    /**
     * Performs the next event for the given individual.
     *
     * @param individual The individual for which to perform the next event.
     * @param currentTime The current time in the simulation.
     * @return The time at which the event occurred.
     */
    public double performEvent(Individual individual, double currentTime) {
        Event event = individual.getEvent();
        event.execute(individual, currentTime, this);
        return individual.getTime();
    }

    /**
     * Runs the simulation.
     */
    public void run() {
        generateInitialPopulation();
        int events = 0;
        int epidemics = 0;
        double currentTime = 0.0;
        int observationInterval = Math.max(1, tau / 20);

        //give me an alternative
        for (Individual individual : population.getIndividuals()) {
            setNextEvent(currentTime, individual);
        }

        while (currentTime < tau && !population.getIndividuals().isEmpty()) {
            List<Individual> individualsByTime = new ArrayList<>(population.getIndividuals());
            individualsByTime.sort(Comparator.comparingDouble(Individual::getTime));

            if (population.getIndividuals().size() >= population.getMaxPopulation()) {
                population.handleEpidemic();
                epidemics++;
            }

            if (currentTime > tau) break;
            currentTime = performEvent(individualsByTime.get(0), currentTime);
            events++;

            if ((events % observationInterval) == 0 || currentTime >= tau) {
                outputObservation(currentTime, events, epidemics);
            }
        }

        outputFinalObservation(currentTime, events, epidemics);
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

        int numberOfCandidates = Math.min(5, population.getIndividuals().size() - 1);
        for (int i = 1; i <= numberOfCandidates; i++) {
            Individual individual = population.getIndividuals().get(i);
            System.out.println("otherdist" + i + ": " + formatAllocation(individual.getAllocation()) + " : " + (1 / individual.getComfort()) + " : " + individual.getComfort());
        }
        double my_time = 0;
        double maxTime = -1;
        for (Patrol patrol : bestIndividual.getAllocation().keySet()) {
            //int max integer
            my_time = 0;
            for (PlanetarySystem system : bestIndividual.getAllocation().get(patrol))
                my_time += system.getTimeForPatrol(patrol.getId());
            if (my_time > maxTime) {
                maxTime = my_time;
            }
        }
        System.out.println("-Max time: " + maxTime);
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
        double my_time = 0;
        double maxTime = -1;
        for (Patrol patrol : bestIndividual.getAllocation().keySet()) {
            //int max integer
            my_time = 0;
            for (PlanetarySystem system : bestIndividual.getAllocation().get(patrol))
                my_time += system.getTimeForPatrol(patrol.getId());
            if (my_time > maxTime) {
                maxTime = my_time;
            }
        }
        System.out.println("-Max time: " + maxTime);
        System.out.println();
    }
    

    private String formatAllocation(Map<Patrol, List<PlanetarySystem>> allocation) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // Extract entries and sort by patrol ID
        List<Map.Entry<Patrol, List<PlanetarySystem>>> entries = new ArrayList<>(allocation.entrySet());
        entries.sort(Comparator.comparingInt(e -> e.getKey().getId()));

        boolean firstPatrol = true;
        for (Map.Entry<Patrol, List<PlanetarySystem>> entry : entries) {
            if (!firstPatrol) {
                sb.append(", ");
            }
            firstPatrol = false;
            sb.append("{");
            sb.append(entry.getKey().getId()).append(": ");
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(entry.getValue().get(i).getId());
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }
    

    public Population getPopulation() {
        return population;
    }

    public List<Patrol> getPatrols() {
        return patrols;
    }

    public List<PlanetarySystem> getSystems() {
        return systems;
    }

    //java -jar project.jar      -r     n m  τ    ν νmax μ ρ δ
    //java -jar MyJarProject.jar -r     3 6  1000 4 0.1  1 1 1
    public static void main(String[] args) {
        Parser parser = Parser.getInstance(args);

        int n = parser.getN(); // Number of patrols
        int m = parser.getM(); // Number of systems
        int tau = parser.getTau(); // Final instant of evolution (> 0);
        int nu = parser.getNu(); // Initial population
        int nuMax = parser.getNuMax(); // Maximum population
        double mu = parser.getMu(); // Mutation rate
        double rho = parser.getRho(); // Reproduction rate
        double delta = parser.getDelta(); // Comfort threshold
        int[][] C = parser.getC(); // Time required by each patrol to pacify each system
        
        List<Patrol> patrols = new ArrayList<>(n);
        for (int i = 0; i < parser.getN(); i++) {
            patrols.add(new Patrol(i));
        }

        List<PlanetarySystem> systems = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            systems.add(new PlanetarySystem(i, C[i % C.length]));
        }

        Main algorithm = Main.getInstance(patrols, systems, tau, nu, nuMax, mu, rho, delta);
        algorithm.run();
    }
}