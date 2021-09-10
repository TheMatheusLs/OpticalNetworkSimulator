package src.Routing;

import java.util.List;

import src.ParametersSimulation;
import src.GeneralClasses.Function;
import src.ParametersSimulation.PhysicalLayerOption;
import src.Types.ModulationLevelType;

public class SA_Routing {
    public static Route findRoute(List<Route> routesOD, int bitRate) throws Exception {
        for (int firstIndexSlot = 0; firstIndexSlot < ParametersSimulation.getNumberOfSlotsPerLink();){
            LOOP_ROUTE:for (Route route : routesOD){

                if (route == null){
                    continue LOOP_ROUTE;
                }

                int reqNumbOfSlots = Integer.MAX_VALUE;

                ModulationLevelType[] allModulations = ParametersSimulation.getMudulationLevelType();

                if (allModulations.length == 1){
                    reqNumbOfSlots = Function.getNumberSlots(ParametersSimulation.getMudulationLevelType()[0], bitRate);
                } else {
                    if (ParametersSimulation.getPhysicalLayerOption().equals(PhysicalLayerOption.Enabled)){
                        int bitRateIndex = Function.getBitRateIndex(bitRate);
    
                        reqNumbOfSlots = route.getNumberOfSlotsByBitRate(bitRateIndex);
                    }
                }

                // Verifica se é possível alocar a requisição
                for (int indexSlot = firstIndexSlot; indexSlot < firstIndexSlot + reqNumbOfSlots; indexSlot++){
                    if (indexSlot >= ParametersSimulation.getNumberOfSlotsPerLink()){
                        continue LOOP_ROUTE;
                    }
                    
                    if (route.getSlotValue(indexSlot) != 0){
                        continue LOOP_ROUTE;
                    }
                }

                return route;
            }
            firstIndexSlot++;
        }

        return null;
    }
}
