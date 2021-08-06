package src.Structure; // NOPMD by Andr� on 29/05/17 11:42

import java.util.ArrayList;
import java.util.List;

import src.ParametersSimulation;
import src.Parameters.SimulationParameters;

/**
 * Descreve o componente optical link usado no simulador.
 * @author Andr� 
 */

public class OpticalLink {	 // NOPMD by Andr� on 29/05/17 11:42
	/**
	 * Identificador do optical link.
	 * @author Andr� 			
	 */		
	private transient int opticalLinkId;
	/**
	 * Comprimento em km do optical link.
	 * @author Andr� 			
	 */	
	private double length;
	/**
	 * Grupo de risco do optical link.
	 * @author Andr� 			
	 */	
	private transient int srlg;
	/**
	 * N� fonte do optical link.
	 * @author Andr� 			
	 */	
	private int source;
	/**
	 * N� destino do optical link.
	 * @author Andr� 			
	 */	
	private int destination;
	/**
	 * Booster do optical link.
	 * @author Andr� 			
	 */
	private OpticalAmplifier booster;
	/**
	 * N�mero de Spans do optical link.
	 * @author Andr� 			
	 */
	private List<OpticalSpan> spans;
	/**
	 * Valor de pot�ncia total antes do booster.
	 * @author Andr� 			
	 */	
	private double  totalPowerInA;
	/**
	 * Valor de pot�ncia total depois do demux.
	 * @author Andr� 			
	 */
	private double  totalPowerInB;
	/**
	 * Lista de pot�ncias individuais antes do booster.
	 * @author Andr� 			
	 */
	private List<Double>  powersA;
	/**
	 * Lista de pot�ncias individuais depois do demux.
	 * @author Andr� 			
	 */
	private List<Double>  powersB;
	/**
	 * Lista de frequencias dos slots no optical link.
	 * @author Andr� 			
	 */
	private List<Double> frequencies;
	/**
     * @brief Cost of this link, based in the 
     * selected metric
     */
    double cost;
	/**
     * @brief Boolean variable to indicate the  link state.
     */
    boolean linkState;
    /**
     * @brief Link utilization value. This parameter is determined by the number
     * of times the link was used by all the routes for all node pairs.
     */
    int utilization;
    /**
     * @brief Link use. This parameter is updated each time a connection uses
     * the link in the allocation path.
     */
    int use;
	/**
	 * @brief Número de usos desse links por todo o cojunto de rotas
	 */
	int betweenness;
	/**
	 * Construtor da classe.
	 * @param source
	 * @param destination
	 * @param srlg
	 * @param length
	 * @author Andr�
	 */
	public OpticalLink(final int opticalLinkId, final int source, final int destination, final int srlg, final double length) throws Exception {		
		if(length < SimulationParameters.getSpanSize()){
			throw new Exception("Fiber length between nodes "+ source +" and "+ destination + " is invalid.");
		}		
		this.opticalLinkId = opticalLinkId;
		this.source = source;
		this.destination = destination;
		this.length = length;
		this.srlg = srlg;
		this.booster = new OpticalAmplifier();
		this.spans = this.configureSpansInLink(length);
		this.inicializePowersAndFrequencies(ParametersSimulation.getNumberOfSlotsPerLink());

		this.cost = 0.0;
        this.linkState = true;
        this.utilization = 0;
        this.use = 0;

		this.betweenness = 0;

	}

	@Override
	public String toString() {
		
		String txt = String.format("Link %d - Source = %d, Destination = %d, Length = %f", this.opticalLinkId, this.source, this.destination, this.length);

		return txt;
	}

	public boolean isLinkWorking(){
        return this.linkState;
    }

	public void setLinkState(Boolean linkState) {
        this.linkState = linkState;
    }

	public int getOriginNode(){
        return this.source;
    }

    public int getDestinationNode(){
        return this.destination;
    }

	public double getCost(){
        return this.cost;
    }

	public void setCost(double cost){
        this.cost = cost;
    }

	/**
	 * M�todo para retornar o identificador do optical link.
	 * @return O atributo opticalLinkId
	 * @author Andr� 			
	 */	
	public int getOpticalLinkId() {
		return opticalLinkId;
	}
	/**
	 * M�todo para configurar o identificador do optical link.
	 * @return O atributo opticalLinkId
	 * @author Andr� 			
	 */
	public void setId(final int opticalLinkId) {
		this.opticalLinkId = opticalLinkId;
	}
	/**
	 * M�todo para retornar o comprimento em km do optical link.
	 * @return O atributo length
	 * @author Andr� 			
	 */	
	public double getLength() {
		return length;
	}
	/**
	 * M�todo para configurar o comprimento em km do optical link.
	 * @param length
	 * @author Andr� 			
	 */	
	public void setLength(final double length) {
		this.length = length;
	}
	/**
	 * M�todo para retornar o grupo de risco do optical link.
	 * @return O atributo srlg
	 * @author Andr� 			
	 */	
	public int getSRLG() {
		return srlg;
	}
	/**
	 * M�todo para configurar o grupos de risco do optical link.
	 * @param srlg
	 * @author Andr� 			
	 */	
	public void setSRLG(final int srlg) {
		this.srlg = srlg;
	}
	/**
	 * M�todo para retornar o n� fonte do optical link.
	 * @return O atributo source
	 * @author Andr� 			
	 */	
	public int getSource() {
		return source;
	}
	/**
	 * M�todo para configurar o n� fonte do optical link.
	 * @param source
	 * @author Andr� 			
	 */
	public void setSource(final int source) {
		this.source = source;
	}
	/**
	 * M�todo para retornar o n� destino do optical link.
	 * @return O atributo destination
	 * @author Andr� 			
	 */	
	public int getDestination() {
		return destination;
	}
	/**
	 * M�todo para configurar o n� destino do optical link.
	 * @param destination
	 * @author Andr� 			
	 */
	public void setDestination(final int destination) {
		this.destination = destination;
	}
	/**
	 * M�todo para retornar a lista de Spans no optical link.
	 * @return O atributo spans
	 * @author Andr� 			
	 */	
	public List<OpticalSpan> getSpans() {
		return spans;
	}
	/**
	 * M�todo para configurar a lista de Spans no optical link.
	 * @param spans
	 * @author Andr� 			
	 */
	public void setSpans(final List<OpticalSpan> spans) {
		this.spans = spans;
	}
	/**
	 * M�todo para retornar o booster do optical link.
	 * @author Andr� 			
	 */	
	public OpticalAmplifier getBooster() {
		return booster;
	}
	/**
	 * M�todo para configurar o booster do optical link.
	 * @param booster
	 * @author Andr� 			
	 */
	public void setBooster(final OpticalAmplifier booster) {
		this.booster = booster;
	}
	/**
	 * M�todo para retornar a pot�ncia total antes do booster
	 * do optical link.
	 * @return O atributo totalPowerInA
	 * @author Andr� 			
	 */	
	public double getTotalPowerInA() {
		return totalPowerInA;
	}
	/**
	 * M�todo para configurar a pot�ncia total antes do booster
	 * do optical link.
	 * @param totalPowerInA
	 * @author Andr� 			
	 */
	public void setTotalPowerInA(final double totalPowerInA) {
		this.totalPowerInA = totalPowerInA;
	}
	/**
	 * M�todo para retornar a pot�ncia total depois do demux
	 * do optical link.
	 * @return O atributo totalPowerInB
	 * @author Andr� 			
	 */	
	public double getTotalPowerInB() {
		return totalPowerInB;
	}
	/**
	 * M�todo para configurar a pot�ncia total depois do demux
	 * do optical link.
	 * @param totalPowerInB
	 * @author Andr� 			
	 */
	public void setTotalPowerInB(final double totalPowerInB) {
		this.totalPowerInB = totalPowerInB;
	}
	/**
	 * M�todo para retornar a lista de pot�ncias individuais dos slots
	 * antes do booster no optical link.
	 * @author Andr� 			
	 */	
	public List<Double> getPowersA() {
		return powersA;
	}
	/**
	 * M�todo para configurar a lista de pot�ncias individuais dos slots depois
	 * do demux no optical link.
	 * @param totalPowerInB
	 * @author Andr� 			
	 */
	public void setPowersA(final List<Double> powersA) {
		this.powersA = powersA;
	}
	/**
	 * M�todo para retornar a lista de pot�ncias individuais dos slots
	 * do optical link.
	 * @return O atributo powersB
	 * @author Andr� 			
	 */	
	public List<Double> getPowersB() {
		return powersB;
	}
	/**
	 * M�todo para configurar a lista de pot�ncias individuais dos slots
	 * depois do demux no optical link.
	 * @param powersB
	 * @author Andr� 			
	 */	
	public void setPowersB(final List<Double> powersB) {
		this.powersB = powersB;
	}
	/**
	 * M�todo para retornar a lista de frequencias dos slots do optical link.
	 * @return O atributo frequencies
	 * @author Andr� 			
	 */	
	public List<Double> getFrequencies() {
		return frequencies;
	}
	/**
	 * M�todo para configurar a lista de frequencias dos slots do optical link.
	 * @param frequencies
	 * @author Andr� 			
	 */	
	public void setFrequencies(final List<Double> frequencies) {
		this.frequencies = frequencies;
	}
	/**
	 * M�todo para configurar a lista de Spans do optical link.
	 * @param length
	 * @author Andr� 			
	 */	
	private List<OpticalSpan> configureSpansInLink(final double length) throws Exception {
		
		final double spanSize = SimulationParameters.getSpanSize();
		
		if(length % spanSize != 0){
			throw new Exception("The fiber size is not multiple of the span size.");
		}
				
		final int count = (int) Math.ceil(length/spanSize); //(int) (length/spanSize);
		final List<OpticalSpan> spans = new ArrayList<OpticalSpan>();
		
		for(int i=0; i<count; i++){
			this.addSpansInLink(i, spanSize, spans);
		}
		return spans;
	}
	/**
	 * M�todo para configurar o span na lista de Spans do optical link.
	 * @param spanId
	 * @param spanSize
	 * @param spans
	 * @author Andr� 			
	 */	
	private void addSpansInLink(final int spanId, final double spanSize, final List<OpticalSpan> spans){
		spans.add(new OpticalSpan (spanId, ParametersSimulation.getNumberOfSlotsPerLink(), new OpticalFiber(spanSize),new OpticalAmplifier()));		
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
	 * M�todo para inicializar as pot�ncias e frequencias do optical link.
	 * @param numberOfSlots
	 * @author Andr� 			
	 */	
	private void inicializePowersAndFrequencies(final int numberOfSlots){
    	
    	this.powersA = new ArrayList<Double>(numberOfSlots);
    	this.powersB = new ArrayList<Double>(numberOfSlots);
    	this.frequencies = new ArrayList<Double>(numberOfSlots);
    	
    	for(int i=0;i<numberOfSlots;i++){    		
    		this.powersA.add(0.0);
    		this.powersB.add(0.0);
    		this.frequencies.add(SimulationParameters.getFinalFrequency()-((i+1)*SimulationParameters.getSpacing()));    		
    	}
    	
    	this.totalPowerInA = 0.0;
    	this.totalPowerInB = 0.0;
    }
	/**
	 * M�todo para avaliar a disponibilidade do slot no optical link.
	 * @param slot
	 * @author Andr� 			
	 */		
	public boolean availableSlot(final int slot){
    	return this.powersA.get(slot) == 0.0;
    }
	/**
	 * M�todo para remover a pot�ncia de um determinado slot na pot�ncia
	 * total do optical link.
	 * @param slot
	 * @author Andr� 			
	 */		
	public void deallocateTotalPower(final int slot){
		this.totalPowerInA -= this.powersA.get(slot);
		this.totalPowerInB -= this.powersB.get(slot);
	}
	/**
	 * M�todo para remover a pot�ncia de um determinado slot do optical link.
	 * @param slot
	 * @author Andr� 			
	 */	
	public void deallocate(final int slot){
		this.deallocateTotalPower(slot);
		this.powersA.set(slot, 0.0);
		this.powersB.set(slot, 0.0);
		
		// desalloacate slots in the spans.
    	for(int i=0; i<this.spans.size();i++){
    		this.desallocateSlotInSpan(slot, spans.get(i));
    	}
	}
	/**
	 * M�todo para remover a pot�ncia de um determinado slot do Spanda fibra.
	 * @param slot
	 * @param span
	 * @author Andr� 			
	 */	
	private void desallocateSlotInSpan(final int slot, final OpticalSpan span){
		span.deallocate(slot);
	}
	/**
	 * M�todo para configurar o ganho em dB do booster no optical link.
	 * @param gainIndB
	 * @author Andr� 			
	 */	
	public void setGainInBooster(final double gainIndB){
		this.booster.setGain(gainIndB);
	}
	/**
	 * M�todo para configurar o ganho em dB no amplificador de um determinado
	 * span no optical link.
	 * @param gainIndB
	 * @param spanId
	 * @author Andr� 			
	 */	
	public void setGainInSpecificOpticalAmplifier(final double gainIndB, final int spanId){
		for(int i=0; i<this.spans.size();i++){
			final int opticalSpanId = getSpanId(spans.get(i));
			if(opticalSpanId==spanId){
				this.configureGainInAmplifier(gainIndB, this.getSpanOpticalAmplifier(spans.get(i)));
			}
		}
	}
	/**
	 * M�todo para retornar o id do span.
	 * @param span
	 * @return O atributo spanId
	 * @author Andr� 			
	 */		
	private int getSpanId(final OpticalSpan span){
		return span.getSpanId();
	}
	/**
	 * M�todo para retornar o amplificador �ptico do span.
	 * @param span
	 * @return O atributo opticalAmplifier
	 * @author Andr� 			
	 */		
	private OpticalAmplifier getSpanOpticalAmplifier(final OpticalSpan span){
		return span.getOpticalAmplifier();
	}
	/**
	 * M�todo para retornar a fibra �ptico do span.
	 * @param span
	 * @return O atributo opticalFiber
	 * @author Andr� 			
	 */		
	private OpticalFiber getSpanOpticalFiber(final OpticalSpan span){
		return span.getOpticalFiber();
	}
	/**
	 * M�todo para configurar a pot�ncia do slot antes do booster no optical link.
	 * @param powerValue
	 * @param slot
	 * @author Andr� 			
	 */	
	private void setPowerA(final double powerValue, final int slot){
		this.totalPowerInA -= this.powersA.get(slot);
		this.totalPowerInA += powerValue;
		this.powersA.set(slot, powerValue);
	}
	/**
	 * M�todo para configurar a pot�ncia do slot depois do demux no optical link.
	 * @param powerValue
	 * @param slot
	 * @author Andr� 			
	 */	   
	private void setPowerB(final double powerValue, final int slot){
		this.totalPowerInB -= this.powersB.get(slot);
		this.totalPowerInB += powerValue;
		this.powersB.set(slot, powerValue);
	}
	/**
	 * M�todo para configurar a pot�ncia do slot no optical link.
	 * @param slot
	 * @param initialPower
	 * @author Andr� 			
	 */	
	public void allocate(final int slot, final double initialPower){		
		
		final double muxGain = Math.pow(10, SimulationParameters.getMuxLoss()/10);
		final double powBefBooster = initialPower*muxGain;			
		this.setPowerA(powBefBooster, slot);		
		
		double signal = powBefBooster*this.booster.getGainInLinear();
		
		for(int i=0;i<this.spans.size();i++){
			final double dioLoss =  Math.pow(10, SimulationParameters.getDioLoss()/10);
			final double fiberGain = this.getSpanOpticalFiber(spans.get(i)).getLength()*SimulationParameters.getFiberAtenuationCoefficient();			
			signal *= dioLoss;			
			signal *= Math.pow(10,fiberGain/10);
			signal *= dioLoss;			
			this.configurePowerInSpan(slot,spans.get(i),signal);
			signal *= this.getSpanOpticalAmplifier(spans.get(i)).getGainInLinear();		
		}
		this.setPowerB(signal*muxGain, slot);		
	}
	/**
	 * M�todo para configurar a pot�ncia do slot no span do optical link.
	 * @param slot
	 * @param span
	 * @param signal
	 * @author Andr� 			
	 */	
	private void configurePowerInSpan(final int slot, final OpticalSpan span, final double signal){
		span.setPower(signal, slot);		
	}
	/**
	 * M�todo para retornar a lista de slots de frequencia
	 * dispon�veis no optical link.
	 * @return O atributo availSlotsList
	 * @author Andr� 			
	 */	
	public List<Integer> getAvailableSlots(){		   
		final List<Integer> availSlotsList = new ArrayList<Integer>();
		availSlotsList.clear();	   
		for(int i=0; i < ParametersSimulation.getNumberOfSlotsPerLink() ;i++){
			if(this.availableSlot(i)){
				availSlotsList.add(i);
			}
		}		
		return availSlotsList;
	}
	/**
	 * M�todo para retornar a pot�ncia do slot antes do booster
	 * no optical link.
	 * @return A pot�ncia do slot antes do booster. 
	 * @param slot
	 * @author Andr� 			
	 */		
	public double getPowerA(final int slot){
		return this.powersA.get(slot);
	}
	/**
	 * M�todo para retornar a pot�ncia do slot depois do demux
	 * no optical link.
	 * @param slot
	 * @return A pot�ncia do slot depois do pr�-amplificador.
	 * @author Andr� 			
	 */		   
	public double getPowerB(final int slot){
		return this.powersB.get(slot);
	}
	/**
	 * M�todo para retornar a frequencia do slot no optical link.
	 * @param slot
	 * @return A frequencia do slot.
	 * @author Andr� 			
	 */	
	public double getFrequency(final int slot){
		return this.frequencies.get(slot);
	}
	/**
	 * M�todo para resetar as vari�veis do optical link.
	 * @author Andr� 			
	 */		
	public void eraseOpticalLink(){
			
		this.powersA = new ArrayList<Double>(ParametersSimulation.getNumberOfSlotsPerLink());
		this.powersB = new ArrayList<Double>(ParametersSimulation.getNumberOfSlotsPerLink());
		this.frequencies = new ArrayList<Double>(ParametersSimulation.getNumberOfSlotsPerLink());
		   
		for(int i=0;i<ParametersSimulation.getNumberOfSlotsPerLink();i++){			   
			this.powersA.add(0.0);
			this.powersB.add(0.0);
			this.frequencies.add(SimulationParameters.getFinalFrequency()-((i+1)*SimulationParameters.getSpacing()));	   			
		}
		   
		this.totalPowerInA = 0.0;
		this.totalPowerInB = 0.0;
		
		// erase spans.
    	for(int i=0; i<this.spans.size();i++){
    		this.resetPowerInSpan(spans.get(i));
    	}
	}
	/**
	 * M�todo para resetar as vari�veis do span no optical link.
	 * @author Andr� 			
	 */	
	private void resetPowerInSpan(final OpticalSpan span){
		span.erasePowerInSpan();
	}

    public void incrementBetweenness() {
		
		this.betweenness += 1;
    }

    public int getBetweenness() {
        return this.betweenness;
    }
}
