package src.CallRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.ParametersSimulation;
import src.ParametersSimulation.CallRequestType;
import src.Routing.Route;
import src.Structure.OpticalLink;
import src.Structure.OpticalSwitch;
import src.Types.ModulationLevelType;
import src.GeneralClasses.Function;
/**
 * Descreve a requisi��o de conex�o usada no simulador.
 * @author Andr� 
 */
public class CallRequest{
	/**
	 * Identificador do n� fonte.
	 * @author Andr� 			
	 */	
	private int sourceId;
	/**
	 * Quantidade de slots requerida.
	 * @author Andr� 			
	 */	
    private transient int reqNumbOfSlots;
    /**
	 * Lista dos slots de frequencia.
	 * @author Andr� 			
	 */	
    private final List<Integer> frequencySlots;
    /**
	 * Taxa de transmiss�o adotada pela requisi��o de chamada.
	 * @author Andr� 			
	 */	
    private int bitRate;
    /**
	 * Identificador do n� destino.
	 * @author Andr� 			
	 */	
    private int destinationId;
    /**
	 * Identificador da requisi��o de chamada.
	 * @author Andr� 			
	 */	
    private int callRequestId;
    /**
	 * Lista de poss�veis taxas de transmiss�o na rede.
	 * @author Andr� 			
	 */	
    private final transient List<Integer> possibleBitRates;
    /**
	 * Tempo de queda da requisi��o de chamada na rede.
	 * @author Andr� 			
	 */	
    private double decayTime;
    /**
	 * Dura��o da requisi��o de chamada na rede.
	 * @author Andr� 			
	 */	
    private double duration;
    /**
	 * Tipo da requisi��o de chamada.
	 * @author Andr� 			
	 */	
    private CallRequestType callRequestType;
    /**
	 * Lista dos optical link que comp�e a rota inbound da requisi��o de chamada.
	 * @author Andr� 			
	 */	
    private final transient List<OpticalLink> uplink;
    /**
	 * Lista dos optical link que comp�e a rota outbound da requisi��o de chamada.
	 * @author Andr� 			
	 */	
    private final transient List<OpticalLink> downlink;
    /**
   	 * Tipo do formato de modula��o adotado pela requisi��o de chamada.
   	 * @author Andr� 			
   	 */	
    private ModulationLevelType modulationType;
	private int ReqID;
	private Route route;
    /**
	 * Construtor da classe.
	 * @param callRequestId
	 * @param bitRate
	 * @param callRequestType
	 * @author Andr� 
	 */
	public CallRequest(final int callRequestId, final int[] bitRate, final CallRequestType callRequestType) {

		this.callRequestId = callRequestId;
		this.callRequestType = callRequestType; 
		this.sourceId = -1;
		this.destinationId = -1;
		this.ReqID = -1;

		this.possibleBitRates = new ArrayList<Integer>();

		this.uplink = new ArrayList<OpticalLink>();
		this.downlink = new ArrayList<OpticalLink>();

		this.frequencySlots = new ArrayList<Integer>();	

		for(int x=0;x<bitRate.length;x++){
			this.possibleBitRates.add(bitRate[x]);
		}
	}
	/**
	 * M�todo para retornar o tipo do formato de
	 * modula��o.
	 * @return O atributo modulationType
	 * @author Andr� 			
	 */	    
    public ModulationLevelType getModulationType() {
		return modulationType;
	}
    /**
	 * M�todo para configurar o tipo do formato de
	 * modula��o.
	 * @param modulationType
	 * @author Andr� 			
	 */
	public void setModulationType(final ModulationLevelType modulationType) {
		this.modulationType = modulationType;
	}
	/**
	 * M�todo para retornar a rota inbound.
	 * @return O atributo uplink
	 * @author Andr� 			
	 */	
	public List<OpticalLink> getOpticalLinkInbound(){
    	return this.uplink;
    }
	/**
	 * M�todo para retornar a rota outbound.
	 * @return O atributo downlink
	 * @author Andr� 			
	 */	    
    public List<OpticalLink> getOpticalLinkOutbound(){
    	return this.downlink;
    }
    /**
	 * M�todo para configurar o tipo de requisi��o de chamada.
	 * @param callRequesttype
	 * @author Andr� 			
	 */	    
    public void setCallRequestType(final CallRequestType callRequesttype){
    	this.callRequestType = callRequesttype;
    }  
    /**
	 * M�todo para retornar o tipo de requisi��o de chamada.
	 * @return O atributo callRequestType
	 * @author Andr� 			
	 */	     
    public CallRequestType getCallRequestType(){
    	return this.callRequestType;
    }
    /**
	 * M�todo para configurar os slots requeridos.
	 * @param frequencySlots
	 * @author Andr� 			
	 */	    
    public void setFrequencySlots(final List<Integer> frequencySlots){
    	this.frequencySlots.clear();
    	
    	if(this.reqNumbOfSlots == frequencySlots.size()){    		
    		for(int i=0;i<this.reqNumbOfSlots;i++){
    			this.frequencySlots.add(frequencySlots.get(i));
    		}    		
    	}    	
    }
    /**
	 * M�todo para retornar os slots requeridos.
	 * @return O atributo frequencySlots
	 * @author Andr� 			
	 */    
    public List<Integer> getFrequencySlots(){
    	return this.frequencySlots;
    }
    /**
	 * M�todo para configurar o n� fonte da requisi��o de chamada.
	 * @param sourceId
	 * @author Andr� 			
	 */	 
    public void setSourceId(final int sourceId){
    	this.sourceId = sourceId;
	}
    /**
	 * M�todo para retornar o identificador do n� fonte da requisi��o de chamada.
	 * @return O atributo sourceId
	 * @author Andr� 			
	 */    
    public int getSourceId(){
    	return this.sourceId;
    }
    /**
	 * M�todo para configurar o n� destino da requisi��o
	 * de chamada.
	 * @param destinationId
	 * @author Andr� 			
	 */	    
    public void setDestinationId(final int destinationId){
    	this.destinationId = destinationId; 
    }
    /**
	 * M�todo para retornar o n� destino da requisi��o
	 * de chamada.
	 * @return O atributo destinationId
	 * @author Andr� 			
	 */    
    public int getDestinationId(){
    	return this.destinationId;
    }
    /**
   	 * M�todo para retornar a lista de slots requisitados.
   	 * @return O atributo reqNumbOfSlots
   	 * @author Andr� 			
   	 */      
    public int getRequiredNumberOfSlots(){
    	return this.reqNumbOfSlots;
	}
    /**
	 * M�todo para configurar a quantidade de slots requeridos.
	 * @param reqNumbOfSlots
	 * @author Andr� 			
	 */
	public void setRequiredNumberOfSlots(final int reqNumbOfSlots){
		this.reqNumbOfSlots = reqNumbOfSlots;
	}
	/**
	 * M�todo para configurar o identificador da requisi��o de
	 * chamada.
	 * @param callRequestId
	 * @author Andr� 			
	 */	
	public void setCallRequestId(final int callRequestId){
		this.callRequestId = callRequestId;
	}
	/**
   	 * M�todo para retornar o identificador da requisi��o de
	 * chamada.
	 * @return O atributo callRequestId
   	 * @author Andr� 			
   	 */ 
	public int getCallRequestId(){
		return this.callRequestId;
	}
	/**
   	 * M�todo para retornar o tempo de dura��o da requisi��o de
	 * chamada.
	 * @return O atributo duration
   	 * @author Andr� 			
   	 */	
	public double getDuration(){
		return this.duration;
	}
	/**
	 * M�todo para configurar o tempo de dura��o da requisi��o de
	 * chamada.
	 * @param time
	 * @author Andr� 			
	 */		
	public void setDuration(final double time){
		this.duration = time;
	}
	/**
   	 * M�todo para retornar a taxa de transmiss�o adotada.
   	 * @return O atributo bitRate
   	 * @author Andr� 			
   	 */		
	public int getBitRate(){
		return this.bitRate;
	}
	/**
	 * M�todo para configurar a taxa de transmiss�o adotada.
	 * @param bitRate
	 * @author Andr� 			
	 */		
	public void setBitRate(final int bitRate){
		this.bitRate = bitRate;
	}
	/**
   	 * M�todo para sortear a taxa de transmiss�o.
   	 * @author Andr� 			
   	 */		
	public void sortBitRate(Random randomGeneration){
		final int size = this.possibleBitRates.size();
		final int number = (int) (randomGeneration.nextDouble() * size);
        this.bitRate = this.possibleBitRates.get(number);
	}
	/**
   	 * M�todo para retornar o tempo de queda da requisi��o 
   	 * de chamada.
   	 * @return O atributo decayTime
   	 * @author Andr� 			
   	 */	
	public double getDecayTime(){
		return this.decayTime;
	}
	/**
   	 * M�todo para configurar o tempo de queda da requisi��o 
   	 * de chamada.
   	 * @param time
   	 * @author Andr� 			
   	 */		
	public void setDecayTime(final double time){
		this.decayTime = time;
	}
	/**
   	 * M�todo para configurar o tempo de dura��o e queda da requisi��o 
   	 * de chamada.
   	 * @param time
   	 * @param meanDurationRate
   	 * @author Andr� 			
   	 */	
	public void setTime(final double time, final double meanDurationRate, Random randomGeneration){
		
		this.duration = Function.exponentialDistribution(meanDurationRate, randomGeneration);;
		this.decayTime = time + this.duration;
	}		
	/**
   	 * M�todo para configurar a requisi��o de chamada.
   	 * @param  optLinkInPath
   	 * @param optLinkOutPath
   	 * @param listOfNodes
   	 * @author Andr� 			
   	 */	
	public void allocate(final List<OpticalLink> optLinkInPath, final List<OpticalLink> optLinkOutPath, final List<OpticalSwitch> listOfNodes){
		
		this.uplink.clear();
		this.downlink.clear();
		
		for(int i=0;i<optLinkInPath.size();i++){			
			this.uplink.add(optLinkInPath.get(i));	
		}	
		
		if(this.callRequestType.equals(ParametersSimulation.CallRequestType.Bidirectional)){			
			for(int i=0;i<optLinkOutPath.size();i++){
				this.downlink.add(optLinkOutPath.get(i));
			}
		}			
		
		for(int i=0;i<this.frequencySlots.size();i++){
			
			final OpticalLink optLinkInbound = this.uplink.get(0);
			final int freqSlotIn = this.frequencySlots.get(i);
			final OpticalSwitch optSwit = Function.getNode(sourceId, listOfNodes);
			final double laserPower = Function.getLaserPowerInNode(optSwit);
			
			Function.allocateSlotInLink(freqSlotIn, laserPower, optLinkInbound);
						
			for(int j=1;j<this.uplink.size();j++){
				
				final int source = this.uplink.get(j).getSource();				
				final OpticalLink optLink = this.uplink.get(j-1);
				final int fSlot = this.frequencySlots.get(i);
				final OpticalSwitch opticalSwitch = Function.getNode(source, listOfNodes);
				final double powerBinSlot = Function.getPowerBInLink(fSlot,optLink); //Pega a pot�ncia na sa�da do �ltimo optical link j� analisado.
				final double switchAten = Function.getSwitchAtenuation(opticalSwitch);				
				final double potOut  = powerBinSlot*switchAten;				
				this.uplink.get(j).allocate(this.frequencySlots.get(i), potOut);

			}
			
			//Alocar a volta da chamada
			if(this.callRequestType.equals(ParametersSimulation.CallRequestType.Bidirectional)){
				
				final OpticalLink optLinkOutbound = this.downlink.get(0);
				final int freqSlotOut = this.frequencySlots.get(i);
				final OpticalSwitch optSwitch = Function.getNode(sourceId, listOfNodes);
				final double lasPower = Function.getLaserPowerInNode(optSwitch);
				
				Function.allocateSlotInLink(freqSlotOut, lasPower, optLinkOutbound);
				
				for(int j=1;j<this.downlink.size();j++){            		
            		final int source = this.downlink.get(j).getSource();            		
            		final OpticalLink optLink = this.downlink.get(j-1);
    				final int fSlot = this.frequencySlots.get(i);
    				final OpticalSwitch opticalSwitch = Function.getNode(source, listOfNodes);
    				final double powerBinSlot = Function.getPowerBInLink(fSlot,optLink);
    				final double switchAten = Function.getSwitchAtenuation(opticalSwitch);				
    				final double potOut  = powerBinSlot*switchAten;    				
    				this.downlink.get(j).allocate(this.frequencySlots.get(i), potOut);
            
            	}
			}
		}		
	}
	/**
   	 * M�todo para retirar a requisi��o de chamada da rede.
   	 * @author Andr� 			
   	 */		
	public void desallocate(){
		
		  for(int y=0;y<this.frequencySlots.size();y++){
			  
			  for(int i=0;i<this.uplink.size();i++){
				  this.uplink.get(i).deallocate(this.frequencySlots.get(y));
			  }
			  
			  if(this.callRequestType.equals(ParametersSimulation.CallRequestType.Bidirectional)){
				  
				  for(int i=0;i<this.downlink.size();i++){
					  this.downlink.get(i).deallocate(this.frequencySlots.get(y));
				  }
			  }
		  }
	}   

	public List<OpticalLink> getOpticalLinks(){
		return this.uplink;
	}

	public void setReqID(int reqID){
		this.ReqID = reqID;
	}

	public int getReqID(){
		return this.ReqID;
	}

	public void setRoute(Route route){
		this.route = route;
	}

	public Route getRoute(){
		return this.route;
	}
}
