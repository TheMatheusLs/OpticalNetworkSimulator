package src.Parameters;
/**
 * Descreve a classe que armazena os principais param�tros da rede.
 * @author Andr� 
 */
public class SimulationParameters {
	/**
	 * Constante da luz.
	 * @author Andr� 
	 */
	private static final double LIGHT_CONSTANT = 2.99792458E8;  // Unidade: m/s (metro/segundo)
	/**
	 * Comprimento de onda inicial.
	 * @author Andr� 
	 */
	private static final double INITIAL_LAMBDA = 1528.77E-9; // Unidade: m (metro)
	/**
	 * Frequencia final.
	 * @author Andr� 
	 */
	private static final double FINAL_FREQUENCY = (LIGHT_CONSTANT)/INITIAL_LAMBDA; // Unidade: Hz (hertz)
	/**
	 * Espa�amento do canal.
	 * @author Andr� 
	 */
	private static final double SPACING = 12.5E9; 		// Unidade: bits
	/**
	 * Constante de Planck.
	 * @author Andr� 
	 */
	private static final double PLANCK = 6.626068E-34;  // Unidade: J * s
	
	//Amplifier parameters
	/**
	 * Figura de ru�do.
	 * @author Andr� 
	 */
	private static final double NOISE_FIGURE = 5.5;
	//Fiber parameters
	/**
	 * Coeficiente de atenua��o da fibra.
	 * @author Andr� 
	 */
	private static final double ATENU_COEFFIC = -0.23;
	//Span parameters
	/**
	 * Tamanho do span.
	 * @author Andr� 
	 */
	private static final double SPAN_SIZE = 100.0;
	
	//Node parameters
	/**
	 * Perda do mux/demux.
	 * @author Andr� 
	 */
	private static final double MUX_LOSS = -3.0;
	/**
	 * Perda do conector.
	 * @author Andr� 
	 */
	private static final double DIO_LOSS = -3.0;
	/**
	 * Perda do comutador.
	 * @author Andr� 
	 */
	private static final double SWITCH_LOSS = -10.0;
	/**
	 * OSNR de entrada.
	 * @author Andr� 
	 */
	private static final double OSNR_IN = 40.0;
	/**
	 * Pot�ncia do laser.
	 * @author Andr� 
	 */
	private static final double LASER_POWER = 0.0;
	//Network parameters
	/**
	 * Tempo m�ximo.
	 * @author Andr� 
	 */
	private static final double MAX_TIME = 100000.0;
	/**
	 * Taxa m�dia de dura��o da chamada.
	 * @author Andr� 
	 */
	private static final double MEAN_RATE = 1.0;
	/**
	 * M�todo para retornar a figura de ru�do.
	 * @author Andr� 
	 */
	public static double getNoiseFigureIndB() {
		return NOISE_FIGURE;
	}
	/**
	 * M�todo para retornar o coeficiente de atenua��o.
	 * @author Andr� 
	 */
	public static double getFiberAtenuationCoefficient() {
		return ATENU_COEFFIC;
	}
	/**
	 * M�todo para retornar o tamanho do span.
	 * @author Andr� 
	 */
	public static double getSpanSize() {
		return SPAN_SIZE;
	}
	/**
	 * M�todo para retornar a perda do mux.
	 * @author Andr� 
	 */
	public static double getMuxLoss() {
		return MUX_LOSS;
	}
	/**
	 * M�todo para retornar a perda do comutador.
	 * @author Andr� 
	 */
	public static double getSwitchLoss() {
		return SWITCH_LOSS;
	}
	/**
	 * M�todo para retornar a pot�ncia do laser.
	 * @author Andr� 
	 */
	public static double getLaserPower() {
		return LASER_POWER;
	}
	/**
	 * M�todo para retornar a frequencia final.
	 * @author Andr� 
	 */
	public static double getFinalFrequency() {
		return FINAL_FREQUENCY;
	}
	/**
	 * M�todo para retornar a OSNR de entrada.
	 * @author Andr� 
	 */
	public static double getOSNRIn() {
		return OSNR_IN;
	}
	/**
	 * M�todo para retornar a perda do conector.
	 * @author Andr� 
	 */
	public static double getDioLoss() {
		return DIO_LOSS;
	}
	/**
	 * M�todo para retornar o valor da constante C.
	 * @author Andr� 
	 */
	public static double getC() {
		return LIGHT_CONSTANT;
	}
	/**
	 * M�todo para retornar o comprimento de
	 * onda inicial.
	 * @author Andr� 
	 */
	public static double getInitialLambda() {
		return INITIAL_LAMBDA;
	}
	/**
	 * M�todo para retornar o espa�amento do canal.
	 * @author Andr� 
	 */
	public static double getSpacing() {
		return SPACING;
	}
	/**
	 * M�todo para retornar o valor da constante Plank.
	 * @author Andr� 
	 */
	public static double getPlanck() {
		return PLANCK;
	}
	/**
	 * M�todo para retornar o tempo m�ximo.
	 * @author Andr� 
	 */
	public static double getMaxTime() {
		return MAX_TIME;
	}
	/**
	 * M�todo para retornar a taxa m�dia de dura��o da chamada.
	 * @author Andr� 
	 */
	public static double getMeanRateOfCallsDuration() {
		return MEAN_RATE;
	}
}