import java.util.Timer;
import java.util.TimerTask;

public class DFS {
    boolean visited[][][];
    public int explored=0,expanded=0;

    public DFS() {
        visited = new boolean[1000][1000][3];
    }

    public node getNode(node parent, int numMissionaries, int numCannibals) {
        int dir = parent.side ? 1 : -1;
        node tempNode = new node(parent.numMissionaries + numMissionaries * dir, parent.numCannibals + numCannibals * dir,
                !parent.side, parent, parent.level + 1);
        // check the node valid or not
        if (!tempNode.valid())
            return null;
        if (visited[tempNode.numMissionaries][tempNode.numCannibals][bool2int(tempNode.side)])
            return null;
        //valid
        expanded++;
        return tempNode;
    }


    public node dfs(node cur){
        if(Main.timerFlag==true)
            return null;
        if(explored>Main.NODE_LIMIT){
            Main.nodeFlag=true;
            return null;
        }
        //
        visited[cur.numMissionaries][cur.numCannibals][bool2int(cur.side)] = true;
        explored++;
        if(cur.equals(new node(0,0,true,99999)))
            return cur;
        //iterate all
        node temp=null;
        for (int i = 0; i <= Main.capacity; i++) {
            for (int j = 0; j <= Main.capacity; j++) {
                if (i == 0 && j == 0)
                    continue;
                if (i + j > Main.capacity)
                    continue;
                temp=getNode(cur, i, j);
                if(temp!=null)
                    temp=dfs(temp);
                if(temp!=null)
                    return temp;
            }
        }
        return null;
    }

    public node solve(node startNode, node endNode){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Main.timerFlag=true;
                return;
            }
        }, Main.TIME_LIMIT);
        return dfs(startNode);
    }

    int bool2int(boolean bool) {
        if (bool)
            return 1;
        return 0;
    }
}
