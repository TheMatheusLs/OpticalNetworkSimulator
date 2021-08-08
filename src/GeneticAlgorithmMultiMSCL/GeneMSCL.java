package src.GeneticAlgorithmMultiMSCL;

import java.util.List;

public class GeneMSCL{
    public List<Boolean> bitsGenes;
    public int source;
    public int destination;

    public GeneMSCL(List<Boolean> bitsGenes, int source, int destination){
        this.bitsGenes = bitsGenes;
        this.source = source;
        this.destination = destination;
    }
}