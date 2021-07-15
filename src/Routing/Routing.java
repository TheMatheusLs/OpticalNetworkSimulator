package src.Routing;

import java.util.ArrayList;
import java.util.List;

import src.ParametersSimulation;
import src.ParametersSimulation.RoutingAlgorithmType;
import src.Structure.OpticalLink;
import src.Structure.Topology.Topology;

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

        System.out.println(this);
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
            if ((routesOD != null) && (routesOD.size() > 0)){
                for (Route mainRoutes : routesOD){
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

        String txt = "\t*** Routing ***\n";

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

    public void YEN() {
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
}
