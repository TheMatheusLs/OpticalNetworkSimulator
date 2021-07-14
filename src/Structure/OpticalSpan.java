package src.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Descreve o componente optical span usado no simulador.
 * @author Andr� 
 */

public class OpticalSpan {
	/**
	 * Identificador do Span no enlace.
	 * @author Andr� 			
	 */		
	private transient int spanId;
	/**
	 * Fibra �ptica do Span.
	 * @author Andr� 			
	 */	
	private OpticalFiber opticalFiber;
	/**
	 * Amplificador do Span.
	 * @author Andr� 			
	 */
	private OpticalAmplifier opticalAmplifier;
	/**
	 * N�mero de slots de frequencia (ou comprimentos de onda) da fibra.
	 * @author Andr�			
	 */
	private transient int numberOfSlots;
	/**
	 * Pot�ncia total na entrada do amplificador.
	 * @author Andr� 			
	 */
	private transient double  totalPower;
	/**
	 * Lista de pot�ncias de cada slot de frequencia.
	 * @author Andr� 			
	 */
	private transient List<Double>  powers;	
	/**
	 * Construtor da classe.
	 * @param spanId
	 * @param numberOfSlots
	 * @param opticalFiber
	 * @param opticalAmplifier
	 * @author Andr�
	 */	
	public OpticalSpan (final int spanId, final int numberOfSlots, final OpticalFiber opticalFiber, final OpticalAmplifier opticalAmplifier){
		this.spanId = spanId;
		this.opticalFiber = opticalFiber;
		this.numberOfSlots = numberOfSlots;
		this.opticalAmplifier = opticalAmplifier;
		this.inicializePowersInSpan(numberOfSlots);
	}
	/**
	 * M�todo para retornar a fibra �ptica do Span.
	 * @return O atributo OpticalFiber
	 * @author Andr� 			
	 */		
	public OpticalFiber getOpticalFiber() {
		return opticalFiber;
	}
	/**
	 * M�todo para configurar a fibra �ptica do Span.
	 * @param opticalFiber
	 * @author Andr� 			
	 */	
	public void setOpticalFiber(final OpticalFiber opticalFiber) {
		this.opticalFiber = opticalFiber;
	}
	/**
	 * M�todo para retornar o amplificador �ptico do Span.
	 * @param O atributo opticalFiber
	 * @author Andr� 			
	 */	
	public OpticalAmplifier getOpticalAmplifier() {
		return opticalAmplifier;
	}
	/**
	 * M�todo para configurar o amplificador �ptico do Span.
	 * @param opticalAmplifier
	 * @author Andr� 			
	 */	
	public void setOpticalAmplifier(final OpticalAmplifier opticalAmplifier) {
		this.opticalAmplifier = opticalAmplifier;
	}
	/**
	 * M�todo para retornar o identificador do Span.
	 * @return O atributo spanId
	 * @author Andr� 			
	 */	
	public int getSpanId() {
		return spanId;
	}
	/**
	 * M�todo para configurar o identificador do Span.
	 * @param spanId
	 * @author Andr� 			
	 */
	public void setId(final int spanId) {
		this.spanId = spanId;
	}
	/**
	 * M�todo para inicializar a lista de pot�ncia da fibra �ptica do Span.
	 * @param numberOfSlots
	 * @author Andr� 			
	 */
	public void inicializePowersInSpan(final int numberOfSlots){    	
    	this.powers = new ArrayList<Double>(numberOfSlots);    	
    	for(int i=0;i<numberOfSlots;i++){    		
    		this.powers.add(0.0);
    	}    	
    	this.totalPower = 0.0;
    }
	/**
	 * M�todo para alterar a quantidade de canais no vetor de pot�ncia.
	 * @param numberOfSlots
	 * @author Andr� 			
	 */
	public void changeNumberOfSlots(final int numberOfSlots){		   
		this.powers = new ArrayList<Double>(numberOfSlots);    	
    	for(int i=0;i<numberOfSlots;i++){    		
    		this.powers.add(0.0);   		
    	}    	
    	this.totalPower = 0.0;
    	this.numberOfSlots = numberOfSlots;
	}
	/**
	 * M�todo para remover a pot�ncia de um determinado slot na pot�ncia
	 * total da fibra do Span.
	 * @param slot
	 * @author Andr� 			
	 */
	public void deallocateTotalPower(final int slot){
		this.totalPower -= this.powers.get(slot);
	}
	/**
	 * M�todo para remover a pot�ncia de um determinado slot da fibra.
	 * @param slot
	 * @author Andr� 			
	 */
	public void deallocate(final int slot){
		this.deallocateTotalPower(slot);
		this.powers.set(slot, 0.0);
	}
	/**
	 * M�todo para configurar a pot�ncia do slot alocado na fibra �ptica do Span.
	 * @param powerValue
	 * @param slot
	 * @author Andr� 			
	 */
	public void setPower(final double powerValue, final int slot){
		this.totalPower -= this.powers.get(slot);
		this.totalPower += powerValue;
		this.powers.set(slot, powerValue);
	}
	/**
	 * M�todo para retornar o n�mero de slots do Span.
	 * @return O atributo numberOfSlots
	 * @author Andr� 			
	 */	
	public int getNumberOfSlots() {
		return numberOfSlots;
	}
	/**
	 * M�todo para retornar a pot�ncia total antes do amplificador �ptico.
	 * @return O atributo totalPower
	 * @author Andr� 			
	 */	
	public double getTotalPower() {
		return totalPower;
	}
	/**
	 * M�todo para retornar o vetor com as pot�ncias individuais
	 * antes do amplificador �ptico.
	 * @return O atributo powers
	 * @author Andr� 			
	 */	
	public List<Double> getPowers() {
		return powers;
	}
	/**
	 * M�todo para resetar a pot�ncia de todos os slots do Span.
	 * @author Andr� 			
	 */	
	public void erasePowerInSpan(){		
		this.powers = new ArrayList<Double>(numberOfSlots);		   
		for(int i=0;i<numberOfSlots;i++){			   
			this.powers.add(0.0);	
		}		   
		this.totalPower = 0.0;
	}
}
