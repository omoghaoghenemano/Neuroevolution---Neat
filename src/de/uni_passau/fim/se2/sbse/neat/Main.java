package de.uni_passau.fim.se2.sbse.neat;

import de.uni_passau.fim.se2.sbse.neat.algorithms.NeatAlgorithm;
import de.uni_passau.fim.se2.sbse.neat.algorithms.Neuroevolution;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.Agent;
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
    public Tasks task;

    @CommandLine.Option(
            names = {"-p", "--population-size"},
            description = "The size of the population.",
            defaultValue = "50"
    )
    public int populationSize;

    @CommandLine.Option(
            names = {"-g", "--max-generations"},
            description = "The maximum number of generations to run the algorithm for.",
            defaultValue = "50"
    )
    public int maxGenerations;

    @CommandLine.Option(
            names = {"-r", "--repetitions"},
            description = "The number of times to repeat the task.",
            defaultValue = "30"
    )
    public int repetitions;

    @CommandLine.Option(
            names = {"-v", "--visualise"},
            description = "Visualises the behaviour of a trained agent in the specified task.",
            defaultValue = "false"
    )
    public boolean visualise;

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

    public final List<Agent> solutions = new ArrayList<>();
    public final List<Integer> generations = new ArrayList<>();
    public final List<Boolean> successes = new ArrayList<>();
    public final List<Long> times = new ArrayList<>();


    /**
     * Use the Neat algorithm to solve the specified reinforcement learning task for the specified number of repetitions.
     *
     * @return 1 if the application encountered an error, 0 otherwise.
     */
    @Override
    public Integer call() {
        System.out.printf(
                "Analysing task '%s' with a population size of %d and a maximum of %d generations over %d repetitions.%n",
                task, populationSize, maxGenerations, repetitions);
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
    public void solveTask() {
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
    public void printResults() {
        long successfulRepetitions = successes.stream().filter(Boolean::booleanValue).count();
        double averageGenerations = generations.stream().mapToInt(Integer::intValue).average().orElseThrow();
        double maxGenerations = generations.stream().mapToInt(Integer::intValue).max().orElseThrow();
        double minGenerations = generations.stream().mapToInt(Integer::intValue).min().orElseThrow();
        double averageTimeSeconds = times.stream().mapToLong(Long::longValue).average().orElseThrow() / 1000.0;
        System.out.println("Successful repetitions: " + successfulRepetitions);
        System.out.println("Min generations: " + minGenerations);
        System.out.println("Average generations: " + averageGenerations);
        System.out.println("Max generations: " + maxGenerations);
        System.out.println("Average time per task (s): " + averageTimeSeconds);
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
          Environment environment = new SinglePoleBalancing(10, false, Randomness.random());
        return new NeatAlgorithm(populationSize, maxGenerations, environment);
    }

    /**
     * Initialises the specified reinforcement learning task.
     *
     * @return The initialised task.
     */
    public Environment initialiseTask() {
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