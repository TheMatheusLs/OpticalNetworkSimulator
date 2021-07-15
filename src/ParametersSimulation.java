package src;

import src.Types.ModulationLevelType;

/**
 * @apiNote Classe contendo todos os parâmetros de simulação.
 * 
 * @author Matheus Lôbo
 * 
 * @version 0.0.1
 */
public class ParametersSimulation{
    // 1 NSFNet
    // 2 YEN
    // 2 FF
    // 1 Hops
    // 1 100-200-400
    // 1 1
    // 0 Disabled
    // 2 EON
    // 0 Routing-SA
    // 0 GA Disabled
    // 0 Disabled
    // 0 Disabled
    // 0 Disabled
    // 0 Invalid
    // 0 Invalid
    // 0 Total number of call requests
    // 0 Same request for all points


    final static double minLoadNetwork = 240;
    final static double maxLoadNetwork = 300;
    final static int numberOfPointsLoadNetwork = 6;

    final static int numberOfSlotsPerLink = 128;
    final static long maxNumberOfRequisitions = (long) 1e6;
    final static int maxNumberOfBlockedRequests = 500;

    final static int kShortestRoutes = 3;
    
    final static int numberOfPolarizations = 2;
    final static int guardBandSize = 0;
    
    final static TopologyType topologyType = TopologyType.NSFNet; //ok
    final static RoutingAlgorithmType routingAlgorithmType = RoutingAlgorithmType.YEN; //ok
    final static SpectralAllocationAlgorithmType spectralAllocationAlgorithmType = SpectralAllocationAlgorithmType.FirstFit; //ok
    final static LinkCostType linkCostType = LinkCostType.Hops;
    final static int[] trafficOption = new int[]{100, 200, 400};
    final static ResourceAllocationOption resourceAllocationOption = ResourceAllocationOption.RSA;
    final static PhysicalLayerOption physicalLayerOption = PhysicalLayerOption.Disabled;
    final static RSAOrder RSAOrderType = RSAOrder.SA_Routing;
    final static GAOption GAOptionType = GAOption.GADisabled;
    final static StopCriteria stopCriteria = StopCriteria.TotalCallRequest;
    final static RandomGeneration randomGeneration = RandomGeneration.SameRequestForAllPoints;
    final static CallRequestType callRequestType = CallRequestType.Unidirectional;

	final static ModulationLevelType[] mudulationLevelType = {
		ModulationLevelType.EIGHT_QAM,
    };

    final static String pathToSaveResults = "D:\\ProgrammingFiles\\ReportsOpticalNetworkSimulator\\";

    public static double getMinLoadNetwork() {
        return minLoadNetwork;
    }

    public static double getMaxLoadNetwork() {
        return maxLoadNetwork;
    }

    public static int getNumberOfPointSloadNetwork() {
        return numberOfPointsLoadNetwork;
    }

    public static int getNumberOfSlotsPerLink() {
        return numberOfSlotsPerLink;
    }

    public static long getMaxNumberOfRequisitions() {
        return maxNumberOfRequisitions;
    }

    public static int getMaxNumberOfBlockedRequests() {
        return maxNumberOfBlockedRequests;
    }

    public static int getKShortestRoutes() {
        return kShortestRoutes;
    }

    public static int getGuardBandSize() {
        return guardBandSize;
    }

    public static TopologyType getTopologyType() {
        return topologyType;
    }

    public static RoutingAlgorithmType getRoutingAlgorithmType() {
        return routingAlgorithmType;
    }

    public static SpectralAllocationAlgorithmType getSpectralAllocationAlgorithmType() {
        return spectralAllocationAlgorithmType;
    }

    public static LinkCostType getLinkCostType() {
        return linkCostType;
    }

    public static int[] getTrafficOption() {
        return trafficOption;
    }

    public static ResourceAllocationOption getResourceAllocationOption() {
        return resourceAllocationOption;
    }

    public static PhysicalLayerOption getPhysicalLayerOption() {
        return physicalLayerOption;
    }

    public static RSAOrder getRSAOrder() {
        return RSAOrderType;
    }

    public static GAOption getGaOption() {
        return GAOptionType;
    }

    public static StopCriteria getStopCriteria() {
        return stopCriteria;
    }

    public static int getNumberofPolarizations() {
        return numberOfPolarizations;
    }

    public static RandomGeneration getRandomGeneration() {
        return randomGeneration;
    }

    public static String getPathToSaveResults() {
        return pathToSaveResults;
    }

    public static CallRequestType getCallRequestType() {
        return callRequestType;
    }
    
    public static ModulationLevelType[] getMudulationLevelType() {
        return mudulationLevelType;
    }

    public enum TopologyType{
        NSFNet,
        Ring,
        Toroidal,
        Finland;
    }

    public enum RoutingAlgorithmType{
        Dijstra,
        YEN;
    }

    public enum SpectralAllocationAlgorithmType{
        Random,
        FirstFit;
    }

    public enum LinkCostType{
        Hops,
        Length,
        LengthNormalized;
    }

    public enum ResourceAllocationOption{
        RSA,
        RMSA;
    }

    public enum PhysicalLayerOption{
        Disabled,
        Enabled;
    }

    public enum RSAOrder{
        Routing_SA,
        SA_Routing,
        MixedOrderGA;
    }

    public enum GAOption{
        GADisabled;
    }

    public enum StopCriteria{
        TotalCallRequest,
        BlockedCallRequest;
    }

    public enum RandomGeneration{
        SameRequestForAllPoints,
        PseudoRandomGeneration,
        RandomGeneration;
    }

    public enum GainAlgorithmType{
        Basic;
    }

    public enum CallRequestType{
        Unidirectional,
        Bidirectional;
    }

    public static String save() {

        String txt = "\t*** Parameters *** \n";

        txt += String.format("minLoadNetwork = %f\n", minLoadNetwork);
        txt += String.format("maxLoadNetwork = %f\n", maxLoadNetwork);
        txt += String.format("numberOfPointsLoadNetwork = %d\n", numberOfPointsLoadNetwork);

        txt += String.format("numberOfSlotsPerLink = %d\n", numberOfSlotsPerLink);

        txt += String.format("maxNumberOfRequisitions = %d\n", maxNumberOfRequisitions);
        txt += String.format("maxNumberOfBlockedRequests = %d\n", maxNumberOfBlockedRequests);

        txt += String.format("kShortestRoutes = %d\n", kShortestRoutes);

        txt += String.format("numberOfPolarizations = %d\n", numberOfPolarizations);
        txt += String.format("guardBandSize = %d\n", guardBandSize);
        
        txt += String.format("topologyType = %s\n", topologyType.name());
        txt += String.format("routingAlgorithmType = %s\n", routingAlgorithmType.name());
        txt += String.format("spectralAllocationAlgorithmType = %s\n", spectralAllocationAlgorithmType.name());
        txt += String.format("linkCostType = %s\n", linkCostType.name());

        txt += String.format("resourceAllocationOption = %s\n", resourceAllocationOption.name());
        txt += String.format("physicalLayerOption = %s\n", physicalLayerOption.name());
        txt += String.format("GAOptionType = %s\n", GAOptionType.name());
        txt += String.format("stopCriteria = %s\n", stopCriteria.name());
        txt += String.format("randomGeneration = %s\n", randomGeneration.name());

        txt += String.format("callRequestType = %s\n", callRequestType.name());
        
        txt += "mudulationLevelType = ";
        for (int m = 0; m < getMudulationLevelType().length - 1; m++){
            txt += String.format("%d-QAM, ", getMudulationLevelType()[m].getConstelation());
        }
        txt += String.format("%d-QAM\n", getMudulationLevelType()[getMudulationLevelType().length-1].getConstelation());

        txt += "trafficOption = ";
        for (int t = 0; t < trafficOption.length - 1; t++){
            txt += String.format("%d, ", trafficOption[t]);
        }
        txt += String.format("%d", trafficOption[trafficOption.length-1]);

        return txt;
    }
}