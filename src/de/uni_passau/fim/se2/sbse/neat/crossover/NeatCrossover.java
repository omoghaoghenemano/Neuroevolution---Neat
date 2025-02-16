package de.uni_passau.fim.se2.sbse.neat.crossover;


import de.uni_passau.fim.se2.sbse.neat.chromosomes.ConnectionGene;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NetworkChromosome;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.NeuronGene;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * A NEAT crossover operation that is used by the NEAT algorithm to combine two parent chromosomes.
 */
public class NeatCrossover implements Crossover<NetworkChromosome> {

    /**
     * The random number generator to use.
     */
    private final Random random;

    /**
     * Creates a new NEAT crossover operator with the given random number generator.
     *
     * @param random The random number generator to use.
     */
    public NeatCrossover(Random random) {
        this.random = requireNonNull(random);
    }

    /**
     * Applies a crossover operation to the given parent chromosomes by combining their genes.
     * During the crossover operation, we determine for each gene whether it is a matching gene or a disjoint/excess gene.
     * Matching genes are inherited with a 50% chance from either parent,
     * while disjoint/excess genes are only inherited from the fitter parent.
     *
     * @param parent1 The first crossover parent.
     * @param parent2 The second crossover parent.
     * @return A new chromosome resulting from the crossover operation.
     */
    @Override
    public NetworkChromosome apply(NetworkChromosome parent1, NetworkChromosome parent2) {
        if (parent2.getFitness() > parent1.getFitness()) {
            //swap parents
            NetworkChromosome tempValue = parent1;
            parent1 = parent2;
            parent2 = tempValue;
        }

         Map<Integer, ConnectionGene> firstConnection = new HashMap<>();
        for (ConnectionGene connection : parent1.getConnections()) {
            firstConnection.put(connection.getInnovationNumber(), connection);
        }
        Map<Integer, ConnectionGene> secondConnection = new HashMap<>();
        for (ConnectionGene connection : parent2.getConnections()) {
            secondConnection.put(connection.getInnovationNumber(), connection);
        }

        Map<Double, List<NeuronGene>> dependantLayers = new HashMap<>();
        for (Map.Entry<Double, List<NeuronGene>> entry : parent1.getLayers().entrySet()) {
            dependantLayers.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
      

        Set<Integer> allInnovations = new HashSet<>();
        allInnovations.addAll(firstConnection.keySet());
        allInnovations.addAll(secondConnection.keySet());

        List<ConnectionGene> childConnections = new ArrayList<>();
        for (Integer innovation : allInnovations) {
            ConnectionGene firstConnectedGene = firstConnection.get(innovation);
            ConnectionGene secondConnectedGene = secondConnection.get(innovation);

            if (firstConnectedGene != null && secondConnectedGene != null) {
                double newWeight = firstConnectedGene.getWeight() + (secondConnectedGene.getWeight() - firstConnectedGene.getWeight()) * random.nextDouble();
         
                childConnections.add(new ConnectionGene(
                    firstConnectedGene.getSourceNeuron(),
                    firstConnectedGene.getTargetNeuron(),
                    newWeight,
                    firstConnectedGene.getEnabled(),
                    firstConnectedGene.getInnovationNumber()
            ));
            }  else if (firstConnectedGene != null) {
            
                double mutatedWeight = firstConnectedGene.getWeight() * (1 + (random.nextGaussian() * 0.1));
                
                childConnections.add( new ConnectionGene(
                    firstConnectedGene.getSourceNeuron(),
                    firstConnectedGene.getTargetNeuron(),
                    mutatedWeight,
                    firstConnectedGene.getEnabled(),
                    firstConnectedGene.getInnovationNumber()
            ));
            }
        }

        return new NetworkChromosome(dependantLayers, childConnections);
    }
}
