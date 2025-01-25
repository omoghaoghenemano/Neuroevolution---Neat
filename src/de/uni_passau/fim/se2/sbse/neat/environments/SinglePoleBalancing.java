package de.uni_passau.fim.se2.sbse.neat.environments;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static java.util.Objects.requireNonNull;

import javax.swing.SwingUtilities;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.Agent;
import de.uni_passau.fim.se2.sbse.neat.environments.visualisation.SinglePoleVisualisation;

/**
 * The environment consists of a cart that can move along a track and a pole attached to the cart.
 * The goal is to balance the pole by moving the cart left and right.
 */
public class SinglePoleBalancing implements Environment {
    // Physical constants
    private static final double GRAVITY = 9.8;
    private static final double CART_MASS = 1.0;
    private static final double POLE_MASS = 0.1;
    private static final double POLE_LENGTH = 0.5; // Half-length of pole
    private static final double FORCE_MAGNITUDE = 10.0;
    private static final double TIME_STEP = 0.02; // 20ms

    private static final double MAX_POSITION = 2.4;
    private static final double MAX_ANGLE = (12 * Math.PI) / 180; // 12 degrees in radians
    private static final double MAX_VELOCITY = 2.5;         // Derived empirically
    private static final double MAX_POLE_VELOCITY = 3.1;    // Derived empirically

    public static final int DEFAULT_MAX_STEPS = 120000;

    // State variables
    private double cartPosition;
    private double cartVelocity;
    private double poleAngle;
    private double poleAngularVelocity;


    // Track simulation status
    private boolean failed;
    private int steps;

    private final int maxSteps;
    private final int repeats;
    private final boolean randomise;
    private final Random random;

    /**
     * Initialises a new single pole balancing environment.
     *
     * @param maxSteps  The maximum number of steps to run the simulation for.
     * @param repeats   The number of times to repeat the simulation to calculate the reward.
     * @param randomise Whether to randomise the initial cart position and pole angle.
     */
    public SinglePoleBalancing(int maxSteps, int repeats, boolean randomise, Random random) {
        this.maxSteps = maxSteps;
        this.repeats = repeats;
        this.randomise = randomise;
        this.random = requireNonNull(random);
        resetState();
    }

    /**
     * Initialises a new single pole balancing environment with the default maximum number of steps.
     *
     * @param repeats   The number of times to repeat the simulation to calculate the reward.
     * @param randomise Whether to randomise the initial cart position and pole angle.
     * @param random    The random number generator to use.
     */
    public SinglePoleBalancing(int repeats, boolean randomise, Random random) {
        this(DEFAULT_MAX_STEPS, repeats, randomise, random);
    }

    @Override
    public int stateSize() {
        return this.getState().size();
    }

    @Override
    public int actionInputSize() {
        return 1;
    }

    /**
     * Resets the state of the environment.
     */
    public void resetState() {
        cartPosition = this.randomise ? this.random.nextDouble(-MAX_POSITION * 0.9, MAX_POSITION * 0.9) : 0;
        cartVelocity = this.randomise ? this.random.nextDouble(-MAX_VELOCITY * 0.5, MAX_VELOCITY * 0.5) : 0;
        poleAngle = this.randomise ? this.random.nextDouble(-MAX_ANGLE * 0.9, MAX_ANGLE * 0.9) : 0.01;
        poleAngularVelocity = this.randomise ? this.random.nextDouble(-MAX_POLE_VELOCITY * 0.5, MAX_POLE_VELOCITY * 0.5) : 0;
        failed = false;
        steps = 0;
    }

    /**
     * Updates the state of the environment based on the given action.
     *
     * @param actions The action to take, positive values move the cart to the right, negative to the left.
     */
    public void updateState(List<Double> actions) {
        double force = actions.getFirst() * FORCE_MAGNITUDE;

        double totalMass = CART_MASS + POLE_MASS;
        double cosTheta = Math.cos(poleAngle);
        double sinTheta = Math.sin(poleAngle);
        double temp = (force + POLE_MASS * POLE_LENGTH * poleAngularVelocity * poleAngularVelocity * sinTheta) / totalMass;

        double poleAccelNum = GRAVITY * sinTheta - cosTheta * temp;
        double poleDenom = POLE_LENGTH * (4.0 / 3.0 - (POLE_MASS * cosTheta * cosTheta) / totalMass);
        double poleAccel = poleAccelNum / poleDenom;

        double cartAccel = temp - (POLE_MASS * POLE_LENGTH * poleAccel * cosTheta) / totalMass;

        cartPosition += TIME_STEP * cartVelocity;
        cartVelocity += TIME_STEP * cartAccel;
        poleAngle += TIME_STEP * poleAngularVelocity;
        poleAngularVelocity += TIME_STEP * poleAccel;

        steps++;

        // Stop if the cart goes off the track or the pole falls over by more than 12 degrees.
        if (Math.abs(cartPosition) > MAX_POSITION || Math.abs(poleAngle) > MAX_ANGLE) {
            failed = true;
        }
    }

    /**
     * Returns the current state of the environment normalised to the range [-1, 1].
     *
     * @return The state of the environment normalised to the range [-1, 1] as an array of doubles.
     */
    public List<Double> getState() {
        return List.of(
                cartPosition / MAX_POSITION,
                cartVelocity / MAX_VELOCITY,
                poleAngle / MAX_ANGLE,
                poleAngularVelocity / MAX_POLE_VELOCITY
        );
    }

    /**
     * Evaluates the given agent in the environment once.
     *
     * @param agent The agent to evaluate.
     * @return The reward obtained by the agent in the environment.
     */
    private double singleEvaluation(Agent agent) {
        resetState();
        while (!isDone()) {
            List<Double> action = agent.getOutput(getState());
            updateState(action);
        }
        return steps;
    }

    /**
     * Evaluates the given agent in the environment for the specified number of repeats.
     *
     * @param agent The agent to evaluate.
     * @return The reward obtained by the agent in the environment.
     */
    @Override
    public double evaluate(Agent agent) {
        double reward = singleEvaluation(agent);
        if (failed) {
            return reward;
        }

        for (int i = 0; i < repeats; i++) {
            reward += singleEvaluation(agent);
        }

        return reward;
    }

    /**
     * The simulation stops if the pole falls over or the cart goes off the track.
     *
     * @return true if the simulation is done, false otherwise.
     */
    public boolean isDone() {
        return failed || steps >= maxSteps;
    }

    /**
     * Determines whether an agent has solved the environment based on obtained reward.
     *
     * @param agent The agent to evaluate.
     * @return true if the environment is solved, false otherwise.
     */
    public boolean solved(Agent agent) {
        return agent.getFitness() >= maxSteps * repeats;
    }

    @Override
    public void visualise(Agent agent, CountDownLatch latch) {
        SinglePoleBalancing visEnv = new SinglePoleBalancing(maxSteps, repeats, randomise, random);
        SwingUtilities.invokeLater(() -> {
            SinglePoleVisualisation visualisation = new SinglePoleVisualisation(visEnv, agent, latch);
            visualisation.setLocationRelativeTo(null);
            visualisation.setVisible(true);
        });
    }
}
