## Project overview
This Java simulation uses evolutionary algorithms to find the best patrol distribution for a galactic empire. It simulates natural selection to evolve patrol allocations, minimizing time spent suppressing rebellions.  The simulation ensures diversity through reproduction, mutation, and even simulated epidemics. This project shows how such algorithms can solve complex scheduling problems in dynamic environments.

## What is an Evolutionary Algorithm?

An evolutionary algorithm is a subset of evolutionary computation, a generic population-based optimization algorithm. Inspired by the process of natural selection, these algorithms generate solutions to optimization and search problems using techniques such as inheritance, mutation, selection, and crossover (recombination).

Key components of an evolutionary algorithm include:

- **Population:** A collection of potential solutions to the problem.
- **Fitness Function:** A function that quantifies how close a given solution is to the optimum.
- **Selection:** The process of choosing individuals based on their fitness to produce offspring for the next generation.
- **Crossover (Recombination):** A genetic operator used to combine the genetic information of two parents to generate new offspring.
- **Mutation:** A genetic operator used to maintain genetic diversity from one generation of a population to the next.
- **Termination:** A condition to stop the algorithm, which could be a fixed number of generations or a satisfactory fitness level.

## Key Features

- **Evolutionary Algorithm:** The simulation uses evolutionary programming techniques, mimicking natural selection processes to iteratively improve patrol distributions.
- **Dynamic Population Management:** The population of patrol distributions evolves over time, with mechanisms for reproduction, mutation, and survival of the fittest.
- **Stochastic Events:** The simulation incorporates random events, including reproduction, mutation, and death, which influence the evolution of patrol distributions.
- **Epidemic Handling:** To control the population size, the simulation includes an epidemic mechanism that ensures only the most efficient patrol distributions survive when the population exceeds a certain threshold.
- **Performance Monitoring:** The simulation periodically outputs observations, allowing users to track the progress and performance of the patrol distributions over time.
