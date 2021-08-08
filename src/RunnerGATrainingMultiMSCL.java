package src;

import src.Save.CreateFolder;
import src.GeneticAlgorithmMultiMSCL.ConfigMSCL;
import src.GeneticAlgorithmMultiMSCL.World;

public class RunnerGATrainingMultiMSCL {
    public static void main(String[] args) throws Exception {

        final long geralInitTime = System.currentTimeMillis();

        CreateFolder folderToSave = new CreateFolder("RunnerGATrainingMultiMSCL");
        folderToSave.writeParameters();
        folderToSave.writeFile("ConfigGA.txt", ConfigMSCL.printParameters());

        // Algoritmo Genético Mono Objetivo
		World world = new World(folderToSave);
		world.Spawn();

		while(world.generationsCount < ConfigMSCL.maxGenerations){
			// Atualiza a visualização
			System.out.println(String.format("%n*** Geração %d ***%n", world.generationsCount));
			
			// Realiza o GA
			world.DoGeneration();
		}

		//world.saveBestsIndividuals();

        final long geralEndTime = System.currentTimeMillis();

		folderToSave.writeDone(((double)(geralEndTime - geralInitTime))/1000);	
    }
}
