package src.Structure;

/**
 * Descreve o componente optical amplifier usado no simulador.
 * @author Andr�
 */

public class OpticalAmplifier {
	/**
	 * Ganho do amplificador em dB.
	 * @author Andr�			
	 */	
	private transient double gainIndB;
	/**
	 * Fator de ru�do do amplificador em dB.
	 * @author Andr�			
	 */	
	private transient double nfIndB;
	/**
	* Construtor da classe.
	* @param gainIndB
	* @param noiseFactorIndB
	* @author Andr�
	*/
	public OpticalAmplifier(final double gainIndB, final double noiseFactorIndB){		
		this.gainIndB = gainIndB;		
		this.nfIndB = noiseFactorIndB;
	}	
	/**
	 * Construtor da classe.
	 * @author Andr�
	 */		
	public OpticalAmplifier(){
		this.gainIndB = -1.0;
		this.nfIndB = -1.0;
	}	
	/**
	 * M�todo para retornar o valor do ganho (linear) do amplificador.
	 * @return O atributo gainIndB no valor linear.
	 * @author Andr� 			
	 */	
	public double getGainInLinear() {
		return Math.pow(10, gainIndB/10);
	}
	/**
	 * M�todo para configurar o valor do ganho (linear) do amplificador.
	 * @param gainIndB
	 * @author Andr�			
	 */	
	public void setGain(final double gainIndB) {
		this.gainIndB = gainIndB;
	}
	/**
	 * M�todo para retornar o valor do fator de ru�do (linear) do amplificador.
	 * @return  O atributo nfIndB no valor linear.
	 * @author Andr�
	 */	
	public double getNoiseFactorInLinear() {
		return Math.pow(10, nfIndB/10);
	}
	/**
	 * M�todo para configurar o valor do fator de ru�do (linear) do amplificador.
	 * @param noiseFactorIndB
	 *			Fator de ru�do em dB.
	 * @author Andr� 			
	 */	
	public void setNoiseFactor(final double noiseFactorIndB) {
		this.nfIndB = noiseFactorIndB;
	}
	/**
	 * M�todo para retornar o valor do ganho (dB) do amplificador.
	 * @return O atributo gainIndB
	 * @author Andr� 			
	*/	
	public double getGainIndB() {
		return gainIndB;
	}
	/**
	 * M�todo para retornar o valor do fator de ru�do (dB) do amplificador.
	 * @return O atributo nfIndB
	 * @author Andr�			
	*/
	public double getNoiseFactorIndB() {
		return nfIndB;
	}
}
