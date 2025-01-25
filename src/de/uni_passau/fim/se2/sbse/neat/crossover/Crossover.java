package de.uni_passau.fim.se2.sbse.neat.crossover;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.Agent;

import java.util.function.BinaryOperator;

/**
 * A crossover operation that can be applied to two parent chromosomes.
 *
 * @param <A> The type of the chromosomes to which the crossover operation can be applied.
 */
public interface Crossover<A extends Agent> extends BinaryOperator<A> {

    /**
     * Applies a crossover operation to the given parent chromosomes.
     * The crossover operation must ensure that the parent chromosomes do not get changed.
     *
     * @param parent1 The first parent chromosome.
     * @param parent2 The second parent chromosome.
     * @return A new chromosome resulting from the crossover operation.
     */
    @Override
    A apply(A parent1, A parent2);
}
