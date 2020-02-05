import java.util.ArrayList;
import java.util.List;

public class SavingHeuristic {
    Node[] node;
    int source, n;
    List<Integer> list;
    double[][] savings;
    int[] subTour,parent;
    int inf=1000000;
    double cost;
    boolean rr;

    SavingHeuristic(Node[] node, int n,boolean rr,int source) {
        list = new ArrayList<>();
        savings = new double[n][n];
        subTour = new int[n];
        parent = new int[n];
        this.node = node;
        this.n = n;
        this.rr=rr;
        cost=0;
        this.source=source;
        if(source==-1)
            this.source = (int)(Math.random()*n);
        node[this.source].visited = true;
        calculateSavings(this.source);
        solve();
        list.add(this.source);
    }

    Node getMaxSavings()
    {
        if(rr==false) {
            double value = -inf * 10;
            Node index = new Node();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (savings[i][j] > value) {
                        value = savings[i][j];
                        index.x = i;
                        index.y = j;
                    }
                }
            }
            return index;
        }
        else{
            ArrayList<Node> minList = new ArrayList<>();
            for(int k=0;k<5;k++) {
                double value = -inf * 10;
                Node index = new Node();
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (savings[i][j] > value && !minList.contains(new Node(i,j))) {
                            value = savings[i][j];
                            index.x = i;
                            index.y = j;
                        }
                    }
                }
                if(!minList.contains(index))
                    minList.add(index);
            }
            return minList.get((int) (Math.random() * minList.size()));
        }
    }

    int getMaxSavings(int x,int source)
    {
        if(rr==false) {
            double value = -inf;
            int index = -1;
            for (int i = 0; i < n; i++) {
                if (savings[x][i] > value && subTour[i] != source) {
                    value = savings[x][i];
                    index = i;
                }
            }
            return index;
        }
        else{
            ArrayList<Integer> minList = new ArrayList<>();
            for(int k=0;k<5;k++) {
                double value = -inf;
                int index = -1;
                for (int i = 0; i < n; i++) {
                    if (savings[x][i] > value && subTour[i] != source && !minList.contains(i)) {
                        value = savings[x][i];
                        index = i;
                    }
                }
                if(!minList.contains(index) && index!=-1)
                    minList.add(index);
            }
            if(minList.size()!=0)
                return minList.get((int) (Math.random() * minList.size()));
            return -1;
        }
    }

    void calculateSavings(int source) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != source && j != source && i != j)
                    savings[i][j] = node[source].distance(node[i]) + node[source].distance(node[j]) - node[i].distance(node[j]);
                else
                    savings[i][j]=-inf;
            }
        }
    }

    void addToPath(int index)
    {
        if(parent[index]!=index)
            addToPath(parent[index]);
        list.add(index);
    }

    void solve(){
        for(int i=0;i<n;i++){
            subTour[i]=i;
            parent[i]=0;
        }
        Node index=getMaxSavings();
        int a=(int)index.x,b=(int)index.y;
        subTour[a]=subTour[b]=source;
        parent[b]=a;
        parent[source]=source;
        int count=n-3;
        while(count!=0)
        {
            int i=getMaxSavings(a,source),j=getMaxSavings(b,source);
            if(i==-1||savings[a][i]<savings[b][j]) {
                parent[j] = b;
                subTour[j] = source;
                b = j;
            }
            else {
                parent[a] = i;
                subTour[i] = source;
                a = i;
            }
            count--;
        }
        parent[a]=source;
        addToPath(b);
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

    public int getSource() {
        return source;
    }

    public double getCost() {
        cost=0;
        for(int i=0;i<list.size()-1;i++) {
            cost += node[list.get(i)].distance(node[list.get(i + 1)]);
        }
        return cost;
    }

    public List<Integer> getList() {
        return list;
    }
}
