package src.Structure.Topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import src.Parameters.SimulationParameters;
import src.Structure.OpticalLink;
import src.Structure.OpticalSwitch;
import src.Structure.Topology.gain_algorithm.GainAlgorithm;

public final class NetworkTopology{

	private final transient GainAlgorithm gainAlgorithm = GainAlgorithm.getGainInstance(); // Instância do algoritmo de configuração de ganho dos amplificadores
	private final transient int numberOfNodes; // Número de nós da rede
	private final transient double switchLoss; // Perda do switch
	private final transient double laserPower; // Potência inicial do laser
	private final transient double osnr; // OSNR de entrada do laser
	private transient List<OpticalLink> opticalLinkList; // Lista de optical links
	private transient OpticalLink network[][]; // Matriz de adjacência da rede
	private transient List<OpticalSwitch> nodes;
    private final double[][] lengthsTopology;

	double maxLength;
	
	public NetworkTopology(double[][] lengthsTopology) {
        this.lengthsTopology = lengthsTopology;
		this.numberOfNodes = this.lengthsTopology.length;
		this.switchLoss = SimulationParameters.getSwitchLoss();
		this.laserPower = SimulationParameters.getLaserPower();
		this.osnr = SimulationParameters.getOSNRIn();
		this.maxLength = 0.0;		
	}

	public int getNumberOfNodes(){
		return this.numberOfNodes;
	}

	public double getMaxLength(){
		return this.maxLength;
	}

	/**
	 * Método para construit a matrix de adjacência da rede.			
	 */	
	private void buildNetworkAdjacencyMatrix() throws Exception{
		
		double length = 0.0;
		int linkId = 0;
		int srlgId = 0;
			
		this.network = new OpticalLink[this.numberOfNodes][this.numberOfNodes];			

		for(int source=0;source<this.numberOfNodes;source++){
			for(int destination=0;destination<this.numberOfNodes;destination++){
				if(this.lengthsTopology[source][destination] != Double.MAX_VALUE && network[source][destination]==null){
										
					length = this.lengthsTopology[source][destination];
					linkId++;
					srlgId++;

					if (this.maxLength < length){
						this.maxLength = length;
					}
					
					network[source][destination] = new OpticalLink(linkId,source,destination,srlgId,length);
					gainAlgorithm.configureGain(network[source][destination]);
					
					if(network[destination][source]==null){							
						linkId++;							
						network[destination][source] = new OpticalLink(linkId,destination,source,srlgId,length);
						gainAlgorithm.configureGain(network[destination][source]);
					}						
				}
			}
		}			
	}
	
	private void buildOpticalLinkList() throws Exception{
		
		this.opticalLinkList = new Vector<OpticalLink>();
		
		int numberOfLinkBetweenNodes = 2; //set the numbers of links for each pair of nodes (same direction).

		double length = 0.0;
		int linkId = 0;
		int srlgId = 0;			
		
		for(int source=0;source<this.numberOfNodes;source++){
			for(int destination=0;destination<this.numberOfNodes;destination++){
				if(this.lengthsTopology[source][destination] != Double.MAX_VALUE){					
					for(int i=0;i<numberOfLinkBetweenNodes;i++){						
						length = this.lengthsTopology[source][destination];
						linkId++;
						srlgId++;						
						this.opticalLinkList.add(new OpticalLink(linkId,source,destination,srlgId,length));	
					}											
				}
			}
		}		
	}
	/**
	  * Método para resetar a matriz de adjac�ncia da rede.
	  * @author Andr� 			
	  */
	public void resetNetworkAdjacencyMatrix(){		
		for(int x=0;x<this.numberOfNodes;x++){
			for(int y=0;y<this.numberOfNodes;y++){
				if(network[x][y] != null){
					network[x][y].eraseOpticalLink(); // NOPMD by Andr� on 21/06/17 13:12										
				}								
			}
		}		
	}
	/**
	  * M�todo para construir a lista de n�s da rede.
	  * @author Andr� 			
	  */
	public void buildNodes(){		
		this.nodes = new ArrayList<OpticalSwitch>();		
		for(int x=0;x<this.numberOfNodes;x++){
			final OpticalSwitch node = new OpticalSwitch(x,this.switchLoss,this.laserPower,this.osnr); // NOPMD by Andr� on 21/06/17 13:12
			nodes.add(node);			
		}		
	}
	/**
	  * M�todo para retornar a matriz de adjac�ncia da rede.
	  * @author Andr� 			
	  */
	public OpticalLink[][] getNetworkAdjacencyMatrix() throws Exception {
		this.buildNetworkAdjacencyMatrix();
		return this.network; // NOPMD by Andr� on 21/06/17 13:11
	}
	/**
	  * Método para retornar a lista de enlaces da rede.			
	  */
	public List<OpticalLink> getOpticalLinkList() throws Exception {
		this.buildOpticalLinkList();
		return this.opticalLinkList;
	}
	/**
	  * Método para retornar a lista de nós da rede.			
	  */
	public List<OpticalSwitch> getListOfNodes() {
		this.buildNodes();
		return this.nodes;
	}
}