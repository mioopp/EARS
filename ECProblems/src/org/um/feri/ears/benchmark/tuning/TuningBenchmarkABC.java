/**
 * Rating benchmark for unconstrained problems, small dimensions, evaluation is limited with maximum evaluations.
 * Results that are E-10 different are treated as same.
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
package org.um.feri.ears.benchmark.tuning;

import java.util.Vector;

import org.um.feri.ears.benchmark.EnumBenchmarkInfoParameters;
import org.um.feri.ears.benchmark.RatingBenchmark;
import org.um.feri.ears.problems.EnumStopCriteria;
import org.um.feri.ears.problems.Individual;
import org.um.feri.ears.problems.Problem;
import org.um.feri.ears.problems.TaskWithReset;
import org.um.feri.ears.problems.unconstrained.cec2010.*;
import org.um.feri.ears.problems.unconstrained.karaboga.*;

//TODO calculate CD for rating
public class TuningBenchmarkABC extends RatingBenchmark{
    public static final String name="Tuning 1";
    protected int evaluationsOnDimension=10000;
    protected int dimension=10;
    private double draw_limit=0.000001;
    public static final Vector<String[]> optimums = new Vector();
    
    public boolean resultEqual(Individual a, Individual b) {
        if ((a==null) &&(b==null)) return true;
        if (a==null) return false;
        if (b==null) return false;
        if (Math.abs(a.getEval()-b.getEval())<draw_limit) return true;
        return false;
    }
    public TuningBenchmarkABC(){
    	this(0.0000001);
    }
    public TuningBenchmarkABC(double draw_limit) {
        super();
        this.draw_limit = draw_limit;
        evaluationsOnDimension=10000;
        dimension=10;
        initFullProblemList();
        addParameter(EnumBenchmarkInfoParameters.DIMENSION,"10");
        addParameter(EnumBenchmarkInfoParameters.EVAL,String.valueOf(evaluationsOnDimension));
        addParameter(EnumBenchmarkInfoParameters.DRAW_PARAM,"abs(evaluation_diff) < "+draw_limit);
    }
    public TuningBenchmarkABC(double draw_limit, int aDim, int aEval) {
        super();
        this.draw_limit = draw_limit;
        evaluationsOnDimension=aEval;
        dimension=aDim;
        initFullProblemList();
        addParameter(EnumBenchmarkInfoParameters.DIMENSION,String.valueOf(dimension));
        addParameter(EnumBenchmarkInfoParameters.EVAL,String.valueOf(evaluationsOnDimension));
        addParameter(EnumBenchmarkInfoParameters.DRAW_PARAM,"abs(evaluation_diff) < "+draw_limit);
    }
    /* (non-Javadoc)
     * @see org.um.feri.ears.benchmark.RatingBenchmark#registerTask(org.um.feri.ears.problems.Problem)
     */
    @Override
    protected void registerTask(Problem p, EnumStopCriteria sc, int eval, double epsilon) {
        listOfProblems.add(new TaskWithReset(sc, eval, epsilon, p));
        String[] optimum = new String[2];
        optimum[0] = p.getName();
        optimum[1] = p.getOptimumEval() + "";
        optimums.add(optimum);
    }
    
    /* (non-Javadoc)
     * @see org.um.feri.ears.benchmark.RatingBenchmark#initFullProblemList()
     */
    @Override
    protected void initFullProblemList() {
    	registerTask(new Schaffer(dimension),stopCriteria, evaluationsOnDimension, draw_limit);
    	//registerTask(new Sphere(dimension),stopCriteria, evaluationsOnDimension, draw_limit);
    	//registerTask(new Griewank(dimension),stopCriteria, evaluationsOnDimension, draw_limit);
    	//registerTask(new Rastrigin(dimension),stopCriteria, evaluationsOnDimension, draw_limit);
    	//registerTask(new Rosenbrock(dimension),stopCriteria, evaluationsOnDimension, draw_limit);
    }
        
    /* (non-Javadoc)
     * @see org.um.feri.ears.benchmark.RatingBenchmark#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.um.feri.ears.benchmark.RatingBenchmark#getAcronym()
     */
    @Override
    public String getAcronym() {
        return "Tuning1";
    }
    /* (non-Javadoc)
     * @see org.um.feri.ears.benchmark.RatingBenchmark#getInfo()
     */
    @Override
    public String getInfo() {
        return "";
    }
    
}