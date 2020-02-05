import java.util.*;

public class aStar {

    public static int[][] cloneArray(int[][] src) {
        int length = src.length;
        int[][] target = new int[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }

    public static int[] searchElement(int[][] board, int value) {
        int[] pos = new int[2];
        for (int i = 0; i < Main.N; i++) {
            for (int j = 0; j < Main.N; j++) {
                if (board[i][j] == value) {
                    pos[0] = i;
                    pos[1] = j;
                    return pos;
                }
            }
        }
        return pos;
    }

    public static int[] blankPos(int[][] board) {
        int[] pos = new int[2];
        for (int i = 0; i < Main.N; i++) {
            for (int j = 0; j < Main.N; j++) {
                if (board[i][j] == 0) {
                    pos[0] = i;
                    pos[1] = j;
                }
            }
        }
        return pos;
    }

    public static int searchElement(int[] board, int value) {
        for (int i = 0; i < Main.N; i++) {
            if (board[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static int[][][] moves(int[][] board, int x, int y) {
        int ret[][][] = new int[4][][];
        int[][] temp = cloneArray(board);
        if (y < (Main.N - 1)) {
            int t = temp[x][y];
            temp[x][y] = temp[x][y + 1];
            temp[x][y + 1] = t;
            ret[0] = temp;
        } else
            ret[0] = null;
        temp = cloneArray(board);
        if (y > 0) {
            int t = temp[x][y];
            temp[x][y] = temp[x][y - 1];
            temp[x][y - 1] = t;
            ret[1] = temp;
        } else
            ret[1] = null;
        temp = cloneArray(board);
        if (x < (Main.N - 1)) {
            int t = temp[x][y];
            temp[x][y] = temp[x + 1][y];
            temp[x + 1][y] = t;
            ret[2] = temp;
        } else
            ret[2] = null;
        temp = cloneArray(board);
        if (x > 0) {
            int t = temp[x][y];
            temp[x][y] = temp[x - 1][y];
            temp[x - 1][y] = t;
            ret[3] = temp;
        } else
            ret[3] = null;
        return ret;
    }

    public static int hammingDistance(int[][] source, int[][] dest) {
        int dist = 0;
        for (int i = 0; i < Main.N; i++) {
            for (int j = 0; j < Main.N; j++) {
                if (dest[i][j] != source[i][j] && dest[i][j] != 0)
                    dist++;
            }
        }
        return dist;
    }

    public static int manhattanDistance(int[][] source, int[][] dest) {
        int dist = 0;
        for (int i = 0; i < Main.N; i++) {
            for (int j = 0; j < Main.N; j++) {
                if (dest[i][j] != 0) {
                    int xy[] = searchElement(source, dest[i][j]);
                    dist += Math.abs(xy[0] - i);
                    dist += Math.abs(xy[1] - j);
                }
            }
        }
        return dist;
    }

    public static int linearConflict(int[][] source, int[][] dest) {
        int dist = 0;
        for (int i = 0; i < Main.N; i++) {
            for (int j = 1; j < Main.N; j++) {
                if ((source[i][j] != 0 || source[i][0] != 0)) {
                    if (Arrays.binarySearch(dest[i], source[i][j]) >= 0 && Arrays.binarySearch(dest[i], source[i][0]) >= 0) {
                        int posJ = searchElement(source[i], source[i][j]);
                        int posK = searchElement(source[i], source[i][0]);
                        boolean side1 = posJ > posK ? true : false;
                        posJ = searchElement(dest[i], source[i][j]);
                        posK = searchElement(dest[i], source[i][0]);
                        boolean side2 = posJ > posK ? true : false;
                        if (side1 != side2)
                            dist += 2;
                    }
                }
            }
        }
        int[][] tSource = cloneArray(source);
        int[][] tDest = cloneArray(dest);
        for (int i = 0; i < Main.N; i++) {
            for (int j = 0; j < Main.N; j++) {
                tSource[i][j] = source[j][i];
                tDest[i][j] = dest[i][j];
            }
        }
        for (int j = 0; j < Main.N; j++) {
            for (int i = 1; i < Main.N; i++) {
                if ((source[i][j] != 0 || source[0][j] != 0)) {
                    if (Arrays.binarySearch(tDest[j], source[i][j]) >= 0 && Arrays.binarySearch(tDest[j], source[0][j]) >= 0) {
                        int posJ = searchElement(tSource[j], source[i][j]);
                        int posK = searchElement(tSource[j], source[0][j]);
                        boolean side1 = posJ > posK ? true : false;
                        posJ = searchElement(tDest[j], source[i][j]);
                        posK = searchElement(tDest[j], source[0][j]);
                        boolean side2 = posJ > posK ? true : false;
                        if (side1 != side2)
                            dist += 2;
                    }
                }
            }
        }
        return manhattanDistance(source, dest) + dist;
    }


    PriorityQueue<Node> openSet = new PriorityQueue<>(new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            if (o1.f > o2.f)
                return 1;
            if (o1.f < o2.f)
                return -1;
            return 0;
        }
    });
    HashSet<Node> hashMap = new HashSet<>();
    HashSet<Node> closedMap = new HashSet<>();

    public static boolean arrEquals(int[][] a1, int[][] a2) {
        if (Arrays.deepEquals(a1, a2))
            return true;
        return false;
    }


    public Node solve(int[][] initital, int[][] goal, int function) {
        Node start = new Node();
        start.board = initital;
        start.prevBoard = null;
        start.g = 0;
        if (function == 1)
            start.f = start.g + hammingDistance(start.board, goal);
        else if (function == 2)
            start.f = start.g + manhattanDistance(start.board, goal);
        else if (function == 3)
            start.f = start.g + linearConflict(start.board, goal);
        Node end = new Node();
        end.board = goal;
        openSet.add(start);
        int i = 0;
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            Main.expanded++;
            if (arrEquals(current.board, goal))
                return current;
            closedMap.add(current);
            int[][] nei;
            int rc[] = blankPos(current.board);
            int[][][] movesBoard = moves(current.board, rc[0], rc[1]);
            for (int j = 0; j < 4; j++) {
                if (movesBoard[j] != null) {
                    nei = movesBoard[j];
                    Node node = new Node();
                    Node temp = new Node();
                    temp.board = nei;
                    node.board = nei;
                    node.g = current.g + 1;
                    //if(!closedSet.contains(node)){
                    if (!closedMap.contains(node)) {
                        int temp_g = current.g + 1;
                        //if(!openSet.contains(node)){
                        if (!hashMap.contains(node)) {
                            node.prevBoard = current;
                            node.g = temp_g;
                            if (function == 1)
                                node.f = node.g + hammingDistance(node.board, end.board);
                            else if (function == 2)
                                node.f = node.g + manhattanDistance(node.board, end.board);
                            else if (function == 3)
                                node.f = node.g + linearConflict(node.board, end.board);
                            openSet.add(node);
                            hashMap.add(node);
                            Main.explored++;
                        }
                    }
                }
            }
        }
        return null;
    }
}
