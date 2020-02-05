import java.util.Scanner;

public class Main {
    public static int nMissionaries, nCanniabals, capacity;
    public static boolean timerFlag=false,nodeFlag=false;
    public static int TIME_LIMIT=30000,NODE_LIMIT=30000000;

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Missionaries Cannibals BoatCapacity");
        nMissionaries = sc.nextInt();
        nCanniabals = sc.nextInt();
        capacity = sc.nextInt();

        node startNode = new node(nMissionaries, nCanniabals, false, 1);
        node endNode = new node(0, 0, true, 99999);

        long sTime = System.nanoTime();
        BFS bfs = new BFS();
        node ans = bfs.solve(startNode, endNode);
        long diff = System.nanoTime() - sTime;
        if (ans != null) {
            System.out.println("Level:" + ans.level + ",Time:" + diff + "ns,Explored:" + bfs.explored + ",Expanded:" + bfs.expanded);
            ans.printNode();
        } else {
            if(timerFlag)
                System.out.println("Timeout");
            else if(nodeFlag)
                System.out.println("Explored node limit exceded");
            else
                System.out.println("No solutions found");
        }
        System.out.println("\n");

        //dfs
        nodeFlag=false;
        timerFlag=false;
        sTime = System.nanoTime();
        DFS dfs = new DFS();
        ans = dfs.solve(startNode, endNode);
        diff = System.nanoTime() - sTime;
        if (ans != null) {
            System.out.println("Level:" + ans.level + ",Time:" + diff + "ns,Explored:" + dfs.explored + ",Expanded:" + dfs.expanded);
            ans.printNode();
        } else {
            if(timerFlag)
                System.out.println("Timeout");
            else if(nodeFlag)
                System.out.println("Explored node limit exceded");
            else
                System.out.println("No solutions found");
        }
        System.out.println("\n");
    }
}
