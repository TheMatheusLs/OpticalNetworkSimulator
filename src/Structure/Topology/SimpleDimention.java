package src.Structure.Topology;

public class SimpleDimention{

    private static int numberOfNodes = 6;

    public static double[][] getLength(){
        //create network adjacency matrix
		double[][] lengths = new double[numberOfNodes][numberOfNodes];			
		for(int x = 0; x < numberOfNodes; x++){
			for(int y = 0 ; y < numberOfNodes; y++){
				lengths[x][y] = Double.MAX_VALUE;
			}
		}
		
		lengths[0][1]   = 200;
		lengths[0][5]   = 200;

		lengths[1][0]   = 200;
		lengths[1][2]   = 200;
		lengths[1][5]   = 200;
		
		lengths[2][1]   = 200;
		lengths[2][3]   = 200;
		lengths[2][4]   = 200;
		
		lengths[3][2]   = 200;
		lengths[3][4]   = 200;
		
		lengths[4][2]   = 200;
		lengths[4][3]   = 200;
		lengths[4][5]   = 200;
		
		lengths[5][0]   = 200;
		lengths[5][1]   = 200;
		lengths[5][4]   = 200;
		
		return lengths;
    }
}