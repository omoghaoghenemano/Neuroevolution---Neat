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
    void testCallWithVisualisation() throws Exception {
        String[] args = {"-t", "XOR", "-v"};
        CommandLine cmd = new CommandLine(main);
        int exitCode = cmd.execute(args);
        assertEquals(0, exitCode);
    }

    @Test
    void testCallWithoutVisualisation() throws Exception {
        String[] args = {"-t", "XOR"};
        CommandLine cmd = new CommandLine(main);
        int exitCode = cmd.execute(args);
        assertEquals(0, exitCode);
    }

    @Test
    void testInitialiseTaskXOR() {
        main.task = Tasks.XOR;
        assertTrue(main.initialiseTask() instanceof XOR);
    }

    @Test
    void testInitialiseTaskCartPole() {
        main.task = Tasks.CARTPOLE;
        assertTrue(main.initialiseTask() instanceof SinglePoleBalancing);
    }

    @Test
    void testInitialiseTaskCartPoleRandom() {
        main.task = Tasks.CARTPOLE_RANDOM;
        assertTrue(main.initialiseTask() instanceof SinglePoleBalancing);
    }

    @Test
    void testSolveTask() {
        main.task = Tasks.XOR;
        main.solveTask();
        assertFalse(main.solutions.isEmpty());
        assertFalse(main.generations.isEmpty());
        assertFalse(main.successes.isEmpty());
        assertFalse(main.times.isEmpty());
    }

   

    @Test
    void testCallWithError() throws Exception {
        main = spy(new Main());
        doThrow(new InterruptedException()).when(main).call();
        String[] args = {"-t", "XOR", "-v"};
        CommandLine cmd = new CommandLine(main);
        int exitCode = cmd.execute(args);
        assertEquals(1, exitCode);
    }

    @Test
    void testNeatAlgorithm() {
        Neuroevolution neat = Main.initialiseNeat(5, 10);
        assertNotNull(neat);
    }

    @Test
    void testMainMethod() {
        String[] args = {"-t", "XOR"};
        Main.main(args);
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
    void testCartPool() {
        String[] args = {"-t", "CART", "-p", "10", "-g", "5", "-r", "1"};
        int exitCode = new CommandLine(main).execute(args);
        assertEquals(0, exitCode);
        assertTrue(outContent.toString().contains("CARTPOLE"));
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

    @Test
    void testVisualisationLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        latch.countDown();
        latch.await();
    }
}