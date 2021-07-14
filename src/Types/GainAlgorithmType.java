package src.Types;
/**
 * Descreve os tipos de algoritmo de atribui��o de ganhos dos 
 * amplificadores usado no simulador.
 * @author Andr� 
 */
public enum GainAlgorithmType {
	
	BASIC (1);
	
	/**
	 * C�digo do algoritmo de ganho.
	 * @author Andr� 			
	 */	
	private int code;	
	/**
	 * Construtor da classe.
	 * @param code
	 * @author Andr�
	 */	
	private GainAlgorithmType(final int code){
		this.code = code;		
	}
	/**
	 * M�todo para retornar o c�digo do algoritmo de ganho.
	 * @return O atributo code
	 * @author Andr� 			
	 */
	public int getCode() {
		return code;
	}
	/**
	 * M�todo para configurar o c�digo do algoritmo de ganho.
	 * @param code
	 * @author Andr� 			
	 */
	public void setCode(final int code) {
		this.code = code;
	}
}
