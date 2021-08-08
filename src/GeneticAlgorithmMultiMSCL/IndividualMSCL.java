package src.GeneticAlgorithmMultiMSCL;

import java.util.List;

public class IndividualMSCL{

    public List<GeneMSCL> chromosome;
    public double PBFitness;
    public double timeFitness;
    public int numberOfSimulations;

    public int mutation;

    public int rank;
    public double crowdingDistance;
    public double NormalizedPBFitness;
    public double NormalizedTimeFitness;

    public IndividualMSCL(List<GeneMSCL> genes){
        this.chromosome = genes;
        this.PBFitness = -1;
        this.timeFitness = -1;
        this.numberOfSimulations = 0;
        this.mutation = 0;

        this.rank = -1;
        this.crowdingDistance = -1.0;
        this.NormalizedPBFitness = -1.0;
        this.NormalizedTimeFitness = -1.0;
    }
}
