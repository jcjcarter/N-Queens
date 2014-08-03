import static edu.rice.hj.Module1.finalizeHabanero;
import static edu.rice.hj.Module1.initializeHabanero;

public class MainSudoku {
    public static void main(final String[] args) {
        initializeHabanero();
        if (args.length == 0) {
            System.out.println("Usage: MainSudoku inputFileName");
        }
        SudokuCS.runSudoku(args[0]);
        finalizeHabanero();
    }
}
