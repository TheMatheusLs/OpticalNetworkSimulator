package src.GeneticAlgorithm.Helpers;

import java.util.ArrayList;
import java.util.List;

class HHRSACombinations{

    int numberOfTerms;
    List<List<Integer>> solutionsTemp;
    List<int[]> solutions;

    public HHRSACombinations(int k){
        this.numberOfTerms = k;
        this.solutionsTemp = new ArrayList<List<Integer>>();
        
        List<Integer> solTemp = new ArrayList<Integer>();
        findAllCombinations(this.numberOfTerms, solTemp);
        
        // Inicia com o SA-R, logo após R-SA e as demais combinações
        this.solutionsTemp.add(0, solutionsTemp.get(this.solutionsTemp.size()-1));
        this.solutionsTemp.remove(this.solutionsTemp.size()-1);
        
        printSolutions();
        
        
        convertSolutions();

        System.out.println(this.solutions.size());
    }

    private void convertSolutions() {
        this.solutions = new ArrayList<int[]>();

        for (List<Integer> solution : this.solutionsTemp){
            int[] newSolution = new int[this.numberOfTerms];

            for (int s = 0; s < solution.size(); s++){
                newSolution[s] = solution.get(s);
            }  

            this.solutions.add(newSolution);
        }
    }

    public List<int[]> getSolutions(){
        return this.solutions;
    }

    void printSolutions() {
        for (List<Integer> solution : this.solutionsTemp){
            for (int s = 0; s < solution.size() - 1; s++){
                System.out.print(solution.get(s) + " ");
            }  
            System.out.println(solution.get(solution.size()-1));
        }
    }

    List<Integer> findAllCombinations(int n, List<Integer> sol){
        if (sol.size() == n){ //stop condition for the recursion
            if (checkSolution(sol)){
                List<Integer> solCopy = new ArrayList<Integer>();
                for (int s = 0; s < sol.size(); s++){
                    solCopy.add(sol.get(s));
                } 
                this.solutionsTemp.add(solCopy);
            }
            return sol;
        }
        for (int x = 0; x <= n; x++){
            sol.add(x);
            findAllCombinations(n, sol);
            sol.remove(sol.size()-1);
        }
        return sol;
    }

    boolean checkSolution(List<Integer> solution){
        int sum = 0;
        for (int value : solution){
            sum += value;
        }
        if ((solution.get(0) != 0) && (sum == this.numberOfTerms)){
            for (int index = 1; index < solution.size(); index++){
                if (!((solution.get(index) != 0) || (solution.get(index) == 0 && (CheckAllZerosRight(solution, index))))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    boolean CheckAllZerosRight(List<Integer> solution, int indexInit){
        for (int index = indexInit; index < solution.size(); index++){
            if (solution.get(index) != 0){
                return false;
            }
        }
        return true;
    }
}