package src.Structure.Topology;

public class AbileneDimention{

    private static int numberOfNodes = 11;

    public static double[][] getLength(){
        //create network adjacency matrix
		double[][] lengths = new double[numberOfNodes][numberOfNodes];			
		for(int x = 0; x < numberOfNodes; x++){
			for(int y = 0 ; y < numberOfNodes; y++){
				lengths[x][y] = Double.MAX_VALUE;
			}
		}
		
		lengths[0][1]   = 900;
		lengths[0][4]   = 2100;

		lengths[1][0]   = 900;
		lengths[1][2]   = 400;
		lengths[1][4]   = 1300;

		lengths[2][1]   = 400;
		lengths[2][3]   = 1900;	

		lengths[3][2]   = 1900;
		lengths[3][5]   = 900;
		lengths[3][10]   = 1200;

		lengths[4][0]   = 2100;		
		lengths[4][1]   = 1300;	
		lengths[4][5]   = 600;

		lengths[5][4]   = 600;	
		lengths[5][3]   = 900;	
		lengths[5][6]   = 500;	
		
		lengths[6][5]   = 500;	
		lengths[6][7]   = 300;	
		lengths[6][10]   = 600;	
		
		lengths[7][6]   = 300;	
		lengths[7][8]   = 700;	
		
		lengths[8][7]   = 700;	
		lengths[8][9]   = 200;	

		lengths[9][8] = 200;
		lengths[9][10] = 800;
		
		lengths[10][9] = 800;
		lengths[10][6] = 600;
		lengths[10][3] = 1200;

		return lengths;
    }
}