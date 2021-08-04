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

    public static List<int[]> geneMapping = WorldHelper.createGeneMapping(ParametersSimulation.getKShortestRoutes());

    private static List<int[]> createGeneMapping(int KYen) {

        if (ParametersSimulation.getGaOption().equals(ParametersSimulation.GAOption.GAHHRSAEnable)){
            
            return new HHRSACombinations(KYen).getSolutions();
            
        } else {
            geneMapping = new ArrayList<int[]>();

            int[] bitGene = new int[ParametersSimulation.getKShortestRoutes()];

            bitGene[0] = KYen;
            for (int i = 1; i < ParametersSimulation.getKShortestRoutes(); i++){
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

    public static List<Individual> SpawnPopulation(Random randomGA) throws Exception{
        List<Individual> population = new ArrayList<Individual>();

        // Inicia um indivíduo com todos os genes da população
        for (int geneSolution = 0; geneSolution < WorldHelper.geneMapping.size(); geneSolution++){
            population.add(GenerateIndividualInteger(Config.numberOfNodes, geneSolution));
        }

        // Cria um indivíduo com a melhor solução HRSA
        // String folder = "05-07-21_12-18-55_NSFNET_14_ALTERNATIVO_GA_MO_HRSA_K=4_GA_Training";
		// String filename = "Solution_150_1.csv";

		// Individual individualBestHRSA = WorldHelper.ReadIndividualFromFile(folder, filename);
        // population.add(individualBestHRSA);

        while (population.size() < Config.populationCounts)
        {
            Individual individual = GenerateIndividualInteger(Config.numberOfNodes, randomGA);

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

    private static Individual GenerateIndividualInteger(int numberOfNodes, Random randomGA) {

        List<Gene> sequenceOfGenes = new ArrayList<Gene>();
        for (int s = 0; s < numberOfNodes; s++){
            for (int d = 0; d < numberOfNodes; d++){
            
                if (s == d){
                    continue;
                }

                int integerGene = randomGA.nextInt(WorldHelper.geneMapping.size());

                sequenceOfGenes.add(new Gene(integerGene, s, d));
            }
        }

        return new Individual(sequenceOfGenes);
    }

    public static Individual[] GetCandidateParents(List<Individual> population, Random randomGA){

        Individual[] individuals = new Individual[Config.numberOfCompetitors];

        for (int c = 0; c < Config.numberOfCompetitors; c++){

            LOOP_IND : while (true) {
                Individual candidate = population.get(randomGA.nextInt(population.size()));
                
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

    public static Individual[] Mutate(Individual individualA, Individual individualB, Random randomGA)
    {
        Individual newIndividualA = doMutate(individualA, randomGA);

        Individual newIndividualB = doMutate(individualB, randomGA);
        
        return new Individual[]{newIndividualA, newIndividualB};  
    }

    private static Individual doMutate(Individual individual, Random randomGA){

        List<Gene> chromosome = new ArrayList<Gene>();

        int mutation = 0;

        // Para cada gene
        for (int geneIndex = 0; geneIndex < individual.chromosome.size(); geneIndex++){

            if (randomGA.nextDouble() < Config.mutationChance){

                Gene currentGene = individual.chromosome.get(geneIndex);
                int geneCurrentValue = currentGene.integerGene;

                List<Integer> possiblesValuesForGene = new ArrayList<Integer>();
                for (int geneSolution = 0; geneSolution < WorldHelper.geneMapping.size(); geneSolution++){
                    if (geneCurrentValue != geneSolution){
                        possiblesValuesForGene.add(geneSolution);
                    }
                }

                int geneMutateValue = possiblesValuesForGene.get(randomGA.nextInt(possiblesValuesForGene.size()));

                Gene geneMutate = new Gene(geneMutateValue, currentGene.source, currentGene.destination);

                chromosome.add(geneMutate);

                mutation++;
            } else {
                chromosome.add(individual.chromosome.get(geneIndex));
            }
        }

        Individual newIndividual = new Individual(chromosome);
        newIndividual.mutation = mutation;

        return newIndividual;
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
                
                int integerGene = gene.get(0);

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

    public static Individual BiasedRandomSelection(List<Individual> populationCandidate, Random randomGA) throws Exception {

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

        double selectedValue = randomGA.nextDouble();

        for (int i = 0; i < cumulativeProportions.size(); i++){
            double value = cumulativeProportions.get(i);

            if (value >= selectedValue){
                return populationCandidate.get(i);
            }

        }

        throw new Exception("O que aconteceu aqui?");
    }

    public static Individual[] DoCrossoverUniforme(Individual individualA, Individual individualB, Random randomGA){

        // Cria inidivíduos temporários
        Individual offSpringA = GenerateIndividualInteger( Config.numberOfNodes, randomGA);
        Individual offSpringB = GenerateIndividualInteger( Config.numberOfNodes, randomGA);

        //Para cada gene
        for (int geneIndex = 0; geneIndex < individualA.chromosome.size(); geneIndex++){
            if (randomGA.nextDouble() < Config.crossoverChance){
                offSpringA.chromosome.set(geneIndex, individualA.chromosome.get(geneIndex));
                offSpringB.chromosome.set(geneIndex, individualB.chromosome.get(geneIndex));
            } else {
                offSpringA.chromosome.set(geneIndex, individualB.chromosome.get(geneIndex));
                offSpringB.chromosome.set(geneIndex, individualA.chromosome.get(geneIndex));
            }
        }

        return new Individual[]{offSpringA, offSpringB};
    }

    public static Individual DoCrossover(Individual individualA, Individual individualB, int crossoverPosition_A, int crossoverPosition_B, Random randomGA)
    {
        crossoverPosition_A = crossoverPosition_A == -1 
            ? 1 + randomGA.nextInt(individualA.chromosome.size() - 2)
            : crossoverPosition_A;

        crossoverPosition_B = crossoverPosition_B == -1 
            ? 1 + randomGA.nextInt(individualA.chromosome.size() - 2)
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
}