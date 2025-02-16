package de.uni_passau.fim.se2.sbse.neat.chromosomes;


import java.util.List;
import java.util.Map;
import java.util.*;

import static java.util.Objects.requireNonNull;

import java.util.Collections;

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

    private double fitnessValue = 0.0;
    private double  fitnessadjustmentValue;

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
        Map<Double, List<NeuronGene>> clonedLayers = new HashMap<>();
        for (Map.Entry<Double, List<NeuronGene>> entry : layers.entrySet()) {
            clonedLayers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return clonedLayers;
    }
    

    @Override
    public List<Double> getOutput(List<Double> state) {
        int inputedNeuron = (int) layers.get(INPUT_LAYER).stream()
        .filter(n -> n.getNeuronType() == NeuronType.INPUT)
        .count();
        if (state == null || state.size() != inputedNeuron) {
            throw new IllegalArgumentException("Invalid input state size. Expected: " + inputedNeuron + ", Got: " +
                    (state == null ? "null" : state.size()));
        }
        Map<NeuronGene, Double> valueNeuron = new HashMap<>();
        int inputIndex = 0;
        for (NeuronGene neuron : layers.get(INPUT_LAYER)) {
            if (neuron.getNeuronType() == NeuronType.INPUT) {
                valueNeuron.put(neuron, state.get(inputIndex++));
            } else if (neuron.getNeuronType() == NeuronType.BIAS) {
                valueNeuron.put(neuron, 1.0);
            }
        }
        List<Double> sortedLayers = new ArrayList<>(layers.keySet());
        Collections.sort(sortedLayers);

        for (double layerDepth : sortedLayers) {
            if (layerDepth == INPUT_LAYER) continue;
            for (NeuronGene neuron : layers.get(layerDepth)) {
                double sum =  connections.stream()
                .filter(conn -> conn.getEnabled() && conn.getTargetNeuron().equals(neuron))
                .mapToDouble(conn -> valueNeuron.getOrDefault(conn.getSourceNeuron(), 0.0) * conn.getWeight())
                .sum();
                valueNeuron.put(neuron, neuron.activate(sum));
            }
        }
        return layers.get(OUTPUT_LAYER).stream()
        .map(valueNeuron::get)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    
    public double getFitnessadjustmentValue() {
        return fitnessadjustmentValue;
    }

  
    @Override
    public void setFitness(double fitness) {
        this.fitnessValue = fitness;
    }

    @Override
    public double getFitness() {
        return fitnessValue;
    }

    public void setFitnessadjustmentValue(double fitnessadjustmentValue) {
        this.fitnessadjustmentValue = fitnessadjustmentValue;
    }

    public List<ConnectionGene> getConnections() {
        return connections;
    }

    
}

