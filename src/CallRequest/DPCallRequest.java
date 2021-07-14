package src.CallRequest; // NOPMD by Andr� on 20/06/17 14:51

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
 * Descreve a requisi��o de conex�o usada no cen�rio 
 * de prote��o dedicada no simulador.
 * @author Andr� 
 */
public class DPCallRequest{ // NOPMD by Andr� on 05/06/17 12:15
	/**
	 * Identificador do n� fonte.
	 * @author Andr� 			
	 */	
	private int sourceId;
	/**
	 * Identificador do n� destino.
	 * @author Andr� 			
	 */	
	private int destinationId;
	/**
	 * Quantidade de slots requerida no caminho de trabalho.
	 * @author Andr� 			
	 */	
    private transient int rNumOfSlotsInWP;
    /**
	 * Quantidade de slots requerida no caminho de prote��o.
	 * @author Andr� 			
	 */	
    private transient int rNumOfSlotsInPP;
    /**
     * Lista dos slots de frequencia no caminho de trabalho.
	 * @author Andr� 			
	 */	
    private final transient List<Integer> freqSlotsInWP;
    /**
	 * Lista dos slots de frequencia no caminho de prote��o.
	 * @author Andr� 			
	 */	
    private final transient List<Integer> freqSlotsInPP;
    /**
	 * Taxa de transmiss�o adotada pela requisi��o de chamada.
	 * @author Andr� 			
	 */	
    private int bitRate;
    /**
	 * Identificador da requisi��o de chamada.
	 * @author Andr� 			
	 */	
    private int callId;
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
	 * Caminho de trabalho.
	 * @author Andr� 			
	 */	
    private Route workingPath;
    /**
   	 * Caminho de prote��o.
   	 * @author Andr� 			
   	 */	
    private Route protectionPath;
    /**
   	 * Tipo do formato de modula��o adotado no caminho de trabalho.
   	 * @author Andr� 			
   	 */	
    private transient ModulationLevelType modTypeInWP;
    /**
   	 * Tipo do formato de modula��o adotado no caminho de prote��o.
   	 * @author Andr� 			
   	 */	
    private transient ModulationLevelType modTypeInPP;
    /**
	 * Construtor da classe.
	 * @param SurvCallReqId
	 * @param bitRates
	 * @param callRequestType
	 * @author Andr� 
	 */
	public DPCallRequest(final int SurvCallReqId, final int[] bitRates, final CallRequestType callRequestType) {
		
		this.setCallId(SurvCallReqId);
		this.callRequestType = callRequestType; 
		this.sourceId = -1;
		this.destinationId = -1;
		this.possibleBitRates = new ArrayList<Integer>();
		this.possibleBitRates.clear();
		this.workingPath = new Route();
		this.protectionPath = new Route();
		this.freqSlotsInWP = new ArrayList<Integer>();
		this.freqSlotsInPP = new ArrayList<Integer>();
		
		for(int x=0;x<bitRates.length;x++){
			this.possibleBitRates.add(bitRates[x]);
		}
	}
	/**
	 * M�todo para retornar o n�mero de slots requeridos no 
	 * caminho de trabalho.
	 * @author Andr� 			
	 */        	
    public int getRequiredNumberOfSlotsInWorkingPath() {
		return rNumOfSlotsInWP;
	}
    /**
	 * M�todo para configurar o n�mero de slots requeridos no 
	 * caminho de trabalho.
	 * @param reqNumbOfSlots
	 * @author Andr� 			
	 */
	public void setRequiredNumberOfSlotsInWorkingPath(final int reqNumbOfSlots) {
		this.rNumOfSlotsInWP = reqNumbOfSlots;
	}
	/**
	 * M�todo para retornar o n�mero de slots requeridos no 
	 * caminho de prote��o.
	 * @author Andr� 			
	 */
	public int getRequiredNumberOfSlotsInProtectionPath() {
		return rNumOfSlotsInPP;
	}
	/**
	 * M�todo para configurar o n�mero de slots requeridos no 
	 * caminho de prote��o.
	 * @param reqNumbOfSlots
	 * @author Andr� 			
	 */
	public void setRequiredNumberOfSlotsInProtectionPath(final int reqNumbOfSlots) {
		this.rNumOfSlotsInPP = reqNumbOfSlots;
	}
	/**
	 * M�todo para retornar o tipo de formato de modula��o usado no 
	 * caminho de trabalho.
	 * @author Andr� 			
	 */
	public ModulationLevelType getModulationTypeInWorkingPath() {
		return modTypeInWP;
	}
	/**
	 * M�todo para configurar o tipo de formato de modula��o usado no 
	 * caminho de trabalho.
	 * @param modulationType
	 * @author Andr� 			
	 */
	public void setModulationTypeInWorkingPath(final ModulationLevelType modulationType) {
		this.modTypeInWP = modulationType;
	}
	/**
	 * M�todo para retornar o tipo de formato de modula��o usado no 
	 * caminho de prote��o.
	 * @author Andr� 			
	 */
	public ModulationLevelType getModulationTypeInProtectionPath() {
		return modTypeInPP;
	}
	/**
	 * M�todo para configurar o tipo de formato de modula��o usado no 
	 * caminho de trabalho.
	 * @param modulationType
	 * @author Andr� 			
	 */
	public void setModulationTypeInProtectionPath(final ModulationLevelType modulationType) {
		this.modTypeInPP = modulationType;
	}
	/**
	 * M�todo para retornar o identificador da requisi��o de chamada.
	 * @author Andr� 			
	 */
	public int getCallId() {
		return callId;
	}
	/**
	 * M�todo para configurar o identificador da requisi��o de chamada.
	 * @param callId
	 * @author Andr� 			
	 */    
	public void setCallId(final int callId) {
		this.callId = callId;
	}
	/**
	 * M�todo para retornar o n� fonte da requisi��o de chamada.
	 * @author Andr� 			
	 */ 
	public int getSourceId() {
		return sourceId;
	}
	/**
	 * M�todo para configurar o n� fonte da requisi��o de chamada.
	 * @param sourceId
	 * @author Andr� 			
	 */	
	public void setSourceId(final int sourceId) {
		this.sourceId = sourceId;
	}
	/**
	 * M�todo para retornar o n� destino da requisi��o de chamada.
	 * @author Andr� 			
	 */
	public int getDestinationId() {
		return destinationId;
	}
	/**
	 * M�todo para configurar o n� destino da requisi��o de chamada.
	 * @param destinationId
	 * @author Andr� 			
	 */	
	public void setDestinationId(final int destinationId) {
		this.destinationId = destinationId;
	}
	/**
   	 * M�todo para retornar a taxa de transmiss�o adotada.
   	 * @author Andr� 			
   	 */	
	public int getBitRate() {
		return bitRate;
	}
	/**
	 * M�todo para configurar a taxa de transmiss�o adotada.
	 * @param bitRate
	 * @author Andr� 			
	 */
	public void setBitRate(final int bitRate) {
		this.bitRate = bitRate;
	}
	/**
   	 * M�todo para retornar os slots usados no caminho de trabalho.
   	 * @author Andr� 			
   	 */	
	public List<Integer> getFrequencySlotsInWorkingPath() {
		return freqSlotsInWP;
	}
	/**
	 * M�todo para configurar os slots usados no caminho de trabalho.
	 * @param frequencySlots
	 * @author Andr� 			
	 */
	public void setFrequencySlotsInWorkingPath(final List<Integer> frequencySlots) {		
		for(int x=0;x<frequencySlots.size();x++){
			this.freqSlotsInWP.add(frequencySlots.get(x));
		}		
	}
	/**
   	 * M�todo para retornar os slots usados no caminho de prote��o.
   	 * @author Andr� 			
   	 */
	public List<Integer> getFrequencySlotsInProtectionPath() {
		return freqSlotsInPP;
	}
	/**
	 * M�todo para configurar os slots usados no caminho de prote��o.
	 * @param frequencySlots
	 * @author Andr� 			
	 */
	public void setFrequencySlotsInProtectionPath(final List<Integer> frequencySlots) {
		for(int x=0;x<frequencySlots.size();x++){
			this.freqSlotsInPP.add(frequencySlots.get(x));
		}
	}
	/**
   	 * M�todo para retornar o tempo de queda da requisi��o 
   	 * de chamada.
   	 * @author Andr� 			
   	 */	
	public double getDecayTime() {
		return decayTime;
	}
	/**
   	 * M�todo para configurar o tempo de queda da requisi��o 
   	 * de chamada.
   	 * @param decayTime
   	 * @author Andr� 			
   	 */	
	public void setDecayTime(final double decayTime) {
		this.decayTime = decayTime;
	}
	/**
	 * M�todo para retornar o tempo de dura��o da requisi��o de
	 * chamada.
	 * @author Andr� 			
	 */	
	public double getDuration() {
		return duration;
	}
	/**
	 * M�todo para configurar o tempo de dura��o da requisi��o de
	 * chamada.
	 * @param duration
	 * @author Andr� 			
	 */	
	public void setDuration(final double duration) {
		this.duration = duration;
	}
	/**
	 * M�todo para configurar o tipo de requisi��o de chamada.
	 * @param callRequesttype
	 * @author Andr� 			
	 */	 	
	public void setCallRequestType(final CallRequestType callRequestType) {
		this.callRequestType = callRequestType;
	}
	/**
	 * M�todo para retornar a rota de trabalho da requisi��o de chamada.
	 * @author Andr� 			
	 */	
	public Route getWorkingPath() {
		return workingPath;
	}
	/**
	 * M�todo para configurar a rota de trabalho da requisi��o de chamada.
	 * @param workingPath
	 * @author Andr� 			
	 */
	public void setWorkingPath(final Route workingPath) {
		this.workingPath = workingPath;
	}
	/**
	 * M�todo para configurar a rota de prote��o da requisi��o de chamada.
	 * @param workingPath
	 * @author Andr� 			
	 */
	public Route getProtectionPath() {
		return protectionPath;
	}
	/**
	 * M�todo para configurar a rota de prote��o da requisi��o de chamada.
	 * @param protectionPath
	 * @author Andr� 			
	 */
	public void setProtectionPath(final Route protectionPath) {
		this.protectionPath = protectionPath;
	}
	/**
   	 * M�todo para sortear a taxa de transmiss�o.
   	 * @author Andr� 			
   	 */		
	public void sortBitRate(){
		final int size = this.possibleBitRates.size();
		final int number = (int) (Math.random()*size);
        this.bitRate = this.possibleBitRates.get(number);
	}
	/**
   	 * M�todo para configurar o tempo de dura��o e queda da requisi��o 
   	 * de chamada.
   	 * @param time
   	 * @param meanDurationRate
   	 * @author Andr� 			
   	 */		
	public void setTime(final double time, final double meanDurationRate, Random randomGeneration){		
		this.decayTime = time + Function.exponentialDistribution(meanDurationRate, randomGeneration);
		this.duration = this.decayTime-time;
	}	
	/**
   	 * M�todo para alocar uma requisi��o a lista.
   	 * @author Andr� 			
   	 */	
	public void allocate(final Route workingPath, final Route protectionPath, final List<OpticalSwitch> listOfNodes){ // NOPMD by Andr� on 20/06/17 14:50
		
		this.workingPath.getUpLink().clear();
		this.workingPath.getDownLink().clear();
		this.protectionPath.getUpLink().clear();
		this.protectionPath.getDownLink().clear();			
				
		//working path
		
		for(int i=0;i<workingPath.getUpLink().size();i++){	// NOPMD by Andr� on 20/06/17 14:50
			this.workingPath.getUpLink().add(workingPath.getUpLink().get(i)); // NOPMD by Andr� on 20/06/17 14:50
		}		
		if(ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){				
			for(int i=0;i<workingPath.getDownLink().size();i++){ // NOPMD by Andr� on 20/06/17 14:50
				this.workingPath.getDownLink().add(workingPath.getDownLink().get(i));	// NOPMD by Andr� on 20/06/17 14:50
			}
		}
		
		for(int i=0;i<this.freqSlotsInWP.size();i++){
			
			final OpticalLink optLinkInbound = this.workingPath.getUpLink().get(0);
			final int freqSlotIn = this.freqSlotsInWP.get(i);
			final OpticalSwitch optSwit = Function.getNode(sourceId, listOfNodes);
			final double laserPower = Function.getLaserPowerInNode(optSwit);
			
			Function.allocateSlotInLink(freqSlotIn, laserPower, optLinkInbound);
						
			for(int j=1;j<this.workingPath.getUpLink().size();j++){
				
				final int source = this.workingPath.getUpLink().get(j).getSource();				
				final OpticalLink optLink = this.workingPath.getUpLink().get(j-1);
				final int fSlot = this.freqSlotsInWP.get(i);
				final OpticalSwitch opticalSwitch = Function.getNode(source, listOfNodes);
				final double powerBinSlot = Function.getPowerBInLink(fSlot,optLink);
				final double switchAten = Function.getSwitchAtenuation(opticalSwitch);				
				final double potOut  = powerBinSlot*switchAten;				
				this.workingPath.getUpLink().get(j).allocate(this.freqSlotsInWP.get(i), potOut);

			}						
			
			//Aloca a volta da chamada
			if(ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){
				
				final OpticalLink optLinkOutbound = this.workingPath.getDownLink().get(0);
				final int freqSlotOut = this.freqSlotsInWP.get(i);
				final OpticalSwitch optSwitch = Function.getNode(sourceId, listOfNodes);
				final double lasPower = Function.getLaserPowerInNode(optSwitch);
				
				Function.allocateSlotInLink(freqSlotOut, lasPower, optLinkOutbound);
				
				for(int j=1;j<this.workingPath.getDownLink().size();j++){            		
            		final int source = this.workingPath.getDownLink().get(j).getSource();            		
            		final OpticalLink optLink = this.workingPath.getDownLink().get(j-1);
    				final int fSlot = this.freqSlotsInWP.get(i);
    				final OpticalSwitch opticalSwitch = Function.getNode(source, listOfNodes);
    				final double powerBinSlot = Function.getPowerBInLink(fSlot,optLink);
    				final double switchAten = Function.getSwitchAtenuation(opticalSwitch);				
    				final double potOut  = powerBinSlot*switchAten;    				
    				this.workingPath.getDownLink().get(j).allocate(this.freqSlotsInWP.get(i), potOut);
            
            	}				
			}
		}
		
		//protection path
		
		for(int i=0;i<protectionPath.getUpLink().size();i++){	// NOPMD by Andr� on 20/06/17 14:50
			this.protectionPath.getUpLink().add(protectionPath.getUpLink().get(i)); // NOPMD by Andr� on 20/06/17 14:50
		}		
		if(ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){				
			for(int i=0;i<protectionPath.getDownLink().size();i++){ // NOPMD by Andr� on 20/06/17 14:50
				this.protectionPath.getDownLink().add(protectionPath.getDownLink().get(i));	// NOPMD by Andr� on 20/06/17 14:50
			}
		}
		
		
		for(int i=0;i<this.freqSlotsInPP.size();i++){
			
			final OpticalLink optLinkInbound = this.protectionPath.getUpLink().get(0);
			final int freqSlotIn = this.freqSlotsInPP.get(i);
			final OpticalSwitch optSwit = Function.getNode(sourceId, listOfNodes);
			final double laserPower = Function.getLaserPowerInNode(optSwit);
			
			Function.allocateSlotInLink(freqSlotIn, laserPower, optLinkInbound);
						
			for(int j=1;j<this.protectionPath.getUpLink().size();j++){
				
				final int source = this.protectionPath.getUpLink().get(j).getSource();				
				final OpticalLink optLink = this.protectionPath.getUpLink().get(j-1);
				final int fSlot = this.freqSlotsInPP.get(i);
				final OpticalSwitch opticalSwitch = Function.getNode(source, listOfNodes);
				final double powerBinSlot = Function.getPowerBInLink(fSlot,optLink);
				final double switchAten = Function.getSwitchAtenuation(opticalSwitch);				
				final double potOut  = powerBinSlot*switchAten;				
				this.protectionPath.getUpLink().get(j).allocate(this.freqSlotsInPP.get(i), potOut);

			}					
			
			//Aloca a volta da chamada
			if(ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){
				
				final OpticalLink optLinkOutbound = this.protectionPath.getDownLink().get(0);
				final int freqSlotOut = this.freqSlotsInPP.get(i);
				final OpticalSwitch optSwitch = Function.getNode(sourceId, listOfNodes);
				final double lasPower = Function.getLaserPowerInNode(optSwitch);
				
				Function.allocateSlotInLink(freqSlotOut, lasPower, optLinkOutbound);
				
				for(int j=1;j<this.protectionPath.getDownLink().size();j++){            		
            		final int source = this.protectionPath.getDownLink().get(j).getSource();            		
            		final OpticalLink optLink = this.protectionPath.getDownLink().get(j-1);
    				final int fSlot = this.freqSlotsInPP.get(i);
    				final OpticalSwitch opticalSwitch = Function.getNode(source, listOfNodes);
    				final double powerBinSlot = Function.getPowerBInLink(fSlot,optLink);
    				final double switchAten = Function.getSwitchAtenuation(opticalSwitch);				
    				final double potOut  = powerBinSlot*switchAten;
    				
    				this.protectionPath.getDownLink().get(j).allocate(this.freqSlotsInPP.get(i), potOut);
            
            	}
			}
		}		
	}
	/**
	 * M�todo para retirar a conex�o da lista.
	 * @author Andr� 			
	 */
	public void desallocate(){		
		for(int y=0;y<this.freqSlotsInWP.size();y++){
				 
			for(int i=0;i<this.workingPath.getUpLink().size();i++){
				this.workingPath.getUpLink().get(i).deallocate(this.freqSlotsInWP.get(y));
			}
			  
			if(ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){					  
				for(int i=0;i<this.workingPath.getDownLink().size();i++){
					this.workingPath.getDownLink().get(i).deallocate(this.freqSlotsInWP.get(y));
				}
			}
		}
		  
		for(int y=0;y<this.freqSlotsInPP.size();y++){
				 
			for(int i=0;i<this.protectionPath.getUpLink().size();i++){
				this.protectionPath.getUpLink().get(i).deallocate(this.freqSlotsInPP.get(y));
			}
			  
			if(ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){					  
				for(int i=0;i<this.protectionPath.getDownLink().size();i++){
					this.protectionPath.getDownLink().get(i).deallocate(this.freqSlotsInPP.get(y));
				}
			}
		}
	}
	/**
	 * M�todo para retornar o tipo da requisi��o de chamada.
	 * @return O atributo callRequestType
	 * @author Andr� 			
	 */	
	public CallRequestType getCallRequestType() {
		return this.callRequestType;
	}     

}
