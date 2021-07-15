package src.Routing;

import java.util.ArrayList;
import java.util.List;

import src.GeneticAlgorithm.Gene;
import src.GeneticAlgorithm.Individual;
import src.GeneticAlgorithm.Helpers.IndividualHelper;
import src.GeneticAlgorithm.Helpers.WorldHelper;

public class HHRSAAlgorithm {

    public static Route findRouteSolution(final List<Route> routeSolutionOD, final int bitrate, Individual individual, int source, int destination, int numberOfNodes) throws Exception{
        
        // Aplica a identificação do GA Híbrido
        Gene geneOD = IndividualHelper.findGene(source, destination, individual, numberOfNodes);

        int[] genesBitsOD = WorldHelper.geneMapping.get(geneOD.integerGene);

        List<Integer> heuristicsOrder = new ArrayList<Integer>();;
        for (int i = 0; i < genesBitsOD.length; i++){
            if (i == 0){
                heuristicsOrder.add(genesBitsOD[i]);
            } else {
                if (genesBitsOD[i - 1] != genesBitsOD[i]){
                    heuristicsOrder.add(genesBitsOD[i]);
                }
            } 
        }

        int currentSetID = genesBitsOD[0]; 
        int lastRouteIndex = 0;
        // Percorre os conjuntos
        for (int bitID : heuristicsOrder){
            List<Route> setRoutes = new ArrayList<Route>();

            LOOP_ROUTE:for (int r = lastRouteIndex; r < routeSolutionOD.size(); r++){
                if (currentSetID == genesBitsOD[r]){
                    currentSetID = genesBitsOD[r];
                    lastRouteIndex = r;
                    setRoutes.add(routeSolutionOD.get(r));
                } else {
                    currentSetID = genesBitsOD[r];
                    lastRouteIndex = r;
                    break LOOP_ROUTE;
                }
            }

            Route route;
            if (bitID == 1){ // Executa o RSA
                route = Routing_SA.findRoute(setRoutes, bitrate);
            } else {         // Executa o SAR
                route = SA_Routing.findRoute(setRoutes, bitrate);
            }

            if (route != null){
                return route;
            } 
        }
        
        return null;
    }
}
