import java.lang.reflect.Array;
import java.util.Arrays;

public class Node {
    int[][] board;
    Node prevBoard=null;
    int g=0,f=0;


    @Override
    public boolean equals(Object obj) {

        Node temp = (Node) obj;
        if (temp == this) {
            return true;
        }
        if(Arrays.deepEquals(this.board,temp.board))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.board);
    }
}
