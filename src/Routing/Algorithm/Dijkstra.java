package src.Routing.Algorithm;

import java.util.ArrayList;
import java.util.List;

import src.Routing.Route;
import src.Routing.Routing;
import src.Structure.OpticalLink;
import src.Structure.Topology.Topology;

public class Dijkstra {
    
    public static Route findRoute(int orNode, int deNode, Topology topology, Routing routing) {
        assert(orNode != deNode);
        
        int k = -1, h, hops;
        int i, j, setVertexes;
        double min;
        int numNodes = topology.getNumNodes();
        List<Integer> path = new ArrayList<Integer>();
        List<Integer> invPath = new ArrayList<Integer>();
        OpticalLink auxLink;
        Route routeDJK = null;
        boolean networkDisconnected = false;
        
        List<Double> custoVertice = new ArrayList<Double>();
        List<Integer> precedente = new ArrayList<Integer>();
        List<Integer> pathRev = new ArrayList<Integer>();
        List<Boolean> status = new ArrayList<Boolean>();

        for (int index = 0; index < numNodes; index++){
            custoVertice.add(0.0);
            precedente.add(0);
            pathRev.add(0);
            status.add(false);
        }
        
        //Initializes all vertices with infinite cost
        //and the source vertice with cost zero
        for(i = 0; i < numNodes; i++){
            if(i != orNode)
                custoVertice.set(i, Double.MAX_VALUE);
            else
                custoVertice.set(i, 0.0);
            precedente.set(i, -1);
            status.set(i, false);
        }
        setVertexes = numNodes;
    
        while(setVertexes > 0 && !networkDisconnected){
    
            min = Double.MAX_VALUE;
            
            for(i = 0; i < numNodes; i++)
                if((status.get(i) == false) && (custoVertice.get(i) < min)){
                    min = custoVertice.get(i);
                    k = i;
                }
    
            if(k == (int) deNode)
                break;
            
            status.set(k, true);
            setVertexes--;
            Boolean outputLinkFound = false;
    
            for(j = 0; j < numNodes; j++){
                auxLink = topology.getLink((int) k, (int) j);

                if((auxLink != null) && (auxLink.isLinkWorking()) && (topology.getNode(auxLink.getOriginNode()).isNodeWorking()) && (topology.getNode(auxLink.getDestinationNode()).isNodeWorking())){
                    outputLinkFound = true;
                    
                    if( (status.get(j) == false) && (custoVertice.get(k) + auxLink.getCost() < custoVertice.get(j)) ){
                       custoVertice.set(j, (custoVertice.get(k) + auxLink.getCost()));
                       precedente.set(j, k);
                    }
                }
            }
            
            if(!outputLinkFound)
                networkDisconnected = true;
        }
        
        if(!networkDisconnected){
            path.add(deNode);
            hops = 0;
            j = deNode;
            
            while(j != orNode){
                hops++;
                if(precedente.get(j) != -1){
                    path.add(precedente.get(j));
                    j = precedente.get(j);
                }
                else{
                    networkDisconnected = true;
                    break;
                }
            }
            if(!networkDisconnected){     
                
                for(h = 0; h <= hops; h++)
                    invPath.add(path.get(hops-h));
    
                routeDJK = new Route(invPath, topology);
            }
        }
        
        return routeDJK;
    }
}
