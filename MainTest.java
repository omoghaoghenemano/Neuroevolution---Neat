package de.uni_passau.fim.se2.sbse.neat;

import de.uni_passau.fim.se2.sbse.neat.environments.Tasks;
import de.uni_passau.fim.se2.sbse.neat.utils.Randomness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the Main class.
 */
class MainTest {

    private Main main;

    @BeforeEach
    void setUp() {
        main = new Main();
    }

    @Test
    void testSetSeed() {
        long seed = 12345L;
        main.setSeed(seed);
        assertEquals(seed, Randomness.random().getSeed());
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
    void testInitialiseNeat() {
        Neuroevolution neat = Main.initialiseNeat(50, 50);
        assertNotNull(neat);
    }

    @Test
    void testMainMethod() {
        String[] args = {"-t", "XOR"};
        Main.main(args);
    }

    @Test
    void testTaskConverter() {
        TaskConverter converter = new TaskConverter();
        assertEquals(Tasks.XOR, converter.convert("XOR"));
        assertEquals(Tasks.CARTPOLE, converter.convert("CART"));
        assertEquals(Tasks.CARTPOLE_RANDOM, converter.convert("CART_RANDOM"));
        assertThrows(IllegalArgumentException.class, () -> converter.convert("INVALID"));
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
