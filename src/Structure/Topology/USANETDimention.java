package src.Structure.Topology;

public class USANETDimention{

    private static int numberOfNodes = 24;

    public static double[][] getLength(){
        //create network adjacency matrix
		double[][] lengths = new double[numberOfNodes][numberOfNodes];			
		for(int x = 0; x < numberOfNodes; x++){
			for(int y = 0 ; y < numberOfNodes; y++){
				lengths[x][y] = Double.MAX_VALUE;
			}
		}
		
		lengths[0][1]   = 800;
		lengths[0][5]   = 1000;
		
		lengths[1][0]   = 800;
		lengths[1][2]   = 1100;
		lengths[1][5]   = 1000;
		
		lengths[2][1]   = 1100;
		lengths[2][3]   = 300;
		lengths[2][4]   = 800;
		lengths[2][6]   = 1000;
		
		lengths[3][2]   = 300;
		lengths[3][4]   = 800;
		lengths[3][6]   = 900;
		
		lengths[4][2]   = 800;
		lengths[4][3]   = 800;
		lengths[4][7]   = 1200;
		
		lengths[5][0]   = 1000;
		lengths[5][1]   = 1000;
		lengths[5][6]   = 1000;
		lengths[5][8]   = 1200;
		lengths[5][10]   = 1900;
		
		lengths[6][2]   = 1000;
		lengths[6][3]   = 900;
		lengths[6][5]   = 1000;
		lengths[6][7]   = 1200;
		lengths[6][8]   = 1000;
		
		lengths[7][4]   = 1200;
		lengths[7][6]   = 1200;
		lengths[7][9]   = 900;
		
		lengths[8][5]   = 1200;
		lengths[8][6]   = 1000;
		lengths[8][9]   = 1000;
		lengths[8][10]  = 1400;
		lengths[8][11]  = 1000;
		
		lengths[9][7]   = 900;
		lengths[9][8]   = 1000;
		lengths[9][12]  = 1000;
		lengths[9][13]  = 900;
		
		lengths[10][5]  = 1900;
		lengths[10][8]  = 1400;
		lengths[10][11] = 900;
		lengths[10][14] = 1300;
		lengths[10][18] = 2600;
		
		lengths[11][8]  = 1000;
		lengths[11][10] = 900;
		lengths[11][12] = 900;
		lengths[11][15] = 1000;
		
		lengths[12][9]  = 1000;
		lengths[12][11] = 900;
		lengths[12][13] = 700;
		lengths[12][16] = 1100;
		
		lengths[13][9]  = 900;
		lengths[13][11] = 700;
		lengths[13][17] = 1200;
		
		lengths[14][10] = 1300;
		lengths[14][15] = 600;
		lengths[14][19] = 1300;
		
		lengths[15][11] = 1000;
		lengths[15][14] = 600;
		lengths[15][16] = 1000;
		lengths[15][20] = 1000;
		lengths[15][21] = 800;
		
		lengths[16][12] = 1100;
		lengths[16][15] = 1000;
		lengths[16][17] = 800;
		lengths[16][21] = 900;
		lengths[16][22] = 1000;
		
		lengths[17][13] = 1200;
		lengths[17][16] = 800;
		lengths[17][23] = 900;
		
		lengths[18][10] = 2600;
		lengths[18][19] = 1200;
		
		lengths[19][14] = 1300;
		lengths[19][18] = 1200;
		lengths[19][20] = 700;
		
		lengths[20][15] = 1000;
		lengths[20][19] = 700;
		lengths[20][21] = 300;
		
		lengths[21][15] = 800;
		lengths[21][16] = 900;
		lengths[21][20] = 300;
		lengths[21][22] = 600;
		
		lengths[22][16] = 1000;
		lengths[22][21] = 600;
		lengths[22][23] = 900;
		
		lengths[23][17] = 900;
		lengths[23][22] = 900;

		return lengths;
    }
}