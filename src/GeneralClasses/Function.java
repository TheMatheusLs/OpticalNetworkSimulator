package src.GeneralClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.ParametersSimulation;
import src.Parameters.SimulationParameters;
import src.Structure.OpticalLink;
import src.Structure.OpticalSwitch;
import src.Types.ModulationLevelType;

public class Function {

	public static double getMeanList(List<Double> list){

		int numSize = list.size();
		double acum = 0.0;

		for (double value : list){
			acum += value;
		}

		return acum / numSize;
	}

	public static int getNumberSlots(ModulationLevelType modulation, int bitRate) {
        int numSlots;
        double bandwidth = bandwidthQAM(modulation, bitRate * 1e9);
        
        if(isEON()){
            numSlots = (int) Math.ceil(bandwidth / SimulationParameters.getSpacing());
            numSlots += ParametersSimulation.getGuardBandSize();
        }
        else
            numSlots = 1;
        
        return numSlots;
    }

	public static boolean isEON() {
        if(SimulationParameters.getSpacing() <= 12.5 * 1E9)
            return true;
        
        return false;
    }

	static double bandwidthQAM(ModulationLevelType M, double Rbps) { 
		double value = Math.log10(M.getConstelation()) / Math.log10(2);

        return ((1.0 + 0.0) * Rbps) / (ParametersSimulation.getNumberofPolarizations() * value);
    }

	public static double exponentialDistribution(double networkLoad, Random rand) {
        return - Math.log(1 - rand.nextDouble())  / networkLoad;
    }
	// public static double exponentialDistribution(double networkLoad, Random rand) {
    //     return Math.log(1-rand.nextDouble())  / (-networkLoad);
    // }

    public static int uniformIntDistribution(int init, int ended, Random rand) {
        return init + rand.nextInt(ended);
    }

    public static List<Integer> sort(List<Integer> posSlots) {

        List<Integer> newList = new ArrayList<Integer>();

        List<Integer> posSlotsAux = new ArrayList<Integer>();
        for (int i = 0; i < posSlots.size(); i++){
            posSlotsAux.add(posSlots.get(i));
        }

        while(newList.size() != posSlots.size()){
            int minValue = Integer.MAX_VALUE;
            int minValueIndex = 0;
            for (int k = 0; k < posSlotsAux.size(); k++){
                if (minValue > posSlotsAux.get(k)){
                    minValue = posSlotsAux.get(k);
                    minValueIndex = k;
                }
            }

            newList.add(minValue);
            posSlotsAux.remove(minValueIndex);
        }

        return newList;
    }

	public static boolean avaliableSlotInOpticalLink(final OpticalLink opticalLink, final int slot){
		return opticalLink.availableSlot(slot);
	}
	// /**
   	//  * M�todo para verificar se a rota est� varia.
   	//  * @param  opticalLink
   	//  * @return Retornar true ou false.
   	//  * @author Andr�
   	//  */
	// public boolean isEmptyOpticalLinkList(final List<OpticalLink> opticalLink){
	// 	return opticalLink.isEmpty();
	// }
	// /**
   	//  * M�todo para retornar o comprimento do optical link.
   	//  * @param  opticalLink
   	//  * @return O atributo length
   	//  * @author Andr�
   	//  */
	// public double getLengthInLink(final OpticalLink opticalLink){
	// 	return opticalLink.getLength();
	// }
	/**
   	 * M�todo para retornar o objeto optical switch atr�ves do id.
   	 * @param  opticalSwitchId
   	 * @param listOfNodes
   	 * @author Andr�
   	 */
	public static OpticalSwitch getNode(final int opticalSwitchId, final List<OpticalSwitch> listOfNodes){

		OpticalSwitch optSwi = null;		 // NOPMD by Andr� on 07/06/17 15:32
		for(int x=0;x<listOfNodes.size();x++){
			final int nodeId = getOpticalSwitchId(listOfNodes.get(x));
			if(nodeId==opticalSwitchId){
				optSwi = listOfNodes.get(x);
				break;
			}
		}

		return optSwi;
	}
	/**
   	 * M�todo para retornar o id do optical switch.
   	 * @param  opticalSwitch
   	 * @author Andr�
   	 */
	private static int getOpticalSwitchId(final OpticalSwitch opticalSwitch){
		return opticalSwitch.getOpticalSwitchId();
	}
	/**
   	 * M�todo para retornar a pot�ncia do laser em um determinado n�.
   	 * @param  opticalSwitch
   	 * @author Andr�
   	 */
	public static double getLaserPowerInNode(final OpticalSwitch opticalSwitch){
		return opticalSwitch.getLaserPower();
	}
	/**
   	 * M�todo para alocar a pot�ncia de um slot em uma determinado optical link.
   	 * @param  slot
   	 * @param laserPower
   	 * @param opticalLink
   	 * @author Andr�
   	 */
	public static void allocateSlotInLink(final int slot, final double laserPower, final OpticalLink opticalLink){
		opticalLink.allocate(slot, laserPower);
	}
	/**
	 * M�todo para retornar a atenua��o do optical switch.
	 * @param  opticalSwitch
	 * @author Andr�
	 */
	public static double getSwitchAtenuation(final OpticalSwitch opticalSwitch) {
		return opticalSwitch.getSwitchAtenuation();
	}
	// /**
	//  * M�todo para retornar a rota de uplink da solu��o de roteamento.
	//  * @param  solution
	//  * @author Andr�
	//  */
	// public List<OpticalLink> getUpLinkInSolution (final RoutingAlgorithmSolution solution){
	// 	return solution.getUpLink();
	// }
	// /**
	//  * M�todo para retornar a rota de downlink da solu��o de roteamento.
	//  * @param  solution
	//  * @author Andr�
	//  */
	// public List<OpticalLink> getDownLinkInSolution (final RoutingAlgorithmSolution solution){
	// 	return solution.getDownLink();
	// }
	/**
   	 * M�todo para retornar a p�t�ncia antes do amplificadores do optical link.
   	 * @param slot
   	 * @param opticalLink
   	 * @author Andr�
   	 */
	public static double getPowerBInLink(final int slot, final OpticalLink opticalLink){
		return opticalLink.getPowerB(slot);
	}
	// /**
   	//  * M�todo para retornar a exponencial.
   	//  * @param meanRate
   	//  * @author Andr�
   	//  */
	// public double exponencial(final double meanRate){
	// 	return (double) ( - Math.log( 1 -  uniform(0.0, 1.0)) / meanRate );
	// }

	// // public double exponencial(final double meanRate){
	// // 	final double random = Math.random();
	// // 	return (-1/meanRate)*(Math.log10(random)/Math.log10(Math.E));
	// // }

	// private double uniform(double x_min, double x_max) {
	// 	double va = (double) random.nextInt() / Integer.MAX_VALUE;
	// 	va += va / Integer.MAX_VALUE;
	// 	va += va / Integer.MAX_VALUE;

	// 	while ((va <= 0) || (va >= 1.0 - (Math.pow(Integer.MAX_VALUE, -3)))){
	// 		va = (double) random.nextInt() / Integer.MAX_VALUE;
	// 		va += va / Integer.MAX_VALUE;
	// 		va += va / Integer.MAX_VALUE;
	// 	}
			
	// 	return (x_min + va * (x_max - x_min));

	// }
	// /**
   	//  * M�todo para retornar o n�mero de slots requeridos
   	//  * conforme o formato de modula��o usado.
   	//  * @param modLevelType
   	//  * @param callRequest
   	//  * @author Andr�
   	//  */
	// public static int calculateNumberOfSlots(final ModulationLevelType modLevelType, final int bitRate){

	// 	final double bZero = SimulationParameters.getSpacing();
	// 	int requNumbOfSlots = 0; // NOPMD by Andr� on 07/06/17 15:31

	// 	final double constelationLog2 = Math.log10(modLevelType.getConstelation())/Math.log10(2);
	// 	final double temp = (constelationLog2*bZero)/1E9;

	// 	if(bitRate%temp==0){
	// 		requNumbOfSlots = (int) (bitRate/temp);
	// 	}else{
	// 		requNumbOfSlots = (int) (bitRate/temp); // NOPMD by Andr� on 07/06/17 15:31
	// 		requNumbOfSlots++;
	// 	}

	// 	return requNumbOfSlots;
	// }
	// /**
   	//  * M�todo para retornar o n�mero de slots requeridos
   	//  * conforme o formato de modula��o usado (Sobrecarregado).
   	//  * @param modLevelType
   	//  * @param callRequest
   	//  * @author Andr�
   	//  */
	// public int calculateNumberOfSlots(final ModulationLevelType modLevelType, final DPCallRequest dpCallRequest){

	// 	final double bZero = SimulationParameters.getSpacing();
	// 	int requNumbOfSlots = 0; // NOPMD by Andr� on 07/06/17 15:31

	// 	final double constelationLog2 = Math.log10(modLevelType.getConstelation())/Math.log10(2);
	// 	final double temp = constelationLog2*bZero/1E9;


	// 	if(dpCallRequest.getBitRate()%temp==0){
	// 		requNumbOfSlots = (int) (dpCallRequest.getBitRate()/temp);
	// 	}else{
	// 		requNumbOfSlots = (int) (dpCallRequest.getBitRate()/temp); // NOPMD by Andr� on 07/06/17 15:32
	// 		requNumbOfSlots++;
	// 	}

	// 	return requNumbOfSlots;
	// }
	// /**
   	//  * M�todo para avaliar a OSNR de um caminho �ptico.
   	//  * @param network
   	//  * @param path
   	//  * @param frequencySlot
   	//  * @return O valor de OSNR do caminho �ptico.
   	//  * @author Andr�
   	//  */

	// public double evaluateOSNR(final List<OpticalLink> path, final int frequencySlot) throws InvalidRoutesException{ // NOPMD by Andr� on 12/06/17 14:38

	// 	final double FREQ = 193400000000000.00;

	// 	if(path.isEmpty()){
	// 		throw new InvalidRoutesException("Invalid path in evaluateOSNR function.", 2);
	// 	}

	// 	final SimulationParameters parameter = new SimulationParameters();
	// 	double signal = Math.pow(10,parameter.getLaserPower()/10);
	// 	double noise = Math.pow(10,parameter.getLaserPower()/10)/Math.pow(10,parameter.getOSNRIn()/10);

	// 	for(final OpticalLink opticalLink : path){

	// 		final double muxGain = Math.pow(10,parameter.getMuxLoss()/10);
	// 		final double switchGain = Math.pow(10,parameter.getSwitchLoss()/10);

	// 		signal *= switchGain*muxGain;
	// 		noise *= switchGain*muxGain;

	// 		final double boosterGain = getGainInAmplifier(opticalLink.getBooster());
	// 		final double nfactBoost = getNFInAmplifier(opticalLink.getBooster());
	// 		//final double addNoisBoost = 500*(boosterGain-1)*opticalLink.getFrequency(frequencySlot)*SimulationParameters.getPlanck()*SimulationParameters.getSpacing()*nfactBoost;
	// 		final double addNoisBoost = 500*(boosterGain-1)*FREQ*SimulationParameters.getPlanck()*SimulationParameters.getSpacing()*nfactBoost;

	// 		signal *= boosterGain;
	// 		noise = noise*boosterGain+addNoisBoost;

	// 		for(final OpticalSpan opticalSpan : opticalLink.getSpans()){

	// 			final double dioLossInLinear = Math.pow(10,parameter.getDioLoss()/10);
	// 			final double atenCoeff = parameter.getFiberAtenuationCoefficient();

	// 			signal *= dioLossInLinear; //First connector in fiber
	// 			noise *= dioLossInLinear;

	// 			final OpticalFiber opticalFiber = getOpticalFiber(opticalSpan);
	// 			final double fiberGain = atenCoeff*getLengthInFiber(opticalFiber);
	// 			signal = signal*Math.pow(10,fiberGain/10);
	// 			noise = noise*Math.pow(10,fiberGain/10);

	// 			signal *= dioLossInLinear; //Second connector in fiber
	// 			noise *= dioLossInLinear;

	// 			final double amplifierGain = getGainInAmplifier(opticalSpan.getOpticalAmplifier());
	// 			final double nFactAmplif = getNFInAmplifier(opticalSpan.getOpticalAmplifier());
	// 			//final double addNoiseAmplif = 500*(amplifierGain-1)*opticalLink.getFrequency(frequencySlot)*SimulationParameters.getPlanck()*SimulationParameters.getSpacing()*nFactAmplif;
	// 			final double addNoiseAmplif = 500*(amplifierGain-1)*FREQ*SimulationParameters.getPlanck()*SimulationParameters.getSpacing()*nFactAmplif;

	// 			signal *= amplifierGain;
	// 			noise = noise*amplifierGain+addNoiseAmplif;
	// 		}

	// 		signal *= muxGain;
	// 		noise *= muxGain;
	// 	}

	// 	return signal/noise;

	// }
	// /**
   	//  * M�todo para retornar o ganho (linear) do objeto optical amplifier.
   	//  * @param opticalAmplifier
   	//  * @return O valor do ganho.
   	//  * @author Andr�
   	//  */
	// private double getGainInAmplifier(final OpticalAmplifier opticalAmplifier){
	// 	return opticalAmplifier.getGainInLinear();
	// }
	// /**
   	//  * M�todo para retornar o fator de ru�do (linear) do objeto optical amplifier.
   	//  * @param opticalAmplifier
   	//  * @return O valor do NF.
   	//  * @author Andr�
   	//  */
	// private double getNFInAmplifier(final OpticalAmplifier opticalAmplifier){
	// 	return opticalAmplifier.getNoiseFactorInLinear();
	// }
	// /**
   	//  * M�todo para retornar o comprimento da fibra no objeto optical fiber.
   	//  * @param opticalFiber
   	//  * @return O valor do comprimento.
   	//  * @author Andr�
   	//  */
	// private double getLengthInFiber(final OpticalFiber opticalFiber) {
	// 	return opticalFiber.getLength();
	// }
	// /**
   	//  * M�todo para retornar o objeto optical fiber.
   	//  * @param opticalSpan
   	//  * @return O objeto opticalFiber.
   	//  * @author Andr�
   	//  */
	// private OpticalFiber getOpticalFiber(final OpticalSpan opticalSpan) {
	// 	return opticalSpan.getOpticalFiber();
	// }
	// /**
   	//  * M�todo para retornar a m�dia entre um grupo de valores.
   	//  * @param values
   	//  * @author Andr�
   	//  */
	// public static double getMean (final List<Double> values){

	// 	double meanValue = 0.0;

	// 	for(int x=0;x<values.size();x++){
	// 		meanValue+=values.get(x);
	// 	}

	// 	return meanValue/values.size();

	// }
	// /**
   	//  * M�todo para retornar o desvio padr�o entre um grupo de valores.
   	//  * @param values
   	//  * @author Andr�
   	//  */
	// public static double getStandardDeviation (final List<Double> values){

	// 	final double mean = getMean(values); // NOPMD by Andr� on 07/06/17 15:33
	// 	double sum = 0.0;

	// 	for(int x=0;x<values.size();x++){
	// 		sum+= Math.pow((values.get(x)-mean), 2);
	// 	}

	// 	return Math.sqrt(sum/values.size());
	// }
	// /**
   	//  * M�todo para retornar a inversa do valor da m�trica CFSA.
   	//  * @param resulting
   	//  * @param requNumbOfSlots
   	//  * @param numberOfSlots
   	//  * @return 1/(CFSA+1)
   	//  * @author Andr�
   	//  */
	// public double evaluateInverseCFSA(final List<Integer> resulting, final int requNumbOfSlots, final int numberOfSlots){ // NOPMD by Andr� on 12/06/17 14:48

    //     int status = 1; // NOPMD by Andr� on 07/06/17 15:37
    //     boolean[] wasVisited = new boolean[numberOfSlots]; //Marca se o lambda ja foi visitado. // NOPMD by Andr� on 07/06/17 15:36

    //     //Inicializa o vetor de visita.
    //     for(int y=0;y<numberOfSlots;y++){
    //     	wasVisited[y]=false; // NOPMD by Andr� on 07/06/17 15:34
    //     }

    //     int countForms = 0;
    //     int count = 0; // NOPMD by Andr� on 07/06/17 15:34

    //     for(int m=0;m<numberOfSlots;m++){
    //     	if(!wasVisited[m]){
    //     		wasVisited[m]=true; // NOPMD by Andr� on 07/06/17 15:33
    //     		if(resulting.get(m)==status){
    //     			count+=1;
    //     			for(int y=m+1;y<numberOfSlots;y++){
    //     				if(resulting.get(m)==status){
    //     					count+=1;
    //     					wasVisited[y]=true; // NOPMD by Andr� on 07/06/17 15:34
    //     					if(y==(numberOfSlots-1) && count>=requNumbOfSlots){
    //     						countForms += count - requNumbOfSlots +1;
    //     					}
    //     				}else{
    //     					if(count>=requNumbOfSlots){
    //     						countForms += count - requNumbOfSlots +1;
    //     						count = 0; // NOPMD by Andr� on 07/06/17 15:34
    //     					}else{
    //     						count = 0; // NOPMD by Andr� on 07/06/17 15:35
    //     					}
    //     					break;
    //     				}
    //     			} //Fim do for y
    //     		}//Fim do if enlace
    //     	}//Fim do if visit
    //     }//for principal

    //     double temp;
    //     double result;
    //     temp = (double)(countForms+1);
    //     result = 1/temp;

    //     return result;
	// }

	// public int getWaysToAllocate(final int numberOfSlots, final int reqNumbOfSlots, final List<OpticalLink> uplink, final List<OpticalLink> downlink) {

	// 	int numberOfWaysToAllocate = 0;

	// 	POINT: for(int indexSlot = 0; indexSlot < numberOfSlots - reqNumbOfSlots +1; indexSlot++){


	// 		// Para cada link em uplink
	// 		for(int f=0;f<uplink.size();f++){
	// 			final OpticalLink opticallinkUp = uplink.get(f);
	// 			final boolean availSlotIn = avaliableSlotInOpticalLink(opticallinkUp, indexSlot);
	// 			if(!availSlotIn){ //Analisa o primeiro slot dispon�vel da grade
	// 				continue POINT; //Procura o pr�ximo slot dispon�vel na grade para come�ar o processo.
	// 			}
	// 		}

	// 		int countEmptySlots = 0;
	// 		// Para cada slot necessário para alocar a requisição;
	// 		for (int indexSlotReq = 0; indexSlotReq < reqNumbOfSlots; indexSlotReq++){

	// 			// Para cada link em uplink
	// 			for(int f=0;f<uplink.size();f++){
	// 				final OpticalLink opticallinkUp = uplink.get(f);
	// 				final boolean availSlotIn = avaliableSlotInOpticalLink(opticallinkUp, indexSlot + indexSlotReq);
	// 				if(!availSlotIn){ //Analisa o primeiro slot dispon�vel da grade
	// 					continue POINT; //Procura o pr�ximo slot dispon�vel na grade para come�ar o processo.
	// 				}
	// 			}
	// 			countEmptySlots ++;
	// 			if (countEmptySlots == reqNumbOfSlots){
	// 				numberOfWaysToAllocate++;
	// 			}
	// 		}
	// 	}

	// 	return numberOfWaysToAllocate;
	// }

	// public static Vector<Double> removeOutliers(Vector<Double> elements, double percents){

	// 	Vector<Double> newElements = new Vector<Double>();

	// 	double elementsArray[] = new double[elements.size()];

	// 	for (int i = 0; i < elements.size(); i++){
	// 		elementsArray[i] = elements.get(i);
	// 	}

	// 	Arrays.sort(elementsArray);

	// 	int numberOfElementsToRemove = (int)(Math.floor(elementsArray.length * percents));

	// 	for (int i = numberOfElementsToRemove; i < (elementsArray.length - numberOfElementsToRemove); i++){
	// 		newElements.add(elementsArray[i]);
	// 	}

	// 	return newElements;
	// }
}
