import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;

public class SudokuCS implements IConstraintSystem {
    private int[] board;
    private int noDigits;

    private int miniBoardSize;
    ProblemState[] solutions = new ProblemState[0];
    ProblemState bestSolution;

    public SudokuCS(int[] setIntegers) {
        // the initial Sudoku board
        board = setIntegers;
        // compute noRows = noColumns = noIntegers, knowing that:
        //    noSquares = noRow*noRow (square board)
        noDigits = (int) Math.sqrt(board.length);
        miniBoardSize = (int) Math.sqrt(noDigits);
    }

    /*
     * Checks is the assignment of curVal to curVar conflicts with
     * the option of assigning v to a subsequent variable freeVar.
     */
    public boolean isConsistent(int curVar, Integer curVal, int freeVar, Integer v) {
        // sanity check:
        assert curVal < noDigits + 1 : "Current Integer "
                + curVal + " is smaller than the maximum " + noDigits;
        assert curVar < noDigits * noDigits : "Current variable number is too large for the board.";

        // if on the same column, check for identical Integers
        int c1 = curVar % noDigits;
        int c2 = freeVar % noDigits;
        if ((c1 == c2) && (curVal.equals(v))) {
            //System.out.println("Failed column check:"+curVar%noDigits+" "+freeVar%noDigits);
            return false;
        }

        // if on the same row, check for identical Integers
        int r1 = curVar / noDigits;
        int r2 = freeVar / noDigits;
        //System.out.println("r1="+r1+"r2="+r2+" noDigits="+noDigits+" =?"+(curVal==v));
        if ((r1 == r2) && (curVal.equals(v))) {
            return false;
        }

        int sr1 = r1 / miniBoardSize;
        int sr2 = r2 / miniBoardSize;
        int sc1 = c1 / miniBoardSize;
        int sc2 = c2 / miniBoardSize;

        if ((sr1 == sr2) && (sc1 == sc2) && (curVal.equals(v))) {
            //System.out.println("!!!! "+curVar+"   "+freeVar+" failed the test.sr1 = "
            //		+sr1+" sr2="+sr2+" sc1="+sc1+" sc2= "+sc2+"miniBoard="+miniBoardSize+" c1="+c1+"c2="+c2);
            return false;
        }

        return true;
    }

    /*
     * Computes what are the possible Integers for each variable
     * on the initial board of the game, and returns the corresponding array
     */
    public FVT getInitialFvt() {
        FVT nFvt = new FVT(board.length);
        for (int i = 0; i < board.length; i++) {
            //System.out.println("Integer "+board[i]);
            if (board[i] != -1) {

                HashSet<Integer> newHsv = new HashSet<>();
                newHsv.add(board[i]);
                nFvt.setValues(i, newHsv);
            } else {
                if (noDigits == 9) {
                    for (int v = 1; v < noDigits + 1; v++) {
                        nFvt.addValue(i, v);
                    }
                } else {
                    for (int v = 0; v < noDigits; v++) {
                        nFvt.addValue(i, v);
                    }
                }
            }
        }
        return nFvt;
    }

    public ProblemState getInitialState() {
        return new SudokuProblemState(noDigits * noDigits);
    }

    public void addSolution(ProblemState solution) {
        if (bestSolution == null) {
            bestSolution = solution;
        }
        if (bestSolution.getCost().compareTo(solution.getCost()) > 0) {
            bestSolution = solution;
        }

    }

    public ProblemState[] getSolutions() {
        return solutions;
    }

    public ProblemState getBestSolution() {
        return bestSolution;
    }

    public static void runSudoku(final String fileName) {
        System.out.println("SUDOKU:");
        final int[] board1 = SudokuReader.read(fileName);
        final SudokuCS sudoku = new SudokuCS(board1);

        ConstraintSatisfaction solver = new ConstraintSatisfaction(sudoku);
        solver.solve();

        System.out.println("Sequential Sudoku: Best solution is\n" + sudoku.getBestSolution());

        for (int iter = 0; iter < 8; iter++) {
            System.out.println("\nIteration-" + iter);
            final SudokuCS sudokuP = new SudokuCS(board1);
            solver = new ConstraintSatisfaction(sudokuP);
            solver.parallelSolve();
            System.out.println("Parallel Sudoku: Best solution is\n" + sudokuP.getBestSolution());

            System.out.print("Comparing solution...");
            if (compareSolutions(sudokuP.getBestSolution(), sudoku.getBestSolution())) {
                System.out.println("OK");
            } else {
                System.out.println("FAIL");
            }
        }
    }

    public static boolean compareSolutions(ProblemState set1, ProblemState set2) {
        if ((set1 == null) || (set2 == null) || !set2.equals(set1)) {
            System.out.println("The solutions are not identical. ");
            return false;
        }
        return true;
    }

    private static class SudokuProblemState extends ProblemState {

        public SudokuProblemState(int noVars) {
            super(noVars);
        }

        public SudokuProblemState(SudokuProblemState otherState) {
            super(otherState);
        }

        public ProblemState copy() {
            return new SudokuProblemState(this);
        }

        public String toString() {
            String s = "* State: \n";
            int rows = (int) Math.sqrt(values.length);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < rows; c++) {
                    s += " " + values[r * rows + c];
                }
                s += "\n";
            }

            return s;
        }

        public BigInteger getCost() {
            int randomvariable;
            if (values[0] == null){
                randomvariable = 0;
            }else{
                randomvariable = values[0];
            }
            BigInteger bi = BigInteger.valueOf(randomvariable);//values[0]

            for (int i = 1; i < values.length; i++) {
                if (values[i] == null){
                     randomvariable = 0;
                }
                else{
                    randomvariable = values[i];
                }
                bi = bi.multiply(BigInteger.valueOf(10)).add(BigInteger.valueOf(randomvariable));//values[i]));
            }
            return bi;
        }
    }

    private static class SudokuReader {
        /*
         * Reads a single sudoku puzzle from a file
         * using the format from
         * http://sudoku.cvs.sourceforge.net/viewvc/sudoku/puzzles/
         * but generalized to support larger board sizes
         */
        public static int[] read(String fileName) {
            File file = new File(fileName);
            StringBuffer contents = new StringBuffer();
            BufferedReader reader = null;
            int lineSize = -1;
            try {
                reader = new BufferedReader(new FileReader(file));
                String text = null;

                // repeat until all lines is read
                while ((text = reader.readLine()) != null) {
                    //System.out.println("Reading.");
                    if (text.length() > 0) {
                        if (text.charAt(0) != '/') {
                            lineSize = text.length();
                            contents.append(text);
                            contents.append(System.getProperty(
                                    "line.separator"));
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // show file contents here
            //System.out.println(contents.toString());

            int noValues = 0;
            for (int i = 0; i < lineSize; i++) {
                if ((contents.charAt(i) == '|')
                        || (contents.charAt(i) == '+')
                        || (contents.charAt(i) == '-')
                        || (contents.charAt(i) == ' ')
                        || (contents.charAt(i) == '\n')
                        || (contents.charAt(i) == '\r')) {
                } else {
                    noValues++;
                }
            }
            int[] board = new int[noValues * noValues];

            int crtPos = 0;
            for (int i = 0; i < contents.length(); i++) {

                if ((contents.charAt(i) == '|')
                        || (contents.charAt(i) == '+')
                        || (contents.charAt(i) == '-')
                        || (contents.charAt(i) == ' ')
                        || (contents.charAt(i) == '\n')
                        || (contents.charAt(i) == '\r')) {
                } else {

                    switch (contents.charAt(i)) {
                        case '.':
                            board[crtPos++] = -1;
                            break;
                        case '0':
                            board[crtPos++] = 0;
                            break;
                        case '1':
                            board[crtPos++] = 1;
                            break;
                        case '2':
                            board[crtPos++] = 2;
                            break;
                        case '3':
                            board[crtPos++] = 3;
                            break;
                        case '4':
                            board[crtPos++] = 4;
                            break;
                        case '5':
                            board[crtPos++] = 5;
                            break;
                        case '6':
                            board[crtPos++] = 6;
                            break;
                        case '7':
                            board[crtPos++] = 7;
                            break;
                        case '8':
                            board[crtPos++] = 8;
                            break;
                        case '9':
                            board[crtPos++] = 9;
                            break;
                        case 'A':
                            board[crtPos++] = 10;
                            break;
                        case 'B':
                            board[crtPos++] = 11;
                            break;
                        case 'C':
                            board[crtPos++] = 12;
                            break;
                        case 'D':
                            board[crtPos++] = 13;
                            break;
                        case 'E':
                            board[crtPos++] = 14;
                            break;
                        case 'F':
                            board[crtPos++] = 15;
                            break;
                        case 'a':
                            board[crtPos++] = 10;
                            break;
                        case 'b':
                            board[crtPos++] = 11;
                            break;
                        case 'c':
                            board[crtPos++] = 12;
                            break;
                        case 'd':
                            board[crtPos++] = 13;
                            break;
                        case 'e':
                            board[crtPos++] = 14;
                            break;
                        case 'f':
                            board[crtPos++] = 15;
                            break;
                    }

                }
            }
            return board;
        }

    }

}

