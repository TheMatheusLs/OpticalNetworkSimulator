package src.Routing;

import java.util.ArrayList;
import java.util.List;

import src.ParametersSimulation;
import src.GeneralClasses.Function;

class Apeture {
    private int initPosition;
    private int size; 

    public Apeture(int initPosition, int size){
        this.initPosition = initPosition;
        this.size = size;
    }

    public int getPosition(){
        return initPosition;
    }

    public int getSize(){
        return size;
    }
}

public class MSCL {
    
    List<List<Integer>> slotsMSCL;
    List<Route> routesOD;
    int bitRate;
    int bestIndexMSCL;

    public MSCL(List<Route> routesOD, int bitRate){
        this.routesOD = routesOD;
        this.slotsMSCL = new ArrayList<List<Integer>>();
        this.bitRate = bitRate;
        this.bestIndexMSCL = 0;
    }

    public boolean Sequencial() throws Exception{

        double valuesLostCapacity;

        for (Route route : this.routesOD){
            valuesLostCapacity = getRouteMSCLCost(route, this.bitRate);

            if (valuesLostCapacity < (Double.MAX_VALUE / 3)){
                return true;
            }
            
            this.bestIndexMSCL++;
        }

        return false;
    }

    public boolean Combinado() throws Exception{

        List<Double> valuesLostCapacity = new ArrayList<Double>();

        for (Route route : this.routesOD){
            valuesLostCapacity.add(getRouteMSCLCost(route, this.bitRate));
        }

        double minValue = Double.MAX_VALUE;

        for (int index = 0; index < this.routesOD.size(); index++){
            if (minValue > valuesLostCapacity.get(index)){
                minValue = valuesLostCapacity.get(index);
                this.bestIndexMSCL = index;
            }
        }

        if (minValue != Double.MAX_VALUE){
            return true;
        }

        return false;
    }

    public Route getRoute() {
        return this.routesOD.get(this.bestIndexMSCL);
    }

    public List<Integer> getSlots() {
        return this.slotsMSCL.get(this.bestIndexMSCL);
    }

    public double getRouteMSCLCost(Route route, int bitRate) throws Exception{

        int sizeSlotReq = Function.getNumberSlots(ParametersSimulation.getMudulationLevelType()[0], bitRate);

        // Encontrar todos os buracos para a rota principal.
        List<Apeture> allApertures = getApetures(route, 0, ParametersSimulation.getNumberOfSlotsPerLink() - 1);
        
        boolean isPossibleToAlocateReq = false;
        // Verifica se é possível alocar essa requisção dentro da rota principal (route)
        for (Apeture aperture : allApertures){
            if (aperture.getSize() >= sizeSlotReq){
                isPossibleToAlocateReq = true;
                break;
            }
        }
        
        if (!isPossibleToAlocateReq){

            this.slotsMSCL.add(new ArrayList<Integer>());
            
            //Se FS não exite então Retorna um valor alto
            return Double.MAX_VALUE / 2; //Divide por 2 para evitar bugs do desempate
        }
        

        double bestLostCapacity = Double.MAX_VALUE / 2;
        int bestSlotToCapacity = -1;
        // Percorre os slots possíveis.
        for (Apeture aperture: allApertures){
            POINT_SLOT:for (int indexSlot = aperture.getPosition(); indexSlot < aperture.getPosition() + aperture.getSize(); indexSlot++){

                int startSlot = indexSlot;
                int finalSlot = indexSlot + sizeSlotReq - 1;

                if (finalSlot >= ParametersSimulation.getNumberOfSlotsPerLink()){
                    break;
                }

                // Verifica se é possível alocar nessa posição
                POINT_TEST:for (int s = startSlot; s <= finalSlot;s++){
                    if (route.getSlotValue(s) > 0){
                        continue POINT_SLOT;
                    }
                    else {
                        continue POINT_TEST;
                    }
                }

                // Chegando aqui é possível fazer a alocação e começa o cálculo de capacidade para o slot
                // Cria a lista dos slots para a requisição
                List<Integer> slotsReqFake = new ArrayList<Integer>();
                for (int s = startSlot; s <= finalSlot; s++){
                    slotsReqFake.add(s);
                }
            
                double lostCapacityTotal = 0.0;
        
                // *** Rota principal
                // Cálculo da capacidade antes da alocação na rota principal
                double capacityBeforeRoute = 0.0;

                List<Integer> possibleSlotsByRoute = this.getSizeSlotByBitrate();

                for (int possibleReqSize: possibleSlotsByRoute){
                    if (possibleReqSize > aperture.getSize()){
                        break;
                    }
                    capacityBeforeRoute += (aperture.getSize() - possibleReqSize + 1);
                }
                
                // Aloca a requisição fake
                route.incrementSlotsOcupy(slotsReqFake);

                //Cálculo da capacidade depois da alocação na rota principal
                //Encontra os buracos formados
                List<Apeture> apertureMainroute = getApetures(route, aperture.getPosition(), aperture.getPosition() + aperture.getSize() - 1);

                double capacityAfterRoute = 0.0;
                for (Apeture apetureInApeture : apertureMainroute) {
                    for (int possibleReqSize: possibleSlotsByRoute){
                        if (possibleReqSize > apetureInApeture.getSize()){
                            break;
                        }
                        capacityAfterRoute += (apetureInApeture.getSize() - possibleReqSize + 1);
                    }
                }

                route.decreasesSlotsOcupy(slotsReqFake);

                // Calcula a perda de capacidade na rota principal
                lostCapacityTotal += capacityBeforeRoute - capacityAfterRoute;

                List<Route> iRoutesOrder = iRoutesOrderBy(route.getConflictRoutesNonDominants(), 12 * route.getNumHops());

                //for (Route iRoute: route.getConflictRoute()){
                for (Route iRoute: iRoutesOrder){

                    // Busca o mínimo a esquerda
                    int minSlot = findSlotLeft(iRoute, startSlot);
                    
                    // Busca o máximo a direita
                    int maxSlot = findSlotRight(iRoute, finalSlot);

                    if (minSlot == -1){
                        throw new Exception("Erro: minSlot = -1");
                    }

                    List<Apeture> apetureAfectInIRoute = getApetures(iRoute, minSlot, maxSlot);

                    //possibleSlotsByRoute = iRoute.getSizeSlotTypeByBitrate();
                    possibleSlotsByRoute = this.getSizeSlotByBitrate();
                    
                    capacityBeforeRoute = 0.0;
                    
                    // *** Rota interferente
                    // Cálculo da capacidade antes da alocação na rota inteferente
                    for (Apeture apetureIRoute : apetureAfectInIRoute) {
                        for (int possibleReqSize: possibleSlotsByRoute){
                            if (possibleReqSize > apetureIRoute.getSize()){
                                break;
                            }
                            capacityBeforeRoute += (apetureIRoute.getSize() - possibleReqSize + 1);
                        }
                    }

                    // Aloca a requisição fake
                    iRoute.incrementSlotsOcupy(slotsReqFake);

                    //Cálculo da capacidade depois da alocação na rota inteferente
                    //Encontra os buracos formados
                    apertureMainroute = getApetures(iRoute, minSlot, maxSlot);

                    capacityAfterRoute = 0.0;
                    for (Apeture apetureInApeture : apertureMainroute) {
                        for (int possibleReqSize: possibleSlotsByRoute){
                            if (possibleReqSize > apetureInApeture.getSize()){
                                break;
                            }
                            capacityAfterRoute += (apetureInApeture.getSize() - possibleReqSize + 1);
                        }
                    }

                    iRoute.decreasesSlotsOcupy(slotsReqFake);

                    if (capacityBeforeRoute < capacityAfterRoute){
                        throw new Exception("Erro: emptySlots = -1");
                    }

                    lostCapacityTotal += capacityBeforeRoute - capacityAfterRoute;
                    
                }

                if (lostCapacityTotal < 0){
                    throw new Exception("Erro: emptySlots = -1");
                }

                if (lostCapacityTotal < bestLostCapacity){
                    bestLostCapacity = lostCapacityTotal;
                    bestSlotToCapacity = indexSlot;
                }
            }
        }
        List<Integer> slotsReq = new ArrayList<Integer>();
        for (int s = bestSlotToCapacity; s <= bestSlotToCapacity + sizeSlotReq - 1; s++){
            slotsReq.add(s);
        }

        //slotsByKRouteMCLS.add(slotsReq);
        this.slotsMSCL.add(slotsReq);

        return bestLostCapacity;
    }

    private List<Route> iRoutesOrderBy(List<Route> conflictRoute, int numberOfRoutes) {
        List<Route> conflictRouteAux = new ArrayList<Route>();
        List<Route> newIRoutesList = new ArrayList<Route>();

        double maxCost = Double.MAX_VALUE;

        // Encontra o maior custo
        for (Route iRoute : conflictRoute){
            conflictRouteAux.add(iRoute);

            if (maxCost > iRoute.getCost()){
                maxCost = iRoute.getCost();
            }
        }

        LOOP: while ((conflictRouteAux.size() > 0) && (newIRoutesList.size() < numberOfRoutes)){
            for (Route iRoute : conflictRouteAux){
                if (maxCost == iRoute.getCost()){
                    newIRoutesList.add(iRoute);
                }

                if (newIRoutesList.size() == numberOfRoutes){
                    break LOOP;
                }
            }

            for (Route route : newIRoutesList){
                conflictRouteAux.remove(route);
            }

            maxCost = Double.MAX_VALUE;
            for (Route iRoute : conflictRouteAux){
                if (maxCost > iRoute.getCost()){
                    maxCost = iRoute.getCost();
                }
            }
        }


        return newIRoutesList;
    }

    private List<Integer> getSizeSlotByBitrate() {

        List<Integer> possibleSlots = new ArrayList<Integer>();

        for (int bitRate : ParametersSimulation.getTrafficOption()){
            possibleSlots.add(Function.getNumberSlots(ParametersSimulation.getMudulationLevelType()[0], bitRate));
        }

        return possibleSlots;
    }

    public List<Apeture> getApetures(Route route, int initSlotToSearch, int finalSlotToSearch) throws Exception{

        List<Apeture> allApertures = new ArrayList<Apeture>();

        int emptySlots = initSlotToSearch;

        if (emptySlots == -1){
            throw new Exception("Erro: emptySlots = -1");
        }

		INDEX_SLOT:for(int indexSlot = emptySlots; indexSlot <= finalSlotToSearch; indexSlot++){
            
            if (indexSlot == -1){
                throw new Exception("Erro: emptySlots = -1");
            }

            if (route.getSlotValue(indexSlot) > 0){
                continue INDEX_SLOT;
            }

            int countEmptySlots = 0;

			// Para cada slot necessário para alocar a requisição;
			EMPTY_SLOTS:for (emptySlots = indexSlot; emptySlots <= finalSlotToSearch; emptySlots++){

				if (route.getSlotValue(emptySlots) > 0){
                    break EMPTY_SLOTS;
                }

				countEmptySlots++;
			}

            allApertures.add(new Apeture(indexSlot, countEmptySlots));

            indexSlot = emptySlots;
        }

		return allApertures;
    }

    public int findSlotLeft(Route route, int initSlot) throws Exception{
        // Busca o mínimo a esquerda
        int minSlot = initSlot; // Força um erro
        for (int min = initSlot; min >= 0;min--){
            if (route.getSlotValue(min) == 0){
                minSlot = min;
            } else {
                break;
            }
        }

        if (minSlot == -1){
            throw new Exception("Erro: emptySlots = -1");
        }

        return minSlot;
    }

    public int findSlotRight(Route route, int initSlot) throws Exception{
        // Busca o máximo a direita
        int maxSlot = initSlot; // Força um erro
        for (int max = initSlot; max < ParametersSimulation.getNumberOfSlotsPerLink(); max++){
            if (route.getSlotValue(max) == 0){
                maxSlot = max;
            } else {
                break;
            }
        }

        if (maxSlot == ParametersSimulation.getNumberOfSlotsPerLink() + 1){
            throw new Exception("Erro: emptySlots = -1");
        }

        return maxSlot;
    }
}