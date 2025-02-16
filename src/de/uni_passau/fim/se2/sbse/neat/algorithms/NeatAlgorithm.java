package de.uni_passau.fim.se2.sbse.neat.algorithms;

import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;
import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.environments.Environment;
import de.uni_passau.fim.se2.sbse.neat.crossover.NeatCrossover;
import de.uni_passau.fim.se2.sbse.neat.mutation.NeatMutation;
import de.uni_passau.fim.se2.sbse.neat.utils.Randomness;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a neuroevolution algorithm that solves reinforcement learning
 * tasks.
 */
public class NeatAlgorithm implements Neuroevolution {
    private final Random random;
    private final int populationSize;
    private final Set<Innovation> innovations;
    private final NeatCrossover crossover;
    private int actualGenerations = 0;
    private final List<ChromesomeToGroup> population;
    private final NeatMutation mutation;
    private final int maximumGenerations;
    private final Environment testEnvironment;

  
    public NeatAlgorithm(int populationSize, int maximumGenerations, Environment testEnvironment) {
        this.populationSize = populationSize;
        this.random = Randomness.random();
        this.innovations = new HashSet<>();
        this.mutation = new NeatMutation(innovations, random);
        this.population = new ArrayList<>();
        this.maximumGenerations = maximumGenerations;
        this.crossover = new NeatCrossover(random);
        this.testEnvironment = testEnvironment;
    }

   
    public class ChromesomeToGroup {
        private double fitnessAverage;
        protected NetworkChromosome representative;
        private final List<NetworkChromosome> candidate = new ArrayList<>();

        public ChromesomeToGroup(NetworkChromosome representative) {
            this.representative = representative;
            this.candidate.add(representative);
        }

        public void addCandidate(NetworkChromosome candidates) {
            candidate.add(candidates);
        }

        public List<NetworkChromosome> getCandidate() {
            return candidate;
        }

        public void calculateAverageFitness() {
            fitnessAverage = candidate.stream().mapToDouble(NetworkChromosome::getFitness).average().orElse(0.0);
        }

        public void remove() {
            candidate.clear();
        }

        public double getFitnessAverage() {
            return fitnessAverage;
        }
    }

    @Override
    public Agent solve(Environment environment) {
        NetworkGenerator generateNetwork = new NetworkGenerator(
                innovations,
                testEnvironment.stateSize(),
                testEnvironment.actionInputSize(),
                random);

        for (int i = 0; i < populationSize; i++) {
            NetworkChromosome chromosome = generateNetwork.generate();
            specieAssignment(chromosome);
        }

        double betterFitness = Double.NEGATIVE_INFINITY;
        NetworkChromosome betterChromosome = null;

        while (actualGenerations < maximumGenerations) {

            for (ChromesomeToGroup group : population) {
                for (NetworkChromosome candidates : group.getCandidate()) {
                    double fitness = environment.evaluate(candidates);
                    candidates.setFitness(fitness);
                }
                group.calculateAverageFitness();
            }

            NetworkChromosome currentBest = population.stream()
                    .flatMap(s -> s.getCandidate().stream())
                    .max(Comparator.comparingDouble(NetworkChromosome::getFitness))
                    .orElseThrow(() -> new IllegalStateException("No chromose found"));
            if (currentBest.getFitness() > betterFitness) {
                betterFitness = currentBest.getFitness();
                betterChromosome = currentBest;
                if (environment.solved(betterChromosome)) {
                    break;
                }
            }

            double totalAdjustedFitness = population.stream()
                    .mapToDouble(s -> {
                        double populationSize = s.getCandidate().size();
                        s.getCandidate().forEach(m -> m.setFitness(m.getFitness() / populationSize));
                        return s.getFitnessAverage();
                    })
                    .sum();

            List<NetworkChromosome> newPopulation = new ArrayList<>();

            for (ChromesomeToGroup group : population) {
                if (group.getCandidate().size() >= 5) {
                    NetworkChromosome best = group.getCandidate().stream()
                            .max(Comparator.comparingDouble(NetworkChromosome::getFitness))
                            .get();
                    newPopulation.add(best);
                }
            }

            while (newPopulation.size() < populationSize) {
                double randomAdjustFitness = random.nextDouble() * totalAdjustedFitness;
                double total = 0;
                ChromesomeToGroup selectedGroup = null;
                for (ChromesomeToGroup specie : population) {
                    total += specie.getFitnessAverage();
                    if (total > randomAdjustFitness) {
                        selectedGroup = specie;
                        break;
                    }
                }

                if (selectedGroup == null) {
                    selectedGroup = population.get(population.size() - 1);
                }

                NetworkChromosome child;

                double threshold = 0.8;
                if (random.nextDouble() > threshold) {
                    child = parentSelection(selectedGroup);
                } else {

                    NetworkChromosome firstParent = parentSelection(selectedGroup);
                    NetworkChromosome secondParent = parentSelection(selectedGroup);
                    child = crossover.apply(firstParent, secondParent);
                }

                if (random.nextDouble() < 0.02) {
                    child = mutation.addNeuron(child);
                }

                if (random.nextDouble() < 0.8) {
                    child = mutation.mutateWeights(child);
                }
                if (random.nextDouble() < 0.05) {
                    child = mutation.addConnection(child);
                }

                newPopulation.add(child);
            }

            population.forEach(ChromesomeToGroup::remove);

            newPopulation.forEach(this::specieAssignment);

            population.removeIf(s -> s.getCandidate().isEmpty());

            if (population.size() < 5) {
                NetworkChromosome randomcandidates = newPopulation.get(random.nextInt(newPopulation.size()));
                population.add(new ChromesomeToGroup(randomcandidates));
            }
            actualGenerations++;
        }

        return betterChromosome != null ? betterChromosome
                : population.stream()
                        .flatMap(s -> s.getCandidate().stream())
                        .max(Comparator.comparingDouble(NetworkChromosome::getFitness))
                        .orElseThrow(() -> new IllegalStateException("No chromose found"));
    }

    private void specieAssignment(NetworkChromosome chromosome) {
        for (ChromesomeToGroup group : population) {
            List<ConnectionGene> firstConnection = chromosome.getConnections();
            List<ConnectionGene> secondConnection = group.representative.getConnections();

            Map<Integer, ConnectionGene> generationToMap1 = firstConnection.stream()
                    .collect(Collectors.toMap(
                            ConnectionGene::getInnovationNumber,
                            c -> c,
                            (current, change) -> current));

            Map<Integer, ConnectionGene> generationToMap2 = secondConnection.stream()
                    .collect(Collectors.toMap(
                            ConnectionGene::getInnovationNumber,
                            c -> c,
                            (current, change) -> current));

          

            Set<Integer> allNetworkGeneration = new HashSet<>();
            allNetworkGeneration.addAll(generationToMap1.keySet());
            allNetworkGeneration.addAll(generationToMap2.keySet());

            int maximumGeneration1 = firstConnection.stream().mapToInt(ConnectionGene::getInnovationNumber).max().orElse(0);
            int maximumGeneration2 = secondConnection.stream().mapToInt(ConnectionGene::getInnovationNumber).max().orElse(0);
            int disjoint = 0;
            double Difweight = 0;
            int compatible = 0;
            for (int generation : allNetworkGeneration) {
                ConnectionGene firstGene = generationToMap1.get(generation);
                ConnectionGene secondGene = generationToMap2.get(generation);

                if (firstGene != null && secondGene != null) {

                    compatible++;
                    Difweight += Math.abs(firstGene.getWeight() - secondGene.getWeight());
                } else {
                    disjoint++;
                }
            }

            int TopSize = Math.max(firstConnection.size(), secondConnection.size());
            if(TopSize < 20){
                TopSize = 1;
            }
       

            double averageWeight = compatible == 0 ? 0 : Difweight / compatible;
            int excess = Math.abs(maximumGeneration1 - maximumGeneration2);

            double distance = (excess + disjoint) / (double) TopSize + averageWeight;
            if (distance < 4.0) {
                group.addCandidate(chromosome);
                return;
            }
        }

        population.add(new ChromesomeToGroup(chromosome));
    }

    private NetworkChromosome parentSelection(ChromesomeToGroup group) {

        List<NetworkChromosome> candidatess = group.getCandidate();
        NetworkChromosome best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < 3; i++) {
            NetworkChromosome contestant = candidatess.get(random.nextInt(candidatess.size()));
            if (contestant.getFitness() > bestFitness) {
                best = contestant;
                bestFitness = contestant.getFitness();
            }
        }

        return best;
    }

    @Override
    public int getGeneration() {
        return actualGenerations;
    }
}
