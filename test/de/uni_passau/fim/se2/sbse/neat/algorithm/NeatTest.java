package de.uni_passau.fim.se2.sbse.neat.algorithm;

import de.uni_passau.fim.se2.sbse.neat.Main;
import de.uni_passau.fim.se2.sbse.neat.chromosome.Agent;
import de.uni_passau.fim.se2.sbse.neat.environments.Environment;
import de.uni_passau.fim.se2.sbse.neat.environments.SinglePoleBalancing;
import de.uni_passau.fim.se2.sbse.neat.environments.XOR;
import de.uni_passau.fim.se2.sbse.neat.utils.Randomness;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static com.google.common.truth.Truth.assert_;

public class NeatTest {

    private final Random random = Randomness.random();
    private static final int POP_SIZE = 50;
    private static final int MAX_GENERATIONS = 50;

    @Test
    void test_Neat_XOR() {
        Environment environment = new XOR();
        Neuroevolution neat = Main.initialiseNeat(MAX_GENERATIONS, POP_SIZE);
        Agent network = neat.solve(environment);

        assert_()
                .withMessage("Neat should be able to solve the XOR environment within 50 generations.")
                .that(environment.solved(network))
                .isTrue();
    }

    @Test
    void test_Neat_singlePoleBalancing() {
        Environment environment = new SinglePoleBalancing(120000, 10, false, random);
        Neuroevolution neat = Main.initialiseNeat(MAX_GENERATIONS, POP_SIZE);
        Agent network = neat.solve(environment);

        assert_()
                .withMessage("Neat should be able to solve the Single Pole Balancing environment within 50 generations.")
                .that(environment.solved(network))
                .isTrue();
    }

    @Test
    void test_Neat_singlePoleBalancingRandomised() {
        Environment environment = new SinglePoleBalancing(120000, 10, true, random);
        Neuroevolution neat = Main.initialiseNeat(MAX_GENERATIONS, POP_SIZE);
        Agent network = neat.solve(environment);

        assert_()
                .withMessage("Neat should be able to solve the randomised Single Pole Balancing environment within 50 generations.")
                .that(environment.solved(network))
                .isTrue();
    }

}
