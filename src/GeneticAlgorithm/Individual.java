package src.GeneticAlgorithm;

import java.util.List;

public class Individual{

    public List<Gene> chromosome;
    public double fitness;
    public int numberOfSimulations;

    public int mutation;

    public Individual(List<Gene> genes){
        this.chromosome = genes;
        this.fitness = -1;
        this.numberOfSimulations = 0;
        this.mutation = 0;
    }
}
