package src.GeneticAlgorithm.Helpers;

import src.GeneticAlgorithm.Gene;
import src.GeneticAlgorithm.Individual;

public class IndividualHelper {
    
    public static Gene findGene(int source, int destination, Individual individual, int numberOfNodes) throws Exception{

        if (individual.chromosome.size() != (numberOfNodes * numberOfNodes - numberOfNodes)){
            throw new Exception("O número de Genes está diferente do informado");
        }

        for (Gene geneOD : individual.chromosome){
            if ((geneOD.source == source) && (geneOD.destination == destination)){
                return geneOD;
            } else {
                continue;
            }
        }

        return null;
    }

}
