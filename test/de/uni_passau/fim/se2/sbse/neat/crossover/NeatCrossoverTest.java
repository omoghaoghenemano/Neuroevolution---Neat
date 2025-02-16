package de.uni_passau.fim.se2.sbse.neat.crossover;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.ConnectionGene;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronGene;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronType;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.ActivationFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NeatCrossoverTest {

    private NeatCrossover neatCrossover;
    private Random random;
    private NetworkChromosome parent1;
    private NetworkChromosome parent2;

    @BeforeEach
    void setUp() {
        random = mock(Random.class);
        neatCrossover = new NeatCrossover(random);

        Map<Double, List<NeuronGene>> layers1 = new HashMap<>();
        layers1.put(NetworkChromosome.INPUT_LAYER, List.of(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT)));
        layers1.put(NetworkChromosome.OUTPUT_LAYER, List.of(new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT)));

        List<ConnectionGene> connections1 = List.of(new ConnectionGene(
                new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT),
                new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT),
                1.0,
                true,
                1
        ));

        parent1 = new NetworkChromosome(layers1, connections1);
        parent1.setFitness(1.0);

        Map<Double, List<NeuronGene>> layers2 = new HashMap<>();
        layers2.put(NetworkChromosome.INPUT_LAYER, List.of(new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT)));
        layers2.put(NetworkChromosome.OUTPUT_LAYER, List.of(new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT)));

        List<ConnectionGene> connections2 = List.of(new ConnectionGene(
                new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT),
                new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT),
                2.0,
                true,
                1
        ));

        parent2 = new NetworkChromosome(layers2, connections2);
        parent2.setFitness(2.0);
    }

    @Test
    void testApplyCrossover() {
        when(random.nextDouble()).thenReturn(0.5);

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        assertNotNull(child);
        assertEquals(1, child.getConnections().size());
        assertEquals(1.5, child.getConnections().get(0).getWeight());
    }

    @Test
    void testApplyCrossoverWithMutation() {
        when(random.nextDouble()).thenReturn(0.5, 0.1);
        when(random.nextGaussian()).thenReturn(0.1);

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        assertNotNull(child);
        assertEquals(1, child.getConnections().size());
        assertNotEquals(1.0, child.getConnections().get(0).getWeight());
    }

    @Test
    void testApplyCrossoverWithOnlyFirstParentGene() {
        when(random.nextDouble()).thenReturn(0.5, 0.1);
        when(random.nextGaussian()).thenReturn(0.1);

        parent2 = new NetworkChromosome(new HashMap<>(), new ArrayList<>());

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        assertNotNull(child);
        assertEquals(1, child.getConnections().size());
        assertNotEquals(1.0, child.getConnections().get(0).getWeight());
    }

    @Test
    void testApplyCrossoverWithOnlySecondParentGene() {
        when(random.nextDouble()).thenReturn(0.5, 0.1);
        when(random.nextGaussian()).thenReturn(0.1);

        parent1 = new NetworkChromosome(new HashMap<>(), new ArrayList<>());

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        assertNotNull(child);
        assertEquals(1, child.getConnections().size());
        assertNotEquals(2.0, child.getConnections().get(0).getWeight());
    }

    @Test
    void testApplyCrossoverWithNoMatchingGenes() {
        when(random.nextDouble()).thenReturn(0.5, 0.1);
        when(random.nextGaussian()).thenReturn(0.1);

        List<ConnectionGene> connections1 = List.of(new ConnectionGene(
                new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT),
                new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT),
                1.0,
                true,
                1
        ));

        List<ConnectionGene> connections2 = List.of(new ConnectionGene(
                new NeuronGene(1, ActivationFunction.SIGMOID, NeuronType.INPUT),
                new NeuronGene(2, ActivationFunction.SIGMOID, NeuronType.OUTPUT),
                2.0,
                true,
                2
        ));

        parent1 = new NetworkChromosome(parent1.getLayers(), connections1);
        parent2 = new NetworkChromosome(parent2.getLayers(), connections2);

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        assertNotNull(child);
        assertEquals(1, child.getConnections().size());
    }
}
