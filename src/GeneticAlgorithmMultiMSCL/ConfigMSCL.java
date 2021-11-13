package src.GeneticAlgorithmMultiMSCL;

public class ConfigMSCL {
    public static final int populationCounts = 40;

    public static final int numberOfNodes = 14;

    public static final double bestIndividuals = 1.0;
    public static final double crossoverChance = 0.5;
    public static final double mutationChance = 0.01;
    public static final int numberOfCompetitors = 2;
    public static final int maxGenerations = 1000;
    public static final int maxNoImprovementCount = 150;
    public final static double networkLoadGATraining = 90;

    public static String printParameters() {

        String txt = "\t*** Config GA ***\n";
        txt += String.format("populationCounts = %d\n", populationCounts);
        txt += String.format("numberOfNodes = %d\n", numberOfNodes);
        txt += String.format("bestIndividuals = %f\n", bestIndividuals);
        txt += String.format("crossoverChance = %f\n", crossoverChance);
        txt += String.format("mutationChance = %f\n", mutationChance);
        txt += String.format("numberOfCompetitors = %d\n", numberOfCompetitors);
        txt += String.format("maxGenerations = %d\n", maxGenerations);
        txt += String.format("maxNoImprovementCount = %d\n", maxNoImprovementCount);
        txt += String.format("networkLoadGATraining = %f\n", networkLoadGATraining);

        return txt;
    }
}