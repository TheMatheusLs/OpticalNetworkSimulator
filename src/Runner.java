package src;

import src.Save.CreateFolder;
import src.Simulation.Simulation;

public class Runner {
    public static void main(String[] args) throws Exception {

        final long geralInitTime = System.currentTimeMillis();

        CreateFolder folderToSave = new CreateFolder("MultiLoad");
        folderToSave.writeParameters();

        Simulation simulation = new Simulation();
        simulation.initialize(folderToSave);

        //simulation.simulateSingle(ParametersSimulation.minLoadNetwork);
        simulation.simulateMultiLoad();

        final long geralEndTime = System.currentTimeMillis();

		folderToSave.writeDone(((double)(geralEndTime - geralInitTime))/1000);	
    }
}
