package src;

import src.GeneticAlgorithmMultiMSCL.ConfigMSCL;
import src.GeneticAlgorithmMultiMSCL.IndividualMSCL;
import src.GeneticAlgorithmMultiMSCL.Helpers.WorldHelper;
import src.Save.CreateFolder;
import src.Simulation.Simulation;

public class RunnerGAMultiMSCLIndividual {
    public static void main(String[] args) throws Exception {
        
        final long geralInitTime = System.currentTimeMillis();

        CreateFolder folderToSave = new CreateFolder("SimpleIndividualGA");
        folderToSave.writeParameters();

        Simulation simulation = new Simulation();
        simulation.initialize(folderToSave);

        IndividualMSCL individual = WorldHelper.GenerateIndividualInteger(ConfigMSCL.numberOfNodes, true, simulation.getRouting().getAllRoutes());

        // String folder = "27-07-21_09-00-54_NSFNet_YEN_RunnerGATraining_OK_H-H-RSA_260E";
		// String filename = "Solution_270_1.csv";

		// Individual individual = WorldHelper.ReadIndividualFromFile(folder, filename);

        simulation.simulateMultiLoadGA(individual);

        final long geralEndTime = System.currentTimeMillis();

		folderToSave.writeDone(((double)(geralEndTime - geralInitTime))/1000);	
        
    }
}
