package src.main;

import src.model.*;
import src.service.*;
import src.util.Parser;
import java.util.*;
import java.util.stream.Collectors;

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
    private double t_min;
    private Random random = new Random();
    private Individual bestIndividual;
    private List<Individual> candidateDistributions;

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
        this.t_min = calculateTmin();
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
     * Calculates the minimum patrol time (tmin) across all planetary systems.
     *
     * @return The minimum patrol time.
     */
    private double calculateTmin() {
        double tmin = 0;
        for (PlanetarySystem system : systems) {
            int minTime = Integer.MAX_VALUE;
            for (Patrol patrol : patrols) {
                minTime = Math.min(minTime, system.getTimeForPatrol(patrol.getId()));
            }
            tmin += minTime;
        }
        return tmin / patrols.size();
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
            population.addIndividual(new Individual(allocation, t_min));
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
     */
    public void performEvent(Individual individual, double currentTime) {
        Event event = individual.getEvent();
        event.execute(individual, currentTime, this);
    }

    /**
     * Runs the simulation.
     */
    public void run() {
        generateInitialPopulation();
        int events = 0;
        int epidemics = 0;
        double currentTime = 0.0;
        double observationInterval = tau / 20.0;
        int observation_number = 1;

        //give me an alternative
        for (Individual individual : population.getIndividuals()) {
            setNextEvent(currentTime, individual);
        }
        
        bestIndividual = population.getBestIndividual().deepCopy();
        candidateDistributions = population.getCandidateDistributions(Math.min(6, population.getIndividuals().size()))
                                          .stream()
                                          .map(Individual::deepCopy)
                                          .collect(Collectors.toList());// 5 candidates + best

        Individual nextIndividual = getNextIndividual();
        currentTime = nextIndividual.getTime();

        while ( !(currentTime >= tau || (population.getIndividuals().isEmpty()) || bestIndividual.getComfort() >= 1)) {

            performEvent(nextIndividual, currentTime);
            events++;

            if (population.getIndividuals().size() >= population.getMaxPopulation()) {
                population.handleEpidemic();
                epidemics++;
            }
            if (population.getIndividuals().isEmpty()){ 
                break;
            }

            updateBestIndividual();
            updateCandidateDistributions();

            nextIndividual = getNextIndividual();

            // printing at approx. tau/20
            if ((currentTime >= observation_number * observationInterval)) {
                outputObservation(currentTime, events, epidemics, observation_number);
                observation_number += 1;
            }

            // printing output for the last population
            if (nextIndividual.getTime() > tau){
                outputObservation(currentTime, events, epidemics, observation_number);
            }

            currentTime = nextIndividual.getTime();
            }

        if (population.getIndividuals().isEmpty() || bestIndividual.getComfort() ==1){
            outputObservation(currentTime, events, epidemics, observation_number);
        }

    }

    /**
     * Outputs observation details to the console.
     *
     * @param time             The present instant.
     * @param events           The number of realized events.
     * @param epidemics        The number of epidemics.
     * @param observation_number The observation number.
     */
    private void outputObservation(double time, int events, int epidemics, int observation_number) {
        System.out.println("Observation number: " + observation_number);
        System.out.println("Present instant: " + time);
        System.out.println("Number of realized events: " + events);
        System.out.println("Population size: " + population.getIndividuals().size());
        System.out.println("Number of epidemics: " + epidemics);
        System.out.println("Best distribution of the patrols: " + formatAllocation(bestIndividual.getAllocation()));
        System.out.println("Empire policing time: " + bestIndividual.getPolicingTime());
        System.out.println("Comfort: " + bestIndividual.getComfort());

        int numberOfCandidates = Math.min(5, candidateDistributions.size() - 1);
        for (int i = 1; i <= numberOfCandidates; i++) { // start at i=1 to ignore the best
            Individual individual = candidateDistributions.get(i);
            System.out.println("otherdist" + i + ": " + formatAllocation(individual.getAllocation()) + " : " + (individual.getPolicingTime()) + " : " + individual.getComfort());
        }
        System.out.println();
    }

    /**
     * Formats the allocation map into a string representation.
     * 
     * @param allocation The allocation map to format.
     * @return A string representation of the allocation.
     */
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
            // sb.append(entry.getKey().getId()).append(": ");
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

    /**
     * Updates the best individual if the current best individual has a higher comfort level.
     */
    private void updateBestIndividual() {
        Individual currentBest = population.getBestIndividual();
        if (currentBest.getComfort() > bestIndividual.getComfort()) {
            bestIndividual = currentBest.deepCopy();
        }
    }
    /**
     * Updates the candidate distributions by selecting the top 6 unique individuals from the population's candidate distributions.
     */
    private void updateCandidateDistributions() {
        List<Individual> newCandidates = population.getCandidateDistributions(Math.min(6, population.getIndividuals().size() - 1))
                                                    .stream()
                                                    .map(Individual::deepCopy)
                                                    .collect(Collectors.toList());
    
        for (Individual individual : candidateDistributions) {
            newCandidates.add(individual.deepCopy());
        }
    
        newCandidates.sort(Comparator.comparingDouble(Individual::getComfort).reversed());
        
        Set<Map<Patrol, List<PlanetarySystem>>> seenAllocations = new HashSet<>();
        candidateDistributions = newCandidates.stream()
                                              .filter(individual -> seenAllocations.add(individual.getAllocation()))
                                              .limit(6)
                                              .map(Individual::deepCopy)
                                              .collect(Collectors.toList());
    }
    /**
     * Gets the next individual based on their time.
     * 
     * @return The next individual.
     */
    private Individual getNextIndividual(){
        List<Individual> individualsByTime = new ArrayList<>(population.getIndividuals());
        individualsByTime.sort(Comparator.comparingDouble(Individual::getTime));
        return individualsByTime.get(0);
    }
    
    /**
     * Returns the population.
     * 
     * @return The population.
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * Returns the list of patrols.
     * 
     * @return The list of patrols.
     */
    public List<Patrol> getPatrols() {
        return patrols;
    }

    /**
     * Returns the list of planetary systems.
     * 
     * @return The list of planetary systems.
     */
    public List<PlanetarySystem> getSystems() {
        return systems;
    }
    /**
     * Returns the minimum time.
     * 
     * @return The minimum time.
     */
    public double getTmin() {
        return t_min;
    }
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
            int[] patrol_times = new int[n];
            for (int j = 0; j < n; j++) {
                patrol_times[j] = C[j][i];
            }

            systems.add(new PlanetarySystem(i, patrol_times));

        }

        Main algorithm = Main.getInstance(patrols, systems, tau, nu, nuMax, mu, rho, delta);
        algorithm.run();
    }
}
