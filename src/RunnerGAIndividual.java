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

        String folder = "15-07-21_19-05-31_NSFNet_YEN_RunnerGATraining";
		String filename = "Solution_110_3.csv";

		Individual individual = WorldHelper.ReadIndividualFromFile(folder, filename);

        simulation.simulateMultiLoadGA(individual);

        final long geralEndTime = System.currentTimeMillis();

		folderToSave.writeDone(((double)(geralEndTime - geralInitTime))/1000);	
        
    }
}
