package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NetworkChromosomeTest {

    private NetworkChromosome networkChromosome;
    private NeuronGene inputNeuron;
    private NeuronGene outputNeuron;
    private NeuronGene biasNeuron;
    private ConnectionGene connection;

    @BeforeEach
    void setUp() {
        inputNeuron = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        outputNeuron = new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        biasNeuron = new NeuronGene(3, ActivationFunction.SIGMOID, NeuronType.BIAS);

        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        layers.put(NetworkChromosome.INPUT_LAYER, Arrays.asList(inputNeuron, biasNeuron));
        layers.put(NetworkChromosome.OUTPUT_LAYER, Collections.singletonList(outputNeuron));

        connection = new ConnectionGene(inputNeuron, outputNeuron, 1.0, true, 1);
        List<ConnectionGene> connections = Collections.singletonList(connection);

        networkChromosome = new NetworkChromosome(layers, connections);
    }

    @Test
    void testGetLayers() {
        Map<Double, List<NeuronGene>> layers = networkChromosome.getLayers();
        assertEquals(2, layers.size());
        assertTrue(layers.containsKey(NetworkChromosome.INPUT_LAYER));
        assertTrue(layers.containsKey(NetworkChromosome.OUTPUT_LAYER));
    }

    @Test
    void testGetConnections() {
        List<ConnectionGene> connections = networkChromosome.getConnections();
        assertEquals(1, connections.size());
        assertEquals(connection, connections.get(0));
    }

    @Test
    void testGetOutput() {
        List<Double> inputState = Collections.singletonList(1.0);
        List<Double> output = networkChromosome.getOutput(inputState);
        assertEquals(1, output.size());
        assertNotNull(output.get(0));
    }

    @Test
    void testGetOutputWithBias() {
        List<Double> inputState = Collections.singletonList(1.0);
        List<Double> output = networkChromosome.getOutput(inputState);
        assertEquals(1, output.size());
        assertNotNull(output.get(0));
    }

    @Test
    void testGetOutputInvalidStateSize() {
        List<Double> inputState = Collections.emptyList();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            networkChromosome.getOutput(inputState);
        });
        assertEquals("Invalid input state size. Expected: 1, Got: 0", exception.getMessage());
    }

    @Test
    void testSetAndGetFitness() {
        networkChromosome.setFitness(10.0);
        assertEquals(10.0, networkChromosome.getFitness());
    }

    @Test
    void testSetAndGetFitnessAdjustmentValue() {
        networkChromosome.setFitnessadjustmentValue(5.0);
        assertEquals(5.0, networkChromosome.getFitnessadjustmentValue());
    }
}