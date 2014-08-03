public interface IConstraintSystem {
    /*
     * Checks is the assignment of curVal to curVar conflicts with
     * the option of assigning v to a subsequent variable freeVar.
     */
    public boolean isConsistent(int curVar, Integer curVal, int freeVar, Integer v);

    /*
     * Computes what are the possible values for each variable
     * on the initial board of the game, and returns the corresponding array
     */
    public FVT getInitialFvt();

    /*
     * Returns the ProblemState subclass corresponding to
     * the type of game you are solving
     */
    public ProblemState getInitialState();

    /*
     * Called when a solution is found by the Constrail Solver
     */
    public void addSolution(ProblemState sol);

    /*
     * Allows access to the obtained solutions
     */
    public ProblemState[] getSolutions();

    /*
     * Returns the solution with the lowest cost
     */
    public ProblemState getBestSolution();
}
