package de.uni_passau.fim.se2.sbse.neat.crossover;

import de.uni_passau.fim.se2.sbse.neat.chromosome.ConnectionGene;
import de.uni_passau.fim.se2.sbse.neat.chromosome.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.chromosome.NetworkGenerator;
import de.uni_passau.fim.se2.sbse.neat.chromosome.NeuronGene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static com.google.common.truth.Truth.assert_;

public class NeatCrossoverTest {

    private NeatCrossover neatCrossover;
    private NetworkChromosome parent1;
    private NetworkChromosome parent2;

    @BeforeEach
    public void setUp() {
        Random random = new Random();
        neatCrossover = new NeatCrossover(random);

        int inputNodes = 10;
        int outputNodes = 5;
        NetworkGenerator generator = new NetworkGenerator(new HashSet<>(), inputNodes, outputNodes, random);
        parent1 = generator.generate();
        parent2 = generator.generate();
    }

    @Test
    void test_apply_fitterParentOne() {
        parent1.setFitness(1.0);
        parent1.getConnections().removeFirst();
        parent2.setFitness(0.5);

        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        validateCrossoverChild(child, parent1, parent2);
    }

    @Test
    void test_apply_fitterParentTwo() {
        parent1.setFitness(0.5);
        parent2.setFitness(1.0);
        parent2.getConnections().removeLast();
        NetworkChromosome child = neatCrossover.apply(parent1, parent2);

        validateCrossoverChild(child, parent2, parent1);
    }

    private void validateCrossoverChild(NetworkChromosome child, NetworkChromosome fitterParent, NetworkChromosome lessFitterParent) {
        assert_()
                .withMessage("The child should not be the same instance as the fitter parent.")
                .that(child)
                .isNotSameInstanceAs(fitterParent);

        assert_()
                .withMessage("The child should not be the same instance as the less fit parent.")
                .that(child)
                .isNotSameInstanceAs(lessFitterParent);

        List<NeuronGene> childNeurons = child.getLayers().values().stream().flatMap(List::stream).toList();
        List<NeuronGene> parentNeurons = fitterParent.getLayers().values().stream().flatMap(List::stream).toList();
        assert_()
                .withMessage("The child should have the same number of neurons as the fitter parent.")
                .that(childNeurons.size())
                .isEqualTo(parentNeurons.size());

        assert_()
                .withMessage("The child should have the same number of connections as the fitter parent.")
                .that(child.getConnections().size())
                .isEqualTo(fitterParent.getConnections().size());

        assert_()
                .withMessage("The connection weights of the child should differ from the fitter parent.")
                .that(child.getConnections().stream().mapToDouble(ConnectionGene::getWeight).toArray())
                .isNotEqualTo(fitterParent.getConnections().stream().mapToDouble(ConnectionGene::getWeight).toArray());
    }
}
