package de.uni_passau.fim.se2.sbse.neat;

import de.uni_passau.fim.se2.sbse.neat.algorithms.Neuroevolution;
import de.uni_passau.fim.se2.sbse.neat.environments.SinglePoleBalancing;
import de.uni_passau.fim.se2.sbse.neat.environments.Tasks;
import de.uni_passau.fim.se2.sbse.neat.environments.XOR;
import de.uni_passau.fim.se2.sbse.neat.utils.Randomness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainTest {

    private Main main;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        main = new Main();
        System.setOut(new PrintStream(outContent));
    }





    @Test
    void testInitialiseTaskCartPoleRandom() {
        main.task = Tasks.CARTPOLE_RANDOM;
        assertTrue(main.initialiseTask() instanceof SinglePoleBalancing);
    }

   


    @Test
    void testNeatAlgorithm() {
        Neuroevolution neat = Main.initialiseNeat(5, 10);
        assertNotNull(neat);
    }


    @Test
    void testTask() {
        TaskConverter converter = new TaskConverter();
        assertThrows(IllegalArgumentException.class, () -> converter.convert("INVALID"));
        assertEquals(Tasks.CARTPOLE_RANDOM, converter.convert("CART_RANDOM"));
        assertEquals(Tasks.CARTPOLE, converter.convert("CART"));
        assertEquals(Tasks.XOR, converter.convert("XOR"));
    }

    @Test
    void testXOR() {
        String[] args = {"-t", "XOR", "-p", "10", "-g", "5", "-r", "2"};
        int exitCode = new CommandLine(main).execute(args);
        assertEquals(0, exitCode);
        assertTrue(outContent.toString().contains("Analysing task 'XOR'"));
    }



    @Test
    void testSeed() {
        String[] args = {"-t", "XOR", "-s", "42", "-p", "5", "-g", "3", "-r", "1"};
        assertTrue(main.times.isEmpty());
    }

    @Test
    void testPrintResults() {
        main.successes.add(true);
        main.generations.add(10);
        main.times.add(1000L);
        main.printResults();
    }

}