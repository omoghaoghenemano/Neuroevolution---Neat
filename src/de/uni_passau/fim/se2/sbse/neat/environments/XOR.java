package de.uni_passau.fim.se2.sbse.neat.environments;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.Agent;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Models an XOR gate as a reinforcement learning environment.
 */
public class XOR implements Environment {

    private final static double[][] XOR_INPUTS = {
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
    };

    private final static double[] XOR_OUTPUTS = {0, 1, 1, 0};

    /**
     * The current inputs of the XOR gate.
     */
    private int currentInput;

    /**
     * The accumulated error of the agent. Will be used to compute the reward.
     */
    private double error;

    public XOR() {
        this.error = 0;
        this.currentInput = 0;
    }

    @Override
    public int stateSize() {
        return 2;
    }

    @Override
    public int actionInputSize() {
        return 1;
    }

    @Override
    public void resetState() {
        this.error = 0;
        this.currentInput = 0;
    }

    @Override
    public void updateState(List<Double> actions) {
        double networkOutput = actions.getFirst() + 1;  // Scale [-1, 1] to [0, 1]
        int result = networkOutput + 1 > 0.5 ? 1 : 0;
        if (result != XOR_OUTPUTS[currentInput]) {
            error += Math.abs(networkOutput - XOR_OUTPUTS[currentInput]);
        }
        currentInput++;
    }

    @Override
    public List<Double> getState() {
        return List.of(XOR_INPUTS[currentInput][0], XOR_INPUTS[currentInput][1]);
    }

    @Override
    public double evaluate(Agent agent) {
        resetState();
        while (!isDone()) {
            List<Double> action = agent.getOutput(getState());
            updateState(action);
        }
        return Math.pow(4 - error, 2); // Cast problem to a maximisation problem
    }

    @Override
    public boolean isDone() {
        return currentInput >= XOR_INPUTS.length;
    }

    @Override
    public boolean solved(Agent agent) {
        return error == 0;
    }

    @Override
    public void visualise(Agent agent, CountDownLatch latch) {
        throw new UnsupportedOperationException("Visualisation not supported for XOR environment.");
    }
}
