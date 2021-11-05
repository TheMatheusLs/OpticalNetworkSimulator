package src.Routing;

import java.util.ArrayList;
import java.util.List;

import src.ParametersSimulation;
import src.GeneralClasses.Function;
import src.GeneticAlgorithmMultiMSCL.GeneMSCL;
import src.GeneticAlgorithmMultiMSCL.IndividualMSCL;
import src.Parameters.SimulationParameters;
import src.ParametersSimulation.PhysicalLayerOption;
import src.ParametersSimulation.RoutingAlgorithmType;
import src.Structure.OpticalLink;
import src.Structure.Topology.Topology;
import src.Types.ModulationLevelType;
import src.Routing.Algorithm.Dijkstra;
import src.Routing.Algorithm.YEN;

public class Routing {

    Topology topology;
    int K;
    List<List<Route>> allRoutes;

    public Routing(Topology topology) throws Exception{

        this.topology = topology;

        RoutingAlgorithmType routingOption = ParametersSimulation.getRoutingAlgorithmType();
        
        if (routingOption.equals(ParametersSimulation.RoutingAlgorithmType.YEN) || routingOption.equals(ParametersSimulation.RoutingAlgorithmType.MSCLSequencial) || routingOption.equals(ParametersSimulation.RoutingAlgorithmType.MSCLCombinado)){
            this.K = ParametersSimulation.getKShortestRoutes();
        } else{
            this.K = 1;
        }

        this.allRoutes = this.init();

        if(isOfflineRouting()){
            if (routingOption.equals(ParametersSimulation.RoutingAlgorithmType.Dijstra)){
                this.Dijkstra();
            } else {
                if (routingOption.equals(ParametersSimulation.RoutingAlgorithmType.YEN) || routingOption.equals(ParametersSimulation.RoutingAlgorithmType.MSCLSequencial) || routingOption.equals(ParametersSimulation.RoutingAlgorithmType.MSCLCombinado)){
                    this.YEN();
                } else {
                    throw new Exception("Invalid offline routing option");
                }
            }
        }

        if (ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){
			this.generateConflictListBidirectional();
		} else {
			this.generateConflictList();
		}

        this.generateDominantConflictRoutes();
        this.generateNonDominantConflictRoutes();

        // Cria o Betweenness para as rotas
        this.generateBetweennessRoutes();

        if (ParametersSimulation.getPhysicalLayerOption().equals(PhysicalLayerOption.Enabled)){
            this.findAllSizeSlotsAndModulationByRoute();
        } else {
            this.findAllSizeSlotsByRoute();
        }

        System.out.println(this);
    }

    private void generateBetweennessRoutes() {

        // Atribui o valor do betweenness ao enlaces
        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoute : routesOD){

                // Verifica se a rota existe
                if (mainRoute == null){
                    continue;
                }

                for (OpticalLink link : mainRoute.getUpLink()){
                    link.incrementBetweenness();
                }

                if (ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){
                    for (OpticalLink link : mainRoute.getDownLink()){
                        link.incrementBetweenness();
                    }
                }
            }
        }

        // Atribui o custo do betweenness as rotas
        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoute : routesOD){

                // Verifica se a rota existe
                if (mainRoute == null){
                    continue;
                }

                int betweennessCostRoute = 0;

                for (OpticalLink link : mainRoute.getUpLink()){
                    betweennessCostRoute += link.getBetweenness();
                }

                if (ParametersSimulation.getCallRequestType().equals(ParametersSimulation.CallRequestType.Bidirectional)){
                    for (OpticalLink link : mainRoute.getDownLink()){
                        betweennessCostRoute += link.getBetweenness();
                    }
                }

                mainRoute.setBetweennessCost(betweennessCostRoute);
            }
        }
    }

    /**
     * Calcula qual a modulação e slots será usada para cada tipo de bitRate
     * @throws Exception
     */
    private void findAllSizeSlotsAndModulationByRoute() throws Exception {
        System.out.println("Encontrando as modulações para as rotas");

        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoute : routesOD){

                // Verifica se a rota existe
                if (mainRoute == null){
                    continue;
                }
                List<ModulationLevelType> modulationLevelByBitrate = new ArrayList<ModulationLevelType>();
                List<Integer> sizeSlotsByBitrate = new ArrayList<Integer>();

                POINT_BITRATE:for (int bitRate: ParametersSimulation.getTrafficOption()){

                    for (ModulationLevelType modLevel: ParametersSimulation.getMudulationLevelType()){

                        final double snrLinear = Math.pow(10, modLevel.getSNRIndB() / 10);
                        final double osnrLinear = (((double) bitRate * 1e9) / (2 * SimulationParameters.getSpacing())) * snrLinear;

                        final double inBoundQot = Function.evaluateOSNR(mainRoute);

                        if(inBoundQot >= osnrLinear){
                            modulationLevelByBitrate.add(modLevel);

                            // Encontra o tamanho do slot
                            final int reqNumbOfSlots = Function.getNumberSlots(modLevel, bitRate);

                            sizeSlotsByBitrate.add(reqNumbOfSlots);

                            continue POINT_BITRATE;
                        }
                    }
                }

                if (ParametersSimulation.getTrafficOption().length == modulationLevelByBitrate.size()){
                    mainRoute.setModulationsTypeByBitrate(modulationLevelByBitrate);
                    mainRoute.setSizeSlotTypeByBitrate(sizeSlotsByBitrate);
                } else {
                    throw new Exception("Erro ao alocar as modulações e slots da camada física!");
                }
            }
        }
    }

    /**
     * Gera uma lista de rotas interferentes que são dominantes.
     */
    private void generateDominantConflictRoutes() {

        System.out.println("Criando a lista de rotas interferentes dominantes...");

        // Percorre todas as rotas encontradas
        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoute : routesOD){
                
                List<Route> dominatsRoutes = new ArrayList<Route>();

                // Verifica se a rota existe
                if (mainRoute == null){
                    continue;
                }

                LOOP_CURRENT : for (Route currentRoute : mainRoute.getConflictRoute()){
                    for (Route routeAux : mainRoute.getConflictRoute()){
                        if (currentRoute.hashCode() == routeAux.hashCode()){
                            continue;
                        }
                        
                        // Essa rota está sendo dominada
                        if (currentRoute.thisRouteIsDominateBy(routeAux)){
                            continue LOOP_CURRENT;
                        }
                    }

                    dominatsRoutes.add(currentRoute);
                }

                mainRoute.setConflitListDominants(dominatsRoutes);
            }
        }
    }

    public String getRoutesCSV() {

        String txt = "Origin,Destination,Cost,K,ConflictRoutes,ConflictRoutesDominants,ConflictRoutesNonDominants,numHops,Path,SlotsByBitrate,ConstalationByBirate\n"; 
        
        for (List<Route> routes : allRoutes){
            for (Route route : routes){
                if (route != null){
                    txt += route.getRouteCSV();
                }
            }
        }

        return txt;
    }

    private void findAllSizeSlotsByRoute() throws Exception {
        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoute : routesOD){

                // Verifica se a rota existe
                if (mainRoute == null){
                    continue;
                }
                List<ModulationLevelType> modulationLevelByBitrate = new ArrayList<ModulationLevelType>();
                List<Integer> sizeSlotsByBitrate = new ArrayList<Integer>();

                POINT_BITRATE:for (int bitRate: ParametersSimulation.getTrafficOption()){

                    modulationLevelByBitrate.add(ParametersSimulation.getMudulationLevelType()[0]);

                    // Encontra o tamanho do slot
                    final int reqNumbOfSlots = Function.getNumberSlots(ParametersSimulation.getMudulationLevelType()[0], bitRate);

                    sizeSlotsByBitrate.add(reqNumbOfSlots);
                }

                if (ParametersSimulation.getTrafficOption().length == modulationLevelByBitrate.size()){
                    mainRoute.setModulationsTypeByBitrate(modulationLevelByBitrate);
                    mainRoute.setSizeSlotTypeByBitrate(sizeSlotsByBitrate);
                } else {
                    throw new Exception("Erro ao alocar as modulações e slots da camada física!");
                }
            }
        }
    }

    /**
     * Gera uma lista de rotas interferentes que são dominantes.
     */
    private void generateNonDominantConflictRoutes() {

        System.out.println("Criando o complemento das rotas interferentes dominantes...");

        // Percorre todas as rotas encontradas
        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoute : routesOD){
                
                List<Route> nonDominatsRoutes = new ArrayList<Route>();

                // Verifica se a rota existe
                if (mainRoute == null){
                    continue;
                }

                // Percorre toda a lista de rotas interferentes
                LOOP_CURRENT : for (Route currentRoute : mainRoute.getConflictRoute()){
                    // Percorre toda a lista de rotas dominantes
                    for (Route dominantRoute : mainRoute.getConflictRoutesDominants()){

                        if (currentRoute.hashCode() == dominantRoute.hashCode()){
                            continue LOOP_CURRENT; 
                        }
                    }

                    nonDominatsRoutes.add(currentRoute);
                }

                // System.out.println("Conflitantes");
                // for (Route domR : mainRoute.getConflictRoute()){
                //     System.out.print(domR);
                // }

                // System.out.println("Não dominadas");
                // for (Route domR : nonDominatsRoutes){
                //     System.out.print(domR);
                // }

                mainRoute.setConflictRoutesNonDominants(nonDominatsRoutes);
            }
        }
    }

    public void generateConflictList(){
        System.out.println("Criando a lista de rotas conflitantes");

        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoutes : routesOD){

                if (mainRoutes == null){
                    continue;
                }

                List<Route> conflictRoutes = new ArrayList<Route>();
                int currentRouteID = mainRoutes.hashCode();

                //Se pelo menos um link for compartilhado pelas rotas há um conflito
                for (OpticalLink mainLink: mainRoutes.getUpLink()){
                    //System.out.println("");

                    // Percorre todas as Rotas
                    for (List<Route> routesODAux : this.allRoutes){
                        for (Route mainRoutesAux : routesODAux){
                            if (mainRoutesAux == null){
                                continue;
                            }
                            BREAK_LINK:for (OpticalLink link: mainRoutesAux.getUpLink()){
                                if ((currentRouteID != mainRoutesAux.hashCode()) && (mainLink == link)){

                                    for (Route pqp : conflictRoutes){
                                        if (pqp == mainRoutesAux){
                                            continue BREAK_LINK;
                                        }
                                    }

                                    conflictRoutes.add(mainRoutesAux);
                                    break BREAK_LINK;
                                }
                            }
                        }
                        
                    }
                }

                mainRoutes.setConflitList(conflictRoutes);
            }
        }
    }

    public void generateConflictListBidirectional() throws Exception{
        System.out.println("Criando a lista de rotas conflitantes. Bidirecional");

        List<Route> allRoutesAux = new ArrayList<Route>();


        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoutes : routesOD){
                if (mainRoutes != null){
                    allRoutesAux.add(mainRoutes);
                }
            }
        }

        // Percorre todas as Rotas
        for (Route mainRoutes : allRoutesAux){

            // Cria a estrutura para armazenar a lista de conflitos
            List<Route> conflictRoutes = new ArrayList<Route>();

            // Armazena o ID da rota principal
            int currentRouteID = mainRoutes.hashCode();

            List<OpticalLink> mainRoutesUplinks = mainRoutes.getUpLink();
            List<OpticalLink> mainRoutesDownlinks = mainRoutes.getDownLink();

            if (mainRoutesUplinks.size() != mainRoutesDownlinks.size()){
                throw new Exception("Os tamanhos de Uplink e Downlink são diferentes");
            }

            // Procura por conflito dentro com os links presente no Uplink da rota principal
            for (int mainl = 0; mainl < mainRoutesUplinks.size(); mainl++) {

                OpticalLink mainRoutesUplink = mainRoutesUplinks.get(mainl);
                OpticalLink mainRoutesDownlink = mainRoutesDownlinks.get(mainl);

                // Percorre todas as Rotas
                for (Route auxConflictRoutes : allRoutesAux){
                
                    List<OpticalLink> auxConflictRoutesUplinks = auxConflictRoutes.getUpLink();
                    List<OpticalLink> auxConflictRoutesDownlinks = auxConflictRoutes.getDownLink();

                    // Percorre os uplinks e downlinks da rota auxConflictRoutes
                    BREAK:for (int auxl = 0; auxl < auxConflictRoutesUplinks.size(); auxl++){

                        OpticalLink auxConflictRoutesUplink = auxConflictRoutesUplinks.get(auxl);
                        OpticalLink auxConflictRoutesDownlink = auxConflictRoutesDownlinks.get(auxl);

                        // Verifica se a rota auxiliar não é a rota principal.
                        boolean isMainRoute = (currentRouteID == auxConflictRoutes.hashCode());

                        // Verifica se o mainUp é diferente do auxUp
                        boolean isMainUpAuxUp = (mainRoutesUplink == auxConflictRoutesUplink);
                        // Verifica se o mainUp é diferente do auxDown
                        boolean isMainUpAuxDown = (mainRoutesUplink == auxConflictRoutesDownlink);
                        // Verifica se o mainDown é diferente do auxUp
                        boolean isMainDownAuxUp = (mainRoutesDownlink == auxConflictRoutesUplink);
                        // Verifica se o mainDown é diferente do auxDown
                        boolean isMainDownAuxDown = (mainRoutesDownlink == auxConflictRoutesDownlink);

                        boolean isAlreadyAdd = false;
                        BREAK_ADD: for (Route RouteInConflict: conflictRoutes){
                            if (RouteInConflict == auxConflictRoutes){
                                isAlreadyAdd = true;
                                break BREAK_ADD;
                            }
                        }

                        if (!isMainRoute && (isMainUpAuxUp || isMainUpAuxDown || isMainDownAuxUp || isMainDownAuxDown) && !isAlreadyAdd){
                            conflictRoutes.add(auxConflictRoutes);
                            break BREAK;
                        }
                    }
                } // end for auxConflictRoutes

                mainRoutes.setConflitList(conflictRoutes);
            } // end for mainRoutesUplink
        }// end for mainRoutes
    }// end function

    public int getK() {
        return K;
    }

    @Override
    public String toString() {

        String txt = ""; //"\t*** Routing ***\n";

        for (List<Route> routes : allRoutes){
            for (Route route : routes){
                if (route != null){
                    txt += route;
                }
            }
        }

        return txt;
    }

    public List<Route> getRoutesForOD(int orN, int deN){
        return allRoutes.get(orN * this.topology.getNumNodes() + deN);
    }

    public boolean isOfflineRouting() {
        
        RoutingAlgorithmType routingOption = ParametersSimulation.getRoutingAlgorithmType();

        if (routingOption.equals(ParametersSimulation.RoutingAlgorithmType.Dijstra) || routingOption.equals(ParametersSimulation.RoutingAlgorithmType.YEN) || routingOption.equals(ParametersSimulation.RoutingAlgorithmType.MSCLSequencial) || routingOption.equals(ParametersSimulation.RoutingAlgorithmType.MSCLCombinado)){
            return true;
        } else {
            return false;
        }
    }

    public void checkIfIsClean() throws Exception{
        for (List<Route> routes: allRoutes){
            for (Route route : routes){
                if (route != null){
                    for (int i = 0; i < ParametersSimulation.getNumberOfSlotsPerLink();i++){
                        if (route.getSlotValue(i) != 0){
                            throw new Exception("As rotas não foram limpas corretamente");
                        }
                    }
                }
            }
		}
    }

    List<List<Route>> init(){
        List<List<Route>> routesInit = new ArrayList<List<Route>>();

        int numNodes = this.topology.getNumNodes();

        for (int OD = 0; OD < (numNodes * numNodes); OD++){
            List<Route> routeAux = new ArrayList<Route>();
            for (int k = 0; k < this.K; k++){
                routeAux.add(null);
            }

            routesInit.add(routeAux);
        }

        return routesInit;
    }

    public void Dijkstra() {
        Route route;
        int numNodes = this.topology.getNumNodes();
        
        for(int orN = 0; orN < numNodes; orN++){
            for(int deN = 0; deN < numNodes; deN++){
                if(orN != deN){
                    route = Dijkstra.findRoute(orN, deN, this.topology, this);
                }
                else{
                    route = null;
                }
                this.setRoute(orN, deN, route);
            }
        }
    }

    public void YEN() throws Exception {
        List<Route> routes;
        int numNodes = this.topology.getNumNodes();
        
        for(int orN = 0; orN < numNodes; orN++){
            for(int deN = 0; deN < numNodes; deN++){
                if(orN != deN){
                    routes = YEN.findRoute(orN, deN, this.topology, this);
                }
                else{
                    List<Route> routesNull = new ArrayList<Route>();
                    for (int i = 0; i < this.getK(); i++){
                        routesNull.add(null);
                    }
                    routes = routesNull;
                }
                this.setRoutes(orN, deN, routes);
            }
        }
    }

    public void setRoute(int orN, int deN, Route route) {
        this.clearRoutes(orN, deN);
        this.addRoute(orN, deN, route);
    }

    public void clearRoutes(int orN, int deN) {
        allRoutes.get(orN * this.topology.getNumNodes() + deN).clear();
    }

    void addRoutes(int orN, int deN, List<Route> routes) {
    
        for(Route it : routes)
            this.addRoute(orN, deN, it);
    }

    void addRoute(int orN, int deN, Route route) {
        allRoutes.get(orN * this.topology.getNumNodes() + deN).add(route);
    }

    public void setRoutes(int orN, int deN, List<Route> routes) {
        this.clearRoutes(orN, deN);
        this.addRoutes(orN, deN, routes);
    }

    public String allSpectrumRoutes() {

        String allSpectrum = "";

        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoutes : routesOD){
                if (mainRoutes == null){
                    continue;
                }

                allSpectrum += String.format("%d,%d,%d,", mainRoutes.origin, mainRoutes.destination, mainRoutes.getConflictRoute().size());

                for (int slot = 0; slot < ParametersSimulation.getNumberOfSlotsPerLink() - 1; slot++){
                    allSpectrum += String.format("%d-", mainRoutes.getSlotValue(slot));  
                }
                allSpectrum += String.format("%d%n", mainRoutes.getSlotValue(ParametersSimulation.getNumberOfSlotsPerLink()-1));
            }
        }

        return allSpectrum;
    }

    public List<List<Route>> getAllRoutes(){
        return this.allRoutes;
    }

    public void updateConflictRoutesForMSCL(IndividualMSCL individual) throws Exception {

        int geneIndex = 0;
        for (int s = 0; s < this.topology.getNumNodes(); s++){
            for (int d = 0; d < this.topology.getNumNodes(); d++){
            
                if (s == d){
                    continue;
                }

                Route route = allRoutes.get(s * this.topology.getNumNodes() + d).get(0);

                int numberOfConflictRoutes = route.getConflictRoute().size();

                GeneMSCL gene = individual.chromosome.get(geneIndex);

                if ((gene.source == s) && (gene.destination == d) && (numberOfConflictRoutes == gene.bitsGenes.size())){
                    List<Route> newRoutesConflict = new ArrayList<Route>(numberOfConflictRoutes);

                    for (int index = 0; index < numberOfConflictRoutes; index++) {
                        if (gene.bitsGenes.get(index)){
                            newRoutesConflict.add(route.getConflictRoute().get(index));
                        }
                    }

                    route.setConflictRoutesForMSCL(newRoutesConflict);

                } else {
                    throw new Exception("Erro na atribuição das rotas a partir do genes");
                }

                geneIndex++;
            }
        }
    }

    public List<Route> orderRoutesByOcupation(List<Route> routeSolution) {

        List<Route> routeList = new ArrayList<Route>();
        List<Route> routeListAux = new ArrayList<Route>();
        
        for (Route route : routeSolution) {
           routeListAux.add(route);
        }

        while (routeListAux.size() > 0){
            
            int minValue = Integer.MAX_VALUE;
            int selectKRouteID = -1;
            Route auxRoute = null;
            for (Route route : routeListAux) {
                if ( route.getSlotOcupationValue() < minValue){
                    minValue = route.getSlotOcupationValue();
                    auxRoute = route;
                    selectKRouteID = route.getK();
                } else {
                    if (( route.getSlotOcupationValue() == minValue) && selectKRouteID > route.getK()){
                        minValue = route.getSlotOcupationValue();
                        auxRoute = route;
                        selectKRouteID = route.getK();
                    }
                }
            }

            routeList.add(auxRoute);
            routeListAux.remove(auxRoute);
        }

        return routeList;
    }
}
