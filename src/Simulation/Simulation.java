package src.Simulation;

import java.util.List;

import src.Structure.Topology.Topology;
import src.Routing.Routing;
import src.Routing.Routing_SA;
import src.Routing.Route;
import src.Save.CreateFolder;
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

    public void initialize(CreateFolder folderToSave) throws Exception{

        //Adiciona o cabeçalho dos resultados
		folderToSave.writeInResults("networkLoad,meanPb,pbDesviation,pbErro,pbMargem,meanSimulationTime");

        this.topology = new Topology();

        this.routing = new Routing(this.topology);

        this.randomGeneration = new Random(42);

    }

    public void simulateMultiLoad(){

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
                source = (int) Math.floor(Math.random() * this.topology.getNumNodes());				
                destination = (int) Math.floor(Math.random() * this.topology.getNumNodes());				
            }while(source == destination);

            listOfCalls.removeCallRequest(time);

            time += Function.exponentialDistribution(networkLoad, this.randomGeneration);

            final CallRequest callRequest = new CallRequest(i, ParametersSimulation.getTrafficOption(), ParametersSimulation.getCallRequestType());

            callRequest.setSourceId(source);
			callRequest.setDestinationId(destination);
			callRequest.setReqID(i);

            callRequest.setTime(time, meanRateCallDur, this.randomGeneration);
            callRequest.sortBitRate();
            int bitRate = callRequest.getBitRate();

            // Captura as rotas para o par origem destino
			List<Route> routeSolution = this.routing.getRoutesForOD(source, destination);
            Route route = null;
            if (ParametersSimulation.getRSAOrder().equals(ParametersSimulation.RSAOrder.Routing_SA)){
                route = Routing_SA.findRoute(routeSolution, bitRate);
            }

            if (route != null){

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
