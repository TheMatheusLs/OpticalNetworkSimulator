package src.Structure;

/**
 * Descreve o componente optical fiber usado no simulador.
 * @author Andr� 
 */

public class OpticalFiber {
	/**
	 * Tamanho da fibra.
	 * @author Andr�
	 */	
	private double length;	
	/**
	* Construtor da classe.
	* @author Andr�
	*/	
	public OpticalFiber(final double length){ 
		this.length = length;		
	}
	/**
	 * M�todo para retornar o valor do tamanho da fibra.
	 * @return O atributo length.
	 * @author Andr� 			 
	 */
	public double getLength() {
		return length;
	}	
	/**
	 * M�todo para configurar o valor do tamanho da fibra.
	 * @author Andr�
	 */
	public void setLength(final double length) {
		this.length = length;
	}	
}
