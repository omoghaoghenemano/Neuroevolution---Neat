package de.uni_passau.fim.se2.sbse.neat.chromosomes;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import static java.util.Objects.requireNonNull;
import java.util.*;
/**
 * Creates fully connected feed-forward neural networks consisting of one input and one output layer.
 */
public class NetworkGenerator {

    /**
     * The number of desired input neurons.
     */
    private final int inputSize;

    /**
     * The number of desired output neurons.
     */
    private final int outputSize;

    /**
     * The random number generator.
     */
    private final Random random;

    /**
     * The set of innovations that occurred so far in the search.
     * Novel innovations created during the generation of the network must be added to this set.
     */
    private final Set<Innovation> innovations;


    /**
     * Creates a new network generator.
     *
     * @param innovations The set of innovations that occurred so far in the search.
     * @param inputSize   The number of desired input neurons.
     * @param outputSize  The number of desired output neurons.
     * @param random      The random number generator.
     * @throws NullPointerException if the random number generator is {@code null}.
     */
    public NetworkGenerator(Set<Innovation> innovations, int inputSize, int outputSize, Random random) {
        this.innovations = requireNonNull(innovations);
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.random = requireNonNull(random);

        int initialinnovation = 19;
        
        if (innovations.isEmpty()) {
            for (int row = 0; row < inputSize + 1; row++) { 
                for (int col = 0; col < outputSize; col++) {
                    int sourceId = row;
                    int targetId = inputSize + 1 + col; 
                    innovations.add(new ConcreteInnovation(sourceId, targetId, initialinnovation++));
                }
            }
        }
    }
    public int getInnovationNumber(int originId, int goalId) {
        for (Innovation innovation : innovations) {
            if (innovation instanceof ConcreteInnovation) {
                ConcreteInnovation connInnovation = (ConcreteInnovation) innovation;
                if (connInnovation.getOriginId() == originId &&
                        connInnovation.getGoalId() == goalId) {
                    return connInnovation.getderivedNumber();
                }
            }
        }
        throw new IllegalStateException("Innovation is not found");
    }

    public NetworkChromosome generate() {
        Map<Double, List<NeuronGene>> layMap = new HashMap<>();
        List<NeuronGene> neurosTobeInputed = new ArrayList<>();
        for (int i = 0; i < inputSize; i++) {
            neurosTobeInputed.add(new NeuronGene(i, ActivationFunction.TANH, NeuronType.INPUT));
        }
        List<NeuronGene> neurosTobeOutputed = new ArrayList<>();
        for (int i = 0; i < outputSize; i++) {
            neurosTobeOutputed.add(new NeuronGene(inputSize + 1 + i, ActivationFunction.TANH, NeuronType.OUTPUT));
        }
    
        neurosTobeInputed.add(new NeuronGene(inputSize, ActivationFunction.NONE, NeuronType.BIAS));

        if(neurosTobeInputed.size() != 0 || neurosTobeOutputed.size() != 0){
            layMap.put(NetworkChromosome.INPUT_LAYER, neurosTobeInputed);
            layMap.put(NetworkChromosome.OUTPUT_LAYER, neurosTobeOutputed);
        }
       
        List<ConnectionGene> connections = new ArrayList<>();
        List<NeuronGene> inputNeurons = layMap.get(NetworkChromosome.INPUT_LAYER);
        List<NeuronGene> outputNeurons = layMap.get(NetworkChromosome.OUTPUT_LAYER);

        for (NeuronGene inputNeuron : inputNeurons) {
            for (NeuronGene outputNeuron : outputNeurons) {
                int innovationNumber = getInnovationNumber(inputNeuron.getId(), outputNeuron.getId());
                double weight = random.nextDouble() * 2 - 1; 
                connections.add(new ConnectionGene(inputNeuron, outputNeuron, weight, true, innovationNumber));
            }
        }
        return new NetworkChromosome(layMap, connections);
    }
}

