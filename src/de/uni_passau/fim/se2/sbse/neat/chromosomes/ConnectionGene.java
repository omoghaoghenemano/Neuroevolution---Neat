package de.uni_passau.fim.se2.sbse.neat.chromosomes;

/**
 * Represents a connection gene that is part of every NEAT chromosome.
 */
public class ConnectionGene {

    // TODO: It's your job to implement this class.
    //  Please do not change the signature of the given constructor and methods and ensure to implement them.
    //  You can add additional methods, fields, and constructors if needed.

    /**
     * Creates a new connection gene with the given source and target neuron, weight, enabled flag, and innovation number.
     *
     * @param sourceNeuronGene The source neuron of the connection.
     * @param targetNeuronGene The target neuron of the connection.
     * @param weight           The weight of the connection.
     * @param enabled          Whether the connection is enabled.
     * @param innovationNumber The innovation number of the connection serving as identifier.
     */
    public ConnectionGene(NeuronGene sourceNeuronGene, NeuronGene targetNeuronGene, double weight, boolean enabled, int innovationNumber) {
        throw new UnsupportedOperationException("Implement me!");
    }

    public NeuronGene getSourceNeuron() {
        throw new UnsupportedOperationException("Implement me!");
    }

    public NeuronGene getTargetNeuron() {
        throw new UnsupportedOperationException("Implement me!");
    }

    public double getWeight() {
        throw new UnsupportedOperationException("Implement me!");
    }

    public boolean getEnabled() {
        throw new UnsupportedOperationException("Implement me!");
    }

    public int getInnovationNumber() {
        throw new UnsupportedOperationException("Implement me!");
    }
}
