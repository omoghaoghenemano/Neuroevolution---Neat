package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import java.util.List;

/**
 * Represents an agent that interacts with a reinforcement learning environment to solve tasks.
 */
public interface Agent {

    /**
     * Returns the output of the agent for the environment state.
     *
     * @param state The state of the environment.
     * @return The output of the agent for the environment state.
     */
    List<Double> getOutput(List<Double> state);

    /**
     * Sets the fitness of the agent.
     * In our case, the fitness is the reward the agent received in the environment over the entire episode.
     * For every reinforcement learning environment, the optimisation goal is to maximise fitness.
     *
     * @param fitness The fitness of the agent.
     */
    void setFitness(double fitness);

    /**
     * Returns the fitness of the agent.
     * In our case, the fitness is the reward the agent received in the environment over the entire episode.
     * For every reinforcement learning environment, the optimisation goal is to maximise fitness.
     *
     * @return The fitness of the agent.
     */
    double getFitness();
}
