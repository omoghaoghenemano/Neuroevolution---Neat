package de.uni_passau.fim.se2.sbse.neat;

import de.uni_passau.fim.se2.sbse.neat.algorithm.Neuroevolution;
import de.uni_passau.fim.se2.sbse.neat.chromosome.Agent;
import de.uni_passau.fim.se2.sbse.neat.environments.Environment;
import de.uni_passau.fim.se2.sbse.neat.environments.SinglePoleBalancing;
import de.uni_passau.fim.se2.sbse.neat.environments.Tasks;
import de.uni_passau.fim.se2.sbse.neat.environments.XOR;
import de.uni_passau.fim.se2.sbse.neat.utils.Randomness;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class Main implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-t", "--task"},
            description = "The reinforcement learning task to solve [XOR, CART, CART_RANDOM].",
            converter = TaskConverter.class,
            required = true
    )
    private Tasks task;

    @CommandLine.Option(
            names = {"-p", "--population-size"},
            description = "The size of the population.",
            defaultValue = "50"
    )
    private int populationSize;

    @CommandLine.Option(
            names = {"-g", "--max-generations"},
            description = "The maximum number of generations to run the algorithm for.",
            defaultValue = "50"
    )
    private int maxGenerations;

    @CommandLine.Option(
            names = {"-r", "--repetitions"},
            description = "The number of times to repeat the task.",
            defaultValue = "30"
    )
    private int repetitions;

    @CommandLine.Option(
            names = {"-v", "--visualise"},
            description = "Visualises the behaviour of a trained agent in the specified task.",
            defaultValue = "false"
    )
    private boolean visualise;

    @CommandLine.Option(
            names = {"-s", "--seed"},
            description = "Sets the random number generator to a fixed seed."
    )
    public void setSeed(long seed) {
        Randomness.random().setSeed(seed);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    private final List<Agent> solutions = new ArrayList<>();
    private final List<Integer> generations = new ArrayList<>();
    private final List<Boolean> successes = new ArrayList<>();
    private final List<Long> times = new ArrayList<>();


    /**
     * Use the Neat algorithm to solve the specified reinforcement learning task for the specified number of repetitions.
     *
     * @return 1 if the application encountered an error, 0 otherwise.
     */
    @Override
    public Integer call() {
        IntStream.range(0, repetitions).forEach(_ -> solveTask());
        printResults();

        if (visualise) {
            Agent solution = solutions.getFirst();
            Environment environment = initialiseTask();
            CountDownLatch visualisationLatch = new CountDownLatch(1);
            environment.visualise(solution, visualisationLatch);
            try {
                visualisationLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return 1;
            }
        }

        return 0;
    }

    /**
     * Solves the specified reinforcement learning task using the Neat algorithm.
     */
    private void solveTask() {
        Environment environment = initialiseTask();
        Neuroevolution neat = initialiseNeat(populationSize, maxGenerations);

        long startTime = System.currentTimeMillis();
        Agent solution = neat.solve(environment);

        times.add(System.currentTimeMillis() - startTime);
        generations.add(neat.getGeneration());
        successes.add(environment.solved(solution));
        solutions.add(solution);
    }

    /**
     * Prints the results of the evaluation.
     */
    private void printResults() {
        long successfulRepetitions = successes.stream().filter(Boolean::booleanValue).count();
        double averageGenerations = generations.stream().mapToInt(Integer::intValue).average().orElseThrow();
        double maxGenerations = generations.stream().mapToInt(Integer::intValue).max().orElseThrow();
        double minGenerations = generations.stream().mapToInt(Integer::intValue).min().orElseThrow();
        double averageTimeSeconds = times.stream().mapToLong(Long::longValue).average().orElseThrow() / 1000.0;
        System.out.println("Task: \t\t" + task);
        System.out.println("Successful repetitions: \t" + successfulRepetitions);
        System.out.println("Min generations: \t" + minGenerations);
        System.out.println("Average generations: \t" + averageGenerations);
        System.out.println("Max generations: \t" + maxGenerations);
        System.out.println("Average time per task (s): \t" + averageTimeSeconds);
    }


    /**
     * Initialises the Neuroevolution algorithm Neat to solve the specified task.
     * Each Neat instance must start with a fresh state, such that no information is shared between instances.
     *
     * @param populationSize The size of the population.
     * @param maxGenerations The maximum number of generations to run the algorithm for.
     * @return The initialised Neat algorithm.
     */
    public static Neuroevolution initialiseNeat(int populationSize, int maxGenerations) {
        throw new UnsupportedOperationException("Implement me!");
    }

    /**
     * Initialises the specified reinforcement learning task.
     *
     * @return The initialised task.
     */
    private Environment initialiseTask() {
        return switch (task) {
            case Tasks.XOR -> new XOR();
            case Tasks.CARTPOLE -> new SinglePoleBalancing(10, false, Randomness.random());
            case Tasks.CARTPOLE_RANDOM -> new SinglePoleBalancing(10, true, Randomness.random());
        };
    }

}


/**
 * Converts supplied cli parameters to the respective {@link Tasks}.
 */
final class TaskConverter implements CommandLine.ITypeConverter<Tasks> {
    @Override
    public Tasks convert(String task) {
        return switch (task.toUpperCase()) {
            case "XOR" -> Tasks.XOR;
            case "CART" -> Tasks.CARTPOLE;
            case "CART_RANDOM" -> Tasks.CARTPOLE_RANDOM;
            default -> throw new IllegalArgumentException("The task '" + task + "' is not a valid reinforcement learning task.");
        };
    }
}