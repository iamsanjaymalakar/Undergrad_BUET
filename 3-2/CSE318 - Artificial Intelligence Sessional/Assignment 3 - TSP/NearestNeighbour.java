import java.util.ArrayList;
import java.util.List;

public class NearestNeighbour {
    Node[] node;
    int source,n;
    List<Integer> list;
    double cost;
    boolean rr;

    NearestNeighbour(Node[] node,int n,boolean rr,int source){
        list = new ArrayList<>();
        this.node=node;
        this.n = n;
        this.source=source;
        if(source==-1)
            this.source = (int)(Math.random()*n);
        this.rr = rr;
        cost=0;
        node[this.source].visited=true;
        list.add(this.source);
        solve(this.source);
        list.add(this.source);
    }

    int minimumNeighbour(int source){
        if(rr==false) {
            int temp = -1;
            double dist, min = 1000000;
            for (int i = 0; i < n; i++) {
                if (i != source && !node[i].visited) {
                    dist = node[source].distance(node[i]);
                    if (dist < min) {
                        min = dist;
                        temp = i;
                    }
                }
            }
            return temp;
        }
        else {
            ArrayList<Integer> minList = new ArrayList<>();
            for(int k=0;k<5;k++) {
                int temp = -1;
                double dist, min = 1000000;
                for (int i = 0; i < n; i++) {
                    if (i != source && !node[i].visited) {
                        dist = node[source].distance(node[i]);
                        if (dist < min && !minList.contains(i)) {
                            min = dist;
                            temp = i;
                        }
                    }
                }
                if(!minList.contains(temp) && temp!=-1)
                    minList.add(temp);
            }
            if(minList.size()!=0)
                return minList.get((int) (Math.random() * minList.size()));
            return -1;
        }
    }

    void solve(int source){
        int minNode=minimumNeighbour(source);
        if(minNode==-1)
            return;
        node[minNode].visited=true;
        list.add(minNode);
        solve(minNode);
    }

    void printAns(){
        System.out.print("Cost:"+String.format("%.2f",cost)+"  ---  ");
        for(int i=0;i<list.size();i++){
            System.out.print(list.get(i));
            node[list.get(i)].printNode();
            if(i!=list.size()-1)
                System.out.print(" -> ");
        }
        System.out.println();
    }

    public double getCost() {
        cost=0;
        for(int i=0;i<list.size()-1;i++) {
            cost += node[list.get(i)].distance(node[list.get(i + 1)]);
        }
        return cost;
    }

    public int getSource() {
        return source;
    }
}
