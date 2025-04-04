package de.uni_passau.fim.se2.sbse.neat.mutation;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NeatMutationTest {

    private NeatMutation neatMutation;
    private Random random;
    private Set<Innovation> innovations;
    private NetworkChromosome parent;

    @BeforeEach
    void setUp() {
        random = mock(Random.class);
        innovations = new HashSet<>();
        neatMutation = new NeatMutation(innovations, random);

        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        layers.put(NetworkChromosome.INPUT_LAYER, List.of(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT)));
        layers.put(NetworkChromosome.OUTPUT_LAYER, List.of(new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT)));

        List<ConnectionGene> connections = List.of(new ConnectionGene(
                new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT),
                new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT),
                1.0,
                true,
                1
        ));

        parent = new NetworkChromosome(layers, connections);
    }

    @Test
    void testAddNeuron() {
        when(random.nextDouble()).thenReturn(0.02); // Trigger addNeuron mutation
        NetworkChromosome result = neatMutation.apply(parent);
        assertEquals(3, result.getConnections().size());
        assertEquals(3, result.getLayers().values().stream().mapToInt(List::size).sum());
    }

    @Test
    void testAddConnection() {
        when(random.nextDouble()).thenReturn(0.06, 0.04); // Trigger addConnection mutation
        NetworkChromosome result = neatMutation.apply(parent);
        assertEquals(1, result.getConnections().size());
    }

    @Test
    void testMutateWeights() {
        when(random.nextDouble()).thenReturn(0.5); // Trigger mutateWeights mutation
        when(random.nextGaussian()).thenReturn(0.1);
        NetworkChromosome result = neatMutation.apply(parent);
        assertNotEquals(1.0, result.getConnections().get(0).getWeight());
    }

    @Test
    void testToggleConnection() {
        when(random.nextDouble()).thenReturn(0.9, 0.05); // Trigger toggleConnection mutation
        NetworkChromosome result = neatMutation.apply(parent);
        assertFalse(result.getConnections().get(0).getEnabled());
    }

    @Test
    void testAddNeuronNoConnections() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        layers.put(NetworkChromosome.INPUT_LAYER, List.of(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT)));
        layers.put(NetworkChromosome.OUTPUT_LAYER, List.of(new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT)));
        NetworkChromosome parentNoConnections = new NetworkChromosome(layers, new ArrayList<>());

        when(random.nextDouble()).thenReturn(0.02); // Trigger addNeuron mutation
        NetworkChromosome result = neatMutation.apply(parentNoConnections);
        assertEquals(0, result.getConnections().size());
    }

    @Test
    void testAddConnectionNoValidPairs() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        layers.put(NetworkChromosome.INPUT_LAYER, List.of(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT)));
        layers.put(NetworkChromosome.OUTPUT_LAYER, List.of(new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT)));
        NetworkChromosome parentNoValidPairs = new NetworkChromosome(layers, new ArrayList<>());

        when(random.nextDouble()).thenReturn(0.06); // Trigger addConnection mutation
        NetworkChromosome result = neatMutation.apply(parentNoValidPairs);
        assertEquals(0, result.getConnections().size());
    }

    @Test
    void testMutateWeightsNoConnections() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        layers.put(NetworkChromosome.INPUT_LAYER, List.of(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT)));
        layers.put(NetworkChromosome.OUTPUT_LAYER, List.of(new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT)));
        NetworkChromosome parentNoConnections = new NetworkChromosome(layers, new ArrayList<>());

        when(random.nextDouble()).thenReturn(0.5); // Trigger mutateWeights mutation
        NetworkChromosome result = neatMutation.apply(parentNoConnections);
        assertEquals(0, result.getConnections().size());
    }

    @Test
    void testToggleConnectionNoConnections() {
        Map<Double, List<NeuronGene>> layers = new HashMap<>();
        layers.put(NetworkChromosome.INPUT_LAYER, List.of(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT)));
        layers.put(NetworkChromosome.OUTPUT_LAYER, List.of(new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT)));
        NetworkChromosome parentNoConnections = new NetworkChromosome(layers, new ArrayList<>());

        when(random.nextDouble()).thenReturn(0.9, 0.05); // Trigger toggleConnection mutation
        NetworkChromosome result = neatMutation.apply(parentNoConnections);
        assertEquals(0, result.getConnections().size());
    }

    @Test
    void testCalculateDepth() {
        NeuronGene neuron = new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT);
        double depth = neatMutation.calculateDepth(parent, neuron);
        assertEquals(NetworkChromosome.INPUT_LAYER, depth);
    }

    @Test
    void testConstructorWithNullValues() {
        assertThrows(NullPointerException.class, () -> new NeatMutation(null, random));
        assertThrows(NullPointerException.class, () -> new NeatMutation(innovations, null));
    }
    @Test
    void testAddNeuronWithDisabledConnections() {
        List<ConnectionGene> disabledConnections = List.of(new ConnectionGene(
                new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT),
                new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT),
                1.0,
                false,
                1
        ));
        NetworkChromosome parentWithDisabled = new NetworkChromosome(parent.getLayers(), disabledConnections);

        when(random.nextDouble()).thenReturn(0.02);
        NetworkChromosome result = neatMutation.apply(parentWithDisabled);

        assertEquals(disabledConnections.size(), result.getConnections().size());
    }
    @Test
    void testAddConnectionWhenNoValidPairs() {
        when(random.nextDouble()).thenReturn(0.06);
        NetworkChromosome result = neatMutation.addConnection(parent);

        assertEquals(parent.getConnections().size(), result.getConnections().size());
    }
    @Test
    void testMutateWeightsBoundaries() {
        when(random.nextDouble()).thenReturn(0.5);
        when(random.nextGaussian()).thenReturn(100.0); // Extreme mutation

        NetworkChromosome result = neatMutation.apply(parent);
        assertTrue(result.getConnections().stream()
                .allMatch(c -> c.getWeight() >= -2.0 && c.getWeight() <= 2.0));
    }
    @Test
    void testToggleMultipleConnections() {
        when(random.nextDouble()).thenReturn(0.9, 0.05);
        List<ConnectionGene> multipleConnections = Arrays.asList(
                new ConnectionGene(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT),
                        new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT), 1.0, true, 1),
                new ConnectionGene(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT),
                        new NeuronGene(3, ActivationFunction.SIGMOID, NeuronType.OUTPUT), 1.0, true, 2)
        );
        NetworkChromosome parentWithMultiple = new NetworkChromosome(parent.getLayers(), multipleConnections);

        NetworkChromosome result = neatMutation.apply(parentWithMultiple);
        long disabledCount = result.getConnections().stream().filter(c -> !c.getEnabled()).count();
        assertEquals(1, disabledCount);
    }
    @Test
    void testCalculateDepthNoConnections() {
        NeuronGene hiddenNeuron = new NeuronGene(3, ActivationFunction.SIGMOID, NeuronType.HIDDEN);
        double depth = neatMutation.calculateDepth(parent, hiddenNeuron);
        assertEquals((NetworkChromosome.INPUT_LAYER + NetworkChromosome.OUTPUT_LAYER) / 2.0, depth);
    }

}
