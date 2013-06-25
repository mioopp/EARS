package org.um.feri.ears.problems.unconstrained.cec2010;


import java.util.Arrays;
import java.util.Random;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.RandomMatrices;
import org.um.feri.ears.problems.Problem;
import org.um.feri.ears.problems.unconstrained.cec2010.base.RastriginRotated;

/**
 * Problem function!
 * 
 * @author Niki Vecek
 * @version 1
 * 
 **/

public class F15 extends Problem {
	
	int[] P;
	
	int properties_multimodal;
	int properties_shifted;
	int properties_separable;
	int properties_scalable;
	int properties_rotated;
	
	int m;
	public double[][] rot_matrix;
	RastriginRotated rastrigin_rotated;
	
	// F15 CEC 2010
	// D/m-group Shifted and m-rotated Rastrigin's Function
	public F15(int d) {
		
		properties_multimodal=Problem.MULTIMODAL;
		properties_shifted=Problem.SHIFTED;
		properties_separable=Problem.NOT_SEPARABLE;
		properties_scalable=Problem.NOT_SCALABLE;
		properties_rotated=Problem.ROTATED;
		
		dim = d;
		rastrigin_rotated= new RastriginRotated(dim);
		interval = new double[d];
		intervalL = new double[d];
		Arrays.fill(interval, 10);
		Arrays.fill(intervalL, -5);
		name = "F15 D/m-group Shifted and m-rotated Rastrigin's Function";
		
		P = new int[dim];
		Random rand = new Random();
		int rand_place = 0;
		
		for (int i=0; i<dim; i++) P[i] = i;
		
		int temp;
		for (int i=dim-1; i>=0; i--){
			rand_place = rand.nextInt(dim);
			temp = P[i];
			P[i] = P[rand_place];	
			P[rand_place] = temp;
		}
		
		m = 2;
		
		rot_matrix = new double[m][m];
		
		Random rand1 = new Random();
		DenseMatrix64F A = RandomMatrices.createOrthogonal(m, m, rand1);
		
		for (int i=0; i<m; i++){
			for (int j=0; j<m; j++){
				rot_matrix[i][j] = A.get(i, j);
			}
		}
	}
	
	public double eval(double x[]) {
		double F = 0;
		for (int k=0; k<dim/m; k++){
			F = F + rastrigin_rotated.eval(x,P,k*m,k*m+m,rot_matrix);
		}
		return F;
	}

	public double getOptimumEval() {
		return 0;
	}

	@Override
	public boolean isFirstBetter(double[] x, double eval_x, double[] y,
			double eval_y) {
		return eval_x < eval_y;
	}

}