import static edu.rice.hj.Module1.finalizeHabanero;
import static edu.rice.hj.Module1.initializeHabanero;

public class MainNQueens {
    public static void main(String[] args) {
        initializeHabanero();
        final int boardSize;
        if (args.length > 0) {
            boardSize = Integer.parseInt(args[0]);
        } else {
            boardSize = 12;//12
        }

        NQueensCS.runNQueens(boardSize);
        finalizeHabanero();
    }
}
