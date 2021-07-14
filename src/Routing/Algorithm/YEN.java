package src.Routing.Algorithm;

import java.util.ArrayList;
import java.util.List;

import src.Routing.Route;
import src.Routing.Routing;
import src.Structure.OpticalSwitch;
import src.Structure.Topology.Topology;

public class YEN {

    public static List<Route> findRoute(int orNode, int deNode, Topology topology, Routing routing) {
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
        
        //for(int k = 1; k < this.K; k++){
        for(int k = 1; k < 200; k++){
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

        List<Route> routesYENOrder= routesOrderByCost(routesYEN, routing.getK());
        
        // while(routesYEN.size() < this.K){
        //     routesYEN.add(null);
        // }
        
        return routesYENOrder;
    }

    private static List<Route> routesOrderByCost(List<Route> routesYEN, int kRoute) {

        List<Route> routesOrder = new ArrayList<Route>();

        for (int k = 1; k <= kRoute; k++){
            double minCost = Double.MAX_VALUE;
            int bestRouteIndex = 0;
            for (int r = 0; r < routesYEN.size(); r++){
                if (minCost > routesYEN.get(r).getCost()){
                    minCost = routesYEN.get(r).getCost();
                    bestRouteIndex = r;
                }
            }

            routesYEN.get(bestRouteIndex).setK(k);

            routesOrder.add(routesYEN.get(bestRouteIndex));
            routesYEN.remove(bestRouteIndex);

            if (routesYEN.size() == 0){
                break;
            }
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
