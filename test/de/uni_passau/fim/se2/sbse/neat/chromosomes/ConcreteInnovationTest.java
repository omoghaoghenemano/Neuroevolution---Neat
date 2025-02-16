package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteInnovationTest {

    private ConcreteInnovation innovation1;
    private ConcreteInnovation innovation2;
    private Set<Innovation> innovations;

    @BeforeEach
    void setUp() {
        innovation1 = new ConcreteInnovation(1, 2, 1);
        innovation2 = new ConcreteInnovation(1, 2, 2);
        innovations = new HashSet<>();
        innovations.add(innovation1);
    }

    @Test
    void testGetDerivedNumber() {
        assertEquals(1, innovation1.getderivedNumber());
    }

    @Test
    void testGetOriginId() {
        assertEquals(1, innovation1.getOriginId());
    }

    @Test
    void testGetGoalId() {
        assertEquals(2, innovation1.getGoalId());
    }

    @Test
    void testConcreteDerivedNumberExisting() {
        int derivedNumber = innovation1.concreteDerivedNumber(1, 2, innovations);
        assertEquals(1, derivedNumber);
    }

    @Test
    void testConcreteDerivedNumberNew() {
        int derivedNumber = innovation2.concreteDerivedNumber(1, 3, innovations);
        assertEquals(2, derivedNumber); // 18 + 1
    }

    @Test
    void testConcreteDerivedNumberNewWithExistingInnovations() {
        innovations.add(new ConcreteInnovation(1, 3, 20));
        int derivedNumber = innovation2.concreteDerivedNumber(1, 4, innovations);
        assertEquals(21, derivedNumber); // 20 + 1
    }

    @Test
    void testHashCode() {
        assertEquals(Objects.hash(1, 2), innovation1.hashCode());
    }

    @Test
    void testEquals() {
        ConcreteInnovation sameInnovation = new ConcreteInnovation(1, 2, 1);
        ConcreteInnovation differentInnovation = new ConcreteInnovation(2, 3, 1);
        assertTrue(innovation1.equals(sameInnovation));
        assertFalse(innovation1.equals(differentInnovation));
        assertFalse(innovation1.equals(null));
        assertFalse(innovation1.equals(new Object()));
    }

    @Test
    void testToString() {
        String expected = "ConcreteInnovation{originId=1, goalId=2, derivedNumber=1}";
        assertEquals(expected, innovation1.toString());
    }
}
