package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NetworkGeneratorTest {

    private NetworkGenerator networkGenerator;
    private Set<Innovation> innovations;
    private Random random;

    @BeforeEach
    void setUp() {
        innovations = new HashSet<>();
        random = mock(Random.class);
        networkGenerator = new NetworkGenerator(innovations, 3, 2, random);
    }

    @Test
    void testConstructorInitializesInnovations() {
        assertEquals(8, innovations.size()); // 4 input neurons (including bias) * 2 output neurons
    }

    @Test
    void testGetInnovationNumber() {
        int originId = 0;
        int goalId = 4;
        int innovationNumber = networkGenerator.getInnovationNumber(originId, goalId);
        assertEquals(19, innovationNumber);
    }

    @Test
    void testGetInnovationNumberThrowsException() {
        int originId = 10;
        int goalId = 20;
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            networkGenerator.getInnovationNumber(originId, goalId);
        });
        assertEquals("Innovation is not found", exception.getMessage());
    }

    @Test
    void testGenerate() {
        when(random.nextDouble()).thenReturn(0.5);

        NetworkChromosome networkChromosome = networkGenerator.generate();

        assertNotNull(networkChromosome);
        assertEquals(2, networkChromosome.getLayers().size());
        assertEquals(4, networkChromosome.getLayers().get(NetworkChromosome.INPUT_LAYER).size());
        assertEquals(2, networkChromosome.getLayers().get(NetworkChromosome.OUTPUT_LAYER).size());
        assertEquals(8, networkChromosome.getConnections().size());
    }

    @Test
    void testGenerateWithDifferentWeights() {
        when(random.nextDouble()).thenReturn(0.5, -0.5);

        NetworkChromosome networkChromosome = networkGenerator.generate();

        assertNotNull(networkChromosome);
        assertEquals(2, networkChromosome.getLayers().size());
        assertEquals(4, networkChromosome.getLayers().get(NetworkChromosome.INPUT_LAYER).size());
        assertEquals(2, networkChromosome.getLayers().get(NetworkChromosome.OUTPUT_LAYER).size());
        assertEquals(8, networkChromosome.getConnections().size());
        assertNotNull( networkChromosome.getConnections().get(0).getWeight());
     
    }
}
