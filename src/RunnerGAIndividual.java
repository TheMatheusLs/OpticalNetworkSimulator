package src;

import java.util.ArrayList;
import java.util.List;

import src.GeneticAlgorithm.Config;
import src.GeneticAlgorithm.Individual;
import src.GeneticAlgorithm.Helpers.WorldHelper;
import src.Save.CreateFolder;
import src.Simulation.Simulation;

public class RunnerGAIndividual {
    public static void main(String[] args) throws Exception {
        
        final long geralInitTime = System.currentTimeMillis();

        CreateFolder folderToSave = new CreateFolder("SimpleIndividualGA");
        folderToSave.writeParameters();

        Simulation simulation = new Simulation();
        simulation.initialize(folderToSave);

        //Individual individual = WorldHelper.GenerateIndividualInteger(Config.numberOfNodes, 0);

        String folder = "24-07-21_15-36-22_NSFNet_YEN_RunnerGATraining_Good";
		String filename = "Solution_75_1.csv";

		Individual individual = WorldHelper.ReadIndividualFromFile(folder, filename);

        simulation.simulateMultiLoadGA(individual);

        final long geralEndTime = System.currentTimeMillis();

		folderToSave.writeDone(((double)(geralEndTime - geralInitTime))/1000);	
        
    }
}
