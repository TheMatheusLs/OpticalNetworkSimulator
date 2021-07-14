package src.Structure.Topology.gain_algorithm;

import java.util.List;

import src.Parameters.SimulationParameters;
import src.Structure.OpticalAmplifier;
import src.Structure.OpticalLink;
import src.Structure.OpticalSpan;
import src.Types.GainAlgorithmType;

/**
 * Descreve a classe para configurar o ganho dos amplificadores no optical link.
 * @author Andr� 
 */
public final class GainAlgorithm {
	/**
	 * Inst�ncia da classe.
	 * @author Andr� 
	 */	
	private static final GainAlgorithm GAIN_INSTANCE = new GainAlgorithm(GainAlgorithmType.BASIC);
	/**
	 * M�trica usada para selecionar o algoritmo de ganho.
	 * @author Andr� 
	 */	
	private transient final GainAlgorithmType metric;
	/**
	 * Construtor da classe.
	 * @author Andr� 
	 */	
	private GainAlgorithm(final GainAlgorithmType metric){
		this.metric=metric;
		
	}
	/**
	 * M�todo para configurar o ganho dos amplificadores
	 * do optical link.
	 * @param link
	 * @author Andr� 			
	 */
	public void configureGain(final OpticalLink link){		
		if(GainAlgorithmType.BASIC == metric){
			this.configureGainInLink(link);	
		}
	}
	/**
	 * M�todo para configurar o ganho dos amplificadores
	 * do optical link. As perdas do link s�o somadas e divididas
	 * pelo n�mero de amplificadores, para ent�o
	 * configurar o ganho de cada amplificador no optical link.
	 * @param link
	 * @author Andr� 			
	 */
	private void configureGainInLink(final OpticalLink link) {
		
		final List<OpticalSpan> spans = getSpansList(link);
		final int spansSize = getSpansSize(spans);												// Quantidade de Span	
		final double fiberLoss = -link.getLength()* SimulationParameters.getFiberAtenuationCoefficient();	// km * (dB/ Km)  = dB
		final double connectorsLoss = (double) (spansSize*(-2.0 * SimulationParameters.getDioLoss()));		// dB
		final double nodesLoss = - SimulationParameters.getMuxLoss() * 2.0 - SimulationParameters.getSwitchLoss();		// dB
		final double totalLoss = fiberLoss+connectorsLoss+nodesLoss;							// dB
		final double numberOfAmpl = (double) (spansSize+1);										// Quantidade de amplificadores
		
		final double gain = totalLoss/numberOfAmpl;												// dB
		final double noiseFactorIndB = SimulationParameters.getNoiseFigureIndB();							// dB
		
		
		//configure the booster
		this.configureGainInAmplifier(gain, link.getBooster());
		this.configureNoiseFactorInAmplifier(noiseFactorIndB, link.getBooster());
				
		//configure the optical amplifiers in the spans
		for(int i=0; i<spansSize;i++){
			this.configureAmplifier(gain, noiseFactorIndB, getSpan(i,spans));
		}
	}
	/**
	 * M�todo para obter um determinado span da lista.
	 * @param index
	 * @param spans
	 * @author Andr� 
	 */
	private OpticalSpan getSpan(final int index, final List<OpticalSpan> spans){
		return spans.get(index);
	}
	/**
	 * M�todo para obter a quantidade de spans no optical link.
	 * @param spans
	 * @author Andr� 
	 */
	private int getSpansSize(final List<OpticalSpan> spans){
		return spans.size();		
	}
	/**
	 * M�todo para obter a lista de spans do optical link.
	 * @param link
	 * @author Andr� 
	 */
	private List<OpticalSpan> getSpansList(final OpticalLink link){
		return link.getSpans();		
	}
	/**
	 * M�todo para configurar o amplificador.
	 * @param gain
	 * @param noiseFactorIndB
	 * @param amplifier
	 * @author Andr� 			
	 */	
	private void configureAmplifier(final double gain, final double noiseFactorIndB, final OpticalSpan span){
		
		final OpticalAmplifier amplifier = span.getOpticalAmplifier();
		this.configureGainInAmplifier(gain, amplifier);
		this.configureNoiseFactorInAmplifier(noiseFactorIndB, amplifier);
		
	}
	/**
	 * M�todo para configurar a fator de ru�do do amplificador.
	 * @param noiseFactorIndB
	 * @param amplifier
	 * @author Andr� 			
	 */		
	private void configureNoiseFactorInAmplifier(final double noiseFactorIndB, final OpticalAmplifier amplifier){
		amplifier.setNoiseFactor(noiseFactorIndB);		
	}
	/**
	 * M�todo para configurar o ganho do amplificador.
	 * @param gain
	 * @param amplifier
	 * @author Andr� 			
	 */	
	private void configureGainInAmplifier(final double gain, final OpticalAmplifier amplifier){
		amplifier.setGain(gain);	
	}
	/**
	 * M�todo para obter a inst�ncia da classe.
	 * @author Andr� 
	 */		
	public static GainAlgorithm getGainInstance(){
		return GAIN_INSTANCE;
	}
}