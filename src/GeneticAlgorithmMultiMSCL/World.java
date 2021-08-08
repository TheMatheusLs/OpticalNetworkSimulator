package src.GeneticAlgorithmMultiMSCL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.GeneticAlgorithmMultiMSCL.Helpers.MultiObjectiveHelper;
import src.GeneticAlgorithmMultiMSCL.Helpers.WorldHelper;
import src.Save.CreateFolder;
import src.Simulation.Simulation;

public class World{
    
    Random randomGenarationGA;

    public List<IndividualMSCL> populationOfIndividuals;

    List<Double> fitnessOverTimer;

    public int generationsCount = 0;
    public int noImprovementCount = 0;

    Simulation simulation = new Simulation();

    CreateFolder createFolder;

    public boolean hasConverged(){
        return generationsCount > ConfigMSCL.maxGenerations || noImprovementCount > ConfigMSCL.maxNoImprovementCount;
    }

    public World(CreateFolder createFolder) throws Exception{

        this.createFolder = createFolder;

        populationOfIndividuals = new ArrayList<IndividualMSCL>();
        fitnessOverTimer = new ArrayList<Double>();

        this.randomGenarationGA = new Random();

		this.simulation.initialize(createFolder);
    }

    public void Spawn() throws Exception{
        this.populationOfIndividuals.addAll(WorldHelper.SpawnPopulation(randomGenarationGA, this.simulation.getRouting().getAllRoutes()));

        // Simula os indivíduos para encontrar os fitness
        this.runPopulationFitness(this.populationOfIndividuals);

        // Atualiza os Fitness (rank) de toda a população
        MultiObjectiveHelper.UpdatePopulationFitness(this.populationOfIndividuals);
        
        this.saveFitnessGeneration();
    }

    private void saveFitnessGeneration() throws Exception {
        String generationPBFitness = "";
        for (IndividualMSCL individual : this.populationOfIndividuals){
            double fitness = individual.PBFitness;
            generationPBFitness += String.format("%f,", fitness);
        }
        generationPBFitness += "OK";

        this.createFolder.writeFile("PBFitnessGeneration.csv", generationPBFitness);

        String generationTimeFitness = "";
        for (IndividualMSCL individual : this.populationOfIndividuals){
            double fitness = individual.timeFitness;
            generationTimeFitness += String.format("%f,", fitness);
        }
        generationTimeFitness += "OK";

        this.createFolder.writeFile("TimeFitnessGeneration.csv", generationTimeFitness);
    }

    private void runPopulationFitness(List<IndividualMSCL> population) throws Exception {
        for (IndividualMSCL individual : population){
            if (individual.numberOfSimulations < 5){

                this.simulation.getRouting().updateConflictRoutesForMSCL(individual);

                double[] result = this.simulation.simulateSingle(ConfigMSCL.networkLoadGATraining);

                individual.numberOfSimulations++;

                if (individual.numberOfSimulations == 1){
                    individual.PBFitness = result[0];
                    individual.timeFitness = result[1]; 
                } else {
                    double meanPBFitness = individual.PBFitness;
                    double meanTimeFitness = individual.timeFitness;

                    double newMeanPBFitness = ((meanPBFitness * (individual.numberOfSimulations - 1)) + result[0]) / individual.numberOfSimulations;
                    double newMeanTimeFitness = ((meanTimeFitness * (individual.numberOfSimulations - 1)) + result[1]) / individual.numberOfSimulations;

                    individual.PBFitness = newMeanPBFitness;
                    individual.timeFitness = newMeanTimeFitness;
                }
            }
        }
    }

    public void DoGeneration() throws Exception{

        generationsCount++;

        List<IndividualMSCL> offspring = new ArrayList<IndividualMSCL>();

        for (int g = 0; g < (this.populationOfIndividuals.size() / 2); g++){
            
            // Encontra os pais
            IndividualMSCL parent_1 = getParent(this.populationOfIndividuals);
            IndividualMSCL parent_2 = getParent(this.populationOfIndividuals);

            // Encontra dois indivíduos diferentes para serem os pais
            while (parent_1 == parent_2){
                parent_2 = getParent(this.populationOfIndividuals);
            }

            // // Realiza o crossover
            IndividualMSCL[] offspringAB = getOffspring(parent_1, parent_2);
            
            // // Realiza a mutação
            offspringAB = mutate(offspringAB[0], offspringAB[1]);
            
            offspring.add(offspringAB[0]);
            offspring.add(offspringAB[1]);

        }

        // Adiciona os novos filhos na população de pais
        this.populationOfIndividuals.addAll(offspring);
        
        // Simula os indivíduos para encontrar os fitness
        this.runPopulationFitness(this.populationOfIndividuals);

        // Atualiza os Fitness de toda a população
        MultiObjectiveHelper.UpdatePopulationFitness(this.populationOfIndividuals);

        // Encontra os melhores novos indivíduos
        List<IndividualMSCL> newPopulation = new ArrayList<IndividualMSCL>();

        LOOP_RANK:for (List<IndividualMSCL> individualRank : Utility.findAllRanksByDistance(this.populationOfIndividuals)){
            LOOP_INDIVIDUAL:for (IndividualMSCL individual : individualRank){
                
                if (WorldHelper.populationContainsThisIndividual(newPopulation, individual)){
                    continue LOOP_INDIVIDUAL;
                }

                newPopulation.add(individual);
                
                if (newPopulation.size() >= (ConfigMSCL.populationCounts * ConfigMSCL.bestIndividuals)){
                    break LOOP_RANK;
                }
            }
        }

        // Adiciona indivíduos aleátorios para completar a população
        while (newPopulation.size() < ConfigMSCL.populationCounts){
            
            List<IndividualMSCL> populationOut = new ArrayList<IndividualMSCL>();
            for (IndividualMSCL individual : this.populationOfIndividuals){
                populationOut.add(individual);
            }

            for (IndividualMSCL individual : newPopulation){
                populationOut.remove(individual);
            }

            LOOP_INDIVIDUAL:while (true){
                IndividualMSCL individualToAdd = populationOut.get(this.randomGenarationGA.nextInt(populationOut.size()-1));

                if (WorldHelper.populationContainsThisIndividual(newPopulation, individualToAdd)){
                    continue LOOP_INDIVIDUAL;
                }

                newPopulation.add(individualToAdd);
                
                break LOOP_INDIVIDUAL;
            }
        }

        this.populationOfIndividuals.clear();

        for (IndividualMSCL individual : newPopulation){
            this.populationOfIndividuals.add(individual);
        }

        this.saveFitnessGeneration();
        
        this.saveParetoFront();

        this.saveAllIndividuals();
        
    }

    public void saveParetoFront() throws Exception {

        List<IndividualMSCL> firstRank = Utility.findAllRanksByDistance(this.populationOfIndividuals).get(0);

        int solutionNumber = 1;
        for (IndividualMSCL individual : firstRank){
            String filename = String.format("ParetoFront_%04d_%02d.csv", this.generationsCount, solutionNumber);

            String solution = String.format("%f,%f,%f,%f,%d,%f,%d%n", individual.PBFitness, individual.timeFitness, individual.NormalizedPBFitness, individual.NormalizedTimeFitness, individual.rank, individual.crowdingDistance, individual.mutation);

            for (GeneMSCL gene : individual.chromosome){

                for (int bitGeneIndex = 0; bitGeneIndex < gene.bitsGenes.size()-1; bitGeneIndex++){
                    solution += String.format("%b,", gene.bitsGenes.get(bitGeneIndex));
                }
                solution += String.format("%b%n", gene.bitsGenes.get(gene.bitsGenes.size()-1));
            }

            this.createFolder.writeFile(filename, solution);

            solutionNumber++;
        }
    }

    public void saveAllIndividuals() throws Exception {

        int solutionNumber = 1;
        for (IndividualMSCL individual : this.populationOfIndividuals){
            String filename = String.format("Individual_%04d_%02d.csv", this.generationsCount, solutionNumber);

            String solution = String.format("%f,%f,%f,%f,%d,%f,%d%n", individual.PBFitness, individual.timeFitness, individual.NormalizedPBFitness, individual.NormalizedTimeFitness, individual.rank, individual.crowdingDistance, individual.mutation);

            for (GeneMSCL gene : individual.chromosome){

                for (int bitGeneIndex = 0; bitGeneIndex < gene.bitsGenes.size()-1; bitGeneIndex++){
                    solution += String.format("%b,", gene.bitsGenes.get(bitGeneIndex));
                }
                solution += String.format("%b%n", gene.bitsGenes.get(gene.bitsGenes.size()-1));
            }

            this.createFolder.writeFile(filename, solution);
            solutionNumber++;
        }
    }

    // public List<Individual> selectPopulation(List<Individual> population){

    //     List<Individual> newPopulation = new ArrayList<Individual>();

    //     List<Individual> populationCopy = new ArrayList<Individual>();
    //     for (Individual individual : population){
    //         populationCopy.add(individual);
    //     }

    //     // Seleciona os melhores indivíduos para a população. De acordo com a taxa. 
    //     while ((newPopulation.size() < (Config.populationCounts * Config.bestIndividuals) ) && (populationCopy.size() > 0)){
    //         Individual bestIndividual = null;
    //         double bestFitness = Double.MAX_VALUE / 3;
    //         for (Individual individual : populationCopy){
    //             if (individual.fitness < bestFitness){
    //                 bestFitness = individual.fitness;
    //                 bestIndividual = individual;
    //             }
    //         }
    //         populationCopy.remove(bestIndividual);

    //         newPopulation.add(bestIndividual);
    //     }

    //     // Completa com indivíduos aleatorios.
    //     while ((newPopulation.size() < Config.populationCounts) && (populationCopy.size() > 0)){

    //         Individual individualToAdd = populationCopy.get(randomGenarationGA.nextInt(populationCopy.size()));

    //         populationCopy.remove(individualToAdd);

    //         newPopulation.add(individualToAdd);
    //     }

    //     return newPopulation;
    // } 

    public IndividualMSCL getParent(List<IndividualMSCL> populationCandidate) throws Exception{
        return TournamentSelection(populationCandidate);
    }

    private IndividualMSCL TournamentSelection(List<IndividualMSCL> populationCandidate){
        
        IndividualMSCL[] candidates = WorldHelper.GetCandidateParents(populationCandidate, randomGenarationGA);
        return WorldHelper.tournamentSelection(candidates[0], candidates[1]);
        
    }

    public void setBestFitness(double fitness) {
        this.fitnessOverTimer.add(fitness);
    }

    public List<Double> getFitnessOverTime() {
        return this.fitnessOverTimer;
    }

    private IndividualMSCL[] getOffspring(IndividualMSCL individualA, IndividualMSCL individualB)
    {   
        return WorldHelper.DoCrossoverUniforme(individualA, individualB, randomGenarationGA);
    }

    private IndividualMSCL[] mutate(IndividualMSCL individualA, IndividualMSCL individualB)
    {
        return WorldHelper.Mutate(individualA, individualB, randomGenarationGA);
    }
}