package src.Structure.Topology;

public class FinlandDimention{

    private static int numberOfNodes = 12;

    public static double[][] getLength(){
        //create network adjacency matrix
		double[][] lengths = new double[numberOfNodes][numberOfNodes];			
		for(int x = 0; x < numberOfNodes; x++){
			for(int y = 0 ; y < numberOfNodes; y++){
				lengths[x][y] = Double.MAX_VALUE;
			}
		}
		
		lengths[0][1]   = 400;
		lengths[1][0]   = 400;		
		lengths[0][2]   = 1800;
		lengths[2][0]   = 1800;		
		lengths[1][3]   = 900;
		lengths[3][1]   = 900;		
		lengths[1][5]   = 1100;
		lengths[5][1]   = 1100;		
		lengths[2][3]   = 600;
		lengths[3][2]   = 600;		
		lengths[2][11]   = 700;
		lengths[11][2]   = 700;		
		lengths[2][10]   = 700;
		lengths[10][2]   = 700;		
		lengths[3][4]   = 800; //
		lengths[4][3]   = 800;		
		lengths[4][5]   = 800;
		lengths[5][4]   = 800;	//	
		lengths[5][6]   = 1300; //
		lengths[6][5]   = 1300;	//	
		lengths[4][6]   = 500; ///
		lengths[6][4]   = 500;	////	
		lengths[4][7]   = 400;///
		lengths[7][4]   = 400;//		
		lengths[6][8]   = 200;//
		lengths[8][6]   = 200;//		
		lengths[6][7]   = 200;//
		lengths[7][6]   = 200;//		
		lengths[7][10]   = 200;//
		lengths[10][7]   = 200;//	
		lengths[9][10]   = 500;//
		lengths[10][9]   = 500;		//
		lengths[9][11]   = 300;
		lengths[11][9]   = 300;		
		lengths[8][9]   = 700;
		lengths[9][8]   = 700;		
		lengths[7][8]   = 300;
		lengths[8][7]   = 300;

		return lengths;
    }
}