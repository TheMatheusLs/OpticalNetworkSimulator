package src.GeneticAlgorithmMultiMSCL.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import src.GeneticAlgorithmMultiMSCL.ConfigMSCL;
import src.GeneticAlgorithmMultiMSCL.GeneMSCL;
import src.GeneticAlgorithmMultiMSCL.IndividualMSCL;
import src.Routing.Route;

public class WorldHelper {

    public static List<IndividualMSCL> SpawnPopulation(Random randomGA, List<List<Route>> allRoutes) throws Exception{
        
        System.out.println("Criando a população de indivíduos únicos... Aguarde.");

        List<IndividualMSCL> population = new ArrayList<IndividualMSCL>();

        while (population.size() < ConfigMSCL.populationCounts)
        {
            IndividualMSCL individual = GenerateIndividualInteger(ConfigMSCL.numberOfNodes, randomGA, allRoutes);

            if (!populationContainsThisIndividual(population, individual))
            {
                population.add(individual);
            }
        }

        return population;
    }

    public static boolean populationContainsThisIndividual(List<IndividualMSCL> population, IndividualMSCL individual){

        LOOP_IND : for (IndividualMSCL individualInPopulation : population){

            int chromosomeSize = individual.chromosome.size();
            // Percorre todo o cromossomo
            for (int geneIndex = 0; geneIndex < chromosomeSize; geneIndex++){

                int bitGeneSize = individualInPopulation.chromosome.get(geneIndex).bitsGenes.size();
                // Percore todos os bitgenes
                for (int bitGeneIndex = 0;  bitGeneIndex < bitGeneSize; bitGeneIndex++){
                    if (individualInPopulation.chromosome.get(geneIndex).bitsGenes.get(bitGeneIndex) != individual.chromosome.get(geneIndex).bitsGenes.get(bitGeneIndex)){
                        continue LOOP_IND;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static IndividualMSCL GenerateIndividualInteger(int numberOfNodes, Boolean bitGene, List<List<Route>> allRoutes) {

        List<GeneMSCL> sequenceOfGenes = new ArrayList<GeneMSCL>();
        for (int s = 0; s < numberOfNodes; s++){
            for (int d = 0; d < numberOfNodes; d++){
            
                if (s == d){
                    continue;
                }

                Route route = allRoutes.get(s * ConfigMSCL.numberOfNodes + d).get(0);

                int numberOfConflictRoutes = route.getConflictRoute().size();

                List<Boolean> bitsGenes = new ArrayList<Boolean>(numberOfConflictRoutes);

                for (int bitGeneIndex = 0; bitGeneIndex < numberOfConflictRoutes; bitGeneIndex++){
                    bitsGenes.add(bitGene);
                }

                sequenceOfGenes.add(new GeneMSCL(bitsGenes, s, d));
            }
        }

        return new IndividualMSCL(sequenceOfGenes);
    }

    private static IndividualMSCL GenerateIndividualInteger(int numberOfNodes, Random randomGA, List<List<Route>> allRoutes) {

        List<GeneMSCL> sequenceOfGenes = new ArrayList<GeneMSCL>();
        for (int s = 0; s < numberOfNodes; s++){
            for (int d = 0; d < numberOfNodes; d++){
            
                if (s == d){
                    continue;
                }

                Route route = allRoutes.get(s * ConfigMSCL.numberOfNodes + d).get(0);

                int numberOfConflictRoutes = route.getConflictRoute().size();

                List<Boolean> bitsGenes = new ArrayList<Boolean>(numberOfConflictRoutes);

                for (int bitGeneIndex = 0; bitGeneIndex < numberOfConflictRoutes; bitGeneIndex++){
                    if (randomGA.nextDouble() > 0.5){
                        bitsGenes.add(true);
                    } else {
                        bitsGenes.add(false);
                    }
                }

                sequenceOfGenes.add(new GeneMSCL(bitsGenes, s, d));
            }
        }

        return new IndividualMSCL(sequenceOfGenes);
    }

    public static IndividualMSCL[] GetCandidateParents(List<IndividualMSCL> population, Random randomGA){

        IndividualMSCL[] individuals = new IndividualMSCL[ConfigMSCL.numberOfCompetitors];

        for (int c = 0; c < ConfigMSCL.numberOfCompetitors; c++){

            LOOP_IND : while (true) {
                IndividualMSCL candidate = population.get(randomGA.nextInt(population.size()));
                
                for (IndividualMSCL individual : individuals){
                    if (individual == candidate){
                        continue LOOP_IND;
                    }
                }

                individuals[c] = candidate;

                break LOOP_IND;
            }
        }

        return individuals;
    }

    public static IndividualMSCL tournamentSelection(IndividualMSCL candidateA, IndividualMSCL candidateB)
    {
        if (candidateA.rank < candidateB.rank){

            return candidateA;

        } else if (candidateA.rank == candidateB.rank){

            return candidateA.crowdingDistance > candidateB.crowdingDistance ? candidateA : candidateB;

        } else{

            return candidateB;
        }
    }

    public static IndividualMSCL[] Mutate(IndividualMSCL individualA, IndividualMSCL individualB, Random randomGA)
    {
        IndividualMSCL newIndividualA = doMutate(individualA, randomGA);

        IndividualMSCL newIndividualB = doMutate(individualB, randomGA);
        
        return new IndividualMSCL[]{newIndividualA, newIndividualB};  
    }

    private static IndividualMSCL doMutate(IndividualMSCL individual, Random randomGA){

        List<GeneMSCL> chromosome = new ArrayList<GeneMSCL>();

        int mutation = 0;

        //Para cada gene
        for (int geneIndex = 0; geneIndex < individual.chromosome.size(); geneIndex++){

            List<Boolean> sequenceOfBitsGenes = new ArrayList<Boolean>();

            for (int bitGeneIndex = 0; bitGeneIndex < individual.chromosome.get(geneIndex).bitsGenes.size(); bitGeneIndex++){
                
                Boolean bitGene = individual.chromosome.get(geneIndex).bitsGenes.get(bitGeneIndex);;

                if (randomGA.nextDouble() < ConfigMSCL.mutationChance){
                    sequenceOfBitsGenes.add(!bitGene);
                } else {
                    sequenceOfBitsGenes.add(bitGene);
                }
            }
            
            int source = individual.chromosome.get(geneIndex).source;
            int destination = individual.chromosome.get(geneIndex).destination;

            chromosome.add(new GeneMSCL(sequenceOfBitsGenes, source, destination));
        }

        IndividualMSCL newIndividual = new IndividualMSCL(chromosome);
        newIndividual.mutation = mutation;

        return newIndividual;
    }

    public static IndividualMSCL[] DoCrossoverUniforme(IndividualMSCL individualA, IndividualMSCL individualB, Random randomGA){
        List<GeneMSCL> sequenceOfGenesOffSpringA = new ArrayList<GeneMSCL>();
        List<GeneMSCL> sequenceOfGenesOffSpringB = new ArrayList<GeneMSCL>();

        //Para cada gene
        for (int geneIndex = 0; geneIndex < individualA.chromosome.size(); geneIndex++){

            List<Boolean> sequenceOfBitsGenesOffSpringA = new ArrayList<Boolean>();
            List<Boolean> sequenceOfBitsGenesOffSpringB = new ArrayList<Boolean>();

            for (int bitGeneIndex = 0; bitGeneIndex < individualA.chromosome.get(geneIndex).bitsGenes.size(); bitGeneIndex++){
                
                Boolean bitGeneA = individualA.chromosome.get(geneIndex).bitsGenes.get(bitGeneIndex);
                Boolean bitGeneB = individualB.chromosome.get(geneIndex).bitsGenes.get(bitGeneIndex);

                if (randomGA.nextDouble() < ConfigMSCL.crossoverChance){
                    sequenceOfBitsGenesOffSpringA.add(bitGeneA);
                    sequenceOfBitsGenesOffSpringB.add(bitGeneB);
                } else {
                    sequenceOfBitsGenesOffSpringA.add(bitGeneB);
                    sequenceOfBitsGenesOffSpringB.add(bitGeneA);
                }
            }
            
            int source = individualA.chromosome.get(geneIndex).source;
            int destination = individualA.chromosome.get(geneIndex).destination;

            sequenceOfGenesOffSpringA.add(new GeneMSCL(sequenceOfBitsGenesOffSpringA, source, destination));
            sequenceOfGenesOffSpringB.add(new GeneMSCL(sequenceOfBitsGenesOffSpringB, source, destination));
        }

        return new IndividualMSCL[]{new IndividualMSCL(sequenceOfGenesOffSpringA), new IndividualMSCL(sequenceOfGenesOffSpringB)};
    }
}