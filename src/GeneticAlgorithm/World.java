package src.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.GeneticAlgorithm.Helpers.WorldHelper;
import src.Save.CreateFolder;
import src.Simulation.Simulation;

public class World{
    
    Random randomGenarationGA;

    public List<Individual> populationOfIndividuals;

    List<Double> fitnessOverTimer;

    public int generationsCount = 0;
    public int noImprovementCount = 0;

    Simulation simulation = new Simulation();

    CreateFolder createFolder;

    public boolean hasConverged(){
        return generationsCount > Config.maxGenerations || noImprovementCount > Config.maxNoImprovementCount;
    }

    public World(CreateFolder createFolder) throws Exception{

        this.createFolder = createFolder;

        populationOfIndividuals = new ArrayList<Individual>();
        fitnessOverTimer = new ArrayList<Double>();

        this.randomGenarationGA = new Random();

		this.simulation.initialize(createFolder);
    }

    public void Spawn() throws Exception{
        this.populationOfIndividuals.addAll(WorldHelper.SpawnPopulation(randomGenarationGA));

        // Simula os indivíduos para encontrar os fitness
        this.runPopulationFitness(this.populationOfIndividuals);

        this.saveFitnessGeneration();
    }

    private void saveFitnessGeneration() throws Exception {
        String generationFitness = "";
        for (Individual individual : this.populationOfIndividuals){
            double fitness = individual.fitness;
            generationFitness += String.format("%f,", fitness);
        }
        generationFitness += "OK";

        this.createFolder.writeFile("FitnessGeneration.csv", generationFitness);
    }

    private void runPopulationFitness(List<Individual> population) throws Exception {
        for (Individual individual : population){
            if (individual.numberOfSimulations < 5){
                double[] result = this.simulation.doSimulateGA(individual, Config.networkLoadGATraining);

                individual.numberOfSimulations++;

                if (individual.numberOfSimulations == 1){
                    individual.fitness = result[0]; // Salva o fitness no indivíduo
                } else {
                    double meanFitness = individual.fitness;

                    double newMeanFitness = ((meanFitness * (individual.numberOfSimulations - 1)) + result[0]) / individual.numberOfSimulations;

                    individual.fitness = newMeanFitness;
                }
            }
        }
    }

    public void DoGeneration() throws Exception{

        generationsCount++;

        List<Individual> offspring = new ArrayList<Individual>();

        for (int g = 0; g < (this.populationOfIndividuals.size() / 2); g++){
            
            // Encontra os pais
            Individual parent_1 = getParent(this.populationOfIndividuals);
            Individual parent_2 = getParent(this.populationOfIndividuals);

            // Encontra dois indivíduos diferentes para serem os pais
            while (parent_1 == parent_2){
                parent_2 = getParent(this.populationOfIndividuals);
            }

            // Realiza o crossover
            Individual[] offspringAB = getOffspring(parent_1, parent_2);
            
            // Realiza a mutação
            offspringAB = mutate(offspringAB[0], offspringAB[1]);
            
            offspring.add(offspringAB[0]);
            offspring.add(offspringAB[1]);

        }
        
        // Adiciona os novos filhos na população de pais
        this.populationOfIndividuals.addAll(offspring);

        // Simula os indivíduos para encontrar os fitness
        this.runPopulationFitness(this.populationOfIndividuals);

        // Encontra os melhores novos indivíduos
        List<Individual> newPopulation = this.selectPopulation(this.populationOfIndividuals);

        this.populationOfIndividuals.clear();

        for (Individual individual : newPopulation){
            this.populationOfIndividuals.add(individual);
        }

        this.saveFitnessGeneration();

        if (this.generationsCount % 5 == 0){
            this.saveBestsIndividuals();
            this.saveAllIndividuals();
        }
    }

    public void saveBestsIndividuals() throws Exception {
        List<Individual> firstRank = this.selectPopulation(this.populationOfIndividuals);

        for (int index = 0; index < 3; index++){
            String filename = String.format("Solution_%d_%d.csv", this.generationsCount, (index + 1));

            String solution = "";
            for (Gene gene : firstRank.get(index).chromosome){
                solution += String.format("%d%n", gene.integerGene);
            }

            this.createFolder.writeFile(filename, solution);
        }
    }

    public void saveAllIndividuals() throws Exception {
        List<Individual> firstRank = this.selectPopulation(this.populationOfIndividuals);

        String allPopulation = "";

        for (Individual individual : firstRank){
            for (Gene gene : individual.chromosome){
                allPopulation += String.format("%d,", gene.integerGene);
            }
            allPopulation += String.format("%f%n", individual.fitness);
        }

        this.createFolder.writeFile(String.format("Population_%d.csv", this.generationsCount), allPopulation);
    }

    public List<Individual> selectPopulation(List<Individual> population){

        List<Individual> newPopulation = new ArrayList<Individual>();

        List<Individual> populationCopy = new ArrayList<Individual>();
        for (Individual individual : population){
            populationCopy.add(individual);
        }

        // Seleciona os melhores indivíduos para a população. De acordo com a taxa. 
        while ((newPopulation.size() < (Config.populationCounts * Config.bestIndividuals) ) && (populationCopy.size() > 0)){
            Individual bestIndividual = null;
            double bestFitness = Double.MAX_VALUE / 3;
            for (Individual individual : populationCopy){
                if (individual.fitness < bestFitness){
                    bestFitness = individual.fitness;
                    bestIndividual = individual;
                }
            }
            populationCopy.remove(bestIndividual);

            newPopulation.add(bestIndividual);
        }

        // Completa com indivíduos aleatorios.
        while ((newPopulation.size() < Config.populationCounts) && (populationCopy.size() > 0)){

            Individual individualToAdd = populationCopy.get(randomGenarationGA.nextInt(populationCopy.size()));

            populationCopy.remove(individualToAdd);

            newPopulation.add(individualToAdd);
        }

        return newPopulation;
    } 

    public Individual getParent(List<Individual> populationCandidate) throws Exception{
        if (randomGenarationGA.nextDouble() > 0.5){
            // Torneio
            return TournamentSelection(populationCandidate);
        } else {
            // Aleatório tendencioso 
            return BiasedRandomSelection(populationCandidate);
        }
    }

    private Individual TournamentSelection(List<Individual> populationCandidate){
        if (populationCandidate.size() >= 2){
            Individual[] candidates = WorldHelper.GetCandidateParents(populationCandidate, randomGenarationGA);
            return WorldHelper.tournamentSelection(candidates);
        } else {
            if (populationCandidate.size() == 1){
                return populationCandidate.get(0);
            } else {
                return null;
            }
        } 
    }

    private Individual BiasedRandomSelection(List<Individual> populationCandidate) throws Exception{
        if (populationCandidate.size() >= 2){
            return WorldHelper.BiasedRandomSelection(populationCandidate, randomGenarationGA);
        } else {
            if (populationCandidate.size() == 1){
                return populationCandidate.get(0);
            } else {
                return null;
            }
        }
    }

    public void setBestFitness(double fitness) {
        this.fitnessOverTimer.add(fitness);
    }

    public List<Double> getFitnessOverTime() {
        return this.fitnessOverTimer;
    }

    private Individual[] getOffspring(Individual individualA, Individual individualB)
    {   
        return WorldHelper.DoCrossoverUniforme(individualA, individualB, randomGenarationGA);
    }

    private Individual[] getOffspring2(Individual individualA, Individual individualB)
    {   
        if (randomGenarationGA.nextDouble() <= Config.crossoverChance){
            int cutPos_A = 1 + randomGenarationGA.nextInt(individualA.chromosome.size() - 2);
            int cutPos_B = 1 + randomGenarationGA.nextInt(individualA.chromosome.size() - 2);

            while (cutPos_A == cutPos_B){
                cutPos_B = 1 + randomGenarationGA.nextInt(individualA.chromosome.size() - 2);
            }

            // Generate the offspring from our selected parents
            Individual offspringA = WorldHelper.DoCrossover(individualA, individualB, cutPos_A, cutPos_B, randomGenarationGA);
            Individual offspringB = WorldHelper.DoCrossover(individualB, individualA, cutPos_A, cutPos_B, randomGenarationGA);

            return new Individual[]{offspringA, offspringB};
        } else {
            return new Individual[]{individualA, individualB};
        }
    }

    private Individual[] mutate(Individual individualA, Individual individualB)
    {
        return WorldHelper.Mutate(individualA, individualB, randomGenarationGA);
    }
}