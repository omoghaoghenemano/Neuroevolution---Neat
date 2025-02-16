package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NeuronGeneTest {

    private NeuronGene neuronGeneSigmoid;
    private NeuronGene neuronGeneTanh;
    private NeuronGene neuronGeneNone;

    @BeforeEach
    void setUp() {
        neuronGeneSigmoid = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        neuronGeneTanh = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        neuronGeneNone = new NeuronGene(3, ActivationFunction.NONE, NeuronType.HIDDEN);
    }

    @Test
    void testGetId() {
        assertEquals(1, neuronGeneSigmoid.getId());
        assertEquals(2, neuronGeneTanh.getId());
        assertEquals(3, neuronGeneNone.getId());
    }

    @Test
    void testGetNeuronType() {
        assertEquals(NeuronType.INPUT, neuronGeneSigmoid.getNeuronType());
        assertEquals(NeuronType.OUTPUT, neuronGeneTanh.getNeuronType());
        assertEquals(NeuronType.HIDDEN, neuronGeneNone.getNeuronType());
    }

    @Test
    void testActivateSigmoid() {
        double input = 0.5;
        double expectedOutput = 1.0 / (1.0 + Math.exp(-input));
        assertEquals(expectedOutput, neuronGeneSigmoid.activate(input), 1e-6);
    }

    @Test
    void testActivateTanh() {
        double input = 0.5;
        double expectedOutput = Math.tanh(input);
        assertEquals(expectedOutput, neuronGeneTanh.activate(input), 1e-6);
    }

    @Test
    void testActivateNone() {
        double input = 0.5;
        assertEquals(input, neuronGeneNone.activate(input), 1e-6);
    }

    @Test
    void testEquals() {
        NeuronGene sameNeuronGene = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        NeuronGene differentNeuronGene = new NeuronGene(2, ActivationFunction.TANH, NeuronType.OUTPUT);
        assertTrue(neuronGeneSigmoid.equals(sameNeuronGene));
        assertFalse(neuronGeneSigmoid.equals(differentNeuronGene));
        assertFalse(neuronGeneSigmoid.equals(null));
        assertFalse(neuronGeneSigmoid.equals(new Object()));
    }

    @Test
    void testHashCode() {
        assertEquals(Integer.hashCode(1), neuronGeneSigmoid.hashCode());
    }

    @Test
    void testSetAndGetParam() {
        neuronGeneSigmoid.setParam(0.5);
        assertEquals(0.5, neuronGeneSigmoid.getparam(), 1e-6);
    }
}
