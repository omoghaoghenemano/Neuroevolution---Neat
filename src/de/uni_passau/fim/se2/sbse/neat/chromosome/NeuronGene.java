package de.uni_passau.fim.se2.sbse.neat.chromosome;

/**
 * Represents a neuron gene that is part of every NEAT chromosome.
 */
public class NeuronGene {

    // TODO: It's your job to implement this class.
    //  Please do not change the signature of the given constructor and methods and ensure to implement them.
    //  You can add additional methods, fields, and constructors if needed.

    /**
     * Creates a new neuron with the given ID and activation function.
     *
     * @param id                 The ID of the neuron.
     * @param activationFunction The activation function of the neuron.
     */
    public NeuronGene(int id, ActivationFunction activationFunction, NeuronType neuronType) {
        throw new UnsupportedOperationException("Implement me!");
    }

    public int getId() {
        throw new UnsupportedOperationException("Implement me!");
    }

    public NeuronType getNeuronType() {
        throw new UnsupportedOperationException("Implement me!");
    }
}
