package src.Structure.Topology;

public class SimpleSquareDimention{

    private static int numberOfNodes = 4;

    public static double[][] getLength(){
        //create network adjacency matrix
		double[][] lengths = new double[numberOfNodes][numberOfNodes];			
		for(int x = 0; x < numberOfNodes; x++){
			for(int y = 0 ; y < numberOfNodes; y++){
				lengths[x][y] = Double.MAX_VALUE;
			}
		}
		
		lengths[0][1]   = 200;
		lengths[0][2]   = 200;
		lengths[0][3]   = 200;

		lengths[1][0]   = 200;
		lengths[1][2]   = 200;
		
		lengths[2][0]   = 200;
		lengths[2][1]   = 200;
		lengths[2][3]   = 200;
		
		lengths[3][0]   = 200;
		lengths[3][2]   = 200;
		
		return lengths;
    }
}