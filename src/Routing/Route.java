package src.Routing;

import java.util.ArrayList;
import java.util.List;

import src.ParametersSimulation;
import src.Structure.OpticalLink;
import src.Structure.OpticalSwitch;
import src.Structure.Topology.Topology;

public class Route {
    /**
     * @brief Topology used in this route.
     */
    Topology topology;
    /**
     * @brief Container of the nodes indexes of this path.
     */
    List<Integer> path;
    /**
     * @brief Container of the nodes pointers of this path.
     */
    List<OpticalSwitch> pathNodes;
    
    List<OpticalLink> pathLinks;
    /**
     * @brief Cost of the route.
     */
    double cost;

    int origin;
    int destination;

    int K;

    private List<OpticalLink> upLink;
	private List<OpticalLink> downLink;

	private List<Route> conflictRoutes;

	private short[] slotOcupation;

    public Route() {		
		this.upLink = new ArrayList<OpticalLink>();
		this.downLink = new ArrayList<OpticalLink>();
	}

    public Route(List<Integer> path, Topology topology){
        this.topology = topology;
        this.path = path;
        this.pathNodes = new ArrayList<OpticalSwitch>();
        this.pathLinks = new ArrayList<OpticalLink>();
        this.cost = 0.0;

        this.K = 1;

        this.origin = path.get(0);
        this.destination = path.get(path.size()-1);

        for(int it: this.path){
            this.pathNodes.add(this.topology.getNode(it));
        }
        
        for(int a = 0; a < this.pathNodes.size()-1; a++){
            pathLinks.add(topology.getLink(pathNodes.get(a).getNodeId(), pathNodes.get(a+1).getNodeId()));
        }
        
        this.setCost();

        this.resetSlotValue();
        
        this.upLink = new ArrayList<OpticalLink>();
        this.downLink = new ArrayList<OpticalLink>(); 

        for(int j = 1; j < path.size(); j++){
            this.upLink.add(topology.getNetworkLinks()[path.get(j-1)][path.get(j)]); 
        }
        if(ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){
            for(int j = (path.size()-1); j > 0; j--){
                this.downLink.add(topology.getNetworkLinks()[path.get(j)][path.get(j-1)]);
            }
        }		
    }

    public void setK(int k) {
        K = k;
    }

    public int getK() {
        return K;
    }

    public List<OpticalLink> getUpLink() {
		return this.upLink;
	}

    public List<OpticalLink> getDownLink() {
		return this.downLink;
	}

    @Override
    public String toString() {

        String txt = String.format("Rota: Origem = %d, Destino = %d, Custo = %f, K = %d, Caminho = ", this.origin, this.destination, this.cost, this.K);
        
        for (int p = 0; p < this.path.size() - 1; p++){
            txt += String.format("%d, ", this.path.get(p));
        }
        txt += String.format("%d\n", this.path.get(this.path.size() - 1));

        return txt;
    }

    boolean isEquals(Route right) {
    
        if(right.path == this.path)
            return true;
        
        return false;
    }
    
    boolean checkShareLink(Route route) {
        
        for(int hop = 0; hop < this.getNumHops(); hop++){
            
            for(int hop2 = 0; hop2 < route.getNumHops(); hop2++){
                
                if(this.getLink(hop) == route.getLink(hop2))
                    return true;
            }
        }
        return false;
    }
    
    boolean isSmallerThan(Route right) {
        
        return right.getCost() > this.getCost();
    }
    
    boolean isBiggerThan(Route right) {
        
        return right.getCost() < this.getCost(); 
    }
    
    int getOrNodeId() {
        assert(this.path.size() > 0);
        
        return this.path.get(0);
    }
    
    OpticalSwitch getOrNode() {
        assert(this.pathNodes.size() > 0);
        
        return this.pathNodes.get(0);
    }
    
    int getDeNodeId() {
        assert(this.path.size() > 0);
        
        return this.path.get(this.path.size()-1);
    }
    
    OpticalSwitch getDeNode() {
        assert(this.pathNodes.size() > 0);
        
        return this.pathNodes.get(this.pathNodes.size()-1);
    }
    
    int getNodeId(int index) {
        assert(index >= 0 && index < this.path.size());
        
        return this.path.get(index);
    }
    
    public OpticalSwitch getNode(int index) {
        assert(index >= 0 && index < this.pathNodes.size());
        
        return this.pathNodes.get(index);
    }
    
    List<OpticalSwitch> getNodes() {
        return pathNodes;
    }
    
    int getNumHops() {
        return this.path.size() - 1;
    }
    
    public int getNumNodes() {
        return this.path.size();
    }
    
    public List<Integer> getPath() {
        return this.path;
    }
    
    public double getCost() {
        return this.cost;
    }
    
    void setCost(double cost) {
        assert(cost >= 0.0);
        
        this.cost = cost;
    }
    
    void setCost() {
        OpticalLink link;
        double cost = 0.0;
        
        for(int a = 0; a < this.getNumHops(); a++){
            link = this.topology.getLink(this.path.get(a), this.path.get(a+1));
            cost += link.getCost();
        }
        
        this.setCost(cost);
    }
    
    public OpticalLink getLink(int index){
        assert(index < this.getNumHops());
        
        return this.topology.getLink(this.getNodeId(index), this.getNodeId(index + 1));
    }
    
    List<OpticalLink> getLinks(Route route) {
        List<Integer> path = route.getPath();
        List<OpticalLink> links = new ArrayList<OpticalLink>();
    
        for(int it : path){
            links.add(this.getLink(it));
        }
        return links;
    }
    
    public void setAllNodesWorking() {
        
        for(OpticalSwitch it: this.pathNodes){
            it.setNodeState(true);
        }
    }
    
    public Route createPartialRoute(int ind1, int ind2) {
        assert(ind2 >= ind1 && ind1 < path.size() && ind2 < path.size());
        List<Integer> newPath = new ArrayList<Integer>();
        
        for(int a = ind1; a <= ind2; a++){
            newPath.add(this.getNodeId(a));
        }
        
        return new Route(newPath, topology);
    }
    
    public Route addRoute(Route route) {
        assert(this.getDeNode() == route.getOrNode());
        List<Integer> newPath = this.getPath();
        
        for(int a = 1; a < route.getNumNodes(); a++){
            newPath.add(route.getNodeId(a));
        }
        
        return new Route(newPath, topology);
    }

    public short getSlotValue(int index){
		return this.slotOcupation[index];
	}

    public void setConflitList(List<Route> conflictRoutes){
		this.conflictRoutes = conflictRoutes;
	}

	public List<Route> getConflictRoute(){
		return this.conflictRoutes;
	}

	public void resetSlotValue(){
		// Criar um vetor de disponibilidade 
		this.slotOcupation = new short[ParametersSimulation.getNumberOfSlotsPerLink()];
	}

    public void incrementSlotsOcupy(List<Integer> slots){

		for (int s: slots){
			this.incrementSlots(s);

			assert ((this.slotOcupation[s] <= this.conflictRoutes.size()) || (this.slotOcupation[s] > 0)):
			"Ocupação fora do limite";

			for (Route route : this.conflictRoutes){
				route.incrementSlots(s);

				assert((route.slotOcupation[s] <= route.conflictRoutes.size()) || (route.slotOcupation[s] > 0)):
				"Ocupação fora do limite";
			}
		}
	}

	public void decreasesSlotsOcupy(List<Integer> slots){

		for (int s: slots){
			this.decreasesSlots(s);

			assert((this.slotOcupation[s] <= this.conflictRoutes.size()) || (this.slotOcupation[s] > 0));

			for (Route route : this.conflictRoutes){
				route.decreasesSlots(s);

				assert((route.slotOcupation[s] <= route.conflictRoutes.size()) || (route.slotOcupation[s] > 0));
			}
		}
	}

	void incrementSlots(int slot){
		this.slotOcupation[slot]++;
	}

	void decreasesSlots(int slot){
		this.slotOcupation[slot]--;
	}
}
