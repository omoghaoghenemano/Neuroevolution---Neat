package de.uni_passau.fim.se2.sbse.neat.chromosome;

import de.uni_passau.fim.se2.sbse.neat.utils.Randomness;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assert_;

class NetworkGeneratorTest {

    private void assertLayers(NetworkChromosome chromosome, int inputSize, int outputSize) {
        Map<Double, List<NeuronGene>> layers = chromosome.getLayers();
        List<NeuronGene> inputLayer = layers.get(NetworkChromosome.INPUT_LAYER);
        List<NeuronGene> outputLayer = layers.get(NetworkChromosome.OUTPUT_LAYER);

        assert_().withMessage("Input layer should contain the specified number of input NeuronGenes and a bias NeuronGene.")
                .that(inputLayer)
                .hasSize(inputSize + 1);

        assert_().withMessage("Output layer should contain the specified number of output NeuronGenes.")
                .that(outputLayer)
                .hasSize(outputSize);
    }

    private void assertConnections(NetworkChromosome chromosome, int inputSize, int outputSize) {
        List<ConnectionGene> connections = chromosome.getConnections();

        assert_()
                .withMessage("The generated chromosome must be a fully connected network.")
                .that(connections)
                .hasSize((inputSize + 1) * outputSize);

        Map<Double, List<NeuronGene>> layers = chromosome.getLayers();
        List<NeuronGene> inputLayer = layers.get(NetworkChromosome.INPUT_LAYER);
        List<NeuronGene> outputLayer = layers.get(NetworkChromosome.OUTPUT_LAYER);

        for (NeuronGene inputNeuronGene : inputLayer) {
            for (NeuronGene outputNeuronGene : outputLayer) {
                long count = connections.stream()
                        .filter(connection -> connection.getSourceNeuron() == inputNeuronGene
                                && connection.getTargetNeuron() == outputNeuronGene)
                        .count();

                assert_()
                        .withMessage("There should be exactly one connection between each input and output NeuronGene.")
                        .that(count)
                        .isEqualTo(1);
            }
        }

        connections.forEach(connection -> {
            assert_()
                    .withMessage("All connections should be enabled by default.")
                    .that(connection.getEnabled())
                    .isTrue();

            assert_()
                    .withMessage("All connections should have a random weight between -1 and 1.")
                    .that(connection.getWeight())
                    .isWithin(1).of(0);
        });

        List<Integer> innovations = connections.stream().map(ConnectionGene::getInnovationNumber).toList();
        assert_()
                .withMessage("All connections should have a unique innovation number.")
                .that(innovations)
                .containsNoDuplicates();
    }

    @Test
    void test_generate_OneNetwork() {
        int inputSize = 3;
        int outputSize = 2;
        NetworkGenerator generator = new NetworkGenerator(new HashSet<>(), inputSize, outputSize, Randomness.random());
        NetworkChromosome chromosome = generator.generate();

        assertLayers(chromosome, inputSize, outputSize);
        assertConnections(chromosome, inputSize, outputSize);
    }

    @Test
    void test_generate_TwoNetworks() {
        int inputSize = 5;
        int outputSize = 3;
        NetworkGenerator generator = new NetworkGenerator(new HashSet<>(), inputSize, outputSize, Randomness.random());
        NetworkChromosome chromosome1 = generator.generate();
        NetworkChromosome chromosome2 = generator.generate();

        assertLayers(chromosome1, inputSize, outputSize);
        assertConnections(chromosome1, inputSize, outputSize);

        assertLayers(chromosome2, inputSize, outputSize);
        assertConnections(chromosome2, inputSize, outputSize);

        assert_()
                .withMessage("Both networks must have the same innovation numbers.")
                .that(chromosome1.getConnections().stream().map(ConnectionGene::getInnovationNumber).toList())
                .isEqualTo(chromosome2.getConnections().stream().map(ConnectionGene::getInnovationNumber).toList());
    }

}