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

    public static Individual[] Mutate(Individual individualA, Individual individualB)
    {
        Individual newIndividualA = new Individual(individualA.chromosome);
        Individual newIndividualB = new Individual(individualB.chromosome);

        newIndividualA = doMutate(individualA);

        newIndividualB = doMutate(individualB);
        
        return new Individual[]{newIndividualA, newIndividualB};  
    }

    private static Individual doMutate(Individual individual){

        // Para cada gene
        for (int geneIndex = 0; geneIndex < individual.chromosome.size(); geneIndex++){

            if (random.nextDouble() < Config.mutationChance){

                Gene currentGene = individual.chromosome.get(geneIndex);
                int geneCurrentValue = currentGene.integerGene;

                List<Integer> possiblesValuesForGene = new ArrayList<Integer>();
                for (int geneSolution = 0; geneSolution < WorldHelper.geneMapping.size(); geneSolution++){
                    if (geneCurrentValue != geneSolution){
                        possiblesValuesForGene.add(geneSolution);
                    }
                }

                currentGene.integerGene = possiblesValuesForGene.get(random.nextInt(possiblesValuesForGene.size()));

                individual.chromosome.set(geneIndex, currentGene);
            }
        }

        return individual;
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

    public static Individual[] DoCrossoverUniforme(Individual individualA, Individual individualB){

        // Cria inidivíduos temporários
        Individual offSpringA = GenerateIndividualInteger( Config.numberOfNodes);
        Individual offSpringB = GenerateIndividualInteger( Config.numberOfNodes);

        //Para cada gene
        for (int geneIndex = 0; geneIndex < individualA.chromosome.size(); geneIndex++){
            if (random.nextDouble() > Config.crossoverChance){
                offSpringA.chromosome.set(geneIndex, individualA.chromosome.get(geneIndex));
                offSpringB.chromosome.set(geneIndex, individualB.chromosome.get(geneIndex));
            } else {
                offSpringA.chromosome.set(geneIndex, individualB.chromosome.get(geneIndex));
                offSpringB.chromosome.set(geneIndex, individualA.chromosome.get(geneIndex));
            }
        }

        return new Individual[]{offSpringA, offSpringB};
    }
}   
