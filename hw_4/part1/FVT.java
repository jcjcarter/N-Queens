import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class FVT {

    public Vector<HashSet<Integer>> data;

    public FVT(int variables) {
        data = new Vector<>();
        for (int i = 0; i < variables; i++) {
            data.add(new HashSet<>());
        }
    }
    public int remaining(){
        int count = 0;
        for(int i = 0; i < data.size(); i++){
            count += data.elementAt(i).size();
        }
        return count;
    }
    public int getNumVars() {
        return data.size();
    }

    public HashSet<Integer> getValues(int pos) {
        return data.elementAt(pos);
    }

    public void addValue(int position, Integer v) {
        data.elementAt(position).add(v);
    }

    public void setValues(int i, HashSet<Integer> values) {
        data.setElementAt(values, i);

    }
    public void emptyHere(int i){////////////////
        HashSet<Integer> temp = data.elementAt(i);
        temp.clear();
    }
    public void putMore(HashSet<Integer> push){//////////////
        data.add(push);
    }
    public Iterator<HashSet<Integer>> addAll(){////
        return data.iterator();
    }

    public int getNumberOfValues(int curVar) {
        /* TODO: this function should be modified depending on the cutoff value you want
            for your parallel algorithm */
        return 1;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < data.size(); i++) {
            s += "[" + data.get(i) + "]\n";
        }
        return s;
    }
}
