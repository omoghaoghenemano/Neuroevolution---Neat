package de.uni_passau.fim.se2.sbse.neat.crossover;


import de.uni_passau.fim.se2.sbse.neat.chromosome.NetworkChromosome;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * A NEAT crossover operation that is used by the NEAT algorithm to combine two parent chromosomes.
 */
public class NeatCrossover implements Crossover<NetworkChromosome> {

    /**
     * The random number generator to use.
     */
    private final Random random;

    /**
     * Creates a new NEAT crossover operator with the given random number generator.
     *
     * @param random The random number generator to use.
     */
    public NeatCrossover(Random random) {
        this.random = requireNonNull(random);
    }

    /**
     * Applies a crossover operation to the given parent chromosomes by combining their genes.
     * During the crossover operation, we determine for each gene whether it is a matching gene or a disjoint/excess gene.
     * Matching genes are inherited with a 50% chance from either parent,
     * while disjoint/excess genes are only inherited from the fitter parent.
     *
     * @param parent1 The first crossover parent.
     * @param parent2 The second crossover parent.
     * @return A new chromosome resulting from the crossover operation.
     */
    @Override
    public NetworkChromosome apply(NetworkChromosome parent1, NetworkChromosome parent2) {
        throw new UnsupportedOperationException("Implement me!");
    }
}
