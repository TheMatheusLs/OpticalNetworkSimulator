package src.Simulation;

import java.util.ArrayList;
import java.util.List;

import src.Structure.Topology.Topology;
import src.Routing.Routing;
import src.Routing.Routing_SA;
import src.Routing.SA_Routing;
import src.Routing.MSCL;
import src.Routing.Route;
import src.Save.CreateFolder;
import src.Spectrum.FirstFit;
import src.ParametersSimulation;
import src.CallRequest.CallRequestList;
import src.CallRequest.CallRequest;
import src.Parameters.SimulationParameters;
import src.ParametersSimulation.PhysicalLayerOption;
import src.GeneralClasses.Function;
import src.GeneticAlgorithm.Individual;
import src.GeneticAlgorithmMultiMSCL.IndividualMSCL;
import src.Routing.HHRSAAlgorithm;

import java.util.Random;

public class Simulation {

    Topology topology;
    Routing routing;

    public Random randomGeneration;

    CreateFolder folderToSave;

    public int[] seedsForLoad = new int[ParametersSimulation.getNumberOfSimulationsPerLoadNetwork()];

    public void initialize(CreateFolder folderToSave) throws Exception{

        //Adiciona o cabeçalho dos resultados
		this.folderToSave = folderToSave;
        
        this.folderToSave.writeInResults("networkLoad,meanPb,pbDesviation,pbErro,pbMargem,meanSimulationTime");

        this.topology = new Topology();
        this.folderToSave.writeFile("Topology.txt", String.valueOf(this.topology));
        
        this.routing = new Routing(this.topology);
        this.folderToSave.writeFile("Routes.txt", String.valueOf(this.routing));

        if (ParametersSimulation.getRandomGeneration().equals(ParametersSimulation.RandomGeneration.PseudoRandomGeneration)){
            Random randomAux = new Random(ParametersSimulation.getMainSeed());

            for (int nSim = 0; nSim < ParametersSimulation.getNumberOfSimulationsPerLoadNetwork(); nSim++){
                seedsForLoad[nSim] = randomAux.nextInt(Integer.MAX_VALUE);
            }

            this.randomGeneration = new Random(ParametersSimulation.getMainSeed());
        } else {
            if (ParametersSimulation.getRandomGeneration().equals(ParametersSimulation.RandomGeneration.SameRequestForAllPoints)){

                Random randomAux = new Random(ParametersSimulation.getMainSeed());
    
                int seedFix = randomAux.nextInt(Integer.MAX_VALUE);

                for (int nSim = 0; nSim < ParametersSimulation.getNumberOfSimulationsPerLoadNetwork(); nSim++){
                    seedsForLoad[nSim] = seedFix;
                }

                this.randomGeneration = new Random(ParametersSimulation.getMainSeed());
            } else {
                if (ParametersSimulation.getRandomGeneration().equals(ParametersSimulation.RandomGeneration.RandomGeneration)){
                    Random randomAux = new Random();

                    this.randomGeneration = new Random(randomAux.nextInt(Integer.MAX_VALUE));

                    for (int nSim = 0; nSim < ParametersSimulation.getNumberOfSimulationsPerLoadNetwork(); nSim++){
                        seedsForLoad[nSim] = randomAux.nextInt(Integer.MAX_VALUE);;
                    }
                }
            }
        }
    }

    public void simulateMultiLoad() throws Exception{
        
        double step = 0; 
        if (ParametersSimulation.getNumberOfPointSloadNetwork() >= 2){
            step = (ParametersSimulation.getMaxLoadNetwork() - ParametersSimulation.getMinLoadNetwork()) / (ParametersSimulation.getNumberOfPointSloadNetwork() - 1);
        }


        for (int loadPoint = 0; loadPoint < ParametersSimulation.getNumberOfPointSloadNetwork(); loadPoint++){
            double networkLoad = ParametersSimulation.getMaxLoadNetwork() - (step * loadPoint);

            System.out.println(String.format("Simulando carga de %f", (networkLoad / SimulationParameters.getMeanRateOfCallsDuration())));

            List<Double> PBLoad = new ArrayList<Double>();
            List<Double> TimeLoad = new ArrayList<Double>();

            for (int nSim = 1; nSim <= ParametersSimulation.getNumberOfSimulationsPerLoadNetwork(); nSim++){
                
                int seedSimulation = seedsForLoad[nSim-1];

                this.randomGeneration = new Random(seedSimulation);

                System.out.println(String.format("Simulação nº: %d com seed = %d", nSim, seedSimulation));

                double[] results = this.simulateSingle((networkLoad / SimulationParameters.getMeanRateOfCallsDuration()));

                PBLoad.add(results[0]);
                TimeLoad.add(results[1]);
            }


            this.folderToSave.writeInResults((networkLoad / SimulationParameters.getMeanRateOfCallsDuration()) + "," + Function.getMeanList(PBLoad) + "," + 0 + "," + 0 + "," + 0 + "," + Function.getMeanList(TimeLoad));
        }
    }

    public void simulateMultiLoadGA(Individual individual) throws Exception{
        
        double step = 0; 
        if (ParametersSimulation.getNumberOfPointSloadNetwork() >= 2){
            step = (ParametersSimulation.getMaxLoadNetwork() - ParametersSimulation.getMinLoadNetwork()) / (ParametersSimulation.getNumberOfPointSloadNetwork() - 1);
        }


        for (int loadPoint = 0; loadPoint < ParametersSimulation.getNumberOfPointSloadNetwork(); loadPoint++){
            double networkLoad = ParametersSimulation.getMaxLoadNetwork() - (step * loadPoint);

            System.out.println(String.format("Simulando carga de %f", (networkLoad / SimulationParameters.getMeanRateOfCallsDuration())));

            List<Double> PBLoad = new ArrayList<Double>();
            List<Double> TimeLoad = new ArrayList<Double>();

            for (int nSim = 1; nSim <= ParametersSimulation.getNumberOfSimulationsPerLoadNetwork(); nSim++){
                
                int seedSimulation = seedsForLoad[nSim-1];

                this.randomGeneration = new Random(seedSimulation);

                System.out.println(String.format("Simulação nº: %d com seed = %d", nSim, seedSimulation));

                double[] results = this.doSimulateGA(individual, (networkLoad / SimulationParameters.getMeanRateOfCallsDuration()));
                PBLoad.add(results[0]);
                TimeLoad.add(results[1]);
            }


            this.folderToSave.writeInResults((networkLoad / SimulationParameters.getMeanRateOfCallsDuration()) + "," + Function.getMeanList(PBLoad) + "," + 0 + "," + 0 + "," + 0 + "," + Function.getMeanList(TimeLoad));
        }
    }


    public void simulateMultiLoadGA(IndividualMSCL individual) throws Exception{
        
        double step = 0; 
        if (ParametersSimulation.getNumberOfPointSloadNetwork() >= 2){
            step = (ParametersSimulation.getMaxLoadNetwork() - ParametersSimulation.getMinLoadNetwork()) / (ParametersSimulation.getNumberOfPointSloadNetwork() - 1);
        }


        for (int loadPoint = 0; loadPoint < ParametersSimulation.getNumberOfPointSloadNetwork(); loadPoint++){
            double networkLoad = ParametersSimulation.getMaxLoadNetwork() - (step * loadPoint);

            System.out.println(String.format("Simulando carga de %f", (networkLoad / SimulationParameters.getMeanRateOfCallsDuration())));

            List<Double> PBLoad = new ArrayList<Double>();
            List<Double> TimeLoad = new ArrayList<Double>();

            for (int nSim = 1; nSim <= ParametersSimulation.getNumberOfSimulationsPerLoadNetwork(); nSim++){
                
                int seedSimulation = seedsForLoad[nSim-1];

                this.randomGeneration = new Random(seedSimulation);

                System.out.println(String.format("Simulação nº: %d com seed = %d", nSim, seedSimulation));

                this.getRouting().updateConflictRoutesForMSCL(individual);

                double[] results = this.simulateSingle((networkLoad / SimulationParameters.getMeanRateOfCallsDuration()));
                PBLoad.add(results[0]);
                TimeLoad.add(results[1]);
            }


            this.folderToSave.writeInResults((networkLoad / SimulationParameters.getMeanRateOfCallsDuration()) + "," + Function.getMeanList(PBLoad) + "," + 0 + "," + 0 + "," + 0 + "," + Function.getMeanList(TimeLoad));
        }
    }


    public double[] simulateSingle(double networkLoad) throws Exception{

        final long geralInitTime = System.currentTimeMillis();

        int source;
		int destination;
		int	numBlockBySlots = 0;
		int numBlockByQoT = 0; 
		double time = 0.0;
        long limitCallRequest = 0;
        long MSCLCycles = 0;

        final CallRequestList listOfCalls = new CallRequestList();
        final double meanRateCallDur = SimulationParameters.getMeanRateOfCallsDuration();

        LOOP_REQ : for(int i = 1; i <= ParametersSimulation.getMaxNumberOfRequisitions(); i++){

            if (ParametersSimulation.getRoutingAlgorithmType().equals(ParametersSimulation.RoutingAlgorithmType.MSCLCombinado)){
                if ((i % 1000) == 0){
                    System.out.println(String.format("I = %d, Block = %d", i, (numBlockBySlots + numBlockByQoT)));
                }
            }

			boolean hasSlots = false; 
            boolean hasQoT = false;

            do{
                source = (int) Math.floor(randomGeneration.nextDouble() * this.topology.getNumNodes());				
                destination = (int) Math.floor(randomGeneration.nextDouble() * this.topology.getNumNodes());				
            }while(source == destination);

            listOfCalls.removeCallRequest(time);

            time += Function.exponentialDistribution(networkLoad, this.randomGeneration);

            final CallRequest callRequest = new CallRequest(i, ParametersSimulation.getTrafficOption(), ParametersSimulation.getCallRequestType());

            callRequest.setSourceId(source);
			callRequest.setDestinationId(destination);
			callRequest.setReqID(i);

            callRequest.setTime(time, 1/meanRateCallDur, this.randomGeneration);
            callRequest.sortBitRate(randomGeneration);
            int bitRate = callRequest.getBitRate();

            // Captura as rotas para o par origem destino
			List<Route> routeSolution = this.routing.getRoutesForOD(source, destination);
            Route route = null;
            List<Integer> slots = new ArrayList<>();

            if (ParametersSimulation.getRSAOrder().equals(ParametersSimulation.RSAOrder.Routing_SA)){
                route = Routing_SA.findRoute(routeSolution, bitRate);
            } else {
                if (ParametersSimulation.getRSAOrder().equals(ParametersSimulation.RSAOrder.SA_Routing)){
                    route = SA_Routing.findRoute(routeSolution, bitRate);
                } else {
                    if (ParametersSimulation.getRSAOrder().equals(ParametersSimulation.RSAOrder.Disable) && ParametersSimulation.getSpectralAllocationAlgorithmType().equals(ParametersSimulation.SpectralAllocationAlgorithmType.MSCL) && ParametersSimulation.getRoutingAlgorithmType().equals(ParametersSimulation.RoutingAlgorithmType.MSCLSequencial)){

                        MSCL mscl = new MSCL(routeSolution, bitRate);

                        if (mscl.Sequencial()){
                            slots = mscl.getSlots();

                            if(!slots.isEmpty()){
                                route = mscl.getRoute();
                                MSCLCycles += mscl.getCycles();
                            }
                        }
                    } else {
                        if (ParametersSimulation.getRSAOrder().equals(ParametersSimulation.RSAOrder.Disable) && ParametersSimulation.getSpectralAllocationAlgorithmType().equals(ParametersSimulation.SpectralAllocationAlgorithmType.MSCL) && ParametersSimulation.getRoutingAlgorithmType().equals(ParametersSimulation.RoutingAlgorithmType.MSCLCombinado)){

                            MSCL mscl = new MSCL(routeSolution, bitRate);
    
                            if (mscl.Combinado()){
                                slots = mscl.getSlots();
    
                                if(!slots.isEmpty()){
                                    route = mscl.getRoute();
                                    MSCLCycles += mscl.getCycles();
                                }
                            }
                        }
                    }
                }
            }

            if (route != null){

                callRequest.setModulationType(ParametersSimulation.getMudulationLevelType()[0]);
                
                final int reqNumbOfSlots = Function.getNumberSlots(callRequest.getModulationType(), callRequest.getBitRate());
				
                callRequest.setRequiredNumberOfSlots(reqNumbOfSlots);

                if (ParametersSimulation.getSpectralAllocationAlgorithmType().equals(ParametersSimulation.SpectralAllocationAlgorithmType.FirstFit)){
                    slots = FirstFit.findFrequencySlots(ParametersSimulation.getNumberOfSlotsPerLink(), reqNumbOfSlots, route.getUpLink(), route.getDownLink());
                } 

                if(!slots.isEmpty() && slots.size() == reqNumbOfSlots){	// NOPMD by Andr� on 13/06/17 13:12
					hasSlots = true;
				}

                if (ParametersSimulation.getPhysicalLayerOption().equals(ParametersSimulation.PhysicalLayerOption.Disabled)){
                    hasQoT = true;
                } else {
                    hasQoT = true;
                }

				if(hasSlots && hasQoT){
					callRequest.setFrequencySlots(slots);
					callRequest.setRoute(route);

					// Incrementar os slots que estão sendo utilizados pelas rotas
					route.incrementSlotsOcupy(slots);

					callRequest.allocate(route.getUpLink(), route.getDownLink(), topology.getListOfNodes());
					listOfCalls.addCall(callRequest);
				}
            }

            if(!hasSlots){
				numBlockBySlots++;
			}else if(!hasQoT){
				numBlockByQoT++; 
			}

            limitCallRequest = i;

            if (ParametersSimulation.getStopCriteria().equals(ParametersSimulation.StopCriteria.BlockedCallRequest)){
                if ((numBlockBySlots + numBlockByQoT) >= ParametersSimulation.getMaxNumberOfBlockedRequests()){
                    break LOOP_REQ;
                }
            }
        }

        listOfCalls.desallocateAllRequests(); // Remove todas as requisições alocadas

        this.checkTopologyAndRouting();

        listOfCalls.eraseCallList();

		final long geralFinalTime = System.currentTimeMillis();

		if (limitCallRequest != 0){
            double PB = (double)(numBlockBySlots + numBlockByQoT) / limitCallRequest;
            double timeTotal = (double)(geralFinalTime - geralInitTime) / 1000;
            String outputString;

            if (ParametersSimulation.getSpectralAllocationAlgorithmType().equals(ParametersSimulation.SpectralAllocationAlgorithmType.MSCL)){
                outputString = String.format("Blocks Slots: %d, Blocks QoT: %d, NumReq: %d, PB: %f, Time: %f, MSCLCycles: %d", numBlockBySlots, numBlockByQoT, limitCallRequest, PB, timeTotal, MSCLCycles);
                
                System.out.println(outputString);

                return new double[] {PB, MSCLCycles};
            } else {
                outputString = String.format("Blocks Slots: %d, Blocks QoT: %d, NumReq: %d, PB: %f, Time: %f", numBlockBySlots, numBlockByQoT, limitCallRequest, PB, timeTotal);

                System.out.println(outputString);
                
                return new double[] {PB, timeTotal};
            }
		}

		return new double[]{-1,-1};
    }

    /**
     * Método para simular um indivíduo do tipo HHRSA
     * 
     * @param individual
     * @param network
     * @return
     * @throws Exception
     */
    public double[] doSimulateGA(Individual individual, double network) throws Exception{

        final long geralInitTime = System.currentTimeMillis();

        int source;
		int destination;
		int	numBlockBySlots = 0;
		int numBlockByQoT = 0; 
		double time = 0.0;
        long limitCallRequest = 0;

        final CallRequestList listOfCalls = new CallRequestList();
        final double meanRateCallDur = SimulationParameters.getMeanRateOfCallsDuration();

        LOOP_REQ : for(int i = 1; i <= ParametersSimulation.getMaxNumberOfRequisitions(); i++){

			boolean hasSlots = false; 
            boolean hasQoT = false;

            do{
                source = (int) Math.floor(randomGeneration.nextDouble() * this.topology.getNumNodes());				
                destination = (int) Math.floor(randomGeneration.nextDouble() * this.topology.getNumNodes());				
            }while(source == destination);

            listOfCalls.removeCallRequest(time);

            time += Function.exponentialDistribution(network, this.randomGeneration);

            final CallRequest callRequest = new CallRequest(i, ParametersSimulation.getTrafficOption(), ParametersSimulation.getCallRequestType());

            callRequest.setSourceId(source);
			callRequest.setDestinationId(destination);
			callRequest.setReqID(i);

            callRequest.setTime(time, 1/meanRateCallDur, this.randomGeneration);
            callRequest.sortBitRate(randomGeneration);
            int bitRate = callRequest.getBitRate();

            // Captura as rotas para o par origem destino
			List<Route> routeSolution = this.routing.getRoutesForOD(source, destination);
            Route route = null;
            List<Integer> slots = new ArrayList<>();
            
            if (ParametersSimulation.getGaOption().equals(ParametersSimulation.GAOption.GAHHRSAEnable) || ParametersSimulation.getGaOption().equals(ParametersSimulation.GAOption.GAHRSAEnable)){
                route = HHRSAAlgorithm.findRouteSolution(routeSolution, bitRate, individual, source, destination, topology.getNumNodes());
            }

            if (route != null){

                int reqNumbOfSlots;
                
                if (ParametersSimulation.getPhysicalLayerOption().equals(PhysicalLayerOption.Enabled)){
                    int bitRateIndex = Function.getBitRateIndex(bitRate);
                    reqNumbOfSlots = route.getNumberOfSlotsByBitRate(bitRateIndex);
                } else {
                    reqNumbOfSlots = Function.getNumberSlots(ParametersSimulation.getMudulationLevelType()[0], bitRate);
                }
				
                callRequest.setRequiredNumberOfSlots(reqNumbOfSlots);

                if (ParametersSimulation.getSpectralAllocationAlgorithmType().equals(ParametersSimulation.SpectralAllocationAlgorithmType.FirstFit)){
                    slots = FirstFit.findFrequencySlots(ParametersSimulation.getNumberOfSlotsPerLink(), reqNumbOfSlots, route.getUpLink(), route.getDownLink());
                } 

                if(!slots.isEmpty() && slots.size() == reqNumbOfSlots){	// NOPMD by Andr� on 13/06/17 13:12
					hasSlots = true;
				} else {
                    if (!slots.isEmpty()){
                        throw new Exception("A requisição não obtive o valor correto para os slots");
                    }
                }

                if (ParametersSimulation.getPhysicalLayerOption().equals(ParametersSimulation.PhysicalLayerOption.Disabled)){
                    hasQoT = true;
                } else {
                    hasQoT = true;
                }

				if(hasSlots && hasQoT){
					callRequest.setFrequencySlots(slots);
					callRequest.setRoute(route);

					// Incrementar os slots que estão sendo utilizados pelas rotas
					route.incrementSlotsOcupy(slots);

					callRequest.allocate(route.getUpLink(), route.getDownLink(), topology.getListOfNodes());
					listOfCalls.addCall(callRequest);
				}
            }

            if(!hasSlots){
				numBlockBySlots++;
			}else if(!hasQoT){
				numBlockByQoT++; 
			}

            if (ParametersSimulation.getDebugOptions().equals(ParametersSimulation.DebugOptions.AllReqs)){
                new java.io.File(folderToSave.getFolderCompletePath(), "LogReq").mkdirs();
                folderToSave.writeFile(String.format("LogReq//Req_%010d.csv", i), routing.allSpectrumRoutes());
            }

            limitCallRequest = i;

            if (ParametersSimulation.getStopCriteria().equals(ParametersSimulation.StopCriteria.BlockedCallRequest)){
                if ((numBlockBySlots + numBlockByQoT) >= ParametersSimulation.getMaxNumberOfBlockedRequests()){
                    break LOOP_REQ;
                }
            }
        }

        listOfCalls.desallocateAllRequests(); // Remove todas as requisições alocadas

        this.checkTopologyAndRouting();

        listOfCalls.eraseCallList();

		final long geralFinalTime = System.currentTimeMillis();

		if (limitCallRequest != 0){
            double PB = (double)(numBlockBySlots + numBlockByQoT) / limitCallRequest;
			System.err.println("Erros Slots: " + numBlockBySlots + " Erros QoT: " + numBlockByQoT + " numReq: " + limitCallRequest + " PB: " + PB);
			return new double[] {PB, (double)(geralFinalTime - geralInitTime) /1000} ;
		}
		return new double[]{-1,-1};
    }

    void checkTopologyAndRouting() throws Exception{
        // Verifica se todos os links estão limpos
		this.topology.checkIfIsClean();

		// Verifica se todas as rotas estão limpas
        this.routing.checkIfIsClean();
    }

    public Routing getRouting(){
        return this.routing;
    }
}
