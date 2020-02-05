import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class Main {
    public static int N = 4;

    public static int explored=0,expanded=0;

    public static void printArr(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public static void printAns(Node node){
        if(node.prevBoard!=null)
            printAns(node.prevBoard);
        printArr(node.board);
        System.out.println("------");
    }

    public static int inversions(int[][] board,boolean odd){
        int cnt=0,row=0;
        int temp[] = new int[N*N];
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                temp[i*N+j]=board[i][j];
                if(board[i][j]==0)
                    row=i;
            }
        }
        for(int i=0;i<N*N;i++){
            for(int j=i;j<N*N;j++){
                if(temp[i]!=0 && temp[j]!=0){
                    if(temp[i]>temp[j])
                        cnt++;
                }
            }
        }
        if(odd)
            return cnt;
        return cnt+row;
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter N:");
        N=sc.nextInt();
        System.out.println("Enter initial board :");
        int[][] board = new int[N][N];
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                board[i][j]=sc.nextInt();
            }
        }
        int[][] goal = new int[N][N];
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                goal[i][j]=i*N+j+1;
                if(i==N-1 && j==N-1)
                    goal[i][j]=0;
            }
        }
        if(N%2==1){
           if(inversions(board,true)%2==1){
               System.out.println("Unsolvable");
               return;
           }
        }
        else{
            if(inversions(board,false)%2==0){
                System.out.println("Unsolvable");
                return;
            }
        }
        int choice = sc.nextInt();
        aStar star;
        Node ans;
        if(choice==1){
        System.out.println("Hamming Distance :");
        star = new aStar();
        ans = star.solve(board,goal,1);
            System.out.println("Explored "+explored+", Expanded "+expanded+", Moves "+ans.g);
        printAns(ans);
        System.out.println();
        }
        else if(choice==2) {
            System.out.println("Manhattan distance :");
            star = new aStar();
            ans = star.solve(board, goal, 2);
            System.out.println("Explored "+explored+", Expanded "+expanded+", Moves "+ans.g);
            printAns(ans);
            System.out.println();
        }
        else {
            System.out.println("Linear conflict :");
            star = new aStar();
            ans = star.solve(board, goal, 3);
            System.out.println("Explored "+explored+", Expanded "+expanded+", Moves "+ans.g);
            printAns(ans);
            System.out.println();
        }

    }
}
