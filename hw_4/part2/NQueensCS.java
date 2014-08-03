public class NQueensCS implements IConstraintSystem {
    static final int[] solutionsNo = {
            1,
            0,
            0,
            2,
            10, /* 5 */
            4,
            40,
            92,
            352,
            724, /* 10 */
            2680,
            14200,
            73712,
            365596,
    };

    private int boardSize;
    ProblemState[] solutions = new ProblemState[0];

    public NQueensCS(int boardSize) {
        this.boardSize = boardSize;
    }

    /*
     * Checks is the assignment of curVal to curVar conflicts with
     * the option of assigning v to a subseqent variable freeVar.
     */
    public boolean isConsistent(int curVar, Integer curVal, int freeVar, Integer v) {
        // sanity check:
        assert curVal < boardSize : "Current value "
                + curVal + " is larger than the maximum " + boardSize;
        assert curVar < boardSize : "Current variable number is too large for the board.";
        if (curVal.equals(v)) {
            return false;
        }
        if (Math.abs(curVar - freeVar) == Math.abs(curVal - v)) {
            return false;
        }
        return true;
    }

    /*
     * Computes what are the possible values for each variable
     * on the initial board of the game, and returns the corresponding array
     */
    public FVT getInitialFvt() {
        FVT nFvt = new FVT(boardSize);
        for (int i = 0; i < boardSize; i++) {
            for (int v = 0; v < boardSize; v++) {
                nFvt.addValue(i, v);
            }
        }
        return nFvt;
    }

    /*
     * Return the ProblemState subclass corresponding to
     * the type of game you are solving
     */
    public ProblemState getInitialState() {
        return new NQueensProblemState(boardSize);
    }

    public void addSolution(ProblemState solution) {
        ProblemState[] oldSolutions = solutions;
        solutions = new ProblemState[solutions.length + 1];
        for (int i = 0; i < solutions.length - 1; i++) {
            solutions[i] = oldSolutions[i];
        }
        solutions[solutions.length - 1] = solution;
    }

    public ProblemState[] getSolutions() {
        return solutions;
    }

    /*
     * No cost function defined for NQueens
     */
    public int getCost() {
        return 0;
    }

    /*
     * Because there is no cost function,
     * we don't return anything
     */
    public ProblemState getBestSolution() {
        return null;
    }

    public static void runNQueens(final int boardSize) {

        System.out.println("NQUEENS:");
        NQueensCS queens = new NQueensCS(boardSize);
        ConstraintSatisfaction solver = new ConstraintSatisfaction(queens);
        solver.solve();

        // Display solutions

        ProblemState[] solutions = queens.getSolutions();
        /*
        for(int i=0; i< solutions.length; i++) {
        	System.out.println("Solution no "+i+":");
        	System.out.println(solutions[i]);
        }
		*/
        System.out.println("Sequential: Number of solutions is:" + queens.getSolutions().length);

        for (int iter = 0; iter < 8; iter++) {//8
            System.out.println("\nIteration-" + iter);
            NQueensCS queensP = new NQueensCS(boardSize);
            solver = new ConstraintSatisfaction(queensP);
            solver.parallelSolve();
            System.out.println("Parallel: Number of solutions is:" + queensP.getSolutions().length);

            System.out.print("Checking solutions number...");
            if (solutionsNo[boardSize - 1] == queensP.getSolutions().length) {
                System.out.println("OK");
            } else {
                System.out.println("FAIL");
            }

            System.out.print("Comparing solution sets...");
            if (compareSolutionSets(queensP.getSolutions(), queens.getSolutions())) {
                System.out.println("OK");
            } else {
                System.out.println("FAIL");
            }
        }
    }

    public static boolean compareSolutionSets(ProblemState[] set1, ProblemState[] set2) {
        if (set1.length != set2.length) {
            return false;
        }
        for (int i = 0; i < set1.length; i++) {
            boolean found = false;
            for (int j = 0; j < set2.length && !found; j++) {
                if (set1[i].equals(set2[j])) {
                    found = true;
                }
            }
            if (!found) {
                System.out.println("Solution not found in second set is: ");
                System.out.println(set1[i]);
                System.out.println("(number " + i + ")");
                return false;
            }
        }
        return true;
    }

    private static class NQueensProblemState extends ProblemState {

        public NQueensProblemState(int noVars) {
            super(noVars);
        }

        public NQueensProblemState(ProblemState otherState) {
            super(otherState);
        }

        public ProblemState copy() {
            return new NQueensProblemState(this);
        }

        public String toString() {
            String s = "* State: \n";
            int rows = (int) Math.sqrt(values.length);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < rows; c++) {
                    s += " " + values[r * rows + c];
                }
            }
            return s;
        }
    }

}
