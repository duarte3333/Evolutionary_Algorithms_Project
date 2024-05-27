package src.main;

import src.model.*;
import src.service.*;
import src.util.Parser;

import java.util.*;


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

    public static Main getInstance(List<Patrol> patrols, List<PlanetarySystem> systems, 
                                   int tau, int initialPopulation, int maxPopulation,
                                   double deathRate, double mutationRate, double reproductionRate) {
        if (instance == null) {
            instance = new Main(patrols, systems, tau, initialPopulation, maxPopulation, deathRate, mutationRate, reproductionRate);
        }
        return instance;
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

    private double setNextEvent(double coffReproduction, double coffMutation, double coffDeath, Individual individual) {
        double deathRate = (1 - Math.log(1 - individual.getComfort())) * coffDeath;
        double mutationRate = (1 - Math.log(individual.getComfort())) * coffMutation;
        double reproductionRate = (1 - Math.log(individual.getComfort())) * coffReproduction;

        double nextDeathSample = random.nextDouble();
        double nextMutationSample = random.nextDouble();
        double nextReproductionSample = random.nextDouble();
        
        double Tdeath = -deathRate * Math.log(1 - nextDeathSample);
        double Tmutation = -mutationRate * Math.log(1 - nextMutationSample);
        double Treproduction = -reproductionRate * Math.log(1 - nextReproductionSample);
        
        EventType eventType = null;
        if (Tmutation < Tdeath && Tmutation < Treproduction) {
            eventType = EventType.MUTATE; // Mutation
        } else if (Treproduction < Tdeath && Treproduction < Tmutation) {
            eventType = EventType.REPRODUCE; // Reproduction
        } else if (Tdeath < Tmutation && Tdeath < Treproduction) {
            eventType = EventType.DEATH; // Death
        }
        double nextEventTime = Math.min(Tdeath, Math.min(Tmutation, Treproduction));
        Event event = EventFactory.createEvent(eventType, individual, nextEventTime);
        individual.setEvent(event);
        return nextEventTime;
    }


    public double performEvent(Individual individual, double currentTime) {
        Event event = individual.getEvent();
        event.trigger();
        return individual.getTime();
    }

    public void run() {
        generateInitialPopulation();
        int events = 0;
        int epidemics = 0;
        double currentTime = 0;

        int observationInterval = Math.max(1, tau / 20); // n percebo a cena do a: 
        for (Individual individual : population.getIndividuals()) {
            double nextEventTime = setNextEvent(this.reproductionRate, this.mutationRate, this.deathRate, individual);
            System.out.println("time-- " + nextEventTime);
            individual.setTime(nextEventTime);
        }
       
        while (currentTime < tau && !population.getIndividuals().isEmpty()) { // Tbm deviamos ver no loop se o confort do melhor individuo é 1

            List<Individual> individualsByTime = new ArrayList<>(population.getIndividuals());
            //Sort individuals by time
            individualsByTime.sort(Comparator.comparingDouble(Individual::getTime));

            if (population.getIndividuals().size() >= population.getMaxPopulation()) {
                System.out.println("......Handling epidemic");
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
        //print max time
        double maxTime = 0;
        for (Patrol patrol : bestIndividual.getAllocation().keySet()) {
            for (PlanetarySystem system : bestIndividual.getAllocation().get(patrol))
                maxTime += system.getTimeForPatrol(patrol.getId());
        }
        System.out.println("Max time: " + maxTime);


        // Output other candidate distributions if available
        int numberOfCandidates = Math.min(5, population.getIndividuals().size());
        for (int i = 1; i < numberOfCandidates; i++) {
            Individual individual = population.getIndividuals().get(i);
            System.out.println("Other candidate distribution " + i + ": " + formatAllocation(individual.getAllocation()));
            System.out.println("Empire policing time: " + (1 / individual.getComfort()));
            System.out.println("Comfort: " + individual.getComfort());
            double my_time = 0;
            double myMaxTime = -1;
            for (Patrol patrol : bestIndividual.getAllocation().keySet()) {
                //int max integer
                my_time = 0;
                for (PlanetarySystem system : bestIndividual.getAllocation().get(patrol))
                    my_time += system.getTimeForPatrol(patrol.getId());
                if (my_time > myMaxTime) {
                    myMaxTime = my_time;
                }
            }
            System.out.println("Max time: " + myMaxTime);
        }
        System.out.println();
    }
    
    private void outputFinalObservation(double time, int events, int epidemics) {
        Individual bestIndividual = population.getBestIndividual(); //Temos de dar store ao melhor ao individuo da simulação inteira e n só do melhor no ultimo instante
        System.out.println("Final instant: " + time);
        System.out.println("Total events: " + events);
        System.out.println("Final population size: " + population.getIndividuals().size());
        System.out.println("Total number of epidemics: " + epidemics);
        System.out.println("Best distribution of the patrols: " + formatAllocation(bestIndividual.getAllocation()));
        System.out.println("Empire policing time: " + (1 / bestIndividual.getComfort()));
        System.out.println("Final comfort: " + bestIndividual.getComfort());
        //print max time
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
        System.out.println("Max time: " + maxTime);
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

        Main algorithm = Main.getInstance(patrols, systems, tau, nu, nuMax, mu, rho, delta);
        algorithm.run();
    }
}
