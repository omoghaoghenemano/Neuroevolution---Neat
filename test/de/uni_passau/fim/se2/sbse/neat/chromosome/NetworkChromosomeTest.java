package de.uni_passau.fim.se2.sbse.neat.chromosome;

import org.junit.jupiter.api.Test;

import java.util.*;

import static com.google.common.truth.Truth.assert_;

class NetworkChromosomeTest {

    @Test
    void test_activateWithValidInputs() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();

        List<NeuronGene> inputLayer = new ArrayList<>();
        inputLayer.add(new NeuronGene(0, ActivationFunction.NONE, NeuronType.INPUT));
        inputLayer.add(new NeuronGene(1, ActivationFunction.NONE, NeuronType.INPUT));
        inputLayer.add(new NeuronGene(2, ActivationFunction.NONE, NeuronType.BIAS));
        layers.put(NetworkChromosome.INPUT_LAYER, inputLayer);

        List<NeuronGene> hiddenLayer = new ArrayList<>();
        hiddenLayer.add(new NeuronGene(3, ActivationFunction.SIGMOID, NeuronType.HIDDEN));
        hiddenLayer.add(new NeuronGene(4, ActivationFunction.SIGMOID, NeuronType.HIDDEN));
        layers.put(0.5, hiddenLayer);

        List<NeuronGene> outputLayer = new ArrayList<>();
        outputLayer.add(new NeuronGene(5, ActivationFunction.SIGMOID, NeuronType.OUTPUT));
        layers.put(NetworkChromosome.OUTPUT_LAYER, outputLayer);

        List<ConnectionGene> connections = new ArrayList<>();
        connections.add(new ConnectionGene(inputLayer.getFirst(), hiddenLayer.getFirst(), -0.75, true, 1));
        connections.add(new ConnectionGene(inputLayer.getFirst(), hiddenLayer.getLast(), 0.2, true, 2));
        connections.add(new ConnectionGene(inputLayer.get(1), hiddenLayer.getFirst(), 0.25, true, 3));
        connections.add(new ConnectionGene(inputLayer.get(1), hiddenLayer.getLast(), -0.8, true, 4));
        connections.add(new ConnectionGene(inputLayer.getLast(), hiddenLayer.getFirst(), -0.5, true, 5));
        connections.add(new ConnectionGene(inputLayer.getLast(), hiddenLayer.getLast(), 0.1, true, 6));
        connections.add(new ConnectionGene(hiddenLayer.getFirst(), outputLayer.getFirst(), 0.4, true, 7));
        connections.add(new ConnectionGene(hiddenLayer.getLast(), outputLayer.getFirst(), -0.2, true, 8));

        NetworkChromosome network = new NetworkChromosome(layers, connections);
        List<Double> output = network.getOutput(List.of(2.0, 3.0));

        assert_()
                .withMessage("Given only one output NeuronGene, there must only be one output value.")
                .that(output)
                .hasSize(1);

        assert_()
                .withMessage("The output value of the network is incorrect." +
                        "The tested network consists of 2 input neurons, 1 bias neuron, 2 hidden neurons, and 1 output neuron." +
                        "The hidden and output layer use a sigmoid activation function.")
                .that(output.getFirst())
                .isWithin(0.01).of(0.52);
    }

}
