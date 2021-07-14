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

        System.out.println(this);
    }

    public int getNumNodes() {
        return numNodes;
    }

    public void load() throws Exception{
        
        double[][] topologyDimention = getDimention();
        
        final NetworkTopology NetworkTopologyInstance = new NetworkTopology(topologyDimention);

		networkLinks = NetworkTopologyInstance.getNetworkAdjacencyMatrix();
        listOfNodes = NetworkTopologyInstance.getListOfNodes();

        this.maxLength = NetworkTopologyInstance.getMaxLength();
        this.numNodes = NetworkTopologyInstance.getNumberOfNodes();

        this.setLinksInitCost();
        this.setNodesNeighbors();
    }

    public void setAllLinksWorking() {
    
        for (int o = 0; o < this.numNodes; o++){
            for (int d = 0; d < this.numNodes; d++){
                if (this.networkLinks[o][d] != null){
                    this.networkLinks[o][d].setLinkState(true);
                }
            }
        }
    }

    public void checkIfIsClean() throws Exception{
        for (int s = 0; s < networkLinks.length;s++){
			for (int d = 0; d < networkLinks.length;d++){
				if (networkLinks[s][d] != null){
					for (int i = 0; i < ParametersSimulation.getNumberOfSlotsPerLink();i++){
						if (networkLinks[s][d].getPowerA(i) != 0){
							throw new Exception("As rotas não foram limpas corretamente");
						}
					}
				}
			}
		}
    }

    public OpticalLink[][] getNetworkLinks() {
        return networkLinks;
    }

    public double getMaxLength() {
        return maxLength;
    }
    
    public void setNodesNeighbors() {
        OpticalLink auxLink;
        
        for(OpticalSwitch node1: listOfNodes){
            
            for(OpticalSwitch node2: listOfNodes) {
                
                if(node1.isEquals(node2)){
                    continue;
                }
                auxLink = this.getLink(node1.getOpticalSwitchId(), node2.getOpticalSwitchId());
                
                if(auxLink == null)
                    continue;

                node1.addNeighborNode(node2);
            }
        }
    }

    public void setLinksInitCost() throws Exception {
        
        if (ParametersSimulation.getLinkCostType().equals(ParametersSimulation.LinkCostType.Hops)){
            for (int o = 0; o < this.numNodes; o++){
                for (int d = 0; d < this.numNodes; d++){
                    if (this.networkLinks[o][d] != null){
                        this.networkLinks[o][d].setCost(1.0);
                    }
                }
            }
        } else {
            if (ParametersSimulation.getLinkCostType().equals(ParametersSimulation.LinkCostType.Length)){
                for (int o = 0; o < this.numNodes; o++){
                    for (int d = 0; d < this.numNodes; d++){
                        if (this.networkLinks[o][d] != null){
                            this.networkLinks[o][d].setCost(this.networkLinks[o][d].getLength());
                        }
                    }
                }
            } else {
                if (ParametersSimulation.getLinkCostType().equals(ParametersSimulation.LinkCostType.Length)){
                    for (int o = 0; o < this.numNodes; o++){
                        for (int d = 0; d < this.numNodes; d++){
                            if (this.networkLinks[o][d] != null){
                                this.networkLinks[o][d].setCost(this.networkLinks[o][d].getLength() / this.getMaxLength());
                            }
                        }
                    }
                } else {
                    throw new Exception("Invalid link cost type");
                }
            }
        }
    }

    double[][] getDimention() throws Exception{
        if (ParametersSimulation.getTopologyType().equals(ParametersSimulation.TopologyType.NSFNet)){
            return NsfnetDimention.getLength();
        } else {
            throw new Exception("Topologia inválida!");
        }
    }

    public OpticalLink getLink(int indexOrNode, int indexDeNode) {
        assert(indexOrNode < this.getNumNodes());
        assert(indexDeNode < this.getNumNodes());
        
        return this.networkLinks[indexOrNode][indexDeNode];
    }

    public OpticalSwitch getNode(int index) {

        assert(index < this.getNumNodes());

        return this.listOfNodes.get(index);
    }
}
