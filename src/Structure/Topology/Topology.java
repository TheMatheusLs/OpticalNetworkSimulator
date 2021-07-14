package src.Structure.Topology;

import java.util.ArrayList;
import java.util.List;

import src.ParametersSimulation;
import src.Structure.OpticalLink;
import src.Structure.OpticalSwitch;

public class Topology {
    /**
     * @brief Vector with all topology nodes 
     */
    List<OpticalSwitch> listOfNodes;
    /**
     * @brief Matrix with all topology links 
     */
    OpticalLink[][] networkLinks;
    /**
     * @brief Total number of nodes in the topology
     */
    int numNodes;
    /**
     * @brief Length of the longest link
     */
    double maxLength;

    @Override
    public String toString() {
        String txt = String.format("\t*** Topology ***\nNumber of nodes: %d\n", this.numNodes);

        txt += "** Nodes: \n";

        for (OpticalSwitch node : this.listOfNodes){
            txt += node + "\n";
        } 

        txt += "** Links: \n";

        for (int o = 0; o < this.numNodes; o++){
            for (int d = 0; d < this.numNodes; d++){
                if (this.networkLinks[o][d] != null){
                    txt += this.networkLinks[o][d] + "\n";
                }
            }
        }

        return txt;
    }

    public Topology() throws Exception {
        this.listOfNodes = new ArrayList<OpticalSwitch>();
        this.numNodes = 0;
        this.maxLength = 0.0;

        load();
    }

    public void load() throws Exception{
        
        double[][] topologyDimention = getDimention();
        
        final NetworkTopology NetworkTopologyInstance = new NetworkTopology(topologyDimention);

		networkLinks = NetworkTopologyInstance.getNetworkAdjacencyMatrix();
        listOfNodes = NetworkTopologyInstance.getListOfNodes();

        this.maxLength = NetworkTopologyInstance.getMaxLength();
        this.numNodes = NetworkTopologyInstance.getNumberOfNodes();
    }

    double[][] getDimention() throws Exception{
        if (ParametersSimulation.getTopologyType().equals(ParametersSimulation.TopologyType.NSFNet)){
            return NsfnetDimention.getLength();
        } else {
            throw new Exception("Topologia inválida!");
        }
    }
}
