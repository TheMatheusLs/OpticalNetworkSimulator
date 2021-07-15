package src.Routing;

import java.util.List;

import src.ParametersSimulation;
import src.GeneralClasses.Function;
import src.Spectrum.FirstFit;
import src.Types.ModulationLevelType;

public class SA_Routing {
    public static Route findRoute(List<Route> routesOD, int bitRate) throws Exception {
        for (int firstIndexSlot = 0; firstIndexSlot < ParametersSimulation.getNumberOfSlotsPerLink();){
            LOOP_ROUTE:for (Route route : routesOD){

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
                }

                // Verifica se é possível alocar a requisição
                List<Integer> slots = FirstFit.findFrequencySlots(ParametersSimulation.getNumberOfSlotsPerLink(), reqNumbOfSlots, route.getUpLink(), route.getDownLink());

                if(!slots.isEmpty() && slots.size()==reqNumbOfSlots){
                    if(slots.get(0) == firstIndexSlot){
                        return route;
                    }
                }
            }
            firstIndexSlot++;
        }

        return null;
    }
}
