package org.um.feri.ears.problems;

/**
* Task is main class, for communication between algorithm and problem  
* <p>
* 
* @author Matej Crepinsek
* @version 1
* 
*          <h3>License</h3>
* 
*          Copyright (c) 2011 by Matej Crepinsek. <br>
*          All rights reserved. <br>
* 
*          <p>
*          Redistribution and use in source and binary forms, with or without
*          modification, are permitted provided that the following conditions
*          are met:
*          <ul>
*          <li>Redistributions of source code must retain the above copyright
*          notice, this list of conditions and the following disclaimer.
*          <li>Redistributions in binary form must reproduce the above
*          copyright notice, this list of conditions and the following
*          disclaimer in the documentation and/or other materials provided with
*          the distribution.
*          <li>Neither the name of the copyright owners, their employers, nor
*          the names of its contributors may be used to endorse or promote
*          products derived from this software without specific prior written
*          permission.
*          </ul>
*          <p>
*          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
*          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
*          LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
*          FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
*          COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
*          INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
*          BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
*          LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
*          CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
*          LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
*          ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
*          POSSIBILITY OF SUCH DAMAGE.
* 
*/
public class Task {
	protected EnumStopCriteria stopCriteria;
	protected int maxEvaluations; // for Stop criteria
	protected int numberOfEvaluations = 0; // for Stop criteria
	protected double epsilon; // for Stop criteria
	protected boolean isStop;
	protected boolean isGlobal;
	protected int precisionOfRealNumbersInDecimalPlaces; //used only for discreet problem presentation (bit presentation in GA)
	protected Problem p; //form PRIVATE needed for statistic! check if fake is possible!
	public Task(EnumStopCriteria stop, int eval, double epsilon, Problem p) {
	    this(stop, eval, epsilon, p,  (int) Math.log10((1./epsilon)+1));
	}
	/**
	 * Used for memory pool! ID must be unique to the problem
	 * @return
	 */
	public String getID() {
		return getProblemShortName()+"D"+p.getDim()+"-"+maxEvaluations+"-"+stopCriteria.ordinal()+"-"+epsilon;
	}
    public Task(EnumStopCriteria stop, int eval, double epsilon, Problem p, int precisonOfRealNumbers) {
        precisionOfRealNumbersInDecimalPlaces = precisonOfRealNumbers;
        stopCriteria = stop;
        maxEvaluations = eval;
        numberOfEvaluations = 0;
        this.epsilon = epsilon;
        isStop = false;
        isGlobal = false;
        this.p = p;
    }
    
    /**
     * When you subtract 2 solutions and difference is less or equal epsilon,
     * solution are treated as equal good (draw in algorithm match)!
     * 
     * @return condition that is used when 2 solutions are equal good!
     */
	public double getEpsilon() {
	    return epsilon;
	}
	
	/**
	 * Used only for discreet problem presentation (bit presentation in GA)
	 * @return
	 */
	public int getPrecisionMinDecimal() {
	    return precisionOfRealNumbersInDecimalPlaces;
	}
	
	public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public double[] getIntervalLength(){
		return p.interval;
	}
	
	public double[] getIntervalLeft(){
		return p.intervalL;
	}
	
	public double[] getIntervalRight(){
		double intervalR[] = new double[p.interval.length];
		for (int i=0; i<intervalR.length;i++) {
			intervalR[i] = p.intervalL[i]+p.interval[i];
		}
		return intervalR;
	}
	
	public int getDimensions() {
		return p.getDim();
	}
	public int getNumberOfConstrains() {
	    return p.constrains;
	}
	public double[] getRandomVectorX() {
		return p.getRandomVectorX();
	}
	public Individual getRandomIndividual() throws StopCriteriaException {
		return eval(p.getRandomVectorX()); 
	}
	
	public boolean isFirstBetter(Individual x, Individual y) {
		return p.isFirstBetter(x.getX(), x.getEval(), y.getX(), y.getEval());
	}
	/**
	 * @deprecated
	 * Deprecated is because it is better to use individuals and
	 * isFirstBetter that already is influenced by this parameter.
	 * Returns true id global maximum searching!
	 * 
	 * @return
	 */
	public boolean isMaximize() {
	    return !p.minimum;
	}
	private void incEvaluate() throws StopCriteriaException {
		if (numberOfEvaluations >= maxEvaluations)
			throw new StopCriteriaException("Max evaluations");
		numberOfEvaluations++;
		if (numberOfEvaluations >= maxEvaluations)
			isStop = true;
	}
	
	public int getNumberOfEvaluations(){
		return numberOfEvaluations;
	}
	
	public boolean isStopCriteria() {
		return isStop||isGlobal;
	}

	/**
	 * This function is not ok, because you do not get informations about
	 * constrains, etc.. Just value
	 *  
	 * @see org.um.feri.ears.problems.Task#eval(double[])
	 * @deprecated
	 * @param ds
	 * @return
	 * @throws StopCriteriaException
	 */
	public double justEval(double[] ds) throws StopCriteriaException {
        if (stopCriteria == EnumStopCriteria.EVALUATIONS) {
            incEvaluate();
            return p.eval(ds);
        }
        if (stopCriteria == EnumStopCriteria.GLOBAL_OPTIMUM_OR_EVALUATIONS) {
            if (isGlobal)
                throw new StopCriteriaException("Global optimum already found");
            incEvaluate();
            double d = p.eval(ds);
            if (Math.abs(d - p.getOptimumEval()) <= epsilon) {
                isGlobal = true;
            }
            return d;
        }
        assert false; // Execution should never reach this point!
        return Double.MAX_VALUE; //error
    }
	

	 /**
     * with no evaluations just checks
     * if algorithm result is in interval.
     * This is not checking constrains, just basic intervals!  
     * Delegated from Problem!
     * 
     * @param ds vector of possible solution
     * @return
     */
	public boolean areDimensionsInFeasableInterval(double[] ds) {
	    return p.areDimensionsInFeasableInterval(ds);
	}

	public String getStopCriteriaDescription() {
        if (stopCriteria == EnumStopCriteria.EVALUATIONS) {
            return "E="+getMaxEvaluations();
        }
        if (stopCriteria == EnumStopCriteria.GLOBAL_OPTIMUM_OR_EVALUATIONS) {
                return "Global optimum epsilon="+epsilon+" or  E="+getMaxEvaluations();
        }
        return "not defened";
	}
	/**
	 * Better use method eval returns Individual with calculated fitness and constrains
	 * @deprecated
	 * 
	 * @param ds real vector to be evaluated (just calc constraines
	 * @return
	 */
	public double[] calcConstrains(double[] ds) {
	    return p.calc_constrains(ds);
	}
	
	public Individual eval(double[] ds) throws StopCriteriaException {
		if (stopCriteria == EnumStopCriteria.EVALUATIONS) {
			incEvaluate();
			return new Individual(ds,p.eval(ds),p.calc_constrains(ds));
		}
		if (stopCriteria == EnumStopCriteria.GLOBAL_OPTIMUM_OR_EVALUATIONS) {
			if (isGlobal)
				throw new StopCriteriaException("Global optimum already found");
			incEvaluate();
			double d = p.eval(ds);
			if (Math.abs(d - p.getOptimumEval()) <= epsilon) {
				isGlobal = true;
			}
			return new Individual(ds,d,p.calc_constrains(ds));
		}
		assert false; // Execution should never reach this point!
		return null; //error
	}

    /**
     * @return
     */
    public String getProblemShortName() {
        return p.getName();
    }
    /**
     * Works only for basic interval setting!
     * Sets interval!
     * for example -40<x_i<40 <p>
     * if x_i <-40 -> -40 same for 40!
     * 
     * @param d value
     * @param i index of dimension
     * @return
     */
    public double feasible(double d, int i){
        return p.feasible(d, i);
    }
    
    public boolean isFeasible(double d, int i){
        return p.isFeasibleDimension(d, i);
    }

    /**
     * @deprecated
     * @param d
     * @param bestEvalCond
     * @return
     */
    public boolean isFirstBetter(double a, double b) {
        return p.isFirstBetter(a, b);
    }

    /**
     * @deprecated
     * @param ds
     * @param d
     * @param es
     * @param e
     * @return
     */
    public boolean isFirstBetter(double[] ds, double d, double[] es, double e) {
        return p.isFirstBetter(ds,d,es,e);
    }
    @Override
    public String toString() {
        return "Task [stopCriteria=" + stopCriteria + ", maxEvaluations=" + maxEvaluations + ", numberOfEvaluations=" + numberOfEvaluations + ", epsilon="
                + epsilon + ", isStop=" + isStop + ", isGlobal=" + isGlobal + ", precisionOfRealNumbersInDecimalPlaces="
                + precisionOfRealNumbersInDecimalPlaces + ", p=" + p + "]";
    }
    
}
