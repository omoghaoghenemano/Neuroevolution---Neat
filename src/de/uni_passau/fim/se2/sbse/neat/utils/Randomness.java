package de.uni_passau.fim.se2.sbse.neat.utils;

import java.util.Random;

// This class is a utility class that provides a single source of randomness for the entire application.
public class Randomness {

    // Internal source of randomness.
    private static final Random random = new Random();

    private Randomness() {
        // private constructor to prevent instantiation.
    }

    /**
     * Returns the source of randomness.
     *
     * @return randomness
     */
    public static Random random() {
        return random;
    }
}
