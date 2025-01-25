package de.uni_passau.fim.se2.sbse.neat.environments;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.Agent;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Models a reinforcement learning environment.
 */
public interface Environment {

    /**
     * Returns the number of state variables that the environment provides as input to an agent.
     *
     * @return The number of state variables that the environment provides as input to an agent.
     */
    int stateSize();

    /**
     * Returns the number of action inputs the environment accepts as an input from the agent.
     *
     * @return The number of action inputs the environment accepts as an input from the agent.
     */
    int actionInputSize();

    /**
     * Resets the environment to its initial state.
     */
    void resetState();

    /**
     * Updates the environment state based on the given actions.
     *
     * @param actions The actions to apply to the environment. All action values must be in the range [-1, 1].
     */
    void updateState(List<Double> actions);

    /**
     * Returns the current state of the environment that may be used as an input
     * for an agent interacting with the environment.
     *
     * @return The current state of the environment.
     */
    List<Double> getState();

    /**
     * Evaluates the given agent in the environment.
     *
     * @return The reward the agent received after interacting with the environment.
     */
    double evaluate(Agent agent);

    /**
     * Returns whether the environment is in a terminal state.
     *
     * @return True if the environment is in a terminal state, false otherwise.
     */
    boolean isDone();

    /**
     * Returns whether the environment was solved by the given agent.
     *
     * @param agent The agent to evaluate.
     * @return True if the environment is solved, false otherwise.
     */
    boolean solved(Agent agent);

    /**
     * Creates and shows a visualisation of the environment being controlled by the given agent.
     * The provided latch can be used to wait for the visualisation to be closed.
     * To this end, the latch must be counted down when the visualisation is closed.
     *
     * @param agent The agent controlling the environment
     * @param latch The latch to count down when the visualisation is closed.
     */
    void visualise(Agent agent, CountDownLatch latch);
}
