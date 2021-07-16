package src.GeneticAlgorithm.Helpers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import src.ParametersSimulation;
import src.GeneticAlgorithm.Config;
import src.GeneticAlgorithm.Gene;
import src.GeneticAlgorithm.Individual;

public class WorldHelper {
    private static Random random = new Random();

    public static List<int[]> geneMapping = WorldHelper.createGeneMapping(ParametersSimulation.getKShortestRoutes());

    private static List<int[]> createGeneMapping(int KYen) {

        if (ParametersSimulation.getGaOption().equals(ParametersSimulation.GAOption.GAHHRSAEnable)){
            if (KYen == 4){
                geneMapping = new ArrayList<int[]>();
                geneMapping.add( new int[]{0,0,0,0});
                geneMapping.add( new int[]{1,0,0,0});
                geneMapping.add( new int[]{1,1,0,0});
                geneMapping.add( new int[]{0,0,0,1});
                geneMapping.add( new int[]{1,0,0,1});
                geneMapping.add( new int[]{0,0,1,1});
                geneMapping.add( new int[]{1,1,1,1});
    
                return geneMapping;
            } else {
                if (KYen == 3){
                    geneMapping = new ArrayList<int[]>();
                    geneMapping.add( new int[]{0,0,0});
                    geneMapping.add( new int[]{1,0,0});
                    geneMapping.add( new int[]{0,0,1});
                    geneMapping.add( new int[]{1,1,1});
        
                    return geneMapping;
                } else {
                    return null;
                }
            }
        } else {
            geneMapping = new ArrayList<int[]>();

            int[] bitGene = new int[ParametersSimulation.getKShortestRoutes()];

            for (int i = 0; i < ParametersSimulation.getKShortestRoutes(); i++){
                bitGene[i] = 0;
            }
            geneMapping.add( bitGene );

            bitGene = new int[ParametersSimulation.getKShortestRoutes()];

            for (int i = 0; i < ParametersSimulation.getKShortestRoutes(); i++){
                bitGene[i] = 1;
            }
            geneMapping.add( bitGene );

            return geneMapping;
        }

    }

    public static List<Individual> SpawnPopulation() throws Exception{
        List<Individual> population = new ArrayList<Individual>();

        // Cria um indivíduo todo igual a 0
        //population.add(GenerateIndividualInteger(Config.numberOfNodes, 0));
        // Cria um indivíduo todo igual a 1
        //population.add(GenerateIndividualInteger(Config.numberOfNodes, 6));
        // Cria um indivíduo com a melhor solução HRSA
        // String folder = "05-07-21_12-18-55_NSFNET_14_ALTERNATIVO_GA_MO_HRSA_K=4_GA_Training";
		// String filename = "Solution_150_1.csv";

		// Individual individualBestHRSA = WorldHelper.ReadIndividualFromFile(folder, filename);
        // population.add(individualBestHRSA);

        while (population.size() < Config.populationCounts)
        {
            Individual individual = GenerateIndividualInteger(Config.numberOfNodes);

            //TODO: Mudar essa verfificação
            //if (!population.contains(individual))
            if (!populationContainsThisIndividual(population, individual))
            {
                population.add(individual);
            }
        }

        return population;
    }

    public static boolean populationContainsThisIndividual(List<Individual> population, Individual individual){

        LOOP_IND : for (Individual individualInPopulation : population){

            // Percorre todo o cromossomo
            for (int geneIndex = 0; geneIndex < individual.chromosome.size(); geneIndex++){

                if (individualInPopulation.chromosome.get(geneIndex).integerGene != individual.chromosome.get(geneIndex).integerGene){
                    continue LOOP_IND;
                }
            }

            return true;
        }

        return false;
    }

    public static Individual GenerateIndividualInteger(int numberOfNodes, int intGene) {

        List<Gene> sequenceOfGenes = new ArrayList<Gene>();
        for (int s = 0; s < numberOfNodes; s++){
            for (int d = 0; d < numberOfNodes; d++){
            
                if (s == d){
                    continue;
                }

                sequenceOfGenes.add(new Gene(intGene, s, d));
            }
        }

        return new Individual(sequenceOfGenes);
    }

    private static Individual GenerateIndividualInteger(int numberOfNodes) {

        List<Gene> sequenceOfGenes = new ArrayList<Gene>();
        for (int s = 0; s < numberOfNodes; s++){
            for (int d = 0; d < numberOfNodes; d++){
            
                if (s == d){
                    continue;
                }

                int integerGene = random.nextInt(WorldHelper.geneMapping.size());

                sequenceOfGenes.add(new Gene(integerGene, s, d));
            }
        }

        return new Individual(sequenceOfGenes);
    }

    public static Individual[] GetCandidateParents(List<Individual> population){

        Individual[] individuals = new Individual[Config.numberOfCompetitors];

        for (int c = 0; c < Config.numberOfCompetitors; c++){

            LOOP_IND : while (true) {
                Individual candidate = population.get(random.nextInt(population.size()));
                
                for (Individual individual : individuals){
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

    public static Individual tournamentSelection(Individual[] candidates)
    {
        Individual bestIndividual = null;
        double bestFitness = Double.MAX_VALUE / 3;
        for (Individual individual : candidates){
            if (individual.fitness <= bestFitness){
                bestFitness = individual.fitness;
                bestIndividual = individual;
            }
        }

        return bestIndividual;
        
    }

    public static Individual DoCrossover(Individual individualA, Individual individualB){
        return DoCrossover(individualA, individualB, -1, -1);
    }

    public static Individual DoCrossover(Individual individualA, Individual individualB, int crossoverPosition_A, int crossoverPosition_B)
    {
        crossoverPosition_A = crossoverPosition_A == -1 
            ? 1 + random.nextInt(individualA.chromosome.size() - 2)
            : crossoverPosition_A;

        crossoverPosition_B = crossoverPosition_B == -1 
            ? 1 + random.nextInt(individualA.chromosome.size() - 2)
            : crossoverPosition_B;

        int firstIndex = crossoverPosition_A < crossoverPosition_B ?  crossoverPosition_A : crossoverPosition_B;
        int secondIndex = crossoverPosition_A < crossoverPosition_B ?  crossoverPosition_B : crossoverPosition_A;


        List<Gene> offspringGenes = new ArrayList<Gene>();
        for (int i = 0; i < firstIndex; i++){
            offspringGenes.add(individualA.chromosome.get(i));
        }

        for (int i = firstIndex; i < secondIndex; i++){
            offspringGenes.add(individualB.chromosome.get(i));
        }

        for (int i = secondIndex; i < individualA.chromosome.size(); i++){
            offspringGenes.add(individualA.chromosome.get(i));
        }

        return new Individual(offspringGenes);
    }

    public static int[] GetUniqueTowns(List<Integer> sequence){
        // Randomly select two towns
        int townA = random.nextInt(sequence.size());
        int townB = random.nextInt(sequence.size());

        // Ensure that the two towns are not the same
        while (townB == townA)
        {
            townB = random.nextInt(sequence.size());
        }

        return new int[]{townA, townB};
    }

    public static Individual[] Mutate(Individual individualA, Individual individualB)
    {
        boolean individualAMutate = false;
        boolean individualBMutate = false;

        Individual newIndividualA = new Individual(individualA.chromosome);
        Individual newIndividualB = new Individual(individualB.chromosome);

        if (random.nextDouble() < Config.mutationChance){
            newIndividualA = doMutate(individualA);
            individualAMutate = true;
        }

        if (random.nextDouble() < Config.mutationChance){
            newIndividualB = doMutate(individualB);
            individualBMutate = true;
        }

        if (!individualAMutate && !individualBMutate){
            return new Individual[]{individualA, individualB};
        } else {
            if (individualAMutate && !individualBMutate){
                return new Individual[]{newIndividualA, individualB};
            } else {
                if (!individualAMutate && individualBMutate){
                    return new Individual[]{individualA, newIndividualB};
                } else {
                    return new Individual[]{newIndividualA, newIndividualB};
                }
            }
        }
    }

    private static Individual doMutate(Individual individual){
        return doMutateGenes(individual);
    }

    private static Individual doMutateGenes(Individual individual) {

        List<Integer> selectGenes = new ArrayList<Integer>();

        List<Gene> chromosome = new ArrayList<Gene>();
            for (Gene gene : individual.chromosome){
                chromosome.add(gene);
            }

        for (int m = 0; m < (individual.chromosome.size() * Config.geneMutationPercent); m++){
            
            int randomGeneIndex;

            LOOP: while (true){
                randomGeneIndex = random.nextInt(individual.chromosome.size());

                for (int i = 0; i < selectGenes.size(); i++){
                    if (randomGeneIndex == selectGenes.get(i)){
                        continue LOOP;
                    }
                }

                break LOOP;
            }

            Gene gene = chromosome.get(randomGeneIndex);
            
            int integerGene = gene.integerGene;

            int newIntegerGene = random.nextInt(WorldHelper.geneMapping.size());

            while (integerGene == newIntegerGene){
                newIntegerGene = random.nextInt(WorldHelper.geneMapping.size());
            }

            Gene mutateGene = new Gene(newIntegerGene, gene.source, gene.destination);

            chromosome.set(randomGeneIndex, mutateGene);
        }

        return new Individual(chromosome);
    }

    public static Individual ReadIndividualFromFile(String folder, String filename) throws FileNotFoundException {

        Scanner histReqs = null;

		String filenameComplete = String.format("%s\\%s\\%s", ParametersSimulation.getPathToSaveResults(), folder, filename);
            
		histReqs = new Scanner(new FileReader(filenameComplete));

        List<Gene> sequenceOfGenes = new ArrayList<Gene>();
        for (int s = 0; s < Config.numberOfNodes; s++){
            for (int d = 0; d < Config.numberOfNodes; d++){
            
                if (s == d){
                    continue;
                }

                List<Integer> gene = new ArrayList<Integer>();
                for (String bitGeneStr : histReqs.nextLine().split(",")){
                    gene.add(Integer.parseInt(bitGeneStr));
                }
                
                int integerGene = -1;
                LOOP_GENE:for (int g = 0; g < WorldHelper.geneMapping.size(); g++){
                    for (int bGene = 0; bGene < gene.size(); bGene++){
                        if (gene.get(bGene) != WorldHelper.geneMapping.get(g)[bGene]){
                            continue LOOP_GENE;
                        } 
                    }
                    integerGene = g;
                }

                sequenceOfGenes.add(new Gene(integerGene, s, d));
            }
        }
        
        return new Individual(sequenceOfGenes);
    }

    public static boolean isIndividualHasSameSequence(Individual individualA, Individual individualB){

        for (int c = 0; c < individualA.chromosome.size(); c++){
            
            if (individualA.chromosome.get(c).integerGene != individualB.chromosome.get(c).integerGene){
                return false;
            }
        }

        return true;
    }

    public static Individual BiasedRandomSelection(List<Individual> populationCandidate) throws Exception {

        double sum = 0;
        for (Individual individual : populationCandidate){
            sum += individual.fitness;
        }

        double[] proportions = new double[populationCandidate.size()];
        for (int i = 0; i < populationCandidate.size(); i++){
            proportions[i] = sum / populationCandidate.get(i).fitness;
        }

        double proportionSum = 0;
        for (int i = 0; i < populationCandidate.size(); i++){
            proportionSum += proportions[i];
        }

        double[] normalizedProportions = new double[populationCandidate.size()];
        for (int i = 0; i < populationCandidate.size(); i++){
            normalizedProportions[i] = proportions[i] / proportionSum;
        }

        List<Double> cumulativeProportions = new ArrayList<Double>();
        double cumulativeTotal = 0.0;

        for (double proportion : normalizedProportions){
            cumulativeTotal += proportion;
            cumulativeProportions.add(cumulativeTotal);
        }

        double selectedValue = random.nextDouble();

        for (int i = 0; i < cumulativeProportions.size(); i++){
            double value = cumulativeProportions.get(i);

            if (value >= selectedValue){
                return populationCandidate.get(i);
            }

        }

        throw new Exception("O que aconteceu aqui?");
    }
}   
