import java.util.ArrayList;
import java.util.List;

public class TwoOpt {
    ArrayList<Integer> tempList,ansList,oList;
    List<Integer> list;
    int n;
    Node[] node;
    double cost;
    boolean first;

    TwoOpt(List list,Node[] node,int n,boolean first){
        this.first=first;
        cost=0;
        this.list = list;
        oList = new ArrayList<>(list);
        this.n = n;
        this.node = node;
        solve();
    }

    double getCost(ArrayList<Integer> list){
        double dist=0;
        for(int i=0;i<list.size()-1;i++)
        {
            dist+=node[list.get(i)].distance(node[list.get(i+1)]);
        }
        return dist;
    }


    void solve(){
        double cost = getCost(oList);
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                tempList = new ArrayList(list);
                while(i<j) {
                    int temp = tempList.get(i);
                    tempList.set(i,tempList.get(j));
                    tempList.set(j,temp);
                    i++;
                    j--;
                }
                double dist = getCost(tempList);
                if(dist<cost){
                    cost = dist;
                    ansList = new ArrayList<>(tempList);
                    if(first==true)
                        return;
                }
            }
        }
    }

    void printAns(){
        System.out.print("Cost:"+String.format("%.2f",getCost())+"  ---  ");
        for(int i=0;i<ansList.size();i++){
            System.out.print(ansList.get(i));
            node[ansList.get(i)].printNode();
            if(i!=ansList.size()-1)
                System.out.print(" -> ");
        }
        System.out.println();
    }

    public double getCost() {
        cost=0;
        for(int i=0;i<ansList.size()-1;i++) {
            cost += node[ansList.get(i)].distance(node[ansList.get(i + 1)]);
        }
        return cost;
    }


}
