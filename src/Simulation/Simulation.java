package src.Simulation;

import src.Structure.Topology.Topology;
import src.Save.CreateFolder;

public class Simulation {

    Topology topology;

    public void initialize(CreateFolder folderToSave) throws Exception{

        //Adiciona o cabeçalho dos resultados
		folderToSave.writeInResults("networkLoad,meanPb,pbDesviation,pbErro,pbMargem,meanSimulationTime");

        this.topology = new Topology();
        System.out.println(this.topology);

        
    }
}
