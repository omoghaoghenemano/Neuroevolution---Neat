package de.uni_passau.fim.se2.sbse.neat.mutation;

import de.uni_passau.fim.se2.sbse.neat.algorithm.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.chromosome.ConnectionGene;
import de.uni_passau.fim.se2.sbse.neat.chromosome.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.chromosome.NetworkGenerator;
import de.uni_passau.fim.se2.sbse.neat.chromosome.NeuronGene;
import de.uni_passau.fim.se2.sbse.neat.utils.Hyperparameter;
import de.uni_passau.fim.se2.sbse.neat.utils.Randomness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.when;

public class NeatMutationTest {

    private NeatMutation mutation;
    private NetworkChromosome parent;
    private Random randomMock;

    @BeforeEach
    void setUp() {
        randomMock = Mockito.mock(Random.class);
        Set<Innovation> innovations = new HashSet<>();
        mutation = new NeatMutation(innovations, randomMock);

        int inputNodes = 5;
        int outputNodes = 3;
        NetworkGenerator generator = new NetworkGenerator(innovations, inputNodes, outputNodes, Randomness.random());
        parent = generator.generate();
    }

    @Test
    void test_addNeuron() {
        when(randomMock.nextDouble()).thenReturn(
                Hyperparameter.ADD_NEURON_PROBABILITY - 0.01
        );

        NetworkChromosome child = mutation.apply(parent);

        assert_()
                .withMessage("The split connection should be disabled.")
                .that(parent.getConnections().stream().filter(ConnectionGene::getEnabled).count())
                .isEqualTo(child.getConnections().stream().filter(ConnectionGene::getEnabled).count() - 1);

        assert_()
                .withMessage("The parent and child chromosomes should not be the same instance.")
                .that(child)
                .isNotSameInstanceAs(parent);

        List<NeuronGene> childNeurons = child.getLayers().values().stream().flatMap(List::stream).toList();
        List<NeuronGene> parentNeurons = parent.getLayers().values().stream().flatMap(List::stream).toList();
        assert_()
                .withMessage("The add neuron mutation should increase the number of neurons by one.")
                .that(childNeurons.size())
                .isEqualTo(parentNeurons.size() + 1);

        assert_()
                .withMessage("The add neuron mutation should increase the number of connections by two.")
                .that(child.getConnections().size())
                .isEqualTo(parent.getConnections().size() + 2);

        assert_()
                .withMessage("Every neuron must have a unique id.")
                .that(childNeurons.stream().map(NeuronGene::getId))
                .containsNoDuplicates();

        assert_()
                .withMessage("Every connection must have a unique innovation number.")
                .that(child.getConnections().stream().map(ConnectionGene::getInnovationNumber))
                .containsNoDuplicates();
    }

    @Test
    void test_addConnection_FullyConnected() {
        when(randomMock.nextDouble()).thenReturn(
                Hyperparameter.ADD_NEURON_PROBABILITY + 0.01,
                Hyperparameter.ADD_CONNECTION_PROBABILITY - 0.01
        );

        NetworkChromosome child = mutation.apply(parent);

        assert_()
                .withMessage("The parent and child chromosomes should not be the same instance.")
                .that(child)
                .isNotSameInstanceAs(parent);

        assert_()
                .withMessage("The add connection mutation is not allowed to add duplicate connections.")
                .that(child.getConnections().size())
                .isEqualTo(parent.getConnections().size());

        List<NeuronGene> childNeurons = child.getLayers().values().stream().flatMap(List::stream).toList();
        List<NeuronGene> parentNeurons = parent.getLayers().values().stream().flatMap(List::stream).toList();
        assert_()
                .withMessage("The number of neurons should not change.")
                .that(childNeurons.size())
                .isEqualTo(parentNeurons.size());

        assert_()
                .withMessage("Every connection must have a unique innovation number.")
                .that(child.getConnections().stream().map(ConnectionGene::getInnovationNumber))
                .containsNoDuplicates();
    }

    @Test
    void test_addConnection_NotFullyConnected() {
        when(randomMock.nextDouble()).thenReturn(Hyperparameter.ADD_NEURON_PROBABILITY - 0.01);
        NetworkChromosome addNeuronChild = mutation.apply(parent);

        when(randomMock.nextDouble()).thenReturn(
                Hyperparameter.ADD_NEURON_PROBABILITY + 0.01,
                Hyperparameter.ADD_CONNECTION_PROBABILITY - 0.01
        );
        when(randomMock.nextInt(Mockito.anyInt())).thenAnswer((Answer<Integer>) invocation -> {
            int bound = invocation.getArgument(0); // Get the bound passed to nextInt()
            return new Random().nextInt(bound); // Use a real Random instance to return a random value
        });
        NetworkChromosome child = mutation.apply(addNeuronChild);

        assert_()
                .withMessage("The parent and child chromosomes should not be the same instance.")
                .that(child)
                .isNotSameInstanceAs(parent);

        assert_()
                .withMessage("The add connection mutation should increase the number of connections by one.")
                .that(child.getConnections().size())
                .isEqualTo(addNeuronChild.getConnections().size() + 1);

        assert_()
                .withMessage("Every connection must have a unique innovation number.")
                .that(child.getConnections().stream().map(ConnectionGene::getInnovationNumber))
                .containsNoDuplicates();
    }

    @Test
    void test_mutateWeights() {
        when(randomMock.nextDouble()).thenReturn(
                Hyperparameter.ADD_NEURON_PROBABILITY + 0.01,
                Hyperparameter.ADD_CONNECTION_PROBABILITY + 0.01,
                Hyperparameter.WEIGHT_MUTATION_PROBABILITY - 0.01
        );
        when(randomMock.nextGaussian()).thenReturn(Randomness.random().nextGaussian());

        NetworkChromosome child = mutation.apply(parent);

        assert_()
                .withMessage("The parent and child chromosomes should not be the same instance.")
                .that(child)
                .isNotSameInstanceAs(parent);

        assert_()
                .withMessage("The mutated child should have different connection weights than the parent.")
                .that(child.getConnections().stream().map(ConnectionGene::getWeight).toList())
                .containsNoneIn(parent.getConnections().stream().map(ConnectionGene::getWeight).toList());

        assert_()
                .withMessage("Non-structural mutations do not modify the innovation numbers.")
                .that(child.getConnections().stream().map(ConnectionGene::getInnovationNumber).toList())
                .containsExactlyElementsIn(parent.getConnections().stream().map(ConnectionGene::getInnovationNumber).toList());
    }

    @Test
    void test_toggleConnection() {
        when(randomMock.nextDouble()).thenReturn(
                Hyperparameter.ADD_NEURON_PROBABILITY + 0.01,
                Hyperparameter.ADD_CONNECTION_PROBABILITY + 0.01,
                Hyperparameter.TOGGLE_CONNECTION_PROBABILITY - 0.01
        );
        when(randomMock.nextInt(Mockito.anyInt())).thenAnswer((Answer<Integer>) invocation -> {
            int bound = invocation.getArgument(0); // Get the bound passed to nextInt()
            return new Random().nextInt(bound); // Use a real Random instance to return a random value
        });

        NetworkChromosome child = mutation.apply(parent);

        assert_()
                .withMessage("The parent and child chromosomes should not be the same instance.")
                .that(child)
                .isNotSameInstanceAs(parent);

        assert_()
                .withMessage("The number of enabled connections should have changed.")
                .that(child.getConnections().stream().filter(ConnectionGene::getEnabled).count())
                .isNotEqualTo(parent.getConnections().stream().filter(ConnectionGene::getEnabled).count());
    }


}
