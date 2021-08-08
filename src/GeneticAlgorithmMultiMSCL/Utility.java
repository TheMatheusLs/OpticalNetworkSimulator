package src.GeneticAlgorithmMultiMSCL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utility {
    
    // A Function to generate a random permutation of arr[]
    public static void randomize(List<Integer> arr)
    {
        // Creating a object for Random class
        Random r = new Random();

        int n = arr.size();
           
        // Start from the last element and swap one by one. We don't
        // need to run for the first element that's why i > 0
        while (n > 1) {

            n--;

            // Pick a random index from 0 to i
            int k = r.nextInt(n + 1);
               
            // Swap arr[i] with the element at random index
            int temp = arr.get(k);
            arr.set(k, arr.get(n));
            arr.set(n, temp);
        }
    }

    public static void SwapInPlace(List<Integer> arr, int[] indexs){
        int temp = arr.get(indexs[0]);
        arr.set(indexs[0], arr.get(indexs[1]));
        arr.set(indexs[1], temp);
    } 

    public static List<List<IndividualMSCL>> findAllRanks(List<IndividualMSCL> population){
        // Ordena os individuos pela frente de pareto

        List<List<IndividualMSCL>> returnRank = new ArrayList<List<IndividualMSCL>>();

        List<IndividualMSCL> remainingToBeRanked = new ArrayList<IndividualMSCL>();
        for (IndividualMSCL individual : population){
            remainingToBeRanked.add(individual);
        }

        int currentRank = 1;
        while (!remainingToBeRanked.isEmpty()){

            List<IndividualMSCL> individualsInRank = new ArrayList<IndividualMSCL>();

            for (int i = 0; i < remainingToBeRanked.size(); i++){
                IndividualMSCL individual = remainingToBeRanked.get(i);
                if (individual.rank == currentRank){
                    individualsInRank.add(individual);
                }
            }

            for (IndividualMSCL individual: individualsInRank){
                remainingToBeRanked.remove(individual);
            }

            returnRank.add(individualsInRank);

            currentRank++;

        }

        return returnRank;
    }

    public static List<List<IndividualMSCL>> findAllRanksByDistance(List<IndividualMSCL> population){
        // Ordena os individuos pela frente de pareto

        List<List<IndividualMSCL>> returnRank = new ArrayList<List<IndividualMSCL>>();

        List<IndividualMSCL> remainingToBeRanked = new ArrayList<IndividualMSCL>();
        for (IndividualMSCL individual : population){
            remainingToBeRanked.add(individual);
        }

        int currentRank = 1;
        while (!remainingToBeRanked.isEmpty()){

            List<IndividualMSCL> individualsInRank = new ArrayList<IndividualMSCL>();

            for (int i = 0; i < remainingToBeRanked.size(); i++){
                IndividualMSCL individual = remainingToBeRanked.get(i);
                if (individual.rank == currentRank){
                    individualsInRank.add(individual);
                }
            }

            for (IndividualMSCL individual: individualsInRank){
                remainingToBeRanked.remove(individual);
            }

            List<IndividualMSCL> individualsInRankOrderByDistance = new ArrayList<IndividualMSCL>();

            int numSize = individualsInRank.size();
            for (int index = 0; index < numSize; index++){
                int bestIndividualIndex = 0;
                double MaxDistance = -1;
                for (int nSolution = 0; nSolution < individualsInRank.size(); nSolution++){
                    if(individualsInRank.get(nSolution).crowdingDistance > MaxDistance){
                        MaxDistance = individualsInRank.get(nSolution).crowdingDistance; 
                        bestIndividualIndex = nSolution;
                    }
                }
                
                individualsInRankOrderByDistance.add(individualsInRank.get(bestIndividualIndex));

                individualsInRank.remove(bestIndividualIndex);
            }

            returnRank.add(individualsInRankOrderByDistance);

            currentRank++;

        }

        return returnRank;
    }

    
}
