package src.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.GeneticAlgorithm.Helpers.WorldHelper;
import src.Save.CreateFolder;
import src.Simulation.Simulation;

public class World{
    
    Random randomGenarationGA = new Random();

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

		this.simulation.initialize(createFolder);

    }

    public void Spawn() throws Exception{
        this.populationOfIndividuals.addAll(WorldHelper.SpawnPopulation());

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

        // Simula os indivíduos para encontrar os fitness
        this.runPopulationFitness(offspring);

        // Adiciona os novos filhos na população de pais
        this.populationOfIndividuals.addAll(offspring);
        
        // Encontra os melhores novos indivíduos
        //List<Individual> newPopulation = this.findBestsIndividuals(this.populationOfIndividuals);

        // Completa a população com indivíduos aleátorio
        //this.completePopulationWithRandoms(newPopulation);

        //this.populationOfIndividuals.clear();

        // for (Individual individual : newPopulation){
        //     this.populationOfIndividuals.add(individual);
        // }

        this.saveFitnessGeneration();

        if (this.generationsCount % 5 == 0){
            this.saveBestsIndividuals();
        }
    }

    private void completePopulationWithRandoms(List<Individual> population) {
        while (population.size() < Config.populationCounts){
            List<Individual> populationOut = new ArrayList<Individual>();
            for (Individual individual : this.populationOfIndividuals){
                populationOut.add(individual);
            }

            for (Individual individual : population){
                populationOut.remove(individual);
            }

            LOOP_INDIVIDUAL:while (true){
                Individual individualToAdd = populationOut.get(randomGenarationGA.nextInt(populationOut.size()-1));

                for (Individual individualInNewPopulation: population){
                    if (WorldHelper.isIndividualHasSameSequence(individualInNewPopulation, individualToAdd)){
                        continue LOOP_INDIVIDUAL;
                    }
                }

                population.add(individualToAdd);
                
                break LOOP_INDIVIDUAL;
            }
        }
    }

    public void saveBestsIndividuals() throws Exception {
        List<Individual> firstRank = this.findBestsIndividuals(this.populationOfIndividuals);

        for (int index = 0; index < 3; index++){
            String filename = String.format("Solution_%d_%d.csv", this.generationsCount, (index + 1));

            String solution = "";
            for (Gene gene : firstRank.get(index).chromosome){
                solution += String.format("%d%n", gene.integerGene);
            }

            this.createFolder.writeFile(filename, solution);
        }
    }

    public List<Individual> findBestsIndividuals(List<Individual> population){
        ArrayList<Individual> newPopulation = new ArrayList<Individual>();
        ArrayList<Individual> populationCopy = new ArrayList<Individual>();
        for (Individual individual : population){
            populationCopy.add(individual);
        }

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

        return newPopulation;
    } 

    public Individual getParent(List<Individual> populationCandidate) throws Exception{
        if (randomGenarationGA.nextDouble() > 1){
            // Torneio
            return TournamentSelection(populationCandidate);
        } else {
            // Aleatório tendencioso 
            return BiasedRandomSelection(populationCandidate);
        }
    }

    private Individual TournamentSelection(List<Individual> populationCandidate){
        if (populationCandidate.size() >= 2){
            Individual[] candidates = WorldHelper.GetCandidateParents(populationCandidate);
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
            return WorldHelper.BiasedRandomSelection(populationCandidate);
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
        return WorldHelper.DoCrossoverUniforme(individualA, individualB);
    }

    private Individual[] mutate(Individual individualA, Individual individualB)
    {
        return WorldHelper.Mutate(individualA, individualB);
    }
}
