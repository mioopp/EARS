package org.um.feri.ears.problems.unconstrained.karaboga;

import java.util.Arrays;

import org.um.feri.ears.problems.Problem;

public class Bohachevsky3 extends Problem {
	
	public double[][] a;
	
	public Bohachevsky3(int d) {
		dim = d;
		interval = new double[d];
		intervalL = new double[d];
		Arrays.fill(interval, 2*100);
		Arrays.fill(intervalL, -100);
		name = "Bohachevsky3";
		characteristic = "MN";
	}
	
	public double eval(double x[]) {
		double v = 0;
		v = Math.pow(x[0], 2)
		  + 2*Math.pow(x[1], 2)
		  - 0.3*Math.cos(3*Math.PI*x[0]+4*Math.PI*x[1])
		  + 0.3;
		return v;
	}

	public double getOptimumEval() {
		return 0;
	}

	@Override
	public boolean isFirstBetter(double[] x, double eval_x, double[] y,
			double eval_y) {
		return (Math.abs(eval_x - getOptimumEval()) < (Math.abs(eval_y - getOptimumEval())));
	}

}
