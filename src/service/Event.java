package src.service;
import src.model.Individual;
import src.model.Patrol;
import src.model.PlanetarySystem;
import java.util.*;

public abstract class Event {
    protected Individual individual;
    protected double currentTime;
    protected Random random = new Random();
    protected List<PlanetarySystem> systems;
    protected List<Patrol> patrols;
    protected double reproductionRate;
    protected double mutationRate;
    protected double deathRate;
    protected Population population;

    public Event(Individual individual, double currentTime, List<PlanetarySystem> systems, List<Patrol> patrols, double reproductionRate, double mutationRate, double deathRate, Population population) {
        this.individual = individual;
        this.currentTime = currentTime;
        this.systems = systems;
        this.patrols = patrols;
        this.reproductionRate = reproductionRate;
        this.mutationRate = mutationRate;
        this.deathRate = deathRate;
        this.population = population;
    }

    public abstract void trigger(double currentTime);

    public static void setNextEvent(double coffReproduction, double coffMutation, 
            double coffDeath, Individual individual, double currentTime,
            List<PlanetarySystem> systems, List<Patrol> patrols, Population population) {
        Random random = new Random();
        double deathRate = (1 - Math.log(1 - individual.getComfort())) * coffDeath;
        double mutationRate = (1 - Math.log(individual.getComfort())) * coffMutation;
        double reproductionRate = (1 - Math.log(individual.getComfort())) * coffReproduction;

        double nextDeathSample = random.nextDouble();
        double nextMutationSample = random.nextDouble();
        double nextReproductionSample = random.nextDouble();

        double Tdeath = -deathRate * Math.log(1 - nextDeathSample);
        double Tmutation = -mutationRate * Math.log(1 - nextMutationSample);
        double Treproduction = -reproductionRate * Math.log(1 - nextReproductionSample);

        EventType eventType = Event.determineEventType(Tdeath, Tmutation, Treproduction);
        double nextEventTime = Math.min(Tdeath, Math.min(Tmutation, Treproduction));
        Event event = EventFactory.createEvent(eventType, individual, currentTime, systems, patrols, reproductionRate, mutationRate, deathRate, population);
        individual.setEvent(event);
        individual.setTime(nextEventTime + currentTime);
    }

    private static EventType determineEventType(double Tdeath, double Tmutation, double Treproduction) {
        if (Tmutation < Tdeath && Tmutation < Treproduction) {
            return EventType.MUTATE;
        } else if (Treproduction < Tdeath && Treproduction < Tmutation) {
            return EventType.REPRODUCE;
        } else if (Tdeath < Tmutation && Tdeath < Treproduction) {
            return EventType.DEATH;
        }
        return null;
    }
}

class ReproductionEvent extends Event {
    public ReproductionEvent(Individual individual, double currentTime, List<PlanetarySystem> systems, List<Patrol> patrols, double reproductionRate, double mutationRate, double deathRate, Population population) {
        super(individual, currentTime, systems, patrols, reproductionRate, mutationRate, deathRate, population);
    }

    @Override
    public void trigger(double currTime) {
        System.out.println("---reproduce: ");
        Map<Patrol, List<PlanetarySystem>> new_allocation = new HashMap<>(individual.getAllocation());
        int numberOfSystemsToRemove = (int) Math.floor((1 - individual.getComfort()) * systems.size());

        List<PlanetarySystem> systems_to_remove = new ArrayList<>();
        List<PlanetarySystem> tmp_system = new ArrayList<>(this.systems);
        for (int i = 0; i < numberOfSystemsToRemove; i++) {
            int randomIndex = random.nextInt(tmp_system.size());
            systems_to_remove.add(tmp_system.get(randomIndex));
            tmp_system.remove(randomIndex);
        }

        for (Patrol patrol : new_allocation.keySet()) {
            new_allocation.get(patrol).removeAll(systems_to_remove);
        }

        for (PlanetarySystem system : systems_to_remove) {
            Patrol randomPatrol = patrols.get(random.nextInt(patrols.size()));
            new_allocation.get(randomPatrol).add(system);
        }

        Individual newIndividual = new Individual(new_allocation);
        population.addIndividual(newIndividual);
        System.out.println("---Dize population " + population.getIndividuals().size());

        Event.setNextEvent(this.reproductionRate, this.mutationRate, this.deathRate, individual, currTime, systems, patrols, population);
        Event.setNextEvent(this.reproductionRate, this.mutationRate, this.deathRate, newIndividual, currTime, systems, patrols, population);
    }
}


class MutationEvent extends Event {
    public MutationEvent(Individual individual, double currentTime, List<PlanetarySystem> systems, List<Patrol> patrols, double reproductionRate, double mutationRate, double deathRate, Population population) {
        super(individual, currentTime, systems, patrols, reproductionRate, mutationRate, deathRate, population);
    }

    @Override
    public void trigger(double currTime) {
        System.out.println("---mutate: ");
        int randomPatrolIndex = random.nextInt(patrols.size());
        Patrol randomPatrol = patrols.get(randomPatrolIndex);
        List<PlanetarySystem> systemsRandomPatrol = individual.getAllocation().get(randomPatrol);

        if (!systemsRandomPatrol.isEmpty()) {
            PlanetarySystem system = individual.getAllocation().get(randomPatrol).remove(random.nextInt(systemsRandomPatrol.size()));

            int newPatrolIndex = random.nextInt(patrols.size());
            while (newPatrolIndex == randomPatrolIndex) {
                newPatrolIndex = random.nextInt(patrols.size());
            }
            Patrol newPatrol = patrols.get(newPatrolIndex);
            individual.getAllocation().get(newPatrol).add(system);
        }
        Event.setNextEvent(this.reproductionRate, this.mutationRate, this.deathRate, individual, currTime, systems, patrols, population);
    }
}

class DeathEvent extends Event {
    public DeathEvent(Individual individual, double currentTime, List<PlanetarySystem> systems, List<Patrol> patrols, double reproductionRate, double mutationRate, double deathRate, Population population) {
        super(individual, currentTime, systems, patrols, reproductionRate, mutationRate, deathRate, population);
    }

    @Override
    public void trigger(double currTime) {
        System.out.println("---death: ");
        population.getIndividuals().remove(individual);
    }
}
