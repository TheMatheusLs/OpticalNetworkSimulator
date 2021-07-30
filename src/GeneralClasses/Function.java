package src.GeneralClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.ParametersSimulation;
import src.Parameters.SimulationParameters;
import src.Routing.Route;
import src.Structure.OpticalAmplifier;
import src.Structure.OpticalFiber;
import src.Structure.OpticalLink;
import src.Structure.OpticalSpan;
import src.Structure.OpticalSwitch;
import src.Types.ModulationLevelType;

public class Function {

	/**
	 * @param sequence_A
	 * @param sequence_B
	 * @return Verdadeiro caso a sequencia A esteja contida na sequência B; 
	 */
	public static boolean isSequenceADomainsB(List<Integer> sequence_A, List<Integer> sequence_B){
		
		String sequence_A_s = "";
		for (int value : sequence_A){
			sequence_A_s += String.format("|%d|", value);
		}

		String sequence_B_s = "";
		for (int value : sequence_B){
			sequence_B_s += String.format("|%d|", value);
		}

		return sequence_A_s.contains(sequence_B_s);
	}


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
	/**
   	 * M�todo para retornar a p�t�ncia antes do amplificadores do optical link.
   	 * @param slot
   	 * @param opticalLink
   	 * @author Andr�
   	 */
	public static double getPowerBInLink(final int slot, final OpticalLink opticalLink){
		return opticalLink.getPowerB(slot);
	}
	/**
   	 * M�todo para avaliar a OSNR de um caminho �ptico.
   	 * @param network
   	 * @param path
   	 * @param frequencySlot
   	 * @return O valor de OSNR do caminho �ptico.
   	 * @author Andr�
   	 */
	public static double evaluateOSNR(Route route) throws Exception{

		final List<OpticalLink> path = route.getUpLink();

		final double FREQ = 193400000000000.00; //TODO: Levar isso para a classe de parâmetros

		if(path.isEmpty()){
			throw new Exception("Invalid path in evaluateOSNR function.");
		}
		double signal = Math.pow(10, SimulationParameters.getLaserPower()/10);
		double noise = Math.pow(10, SimulationParameters.getLaserPower()/10)  /Math.pow(10, SimulationParameters.getOSNRIn()/10);

		for(final OpticalLink opticalLink : path){

			final double muxGain = Math.pow(10, SimulationParameters.getMuxLoss()/10);
			final double switchGain = Math.pow(10, SimulationParameters.getSwitchLoss()/10);

			signal *= switchGain*muxGain;
			noise *= switchGain*muxGain;

			final double boosterGain = getGainInAmplifier(opticalLink.getBooster());
			final double nfactBoost = getNFInAmplifier(opticalLink.getBooster());
			//final double addNoisBoost = 500*(boosterGain-1)*opticalLink.getFrequency(frequencySlot)*SimulationParameters.getPlanck()*SimulationParameters.getSpacing()*nfactBoost;
			final double addNoisBoost = 500 * (boosterGain-1) * FREQ * SimulationParameters.getPlanck() * SimulationParameters.getSpacing() * nfactBoost;

			signal *= boosterGain;
			noise = noise * boosterGain + addNoisBoost;

			for(final OpticalSpan opticalSpan : opticalLink.getSpans()){

				final double dioLossInLinear = Math.pow(10, SimulationParameters.getDioLoss()/10);
				final double atenCoeff = SimulationParameters.getFiberAtenuationCoefficient();

				signal *= dioLossInLinear; //First connector in fiber
				noise *= dioLossInLinear;

				final OpticalFiber opticalFiber = getOpticalFiber(opticalSpan);
				final double fiberGain = atenCoeff*getLengthInFiber(opticalFiber);
				signal = signal*Math.pow(10,fiberGain/10);
				noise = noise*Math.pow(10,fiberGain/10);

				signal *= dioLossInLinear; //Second connector in fiber
				noise *= dioLossInLinear;

				final double amplifierGain = getGainInAmplifier(opticalSpan.getOpticalAmplifier());
				final double nFactAmplif = getNFInAmplifier(opticalSpan.getOpticalAmplifier());
				//final double addNoiseAmplif = 500*(amplifierGain-1)*opticalLink.getFrequency(frequencySlot)*SimulationParameters.getPlanck()*SimulationParameters.getSpacing()*nFactAmplif;
				final double addNoiseAmplif = 500*(amplifierGain-1)*FREQ*SimulationParameters.getPlanck()*SimulationParameters.getSpacing()*nFactAmplif;

				signal *= amplifierGain;
				noise = noise*amplifierGain+addNoiseAmplif;
			}

			signal *= muxGain;
			noise *= muxGain;
		}

		return signal/noise;
	}
	/**
   	 * M�todo para retornar o ganho (linear) do objeto optical amplifier.
   	 * @param opticalAmplifier
   	 * @return O valor do ganho.
   	 * @author Andr�
   	 */
	private static double getGainInAmplifier(final OpticalAmplifier opticalAmplifier){
		return opticalAmplifier.getGainInLinear();
	}
	/**
		* M�todo para retornar o fator de ru�do (linear) do objeto optical amplifier.
		* @param opticalAmplifier
		* @return O valor do NF.
		* @author Andr�
		*/
	private static double getNFInAmplifier(final OpticalAmplifier opticalAmplifier){
		return opticalAmplifier.getNoiseFactorInLinear();
	}
	/**
   	 * M�todo para retornar o comprimento da fibra no objeto optical fiber.
   	 * @param opticalFiber
   	 * @return O valor do comprimento.
   	 * @author Andr�
   	 */
	private static double getLengthInFiber(final OpticalFiber opticalFiber) {
		return opticalFiber.getLength();
	}
	/**
		* M�todo para retornar o objeto optical fiber.
		* @param opticalSpan
		* @return O objeto opticalFiber.
		* @author Andr�
		*/
	private static OpticalFiber getOpticalFiber(final OpticalSpan opticalSpan) {
		return opticalSpan.getOpticalFiber();
	}
	// /**
   	//  * M�todo para retornar a m�dia entre um grupo de valores.
   	//  * @param values
   	//  * @author Andr�
   	//  */
	// public static double getMean (final List<Double> values){
	public static int getBitRateIndex(int bitRate) {

		int bitRateIndex = 0;
		for (int b = 0; b <= ParametersSimulation.getTrafficOption().length; b++){
			if (ParametersSimulation.getTrafficOption()[b] == bitRate){
				bitRateIndex = b;
				break;
			}
		}
		return bitRateIndex;
	}
}
