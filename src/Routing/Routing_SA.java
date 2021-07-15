package src.Routing;

import java.util.List;

import src.ParametersSimulation;
import src.GeneralClasses.Function;
import src.Spectrum.FirstFit;
import src.Types.ModulationLevelType;

public class Routing_SA {

    public static Route findRoute(List<Route> routesOD, int bitRate) throws Exception{

        for (Route route : routesOD){
            // Verifica se a rota tem capacidade de alocar a requisição
            int reqNumbOfSlots = Integer.MAX_VALUE;
            ModulationLevelType[] allModulations = ParametersSimulation.getMudulationLevelType();

            if (allModulations.length == 1){
                reqNumbOfSlots = Function.getNumberSlots(ParametersSimulation.getMudulationLevelType()[0], bitRate);
            } else {
                // int bitRateIndex = -1;
                // for (int b = 0; b <= allBitRates.length; b++){
                //     if (allBitRates[b] == bitRate){
                //         bitRateIndex = b;
                //         break;
                //     }
                // }

                // reqNumbOfSlots = Function.getNumberSlots(ParametersSimulation.getMudulationLevelType()[0], bitRate);
            }

            List<Integer> slots = FirstFit.findFrequencySlots(ParametersSimulation.getNumberOfSlotsPerLink(), reqNumbOfSlots, route.getUpLink(), route.getDownLink());

			if(!slots.isEmpty() && slots.size()==reqNumbOfSlots){
                return route;
            }
        }

        return null;
    }
}
