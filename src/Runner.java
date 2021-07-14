package src;

import src.Save.CreateFolder;
import src.Simulation.Simulation;

public class Runner {
    public static void main(String[] args) throws Exception {

        final long geralInitTime = System.currentTimeMillis();

        CreateFolder folderToSave = new CreateFolder("Teste");
        folderToSave.writeParameters();

        Simulation simulation = new Simulation();
        simulation.initialize(folderToSave);



        final long geralEndTime = System.currentTimeMillis();

		folderToSave.writeDone(((double)(geralEndTime - geralInitTime))/1000);	
    }
}
