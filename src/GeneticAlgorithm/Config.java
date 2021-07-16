package src.GeneticAlgorithm;

public class Config {
    public static final int populationCounts = 50;
    public static final int numberOfNodes = 14;
    public static final boolean isElitism = true;
    public static final double elitismPercent = 0.10;
    public static final double crossoverChance = 0.5;
    public static final double mutationChance = 0.01;
    public static final double geneMutationPercent = 0.15;
    public static final int numberOfCompetitors = 3;
    public static final int maxGenerations = 300;
    public static final int maxNoImprovementCount = 150;
    public final static double networkLoadGATraining = 260;

    public static String printParameters() {

        String txt = "\t*** Config GA ***\n";
        txt += String.format("populationCounts = %d\n", populationCounts);
        txt += String.format("numberOfNodes = %d\n", numberOfNodes);
        txt += String.format("isElitism = %b\n", isElitism);
        txt += String.format("elitismPercent = %f\n", elitismPercent);
        txt += String.format("crossoverChance = %f\n", crossoverChance);
        txt += String.format("mutationChance = %f\n", mutationChance);
        txt += String.format("geneMutationPercent = %f\n", geneMutationPercent);
        txt += String.format("numberOfCompetitors = %d\n", numberOfCompetitors);
        txt += String.format("maxGenerations = %d\n", maxGenerations);
        txt += String.format("maxNoImprovementCount = %d\n", maxNoImprovementCount);
        txt += String.format("networkLoadGATraining = %f\n", networkLoadGATraining);

        return txt;
    }
}