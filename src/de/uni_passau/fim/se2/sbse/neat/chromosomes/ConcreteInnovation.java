package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import java.util.Objects;


public class ConcreteInnovation implements Innovation {
    private final int originId;
    private final int goalId;
    private final int derivedNumber;


    public ConcreteInnovation(int originId, int goalId, int derivedNumber) {
        this.originId = originId;
        this.goalId = goalId;
        this.derivedNumber = derivedNumber;
    }
    public int getderivedNumber() {
        return derivedNumber;
    }

    public int getOriginId() {
        return originId;
    }

    public int getGoalId() {
        return goalId;
    }

   

    @Override
    public int hashCode() {
        return Objects.hash(originId, goalId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConcreteInnovation)) return false;
        ConcreteInnovation that = (ConcreteInnovation) obj;
        return originId == that.originId && goalId == that.goalId;
    }

    @Override
    public String toString() {
        return "ConcreteInnovation{" +
                "originId=" + originId +
                ", goalId=" + goalId +
                ", derivedNumber=" + derivedNumber +
                '}';
    }
}
