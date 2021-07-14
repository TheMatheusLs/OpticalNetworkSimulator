package src.Routing;

import java.util.ArrayList;
import java.util.List;

import src.ParametersSimulation;
import src.ParametersSimulation.RoutingAlgorithmType;
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
        
        if (routingOption.equals(ParametersSimulation.RoutingAlgorithmType.YEN)){
            this.K = ParametersSimulation.getKShortestRoutes();
        } else{
            this.K = 1;
        }

        this.allRoutes = this.init();

        if(isOfflineRouting()){
            if (routingOption.equals(ParametersSimulation.RoutingAlgorithmType.Dijstra)){
                this.Dijkstra();
            } else {
                if (routingOption.equals(ParametersSimulation.RoutingAlgorithmType.YEN)){
                    this.YEN();
                } else {
                    throw new Exception("Invalid offline routing option");
                }
            }
        }

        System.out.println(this);
    }

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

        if (routingOption.equals(ParametersSimulation.RoutingAlgorithmType.Dijstra) || routingOption.equals(ParametersSimulation.RoutingAlgorithmType.YEN)){
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
