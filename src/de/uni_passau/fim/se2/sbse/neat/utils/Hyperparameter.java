package de.uni_passau.fim.se2.sbse.neat.utils;

public class Hyperparameter {

    // Mutation
    public static double ADD_NEURON_PROBABILITY = 0.03;
    public static double ADD_CONNECTION_PROBABILITY = 0.05;
    public static double TOGGLE_CONNECTION_PROBABILITY = 0.1;
    public static double WEIGHT_MUTATION_PROBABILITY = 0.8;
    public static double WEIGHT_MUTATION_STD = 1;

    // Population Management
    public static int PENALISING_THRESHOLD = 15;
    public static double PARENT_FRACTION = 0.25;
    public static double MUTATION_RATE = 0.75;

    // Speciation
    public static double EXCESS_COEFFICIENT = 1;
    public static double DISJOINT_COEFFICIENT = 1;
    public static double WEIGHT_COEFFICIENT = 0.4;
    public static double COMPATIBILITY_THRESHOLD = 3;
}