package src.Routing.Algorithm;

import java.util.ArrayList;
import java.util.List;

import src.Routing.Route;
import src.Routing.Routing;
import src.Structure.OpticalSwitch;
import src.Structure.Topology.Topology;
import src.Types.ModulationLevelType;
import src.ParametersSimulation;
import src.GeneralClasses.Function;
import src.Parameters.SimulationParameters;
import src.ParametersSimulation.PhysicalLayerOption;

public class YEN {

    public static List<Route> findRoute(int orNode, int deNode, Topology topology, Routing routing) throws Exception{
        assert(orNode != deNode);

        List<Route> routesYEN = new ArrayList<Route>();
        List<Route> candidateRoutes = new ArrayList<Route>();
        OpticalSwitch spurNode;
        Route spurPath;
        Route rootPath;
        Route totalPath;
        
        // Determine the shortest path from the source to the destination.
        Route newRoute = Dijkstra.findRoute(orNode, deNode, topology, routing);
        routesYEN.add(newRoute);
        
        //for(int k = 1; k < ParametersSimulation.getKShortestRoutes(); k++){
        for(int k = 1; k < 250; k++){
            int auxSize = routesYEN.get(k-1).getNumNodes() - 2;
            
            //The spurNode ranges from the first node to the next to last node 
            //in the previous k-shortest path.
            for(int i = 0; i <= auxSize; i++){
                //spurNode is retrieved from the previous k-shortest path, k − 1.
                spurNode = routesYEN.get(k-1).getNode(i);
                // The sequence of nodes from the source to the spurNode of the 
                //previous k-shortest path.
                rootPath = routesYEN.get(k-1).createPartialRoute(0, i);
                
                for(Route it: routesYEN){
                    //Remove the links that are part of the previous shortest 
                    //paths which share the same rootPath.
                    if(i < it.getNumNodes()){
                        newRoute = it.createPartialRoute(0, i);
                        if(checkPathEquals(rootPath.getPath(), newRoute.getPath())){
                            it.getLink(i).setLinkState(false);
                        }
                    }
                }
                
                for(int  a = 0; a < rootPath.getNumNodes(); a++){
                    if(rootPath.getNode(a).getNodeId() == spurNode.getNodeId())
                        continue;
                    rootPath.getNode(a).setNodeState(false);
                }
                
                // Calculate the spurPath from the spurNode to the destination.
                spurPath = Dijkstra.findRoute(spurNode.getNodeId(), deNode, topology, routing);
                
                if(spurPath != null){
                    // Entire path is made up of the rootPath and spurPath.
                    totalPath = rootPath.addRoute(spurPath);
                    // Add the potential k-shortest path to the queue.
                    candidateRoutes.add(totalPath);
                }
                
                // Add back the edges and nodes that were removed from the graph.
                topology.setAllLinksWorking();
                rootPath.setAllNodesWorking();
            }
            if(candidateRoutes.size() == 0)
                break;    
            
            //Get the first route and store it in vector candidateRoutes
            routesYEN.add(candidateRoutes.get(0));
            candidateRoutes.remove(0);
        }

        List<Route> routesYENOrder = routesOrderByCost(routesYEN, routing.getK());
        
        while(routesYENOrder.size() < ParametersSimulation.getKShortestRoutes()){
            routesYENOrder.add(null);
        }
        
        return routesYENOrder;
    }

    private static List<Route> routesOrderByCost(List<Route> routesYEN, int kRoute) throws Exception {

        List<Route> routesOrder = new ArrayList<Route>();

        int kValue = 1;
        LOOP_ROUTE : while ((routesOrder.size() < kRoute) && (routesYEN.size() > 0)){
            double minCost = Double.MAX_VALUE;
            int bestRouteIndex = 0;
            for (int r = 0; r < routesYEN.size(); r++){
                if (minCost > routesYEN.get(r).getCost()){
                    minCost = routesYEN.get(r).getCost();
                    bestRouteIndex = r;
                }
            }

            Route route = routesYEN.get(bestRouteIndex);

            // Avalia se a rota aceita o SNR, quando utilizada a camada física
            if (ParametersSimulation.getPhysicalLayerOption().equals(PhysicalLayerOption.Enabled)){
                ModulationLevelType lessModulation = ParametersSimulation.getMudulationLevelType()[ParametersSimulation.getMudulationLevelType().length - 1];
    
                int biggestBitRate = ParametersSimulation.getTrafficOption()[ParametersSimulation.getTrafficOption().length - 1]; 
    
                final double snrLinear = Math.pow(10, lessModulation.getSNRIndB()/10);

                final double osnrLinear = (((double) biggestBitRate * 1e9) / (2 * SimulationParameters.getSpacing())) * snrLinear;

                final double inBoundQot = Function.evaluateOSNR(route);

                if(inBoundQot < osnrLinear){
                    routesYEN.remove(bestRouteIndex);
                    continue LOOP_ROUTE;			
                }
            }

            route.setK(kValue);

            routesOrder.add(route);
            routesYEN.remove(bestRouteIndex);

            kValue++;
        }

        return routesOrder;
    }

    static boolean checkPathEquals(List<Integer> path1, List<Integer> path2){
        
        if (path1.size() != path2.size()){
            return false;
        }

        for (int index = 0; index < path1.size(); index++){
            if (path1.get(index) != path2.get(index)){
                return false; 
            }
        }

        return true;
    }
}
