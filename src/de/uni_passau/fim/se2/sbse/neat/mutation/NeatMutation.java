package de.uni_passau.fim.se2.sbse.neat.mutation;

import de.uni_passau.fim.se2.sbse.neat.algorithms.innovations.Innovation;
import de.uni_passau.fim.se2.sbse.neat.chromosomes.*;

import java.util.*;


import static java.util.Objects.requireNonNull;

/**
 * Implements the mutation operator for the Neat algorithm, which applies four types of mutations based on probabilities:
 * 1. Add a new neuron to the network.
 * 2. Add a new connection to the network.
 * 3. Mutate the weights of the connections in the network.
 * 4. Toggle the enabled status of a connection in the network.
 */
public class NeatMutation implements Mutation<NetworkChromosome> {

    /**
     * The random number generator to use.
     */
    private final Random random;

    /**
     * The list of innovations that occurred so far in the search.
     * Since Neat applies mutations that change the structure of the network,
     * the set of innovations must be updated appropriately.
     */
    private final Set<Innovation> innovations;



    /**
     * Constructs a new NeatMutation with the given random number generator and the list of innovations that occurred so far in the search.
     *
     * @param innovations The list of innovations that occurred so far in the search.
     * @param random      The random number generator.
     */
    public NeatMutation(Set<Innovation> innovations, Random random) {
        this.innovations = requireNonNull(innovations);
        this.random = requireNonNull(random);
    }

    private static class Pair<T, U> {
        private  T parent_1;
        private  U parent_2;

        public Pair(T parent, U parent_2) {
            this.parent_1 = parent;
            this.parent_2 = parent_2;
        }

        public T getFirstNew() {
            return parent_1;
        }

        public U getSecondNew() {
            return parent_2;
        }
    }
  
    @Override
    public NetworkChromosome apply(NetworkChromosome parent) {
        double  addingProbability  = 0.03;
        double addingConnectionProbability = 0.05;
        double mutateWeightsProbability = 0.8;
        double toggleConnectionProbability = 0.2;

        if (random.nextDouble() < addingProbability) {
            return addNeuron(parent);
        }
        if (random.nextDouble() < addingConnectionProbability) {
            return addConnection(parent);
        }

        NetworkChromosome mutated = parent;
        if (random.nextDouble() < mutateWeightsProbability) {
            mutated = mutateWeights(mutated);
        }
        if (random.nextDouble() < toggleConnectionProbability) {
            mutated = toggleConnection(mutated);
        }
        return mutated;
    }

    /**
     * Adds a hidden neuron to the given network chromosome by splitting an existing connection.
     * The connection to be split is chosen randomly from the list of connections in the network chromosome.
     * The connection is disabled and two new connections are added to the network chromosome:
     * One connection with a weight of 1.0 from the source neuron of the split connection to the new hidden neuron,
     * and one connection with the weight of the split connection from the new hidden neuron to the target neuron of the split connection.
     * <p>
     * Since this mutation changes the structure of the network,
     * novel innovations for the new connections must be created if the same mutation has not occurred before.
     * If the same innovation has occurred before, the corresponding innovation numbers must be reused.
     *
     * @param parent The network chromosome to which the new neuron and connections will be added.
     * @return The mutated network chromosome.
     */

    public NetworkChromosome addNeuron(NetworkChromosome parent) {
        List<ConnectionGene> connections = parent.getConnections();
        if (connections.isEmpty()) {
            return new NetworkChromosome(
                    new HashMap<>(parent.getLayers()),
                    new ArrayList<>(connections)
            );
        }


        List<ConnectionGene> enabledConnections = connections.stream()
                .filter(ConnectionGene::getEnabled)
                .toList();
        if (enabledConnections.isEmpty()) {
            return new NetworkChromosome(
                    new HashMap<>(parent.getLayers()),
                    new ArrayList<>(connections)
            );
        }

        ConnectionGene connectionToSplit = enabledConnections.get(random.nextInt(enabledConnections.size()));
        NeuronGene sourceNeuron = connectionToSplit.getSourceNeuron();
        NeuronGene targetNeuron = connectionToSplit.getTargetNeuron();


        double depth_neuron = (calculateDepth(parent, sourceNeuron) + calculateDepth(parent, targetNeuron)) / 2.0;
        int neuronId = parent.getLayers().values().stream()
        .flatMap(List::stream)
        .mapToInt(NeuronGene::getId)
        .max()
        .orElse(0) + 1;
        NeuronGene latestNeuron = new NeuronGene(
            neuronId,
                ActivationFunction.SIGMOID,
                NeuronType.HIDDEN
        );

        Map<Double, List<NeuronGene>> newLayers = new HashMap<>(parent.getLayers());
        newLayers.computeIfAbsent(depth_neuron, k -> new ArrayList<>()).add(latestNeuron);

        List<ConnectionGene> newConnections = new ArrayList<>(connections);

        newConnections.remove(connectionToSplit);
        newConnections.add(new ConnectionGene(
                connectionToSplit.getSourceNeuron(),
                connectionToSplit.getTargetNeuron(),
                connectionToSplit.getWeight(),
                false,
                connectionToSplit.getInnovationNumber()
        ));

        ConcreteInnovation concreteInnovation = new ConcreteInnovation(sourceNeuron.getId(), latestNeuron.getId(), 0);
        int concreteInnovation1 = concreteInnovation.concreteDerivedNumber(sourceNeuron.getId(), latestNeuron.getId(), innovations);
        int concreteInnovation2 = concreteInnovation.concreteDerivedNumber(latestNeuron.getId(), targetNeuron.getId(), innovations);

        newConnections.add(new ConnectionGene(sourceNeuron, latestNeuron, 1.0, true, concreteInnovation1));
        newConnections.add(new ConnectionGene(latestNeuron, targetNeuron, connectionToSplit.getWeight(), true, concreteInnovation2));

        return new NetworkChromosome(newLayers, newConnections);
    }

     /**
     * Adds a connection to the given network chromosome.
     * The source neuron of the connection is chosen randomly from the list of neurons in the network chromosome,
     * excluding output neurons.
     * The target neuron of the connection is chosen randomly from the list of neurons in the network chromosome,
     * excluding input and bias neurons.
     * The connection is added to the network chromosome with a random weight between -1.0 and 1.0.
     * The connection must not be recurrent.
     * <p>
     * Since this mutation changes the structure of the network,
     * novel innovations for the new connection must be created if the same mutation has not occurred before.
     * If the same innovation has occurred before, the corresponding innovation number must be reused.
     *
     * @param parent The network chromosome to which the new connection will be added.
     * @return The mutated network chromosome.
     */  
    public NetworkChromosome addConnection(NetworkChromosome parent) {
        Map<Double, List<NeuronGene>> layers = parent.getLayers();
        List<ConnectionGene> connections = parent.getConnections();

        List<NeuronGene> possibleSources = new ArrayList<>();
        List<NeuronGene> possibleTargets = new ArrayList<>();


        for (Map.Entry<Double, List<NeuronGene>> entry : layers.entrySet()) {
            for (NeuronGene neuron : entry.getValue()) {
                if (neuron.getNeuronType() != NeuronType.OUTPUT) {
                    possibleSources.add(neuron);
                }
                if (neuron.getNeuronType() != NeuronType.INPUT && 
                    neuron.getNeuronType() != NeuronType.BIAS) {
                    possibleTargets.add(neuron);
                }
            }
        }

        List<Pair<NeuronGene, NeuronGene>> validPairs = new ArrayList<>();
        for (NeuronGene source : possibleSources) {
            for (NeuronGene target : possibleTargets) {
                if (calculateDepth(parent, source) < calculateDepth(parent, target)) {
                    boolean connectionExists = connections.stream()
                        .anyMatch(c -> c.getSourceNeuron().equals(source) &&
                                c.getTargetNeuron().equals(target));
                    if (!connectionExists) {
                        validPairs.add(new Pair<>(source, target));
                    }
                }
            }
        }

        if (!validPairs.isEmpty()) {
            Pair<NeuronGene, NeuronGene> chosen = validPairs.get(random.nextInt(validPairs.size()));
            NeuronGene source = chosen.getFirstNew();
            NeuronGene target = chosen.getSecondNew();
            ConcreteInnovation concreteInnovation = new ConcreteInnovation(source.getId(), target.getId(), 0);
            int innovationSize = concreteInnovation.concreteDerivedNumber(source.getId(), target.getId(), innovations);
            ConnectionGene newConnection = new ConnectionGene(
                source,
                target,
                random.nextDouble() * 2.0 - 1.0,
                true,
                innovationSize
            );

            List<ConnectionGene> newConnections = new ArrayList<>(connections);
            newConnections.add(newConnection);
            return new NetworkChromosome(new HashMap<>(layers), newConnections);
        }

        return new NetworkChromosome(new HashMap<>(layers), new ArrayList<>(connections));
    }

    /**
     * Mutates the weights of the connections in the given network chromosome.
     * The weight is mutated by adding gaussian noise to every weight in the network chromosome.
     *
     * @param parent The network chromosome to mutate.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome mutateWeights(NetworkChromosome parent) {
        List<ConnectionGene> latestConnections = new ArrayList<>();
        for (ConnectionGene connection : parent.getConnections()) {
            double latestWeight = connection.getWeight();
            if (random.nextDouble() > 0.1) {
             
                latestWeight += random.nextGaussian() * 0.1;
            } else {
                latestWeight = random.nextDouble() * 4.0 - 2.0;
            }
            latestWeight = Math.max(-2.0, Math.min(2.0, latestWeight));
            latestConnections.add(new ConnectionGene(
                    connection.getSourceNeuron(),
                    connection.getTargetNeuron(),
                    latestWeight,
                    connection.getEnabled(),
                    connection.getInnovationNumber()
            ));
        }
        return new NetworkChromosome(new HashMap<>(parent.getLayers()), latestConnections);
    }

    /**
     * Toggles the enabled status of a random connection in the given network chromosome.
     *
     * @param parent The network chromosome to mutate.
     * @return The mutated network chromosome.
     */
    public NetworkChromosome toggleConnection(NetworkChromosome parent) {
        List<ConnectionGene> connections = parent.getConnections();
        if (connections.isEmpty()) {
            return new NetworkChromosome(
                    new HashMap<>(parent.getLayers()),
                    new ArrayList<>(connections)
            );
        }

        int index = random.nextInt(connections.size());
        ConnectionGene connection = connections.get(index);

        List<ConnectionGene> newConnections = new ArrayList<>(connections);
        newConnections.set(index, new ConnectionGene(
                connection.getSourceNeuron(),
                connection.getTargetNeuron(),
                connection.getWeight(),
                !connection.getEnabled(),
                connection.getInnovationNumber()
        ));

        return new NetworkChromosome(new HashMap<>(parent.getLayers()), newConnections);
    }

    private double calculateDepth(NetworkChromosome geneNetwork, NeuronGene neuronGene) {
        
        for (Map.Entry<Double, List<NeuronGene>> entry : geneNetwork.getLayers().entrySet()) {
            if (entry.getValue().contains(neuronGene)) {
                return entry.getKey();
            }
        }
       

        if  (neuronGene.getNeuronType() == NeuronType.OUTPUT)  {
            return NetworkChromosome.OUTPUT_LAYER;
        
        } else if(neuronGene.getNeuronType() == NeuronType.INPUT || neuronGene.getNeuronType() == NeuronType.BIAS) {
            return NetworkChromosome.INPUT_LAYER;
        }

        List<ConnectionGene> oldConnection = geneNetwork.getConnections().stream()
                .filter(conn -> conn.getSourceNeuron().equals(neuronGene))
                .toList();


        List<ConnectionGene> newConnection = geneNetwork.getConnections().stream()
        .filter(conn -> conn.getTargetNeuron().equals(neuronGene))
        .toList();

        double minimumInput = NetworkChromosome.OUTPUT_LAYER;
        double maximumInput = NetworkChromosome.INPUT_LAYER;

        if (!newConnection.isEmpty()) {
            for (ConnectionGene conn : newConnection) {
                NeuronGene source = conn.getSourceNeuron();
                if (source.getNeuronType() == NeuronType.INPUT || source.getNeuronType() == NeuronType.BIAS) {
                    maximumInput = Math.max(maximumInput, NetworkChromosome.INPUT_LAYER);
                } else if (source.getNeuronType() == NeuronType.OUTPUT) {
                    maximumInput = Math.max(maximumInput, NetworkChromosome.OUTPUT_LAYER);
                } else {
                    maximumInput = Math.max(maximumInput, NetworkChromosome.INPUT_LAYER + 1.0);
                }
            }
        }

        if (!oldConnection.isEmpty()) {
            for (ConnectionGene conn : oldConnection) {
                NeuronGene target = conn.getTargetNeuron();
                if (target.getNeuronType() == NeuronType.OUTPUT) {
                    minimumInput = Math.min(minimumInput, NetworkChromosome.OUTPUT_LAYER);
                } else if (target.getNeuronType() == NeuronType.INPUT || target.getNeuronType() == NeuronType.BIAS) {
                    minimumInput = Math.min(minimumInput, NetworkChromosome.INPUT_LAYER);
                } else {
                    minimumInput = Math.min(minimumInput, NetworkChromosome.OUTPUT_LAYER - 1.0);
                }
            }
        }

        if (newConnection.isEmpty() && oldConnection.isEmpty() || maximumInput >= minimumInput) {
            return (NetworkChromosome.INPUT_LAYER + NetworkChromosome.OUTPUT_LAYER) / 2.0;
        }

        return maximumInput + (minimumInput - maximumInput) / 2.0;
    }
   
}

