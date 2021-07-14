package src.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Descreve o componente optical switch usado no simulador.
 * @author Andr� 
 */
public class OpticalSwitch {
	
	/**
	 * Identificador do optical switch.
	 * @author Andr� 			
	 */	
	private transient int opticalSwitchId;
	/**
	 * Valor de atenua��o (linear) do optical switch.
	 * @author Andr� 			
	 */	
	private transient double switchAtenuation;
	/**
	 * Pot�ncia inicial do laser.
	 * @author Andr� 			
	 */	
    private transient double laserPower;
    /**
	 * OSNR de entrada do laser.
	 * @author Andr� 			
	 */	
    private transient double laserOSNR;
    /**
	 * N�mero de regeneradores no optical switch.
	 * @author Andr� 			
	 */	
    private transient int numberOfRegen;
    /**
   	 * N�mero de regeneradores dispon�veis no optical switch.
   	 * @author Andr� 			
   	 */	
    private transient int numberOfFreeRegen;
    /**
   	 * N�mero de regenera��es ocorridas neste optical switch.
   	 * @author Andr� 			
   	 */	
    private transient int numbOfUsedReg;
    /**
   	 * N�mero m�ximo de regeneradores usados no optical switch.
   	 * @author Andr� 			
   	 */	
    private transient int maxSimUsedRegen;

    boolean nodeWorking;
    List<OpticalSwitch> neighborNodes;

	@Override
    public String toString() {
        return String.format("Node Id: %d", this.opticalSwitchId);
    }

    /**
	 * Construtor da classe.
	 * @param opticalSwitchId
	 * @param switchAtenIndB
	 * @param laserPowerIndBm
	 * @param laserOSNRindB
	 * @author Andr�
	 */		
	public OpticalSwitch(final int opticalSwitchId, final double switchAtenIndB, final double laserPowerIndBm, final double laserOSNRindB){		
		this.opticalSwitchId = opticalSwitchId;
		this.switchAtenuation = Math.pow(10,switchAtenIndB/10);
		this.laserPower = Math.pow(10,laserPowerIndBm/10)*Math.pow(10,switchAtenIndB/10);
		this.laserOSNR = Math.pow(10,laserOSNRindB/10);
		this.numberOfRegen = 0;
		this.numberOfFreeRegen = 0;
		this.numbOfUsedReg = 0;
		this.maxSimUsedRegen = 0;

        this.nodeWorking = true;
        neighborNodes = new ArrayList<OpticalSwitch>();
	}	
	/**
	 * M�todo para configurar o identificador do optical switch.
	 * @return opticalSwitchId
	 * @author Andr� 			
	 */
    public void setOpticalSwitchId(final int opticalSwitchId){
    	this.opticalSwitchId = opticalSwitchId;
    }
    /**
     * M�todo para retornar o identificador do optical switch.
     * @return O atributo opticalSwitchId
     * @author Andr� 			
     */    
    public int getOpticalSwitchId(){
    	return this.opticalSwitchId;
    }
    /**
	 * M�todo para configurar a OSNR do laser.
	 * @param osnrIndB
	 * @author Andr� 			
	 */  
    public void setLaserOSNR(final double osnrIndB){
    	this.laserOSNR = Math.pow(10,osnrIndB/10);
    }
    /**
     * M�todo para retornar a OSNR do laser.
     * @return O atributo laserOSNR
     * @author Andr� 			
     */ 
    public double getLaserOSNR(){
    	return this.laserOSNR;
    }
    /**
     * M�todo para retornar a atenua��o do optical switch.
     * @return O atributo switchAtenuation
     * @author Andr� 			
     */
    public double getSwitchAtenuation(){
    	return this.switchAtenuation;
    }
    /**
	 * M�todo para configurar a atenua��o do optical switch.
	 * @param switchAtenIndB
	 * @author Andr� 			
	 */
    public void setSwitchAtenuation(final double switchAtenIndB){
    	this.switchAtenuation = Math.pow(10,switchAtenIndB/10);
    }
    /**
     * M�todo para retornar a pot�ncia inicial do laser.
     * @return O atributo laserPower
     * @author Andr� 			
     */ 
    public double getLaserPower(){
    	return this.laserPower;
    }
    /**
	 * M�todo para configurar a pot�ncia inicial do laser.
	 * @param powerIndBm
	 * @author Andr� 			
	 */
    public void setLaserPower(final double powerIndBm){
    	this.laserPower = Math.pow(10,powerIndBm/10)*this.switchAtenuation;
    }
    /**
     * M�todo para retornar o n�mero de regeneradores.
     * @return O atributo numberOfRegen
     * @author Andr� 			
     */ 
	public int getNumberOfRegenerators() {
		return numberOfRegen;
	}
	/**
	 * M�todo para configurar o n�mero de regeneradores do optical switch.
	 * @param numberOfRegen
	 * @author Andr� 			
	 */
	public void setNumberOfRegenerators(final int numberOfRegen) {
		this.numberOfRegen = numberOfRegen;
	}
	/**
     * M�todo para incrementar o n�mero de regeneradores livre
     * no optical switch.
     * @author Andr� 			
     */ 	
	public void increaseNumberOfFreeRegeneratorsd(){		
		this.numbOfUsedReg++;
		final int usedRegenerators = this.numberOfRegen - this.numberOfFreeRegen;
		if (this.maxSimUsedRegen < usedRegenerators){
			this.maxSimUsedRegen = usedRegenerators;
		}
		this.numberOfFreeRegen++;
	}
	/**
     * M�todo para decrementa o n�mero de regeneradores livre
     * no optical switch.
     * @author Andr� 			
     */ 	
	public void decreaseNumberOfFreeRegenerators(){		
		this.numberOfFreeRegen--;
	}
	/**
     * M�todo para retornar o n�mero de regeneradores livres
     * no optical switch.
     * @return O atributo numberOfFreeRegen
     * @author Andr� 			
     */
	public int getNumberOfFreeRegenerators() {
		return numberOfFreeRegen;
	}
	/**
	 * M�todo para configurar o n�mero de regeneradores livres
	 * do optical switch.
	 * @param numbOfFreeRegen
	 * @author Andr� 			
	 */
	public void setNumberOfFreeRegenerators(final int numbOfFreeRegen) {
		this.numberOfFreeRegen = numbOfFreeRegen;
	}
	/**
     * M�todo para resetar o n�mero de regeneradores livres
     * no optical switch.
     * @author Andr� 			
     */	
	public void resetNumberOfFreeRegen(){
		this.numberOfFreeRegen = this.numberOfRegen;
	}
	/**
     * M�todo para retornar o n�mero m�ximo de regeneradores usados
     * no optical switch.
     * @return O atributo maxSimUsedRegen
     * @author Andr� 			
     */
	public int getMaxSimutaneusllyUsedRegenerators() {
		return maxSimUsedRegen;
	}
	/**
	 * M�todo para configurar o n�mero m�ximo de regeneradores usados
	 * do optical switch.
	 * @param maxSimUsedRegen
	 * @author Andr� 			
	 */
	public void setMaxSimutaneusllyUsedRegenerators(final int maxSimUsedRegen) {
		this.maxSimUsedRegen = maxSimUsedRegen;
	}
	/**
     * M�todo para retornar o n�mero de regeneradores usados
     * no optical switch.
     * @return O atributo numbOfUsedReg
     * @author Andr� 			
     */
	public int getNumberOfTimesRegeneratedHere() {
		return numbOfUsedReg;
	}
	/**
	 * M�todo para configurar o n�mero regenera��es ocorridas 
	 * no optical switch.
	 * @param numbOfTimesReg
	 * @author Andr� 			
	 */
	public void setNumberOfTimesRegeneratedHere(final int numbOfTimesReg) {
		this.numbOfUsedReg = numbOfTimesReg;
	}   
}
