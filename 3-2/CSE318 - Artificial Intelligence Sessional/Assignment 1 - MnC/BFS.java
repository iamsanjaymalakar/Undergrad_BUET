import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class BFS {
    public Queue<node> queue;
    boolean visited[][][];
    public int explored=0,expanded=0;

    public BFS() {
        queue = new LinkedList();
        visited = new boolean[1000][1000][3];
    }

    public void addNode(node parent, int numMissionaries, int numCannibals) {
        int dir = parent.side ? 1 : -1;
        node tempNode = new node(parent.numMissionaries + numMissionaries * dir, parent.numCannibals + numCannibals * dir,
                !parent.side, parent, parent.level + 1);
        // check the node valid or not
        if (!tempNode.valid())
            return;
        if (visited[tempNode.numMissionaries][tempNode.numCannibals][bool2int(tempNode.side)])
            return;
        //valid
        explored++;
        queue.add(tempNode);
    }

    public void addNode(node temp) {
        if (!temp.valid())
            return;
        //valid
        queue.add(temp);
    }

    public node solve(node startNode, node endNode) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Main.timerFlag=true;
                return;
            }
        }, Main.TIME_LIMIT);
        addNode(startNode); // add root

        while (!queue.isEmpty()) {
            if(Main.timerFlag==true)
                return null;
            if(explored>Main.NODE_LIMIT){
                Main.nodeFlag=true;
                return null;
            }
            //
            node cur = queue.poll();
            visited[cur.numMissionaries][cur.numCannibals][bool2int(cur.side)] = true;
            expanded++;
            if (cur.equals(endNode)) {
                return cur;
            }
            else {
                expandNode(cur);
            }
        }
        return null;
    }

    public void expandNode(node temp) {
        int numMissionaries = 0, numCannibals = 0;
        for (int i = 0; i <= Main.capacity; i++) {
            for (int j = 0; j <= Main.capacity; j++) {
                if (i == 0 && j == 0)
                    continue;
                if (i + j > Main.capacity)
                    continue;
                numMissionaries = i;
                numCannibals = j;
                addNode(temp, numMissionaries, numCannibals);
            }
        }
    }

    int bool2int(boolean bool) {
        if (bool)
            return 1;
        return 0;
    }
}
