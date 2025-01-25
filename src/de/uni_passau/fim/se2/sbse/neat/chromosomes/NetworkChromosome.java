package de.uni_passau.fim.se2.sbse.neat.chromosomes;


import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Represents a network chromosome in the NEAT algorithm.
 */
public class NetworkChromosome implements Agent {

    // TODO: It's your job to implement this class.
    //  Please do not change the signature of the given constructor and methods and ensure to implement them.
    //  You can add additional methods, fields, and constructors if needed.

    public static final double INPUT_LAYER = 0;
    public static final double OUTPUT_LAYER = 1;

    /**
     * Maps the layer number to a list of neurons in that layer, with zero representing the input layer and one the output layer.
     * All hidden layers between the input and output layer are represented by values between zero and one.
     * For instance, if a new neuron gets added between the input and output layer, it might get the layer number 0.5.
     */
    private final Map<Double, List<NeuronGene>> layers;

    /**
     * Hosts all connections of the network.
     */
    private final List<ConnectionGene> connections;

    /**
     * Creates a new network chromosome with the given layers and connections.
     *
     * @param layers      The layers of the network.
     * @param connections The connections of the network.
     */
    public NetworkChromosome(Map<Double, List<NeuronGene>> layers, List<ConnectionGene> connections) {
        this.layers = requireNonNull(layers);
        this.connections = requireNonNull(connections);
    }

    public Map<Double, List<NeuronGene>> getLayers() {
        return layers;
    }

    public List<ConnectionGene> getConnections() {
        return connections;
    }

    @Override
    public List<Double> getOutput(List<Double> state) {
        throw new UnsupportedOperationException("Implement me!");
    }

    @Override
    public void setFitness(double fitness) {
        throw new UnsupportedOperationException("Implement me!");
    }

    @Override
    public double getFitness() {
        throw new UnsupportedOperationException("Implement me!");
    }
}
