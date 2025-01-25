package de.uni_passau.fim.se2.sbse.neat.algorithms;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.Agent;
import de.uni_passau.fim.se2.sbse.neat.environments.Environment;

/**
 * Represents a neuroevolution algorithm that solves reinforcement learning tasks.
 */
public interface Neuroevolution {

    /**
     * Solves the given reinforcement learning task.
     *
     * @return The agent that solves the task.
     */
    Agent solve(Environment environment);

    /**
     * Returns the current generation of the neuroevolution algorithm.
     *
     * @return The current generation of the neuroevolution algorithm.
     */
    int getGeneration();
}
