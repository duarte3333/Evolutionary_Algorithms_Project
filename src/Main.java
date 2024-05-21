package src;

import java.util.*;

public class Main {
    private Population population;
    private List<Patrol> patrols;
    private List<PlanetarySystem> systems;
    private int tau;
    private int initialPopulation;
    private double mutationRate;
    private double reproductionRate;
    private Random random = new Random();

    public Main(List<Patrol> patrols, List<PlanetarySystem> systems, 
                int tau, double comfortThreshold, 
                int initialPopulation, int maxPopulation,
                double mutationRate, double reproductionRate) {
        this.patrols = patrols;
        this.systems = systems;
        this.tau = tau;
        this.initialPopulation = initialPopulation;
        this.mutationRate = mutationRate;
        this.reproductionRate = reproductionRate;
        population = new Population(maxPopulation, comfortThreshold);
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
        
        double Tdeath = deathRate * Math.log(1 - nextDeathSample);
        double Tmutation = mutationRate * Math.log(1 - nextMutationSample);
        double Treproduction = reproductionRate * Math.log(1 - nextReproductionSample);
        
        if (mutationRate < deathRate && mutationRate < reproductionRate) {
            individual.setEvent(new Event(2));

        }
        else if (reproductionRate < deathRate && reproductionRate < mutationRate) {
            individual.setEvent(new Event(1));

        }
        else if (deathRate < mutationRate && deathRate < reproductionRate) {
            individual.setEvent(new Event(0));
        }
        double nextEventTime = Math.min(Tdeath, Math.min(Tmutation, Treproduction));
        return nextEventTime;
    }

    public double performEvent(Individual individual) {
        Event event = individual.getEvent();
        switch (event.getType()) {
            case DEATH:
                death(individual);
                break;
            case MUTATE:
                mutate(individual);
                break;
            case REPRODUCE:
                reproduce(individual);
                break;
        }
        return individual.getTime();
    }

    private void mutate(Individual individual) {
        // Select a random patrol
        int randomPatrolIndex = random.nextInt(patrols.size());
        Patrol randomPatrol = patrols.get(randomPatrolIndex);
        List<PlanetarySystem> systemsRandomPatrol = individual.getAllocation().get(randomPatrol);
        
        if (!systemsRandomPatrol.isEmpty()) {
            // Select a random system from the chosen patrol
            PlanetarySystem system = systemsRandomPatrol.remove(random.nextInt(systems.size())); // N deveria ser o size dos systemes deste patrol
            
            // Select a new patrol to move the system to
            int newPatrolIndex = random.nextInt(patrols.size());
            while (newPatrolIndex == randomPatrolIndex) {
                newPatrolIndex = random.nextInt(patrols.size());
            }
            Patrol newPatrol = patrols.get(newPatrolIndex);
            individual.getAllocation().get(newPatrol).add(system);
        }
        double nextEventTime = setNextEvent(this.reproductionRate, this.mutationRate, 0.1, individual); // coffDeath n devia ser um input?
        individual.setTime(nextEventTime);
    }

    private void reproduce(Individual individual) {
        Map<Patrol, List<PlanetarySystem>> new_allocation = new HashMap<>(individual.getAllocation());
        int numberOfSystemsToRemove = (int)Math.round((1 - individual.getComfort()))*(systems.size());
        
        // Select systems to remove
        List<Integer> systems_to_remove = new ArrayList<>();
        for (int i = 0; i < numberOfSystemsToRemove; i++) {
            int value = random.nextInt(this.systems.size());
            while (systems_to_remove.contains(value)) { // systems_to_remove.contains(this.systems.get(value))
                value = random.nextInt(this.systems.size());
            }
            systems_to_remove.add(value);
        }
        
        // Remove Systems from the Patrols
        for (Patrol patrol : new_allocation.keySet()) {
            List<PlanetarySystem> systems = new_allocation.get(patrol);
            for (int i = 0; i < systems_to_remove.size(); i++) {
                if (systems.contains(systems.get(systems_to_remove.get(i).intValue()))) {
                    systems.remove(systems_to_remove.get(i).intValue());
                }
            }
        }

        // Add removed systems to new patrols
        for (int i = 0; i < numberOfSystemsToRemove; i++) {
            // Select a random patrol
            Patrol randomPatrol = patrols.get(random.nextInt(patrols.size()));
            List<PlanetarySystem> systems = individual.getAllocation().get(randomPatrol);
            systems.add(null) // ???
            if (!systems.isEmpty()) {
                // Select a random system from the chosen patrol
                PlanetarySystem system = systems.remove(random.nextInt(systems.size())); // Não percebi pq estamos a remover um planeta do system
                
                // Select a new patrol to move the system to
                Patrol newPatrol = patrols.get(random.nextInt(patrols.size()));
                individual.getAllocation().get(newPatrol).add(system);
            }
        }
        double nextEventTime = setNextEvent(this.reproductionRate, this.mutationRate, 0.1, individual);
        individual.setTime(nextEventTime);
    }

    private void death(Individual individual) {
        population.getIndividuals().remove(individual);
    }

    public void run() {
        generateInitialPopulation();
        int events = 0;
        int epidemics = 0;
        double currentTime = 0;

        int observationInterval = Math.max(1, tau / 20); // n percebo a cena do a: 
        for (Individual individual : population.getIndividuals()) {
            double nextEventTime = setNextEvent(this.reproductionRate, this.mutationRate, 0.1, individual);
            individual.setTime(nextEventTime);
        }
       
        while (currentTime < tau && !population.getIndividuals().isEmpty()) { // Tbm deviamos ver no loop se o confort do melhor individuo é 1

            List<Individual> individualsByTime = new ArrayList<>(population.getIndividuals());
            //sort individuals by time
            individualsByTime.sort(Comparator.comparingDouble(Individual::getTime));

            // N deviamos 1o atualizar o current time e só depos checkar a condição?
            // currentTime = performEvent(individualsByTime.get(0));
            // events++;
            // if (currentTime > tau) break;

            if (currentTime > tau) break;
            currentTime = performEvent(individualsByTime.get(0));
            events++;

            if (population.getIndividuals().size() > population.getMaxPopulation()) {
                population.handleEpidemic();
                epidemics++;
            }
            if ((events % observationInterval) == 0 || currentTime >= tau) {
                outputObservation(currentTime, events, epidemics);
            }
        }

        outputFinalObservation(currentTime, events, epidemics);
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
        Individual bestIndividual = population.getBestIndividual(); //Temos de dar store ao melhor ao individuo da simulação inteira e n só do melhor no ultimo instante
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
