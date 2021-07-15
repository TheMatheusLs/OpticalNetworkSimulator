package src.Simulation;

import java.util.List;

import src.Structure.Topology.Topology;
import src.Routing.Routing;
import src.Routing.Routing_SA;
import src.Routing.SA_Routing;
import src.Routing.Route;
import src.Save.CreateFolder;
import src.Spectrum.FirstFit;
import src.ParametersSimulation;
import src.CallRequest.CallRequestList;
import src.CallRequest.CallRequest;
import src.Parameters.SimulationParameters;
import src.GeneralClasses.Function;

import java.util.Random;

public class Simulation {

    Topology topology;
    Routing routing;

    Random randomGeneration;

    CreateFolder folderToSave;

    public void initialize(CreateFolder folderToSave) throws Exception{

        //Adiciona o cabeçalho dos resultados
		this.folderToSave = folderToSave;
        
        this.folderToSave.writeInResults("networkLoad,meanPb,pbDesviation,pbErro,pbMargem,meanSimulationTime");

        this.topology = new Topology();

        this.routing = new Routing(this.topology);

        this.randomGeneration = new Random(42);

    }

    public void simulateMultiLoad() throws Exception{

        double step = (ParametersSimulation.getMaxLoadNetwork() - ParametersSimulation.getMinLoadNetwork()) / (ParametersSimulation.getNumberOfPointSloadNetwork() - 1);

        for (int loadPoint = 0; loadPoint < ParametersSimulation.getNumberOfPointSloadNetwork(); loadPoint++){
            double networkLoad = ParametersSimulation.getMaxLoadNetwork() - (step * loadPoint);

            System.out.println(String.format("Simulando carga de %f", (networkLoad / SimulationParameters.getMeanRateOfCallsDuration())));

            double[] results = this.simulateSingle((networkLoad / SimulationParameters.getMeanRateOfCallsDuration()));

            this.folderToSave.writeInResults((networkLoad / SimulationParameters.getMeanRateOfCallsDuration()) + "," + results[0] + "," + 0 + "," + 0 + "," + 0 + "," + results[1]);
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
            if (ParametersSimulation.getRSAOrder().equals(ParametersSimulation.RSAOrder.Routing_SA)){
                route = Routing_SA.findRoute(routeSolution, bitRate);
            } else {
                if (ParametersSimulation.getRSAOrder().equals(ParametersSimulation.RSAOrder.SA_Routing)){
                    route = SA_Routing.findRoute(routeSolution, bitRate);
                }
            }

            if (route != null){

                callRequest.setModulationType(ParametersSimulation.getMudulationLevelType()[0]);
                
                final int reqNumbOfSlots = Function.getNumberSlots(callRequest.getModulationType(), callRequest.getBitRate());
				
                callRequest.setRequiredNumberOfSlots(reqNumbOfSlots);

                List<Integer> slots = FirstFit.findFrequencySlots(ParametersSimulation.getNumberOfSlotsPerLink(), reqNumbOfSlots, route.getUpLink(), route.getDownLink());

                if(!slots.isEmpty() && slots.size() == reqNumbOfSlots){	// NOPMD by Andr� on 13/06/17 13:12
					hasSlots = true;
				}

                if (ParametersSimulation.getPhysicalLayerOption().equals(ParametersSimulation.PhysicalLayerOption.Disabled)){
                    hasQoT = true;
                }

				if(hasSlots && hasQoT){
					callRequest.setFrequencySlots(slots);
					callRequest.setRoute(route);

					// Incrementar os slots que estão sendo utilizados pelas rotas
					//route.incrementSlotsOcupy(slots);

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
                if ((numBlockBySlots + numBlockByQoT) >= 1000){
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
}
