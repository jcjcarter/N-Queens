import java.math.BigInteger;
import java.util.Iterator;

import static edu.rice.hj.Module1.finish;
import static edu.rice.hj.Module2.isolated;
import static edu.rice.hj.ModuleN.async;

public class ConstraintSatisfaction {
    IConstraintSystem problem;
    FVT fvt;
    int thresholdSize = 1;
    BigInteger bestCost;

    int exitTime = 0;

    public ConstraintSatisfaction(IConstraintSystem inputProblem) {
        problem = inputProblem;
    }

    public void solve() {
        double t1 = System.nanoTime() / 1e9;

        // Start the parallel search
        search(problem.getInitialState(), 0, problem.getInitialFvt());

        double t2 = System.nanoTime() / 1e9;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");

        // Print out the final output
        System.out.println("Time = " + df.format(t2 - t1) + " sec");
    }

    public void search(ProblemState state, int curVar, FVT fvt) {


        if (bestCost != null && state.getCost().compareTo(bestCost) >= 0){
            //System.out.println("best Cost turned out to be null.");
            return;
        }
        if (curVar == fvt.getNumVars()) {
                problem.addSolution(state);
                if (bestCost == null || state.getCost().compareTo(bestCost) < 0 ){
                    bestCost = state.getCost();
                }


        } else {
            Iterator<Integer> itr = fvt.getValues(curVar).iterator();
            while (itr.hasNext()) {
                Integer v = itr.next();
                ProblemState newState = state.copy();
                newState.setValue(curVar, v);
                FVT newFvt = forwardCheck(curVar, v, fvt);
                if (newFvt != null) {
                    //System.out.println("Current Value of the state: " + newState.getCost());
                    search(newState, curVar + 1, newFvt);
                }
            }
        }

    }

    public void parallelSolve() {
        double t1 = System.nanoTime() / 1e9;

        // Start the parallel search
        finish(() -> {
            parallelSearch(problem.getInitialState(), 0, problem.getInitialFvt());
        });
        double t2 = System.nanoTime() / 1e9;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");

        // Print out the final output
        System.out.println("Time = " + df.format(t2 - t1) + " sec");
    }

    public void parallelSearch(ProblemState state, int curVar, FVT fvt) {
        /* TODO: Create a parallel implementation of the constraint solver search() */

//        searchHelper(state, curVar, fvt, 1);
        if (bestCost != null && state.getCost().compareTo(bestCost) >= 0){
           //System.out.println("best Cost turned out to be null.");
            return;
        }
        if (curVar == fvt.getNumVars()) {
            isolated(() -> {
                problem.addSolution(state);
                if (bestCost == null || state.getCost().compareTo(bestCost) < 0 ){
                 bestCost = state.getCost();
                }
            });

        } else {
            //System.out.println("Bong");
            Iterator<Integer> itr = fvt.getValues(curVar).iterator();
            while (itr.hasNext()) {
                Integer v = itr.next();
                ProblemState newState = state.copy();
                newState.setValue(curVar, v);
                FVT newFvt = forwardCheck(curVar, v, fvt);
                if (newFvt != null) {
                    async(() -> parallelSearch(newState, curVar + 1, newFvt));
                }
            }
        }



    }

    public FVT forwardCheck(int curVar, Integer curVal, FVT fvt) {
        FVT newFvt = new FVT(fvt.getNumVars());
        for (int freeVar = curVar + 1; freeVar < fvt.getNumVars(); freeVar++) {
            Iterator<Integer> itr = fvt.getValues(freeVar).iterator();
            while (itr.hasNext()) {
                Integer v = itr.next();
                if (problem.isConsistent(curVar, curVal, freeVar, v)) {
                    newFvt.addValue(freeVar, v);
                    //System.out.println("NOn null: " + newFvt.toString());
                }
            }
            if (newFvt.getValues(freeVar).size() == 0) {
                //System.out.println("Found a Null: " + newFvt.toString());
                return null;
            }
        }// end of seq for loop

        return newFvt;
    }
//    public void searchHelper(ProblemState state, int curVar, FVT fvt, int fixed) {
//
//        if (curVar == fvt.getNumVars() ) {
//            isolated(() ->{
//                problem.addSolution(state);
//                exitTime = fixed;
//            });
//            //System.out.println(state);
//            //System.exit(0);
//
//        } else {
//            Iterator<Integer> itr = fvt.getValues(curVar).iterator();
//            while (itr.hasNext()&& exitTime == 0) {
//                Integer v = itr.next();
//                ProblemState newState = state.copy();
//                newState.setValue(curVar, v);
//                FVT newFvt = forwardCheck(curVar, v, fvt);
//                if (newFvt != null) {
//                    async(() -> searchHelper(newState, curVar + 1, newFvt, fixed));
//
//                }
//            }
//        }
//
//    }


}// end of file
