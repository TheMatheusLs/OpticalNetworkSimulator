package src.Routing;

import java.util.ArrayList;
import java.util.List;

import src.ParametersSimulation;
import src.GeneralClasses.Function;
import src.ParametersSimulation.MSCLMetric;
import src.ParametersSimulation.PhysicalLayerOption;

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
    int selectKRouteID;
    private long cycles;

    public MSCL(List<Route> routesOD, int bitRate){
        this.routesOD = routesOD;
        this.slotsMSCL = new ArrayList<List<Integer>>();
        this.bitRate = bitRate;
        this.bestIndexMSCL = 0;
        this.selectKRouteID = -1;
        this.cycles = 0;
    }

    public boolean Sequencial() throws Exception{

        double valuesLostCapacity = Double.MAX_VALUE;

        for (Route route : this.routesOD){

            //this.cycles++;

            if (route != null){
                valuesLostCapacity = getRouteMSCLCost(route, this.bitRate);
            }

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

            //this.cycles++;

            if (route != null){
                valuesLostCapacity.add(getRouteMSCLCost(route, this.bitRate));
            } else {
                valuesLostCapacity.add(Double.MAX_VALUE / 2);
            }
        }

        double minValue = Double.MAX_VALUE;

        for (int index = 0; index < this.routesOD.size(); index++){

            if (minValue > valuesLostCapacity.get(index)){ // Se for igual, escolher a menor rota
                minValue = valuesLostCapacity.get(index);
                this.bestIndexMSCL = index;
                this.selectKRouteID = this.routesOD.get(this.bestIndexMSCL).getK();
            } else {
                if ((minValue == valuesLostCapacity.get(index)) && this.selectKRouteID > this.routesOD.get(index).getK()) { // Se for igual, escolher a menor rota
                    minValue = valuesLostCapacity.get(index);
                    this.bestIndexMSCL = index;
                    this.selectKRouteID = this.routesOD.get(this.bestIndexMSCL).getK();
                }
            }
        }

        if (minValue < (Double.MAX_VALUE / 2) - 500){
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

        int sizeSlotReq = -1;

        if (ParametersSimulation.getPhysicalLayerOption().equals(PhysicalLayerOption.Enabled)){
            int bitRateIndex = Function.getBitRateIndex(bitRate);

            if (route == null){
                return 0;
            }

            sizeSlotReq = route.getNumberOfSlotsByBitRate(bitRateIndex);
        } else {
            sizeSlotReq = Function.getNumberSlots(ParametersSimulation.getMudulationLevelType()[0], bitRate);
        }

        // Encontrar todos os buracos para a rota principal.
        List<Apeture> allApertures = getApetures(route, 0, ParametersSimulation.getNumberOfSlotsPerLink() - 1);
        
        boolean isPossibleToAlocateReq = false;
        // Verifica se ?? poss??vel alocar essa requis????o dentro da rota principal (route)
        for (Apeture aperture : allApertures){

            //this.cycles++;

            if (aperture.getSize() >= sizeSlotReq){
                isPossibleToAlocateReq = true;
                break;
            }
        }
        
        if (!isPossibleToAlocateReq){

            this.slotsMSCL.add(new ArrayList<Integer>());
            
            //Se FS n??o exite ent??o Retorna um valor alto
            return Double.MAX_VALUE / 2; //Divide por 2 para evitar bugs do desempate
        }
        

        double bestLostCapacity = Double.MAX_VALUE / 2;
        int bestSlotToCapacity = -1;
        // Percorre os slots poss??veis.
        for (Apeture aperture: allApertures){

            //this.cycles++;

            POINT_SLOT:for (int indexSlot = aperture.getPosition(); indexSlot < aperture.getPosition() + aperture.getSize(); indexSlot++){

                //this.cycles++;

                int startSlot = indexSlot;
                int finalSlot = indexSlot + sizeSlotReq - 1;

                if (finalSlot >= ParametersSimulation.getNumberOfSlotsPerLink()){
                    break;
                }

                // Verifica se ?? poss??vel alocar nessa posi????o
                POINT_TEST:for (int s = startSlot; s <= finalSlot;s++){

                    //this.cycles++;

                    if (route.getSlotValue(s) > 0){
                        continue POINT_SLOT;
                    }
                    else {
                        continue POINT_TEST;
                    }
                }

                // Chegando aqui ?? poss??vel fazer a aloca????o e come??a o c??lculo de capacidade para o slot
                // Cria a lista dos slots para a requisi????o
                List<Integer> slotsReqFake = new ArrayList<Integer>();
                for (int s = startSlot; s <= finalSlot; s++){

                    //this.cycles++;

                    slotsReqFake.add(s);
                }
            
                double lostCapacityTotal = 0.0;
        
                // *** Rota principal
                // C??lculo da capacidade antes da aloca????o na rota principal
                double capacityBeforeRoute = 0.0;

                List<Integer> possibleSlotsByRoute = null;
                if (ParametersSimulation.getPhysicalLayerOption().equals(PhysicalLayerOption.Enabled)){
                    possibleSlotsByRoute = route.getSizeSlotTypeByBitrate();
                } else {
                    possibleSlotsByRoute = this.getSizeSlotByBitrate();
                }

                for (int possibleReqSize: possibleSlotsByRoute){

                    //this.cycles++;

                    if (possibleReqSize > aperture.getSize()){
                        break;
                    }
                    capacityBeforeRoute += (aperture.getSize() - possibleReqSize + 1);
                }
                
                // Aloca a requisi????o fake
                route.incrementSlotsOcupy(slotsReqFake);

                //C??lculo da capacidade depois da aloca????o na rota principal
                //Encontra os buracos formados
                List<Apeture> apertureMainroute = getApetures(route, aperture.getPosition(), aperture.getPosition() + aperture.getSize() - 1);

                double capacityAfterRoute = 0.0;
                for (Apeture apetureInApeture : apertureMainroute) {

                    //this.cycles++;

                    for (int possibleReqSize: possibleSlotsByRoute){

                        //this.cycles++;

                        if (possibleReqSize > apetureInApeture.getSize()){
                            break;
                        }
                        capacityAfterRoute += (apetureInApeture.getSize() - possibleReqSize + 1);
                    }
                }

                route.decreasesSlotsOcupy(slotsReqFake);

                // Calcula a perda de capacidade na rota principal
                lostCapacityTotal += capacityBeforeRoute - capacityAfterRoute;

                List<Route> iRoutes;
                if (ParametersSimulation.getGaOption().equals(ParametersSimulation.GAOption.GAMSCL)){
                    iRoutes = route.getConflictRoutesForMSCL();
                } else {
                    if (ParametersSimulation.getInterRoutesMSCL().equals(ParametersSimulation.InterRoutesMSCL.AllRoutes)){
                        iRoutes = route.getConflictRoute();
                    } else {
                        if (ParametersSimulation.getInterRoutesMSCL().equals(ParametersSimulation.InterRoutesMSCL.Dominant)){
                            iRoutes = route.getConflictRoutesDominants();
                        } else {
                            if (ParametersSimulation.getInterRoutesMSCL().equals(ParametersSimulation.InterRoutesMSCL.NonDominant)){
                                iRoutes = route.getConflictRoutesNonDominants();
                            } else {
                                if (ParametersSimulation.getInterRoutesMSCL().equals(ParametersSimulation.InterRoutesMSCL.None)){
                                    iRoutes = new ArrayList<>();
                                } else {
                                    throw new Exception("Uma m??trica diferente de disable teve ser selecionada.");
                                }
                            }
                        }
                    }
                }

                List<Route> iRoutesFilter;
                if (!ParametersSimulation.getMSCLMetric().equals(ParametersSimulation.MSCLMetric.Disable)){
                    iRoutesFilter = iRoutesOrderBy(iRoutes, ParametersSimulation.getInterRoutesMSCLFactor());
                } else {
                    iRoutesFilter = iRoutes;   
                }

                for (Route iRoute: iRoutesFilter){

                    this.cycles++;

                    // Busca o m??nimo a esquerda
                    int minSlot = findSlotLeft(iRoute, startSlot);
                    
                    // Busca o m??ximo a direita
                    int maxSlot = findSlotRight(iRoute, finalSlot);

                    if (minSlot == -1){
                        throw new Exception("Erro: minSlot = -1");
                    }

                    List<Apeture> apetureAfectInIRoute = getApetures(iRoute, minSlot, maxSlot);

                    if (ParametersSimulation.getPhysicalLayerOption().equals(PhysicalLayerOption.Enabled)){
                        possibleSlotsByRoute = iRoute.getSizeSlotTypeByBitrate();
                    } else {
                        possibleSlotsByRoute = this.getSizeSlotByBitrate();
                    }
                    
                    capacityBeforeRoute = 0.0;
                    
                    // *** Rota interferente
                    // C??lculo da capacidade antes da aloca????o na rota inteferente
                    for (Apeture apetureIRoute : apetureAfectInIRoute) {

                        //this.cycles++;

                        for (int possibleReqSize: possibleSlotsByRoute){

                            //this.cycles++;

                            if (possibleReqSize > apetureIRoute.getSize()){
                                break;
                            }
                            capacityBeforeRoute += (apetureIRoute.getSize() - possibleReqSize + 1);
                        }
                    }

                    // Aloca a requisi????o fake
                    iRoute.incrementSlotsOcupy(slotsReqFake);

                    //C??lculo da capacidade depois da aloca????o na rota inteferente
                    //Encontra os buracos formados
                    apertureMainroute = getApetures(iRoute, minSlot, maxSlot);

                    capacityAfterRoute = 0.0;
                    for (Apeture apetureInApeture : apertureMainroute) {

                        //this.cycles++;

                        for (int possibleReqSize: possibleSlotsByRoute){

                            //this.cycles++;

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

            //this.cycles++;

            slotsReq.add(s);
        }

        //slotsByKRouteMCLS.add(slotsReq);
        this.slotsMSCL.add(slotsReq);

        return bestLostCapacity;
    }

    List<Route> iRoutesOrderByHops(List<Route> conflictRoute, double factor) {
        List<Route> conflictRouteAux = new ArrayList<Route>();
        List<Route> newIRoutesList = new ArrayList<Route>();

        double maxCost = Double.MAX_VALUE;

        // Encontra o maior custo
        for (Route iRoute : conflictRoute){

            //this.cycles++;

            conflictRouteAux.add(iRoute);

            if (maxCost > iRoute.getCost()){
                maxCost = iRoute.getCost();
            }
        }

        LOOP: while ((conflictRouteAux.size() > 0) && (newIRoutesList.size() < (conflictRoute.size() * factor))){

            //this.cycles++;

            for (Route iRoute : conflictRouteAux){

                //this.cycles++;

                if (maxCost == iRoute.getCost()){
                    newIRoutesList.add(iRoute);
                }

                if (newIRoutesList.size() == (conflictRoute.size() * factor)){
                    break LOOP;
                }
            }

            for (Route route : newIRoutesList){

                //this.cycles++;

                conflictRouteAux.remove(route);
            }

            maxCost = Double.MAX_VALUE;
            for (Route iRoute : conflictRouteAux){

                //this.cycles++;

                if (maxCost > iRoute.getCost()){
                    maxCost = iRoute.getCost();
                }
            }
        }


        return newIRoutesList;
    }

    List<Route> iRoutesOrderBy(List<Route> conflictRoute, double factor) throws Exception {
        List<Route> conflictRouteAux = new ArrayList<Route>();
        List<Route> newIRoutesList = new ArrayList<Route>();

        if (ParametersSimulation.getMetricMSCLMinToMax()){ // Ordena do menor para o maior
            double minCost = Double.MAX_VALUE;

            // Encontra o menor custo
            for (Route iRoute : conflictRoute){

                //this.cycles++;

                conflictRouteAux.add(iRoute);

                if (minCost > returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric())){
                    minCost = returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric());
                }
            }

            LOOP: while ((conflictRouteAux.size() > 0) && (newIRoutesList.size() < (conflictRoute.size() * factor))){

                //this.cycles++;

                for (Route iRoute : conflictRouteAux){

                    //this.cycles++;

                    if (Function.compareDouble(minCost, returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric()))){
                        newIRoutesList.add(iRoute);
                    }
    
                    if (newIRoutesList.size() >= (conflictRoute.size() * factor)){
                        break LOOP;
                    }
                }
    
                for (Route route : newIRoutesList){

                    //this.cycles++;

                    conflictRouteAux.remove(route);
                }
    
                minCost = Double.MAX_VALUE;

                // Encontra o menor custo
                for (Route iRoute : conflictRouteAux){

                    //this.cycles++;

                    if (minCost > returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric())){
                        minCost = returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric());
                    }
                }
            }
    
            return newIRoutesList;
        } else { // Ordena do maior para o menor

            double maxCost = Double.MIN_VALUE;

            // Encontra o menor custo
            for (Route iRoute : conflictRoute){

                //this.cycles++;

                conflictRouteAux.add(iRoute);

                if (maxCost < returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric())){
                    maxCost = returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric());
                }
            }

            LOOP: while ((conflictRouteAux.size() > 0) && (newIRoutesList.size() < (conflictRoute.size() * factor))){

                //this.cycles++;

                for (Route iRoute : conflictRouteAux){

                    //this.cycles++;

                    if (Function.compareDouble(maxCost, returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric()))){
                        newIRoutesList.add(iRoute);
                    }
    
                    if (newIRoutesList.size() >= (conflictRoute.size() * factor)){
                        break LOOP;
                    }
                }
    
                for (Route route : newIRoutesList){

                    //this.cycles++;

                    conflictRouteAux.remove(route);
                }
    
                maxCost = Double.MIN_NORMAL;

                // Encontra o menor custo
                for (Route iRoute : conflictRouteAux){

                    //this.cycles++;

                    if (maxCost < returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric())){
                        maxCost = returnMetricValue(iRoute, ParametersSimulation.getMSCLMetric());
                    }
                }
            }
    
            return newIRoutesList;
        }
    }

    double returnMetricValue(Route route, MSCLMetric metric) throws Exception{
        if (ParametersSimulation.getMSCLMetric().equals(ParametersSimulation.MSCLMetric.Hops)){
            return (double) route.getNumHops();
        } else {
            if (ParametersSimulation.getMSCLMetric().equals(ParametersSimulation.MSCLMetric.Betweenness)){
                return (double) route.getBetweennessCost();
            } else {
                if (ParametersSimulation.getMSCLMetric().equals(ParametersSimulation.MSCLMetric.Ocupation)){
                    return (double) route.getSlotOcupationValue();
                } else {
                    if (ParametersSimulation.getMSCLMetric().equals(ParametersSimulation.MSCLMetric.Disable)){
                        return (double) 0;
                    } else {
                        throw new Exception("Par??metro inv??lido | MSCLMetric");
                    }
                }
            }
        }
    }

    private List<Route> iRoutesOrderByBetweenness(List<Route> conflictRoute, double factor) {

        List<Route> conflictRouteAux = new ArrayList<Route>();
        List<Route> newIRoutesList = new ArrayList<Route>();

        Integer maxCost = Integer.MAX_VALUE;

        // Encontra o maior custo
        for (Route iRoute : conflictRoute){
            conflictRouteAux.add(iRoute);

            if (maxCost > iRoute.getBetweennessCost()){
                maxCost = iRoute.getBetweennessCost();
            }
        }

        LOOP: while ((conflictRouteAux.size() > 0) && (newIRoutesList.size() < (conflictRoute.size() * factor))){
            for (Route iRoute : conflictRouteAux){
                if (maxCost == iRoute.getBetweennessCost()){
                    newIRoutesList.add(iRoute);
                }

                if (newIRoutesList.size() == (conflictRoute.size() * factor)){
                    break LOOP;
                }
            }

            for (Route route : newIRoutesList){
                conflictRouteAux.remove(route);
            }

            maxCost = Integer.MAX_VALUE;
            for (Route iRoute : conflictRouteAux){
                if (maxCost > iRoute.getBetweennessCost()){
                    maxCost = iRoute.getBetweennessCost();
                }
            }
        }

        return newIRoutesList;
    }

    private List<Integer> getSizeSlotByBitrate() {

        List<Integer> possibleSlots = new ArrayList<Integer>();

        for (int bitRate : ParametersSimulation.getTrafficOption()){

            //this.cycles++;

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
            
            //this.cycles++;

            if (indexSlot == -1){
                throw new Exception("Erro: emptySlots = -1");
            }

            if (route.getSlotValue(indexSlot) > 0){
                continue INDEX_SLOT;
            }

            int countEmptySlots = 0;

			// Para cada slot necess??rio para alocar a requisi????o;
			EMPTY_SLOTS:for (emptySlots = indexSlot; emptySlots <= finalSlotToSearch; emptySlots++){

                //this.cycles++;

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
        // Busca o m??nimo a esquerda
        int minSlot = initSlot; // For??a um erro
        for (int min = initSlot; min >= 0;min--){

            //this.cycles++;

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
        // Busca o m??ximo a direita
        int maxSlot = initSlot; // For??a um erro
        for (int max = initSlot; max < ParametersSimulation.getNumberOfSlotsPerLink(); max++){

            //this.cycles++;

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

    public long getCycles() {
        return this.cycles;
    }
}
