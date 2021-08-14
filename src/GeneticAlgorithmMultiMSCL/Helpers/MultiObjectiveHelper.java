package src.GeneticAlgorithmMultiMSCL.Helpers;

import java.util.ArrayList;
import java.util.List;

import src.GeneticAlgorithmMultiMSCL.IndividualMSCL;
import src.GeneticAlgorithmMultiMSCL.Utility;
import src.GeneticAlgorithmMultiMSCL.ExtensionMethods.Vector2f;

public class MultiObjectiveHelper {

    public static void UpdatePopulationFitness(List<IndividualMSCL> population)
    {
        for (IndividualMSCL individual : population){
            individual.rank = -1;
            individual.crowdingDistance = -1;
        }

        normalizeFitnessValues(population);

        List<IndividualMSCL> remainingToBeRanked = new ArrayList<IndividualMSCL>();
        for (IndividualMSCL individual : population){
            remainingToBeRanked.add(individual);
        }

        int rank = 1;
        while (!remainingToBeRanked.isEmpty()){

            List<IndividualMSCL> individualsInRank = new ArrayList<IndividualMSCL>();

            for (int i = 0; i < remainingToBeRanked.size(); i++){
                IndividualMSCL individual = remainingToBeRanked.get(i);
                if (isNotDominated(individual, remainingToBeRanked))
                {
                    individual.rank = rank;
                    individualsInRank.add(individual);
                }
            }

            for (IndividualMSCL individual: individualsInRank){
                remainingToBeRanked.remove(individual);
            }

            rank++;
        }

        List<List<IndividualMSCL>> ranks = Utility.findAllRanks(population);

        for (List<IndividualMSCL> singleRank : ranks){
            calculateCrowdingDistance(singleRank);
        }

        // Verifica se toda a população tem o crowdingDistance diferente de -1
        for (IndividualMSCL individual : population){
            if (individual.crowdingDistance == -1){
                System.out.println("Erro");
            }
        }
    }

    private static void normalizeFitnessValues(List<IndividualMSCL> population)
    {
        double maxPB = -1;
        double maxTime = -1;

        for (IndividualMSCL individual : population){
            if (maxPB < individual.PBFitness){
                maxPB = individual.PBFitness;
            }
            if (maxTime < individual.timeFitness){
                maxTime = individual.timeFitness;
            }
        }

        for (IndividualMSCL individual : population){
            individual.NormalizedPBFitness = individual.PBFitness / maxPB;
            individual.NormalizedTimeFitness = individual.timeFitness / maxTime;
        }
    }

    public static boolean isNotDominated(IndividualMSCL individualA, List<IndividualMSCL> remainingToBeRanked)
    {
        for (IndividualMSCL individualB : remainingToBeRanked){
            if (individualA == individualB){
                continue;
            }

            // Verifica se os indivíduos estão na mesma posição em X e Y 
            if ((individualA.PBFitness == individualB.PBFitness) && (individualA.timeFitness == individualB.timeFitness) ){
                continue;
                // Não há dominância. As duplicatas são retiradas em outro trecho do código
            }

            if ((individualB.PBFitness <= individualA.PBFitness) && (individualB.timeFitness <= individualA.timeFitness)){
                return false;
            }
        }

        return true;
    }

    private static void calculateCrowdingDistance(List<IndividualMSCL> singleRank)
    {
        List<IndividualMSCL> auxSingleRank = new ArrayList<IndividualMSCL>();
        for (IndividualMSCL auxIndividual : singleRank){
            auxSingleRank.add(auxIndividual);
        }

        List<IndividualMSCL> orderedIndividuals = new ArrayList<IndividualMSCL>();

        int individualsInFront = auxSingleRank.size();
        for (int index = 0; index < individualsInFront; index++){
            int bestIndividualIndex = 0;
            double MaxFitness = -1;
            for (int nSolution = 0; nSolution < auxSingleRank.size(); nSolution++){
                if(auxSingleRank.get(nSolution).PBFitness > MaxFitness){
                    MaxFitness = auxSingleRank.get(nSolution).PBFitness;
                    bestIndividualIndex = nSolution;
                }
            }

            orderedIndividuals.add(auxSingleRank.get(bestIndividualIndex));

            auxSingleRank.remove(bestIndividualIndex);
        }

        for (int i = 0; i < individualsInFront; i++)
        {
            if (i == 0 || i == (individualsInFront - 1)){
                orderedIndividuals.get(i).crowdingDistance = Double.POSITIVE_INFINITY;
            } else {
                IndividualMSCL current = orderedIndividuals.get(i);
                IndividualMSCL left = orderedIndividuals.get(i - 1);
                IndividualMSCL right = orderedIndividuals.get(i + 1);

                Vector2f currentPosition = new Vector2f(current.NormalizedTimeFitness, current.NormalizedPBFitness);
                Vector2f leftPosition = new Vector2f(left.NormalizedTimeFitness, left.NormalizedPBFitness);
                Vector2f rightPosition = new Vector2f(right.NormalizedTimeFitness, right.NormalizedPBFitness);

                double distanceLeft = currentPosition.Distance(leftPosition);
                double distanceRight = currentPosition.Distance(rightPosition);

                orderedIndividuals.get(i).crowdingDistance = distanceLeft + distanceRight;
            }
        }
    }
}
