package de.uni_passau.fim.se2.sbse.neat.mutation;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.Agent;

import java.util.function.UnaryOperator;

/**
 * Represents a mutation operation that can be applied to a chromosome to introduce random variations to the genotype.
 *
 * @param <A> The type of the chromosome to which the mutation operation will be applied.
 */
public interface Mutation<A extends Agent> extends UnaryOperator<A> {

    /**
     * Applies a mutation operation to the given chromosome.
     * The mutation operation must ensure that the parent chromosome does not get changed.
     *
     * @param parent The chromosome to which the mutation operation will be applied.
     * @return A new chromosome resulting from the mutation operation.
     */
    @Override
    A apply(A parent);
}
