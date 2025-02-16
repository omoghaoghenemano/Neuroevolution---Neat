package de.uni_passau.fim.se2.sbse.neat.chromosomes;

/**
 * Represents a neuron gene that is part of every NEAT chromosome.
 */
public class NeuronGene {

    private final int id;
    private final ActivationFunction activationFunction;
    private final NeuronType neuronType;
    private double param = 0.0;
    /**
     * Creates a new neuron with the given ID and activation function.
     *
     * @param id                 The ID of the neuron.
     * @param activationFunction The activation function of the neuron.
     */
    public NeuronGene(int id, ActivationFunction activationFunction, NeuronType neuronType) {
       this.id = id;
         this.activationFunction = activationFunction;
            this.neuronType = neuronType;
    }

    public int getId() {
        return id;
    }

    public NeuronType getNeuronType() {
        return  neuronType;
    }




    public double activate(double input) {
        switch (activationFunction) {
            
            case TANH:
                return Math.tanh(input);
            case SIGMOID:
                return 1.0 / (1.0 + Math.exp(-input));
            case NONE:
            return input;
            default:
                return input;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeuronGene)) return false;
        NeuronGene that = (NeuronGene) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public void setParam(double param) {
        this.param = param;
    }

    public double getparam() {
        return param;
    }
}
