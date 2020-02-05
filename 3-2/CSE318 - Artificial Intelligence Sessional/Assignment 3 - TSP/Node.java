import java.util.Objects;

public class Node {
    double x, y;
    boolean visited;
    Node(int x,int y){
        this.x = x;
        this.y = y;
        visited = false;
    }
    Node(){
        x=0;
        y=0;
        visited=false;
    }

    double distance(Node node){
        return Math.sqrt(((x - node.x) * (x - node.x)) + ((y - node.y) * (y - node.y)));
    }

    void printNode(){
        System.out.print("("+x+","+y+") ");
    }

    @Override
    public boolean equals(Object o) {
        Node temp = (Node)o;
        if((this.x==temp.x && this.y==temp.y) || (this.x==temp.y && this.y==temp.x))
            return true;
        return false;
    }

}
