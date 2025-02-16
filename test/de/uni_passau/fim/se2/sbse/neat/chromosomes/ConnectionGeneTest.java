package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionGeneTest {

    private NeuronGene sourceNeuron;
    private NeuronGene targetNeuron;
    private ConnectionGene connectionGene;

    @BeforeEach
    void setUp() {
        sourceNeuron = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        targetNeuron = new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT);
        connectionGene = new ConnectionGene(sourceNeuron, targetNeuron, 1.0, true, 1);
    }

    @Test
    void testGetSourceNeuron() {
        assertEquals(sourceNeuron, connectionGene.getSourceNeuron());
    }

    @Test
    void testGetTargetNeuron() {
        assertEquals(targetNeuron, connectionGene.getTargetNeuron());
    }

    @Test
    void testGetWeight() {
        assertEquals(1.0, connectionGene.getWeight());
    }

    @Test
    void testGetEnabled() {
        assertTrue(connectionGene.getEnabled());
    }

    @Test
    void testGetInnovationNumber() {
        assertEquals(1, connectionGene.getInnovationNumber());
    }

    @Test
    void testDisabledConnection() {
        ConnectionGene disabledConnection = new ConnectionGene(sourceNeuron, targetNeuron, 1.0, false, 2);
        assertFalse(disabledConnection.getEnabled());
    }

    @Test
    void testDifferentWeight() {
        ConnectionGene differentWeightConnection = new ConnectionGene(sourceNeuron, targetNeuron, 2.0, true, 3);
        assertEquals(2.0, differentWeightConnection.getWeight());
    }

    @Test
    void testDifferentInnovationNumber() {
        ConnectionGene differentInnovationConnection = new ConnectionGene(sourceNeuron, targetNeuron, 1.0, true, 4);
        assertEquals(4, differentInnovationConnection.getInnovationNumber());
    }
}
