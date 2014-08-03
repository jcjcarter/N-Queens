import java.math.BigInteger;

public class ProblemState {
    public Integer[] values;

    public ProblemState(int noVars) {
        values = new Integer[noVars];
    }

    public ProblemState(ProblemState otherState) {
        values = new Integer[otherState.values.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = otherState.values[i];
        }
    }
    public Integer getIndex(int pos){
        return values[pos];
    }
    public ProblemState copy() {
        return new ProblemState(this);
    }

    public void setValue(int pos, Integer v) {
        values[pos] = v;
    }

    /*
     * Each constraint system has a
     * particular way for representing the state
     * You need to override this method.
     */
    public String toString() {
        String s = "* State: \n";
        s += "<TODO>";
        return s;
    }

    public int hashCode() {
        if (values != null && values.length > 0) {
            return values[0].hashCode();
        }
        return 1;
    }

    public boolean equals(Object o) {
        ProblemState ps = (ProblemState) o;
        if (ps.values == null && values != null) {
            return false;
        }
        if (ps.values != null && values == null) {
            return false;
        }
        if (ps.values == null && values == null) {
            return true;
        }

        if (ps.values.length != values.length) {
            return false;
        }
        // this part is implementation dependent:
        // for us, the ordering has to be identical
        // but this may not be the case
        // so changing this to look for an identical variable in ps
        // might be required
        for (int i = 0; i < values.length; i++) {
            if (!values[i].equals(ps.values[i])) {
                return false;
            }
        }
        return true;
    }

    /*
     * Returns the cost associated to the state
     * The solution with minimum cost is desirable.
     */
    public BigInteger getCost() {
        return BigInteger.valueOf(0);
    }

}
