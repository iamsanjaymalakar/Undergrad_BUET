package offline;
 
import java.util.Scanner;
 
class WorkItem{
    public int[] rV;
    public int[] cV;
    public int i,j;
    public WorkItem(int i,int j,int[][] A,int[][] B){
        rV=new int[A[0].length];
        cV=new int[B.length];
        this.i=i;
        this.j=j;
        System.arraycopy(A[i], 0, rV, 0, A[0].length);
        for(int b=0;b<B.length;b++){
            cV[b]=B[b][j];
        }
    }    
}
 
class queue
{
    WorkItem[] wi;
    int head;
    int tail;
    int size;
    queue(int a)
    {
        wi=new WorkItem[a];
        size=a;
        head=tail=0;
    }
    public synchronized void push(WorkItem w)
    {
        
        wi[(tail++)]=w;
        notifyAll();
    }
    
    public synchronized WorkItem front()
    {
        while (head==tail)
        {
            try{
                wait();
            }catch(InterruptedException e)
            {
                System.out.println("Error");
            }
        }
        return wi[(head++)];
    }
}
 
 
class W implements Runnable{
    public int tt;
    public int[][] C;
    public WorkItem item;
    Thread t;
    queue obj;
   
    public W(int[][] C,int tt,queue obj){
        this.C=C;
        this.tt=tt;
        this.obj=obj;
        t=new Thread(this);
        t.start();
    }
   
    int dot(WorkItem x){
    int sum=0;
    for(int a=0;a<x.rV.length;a++){
        sum+=(x.rV[a]*x.cV[a]);
    }
    return sum;
    }
   
    @Override
    public void run(){
        for(int i=0;i<tt;i++){
            item=obj.front();
            C[item.i][item.j]=dot(item);
        }
        
    }
}
 
 
public class Main {
   
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        //input
        System.out.println("Enter number of threads:");
        int tn=sc.nextInt();
        System.out.println("Enter row and coloumn of first matrix:");
        int r1=sc.nextInt();
        int c1=sc.nextInt();
        int[][] A=new int[r1][c1];
        System.out.println("Enter elements of first matrix");
        for(int i=0;i<r1;i++){
            for(int j=0;j<c1;j++){
               A[i][j]=sc.nextInt();
            }
        }
        System.out.println("Enter row and coloumn of second matrix:");
       int r2=sc.nextInt();
       int c2=sc.nextInt();
        int[][] B=new int[r2][c2];
        System.out.println("Enter elements of second matrix");
        for(int i=0;i<r2;i++){
            for(int j=0;j<c2;j++){
               B[i][j]=sc.nextInt();
            }
        }
        if(c1!=r2){
            System.out.println("Matrix multipication is not possible.");
            System.exit(0);
        }
        int[][] C=new int[r1][c2];
       
        //WorkItem Initialization
        WorkItem items[]=new WorkItem[r1*c2];
        for(int i=0;i<r1;i++){
           for(int j=0;j<c2;j++){
               items[i*c2+j]=new WorkItem(i,j,A,B);
           }
        }
       
        queue qu=new queue(r1*c2);
       
        //Thread
        W[] thread=new W[tn];
        int index,tc=0;
        int tt=(r1*c2)/tn;
        for(int i=0;i<r1;i++){
            for(int j=0;j<c2;j++){
                index=i*c2+j;
                if(index%tt==0){
                    thread[tc]=new W(C,tt,qu);
                    tc++;
                }
            }  
        }
        
        //Queue
        for(int i=0;i<(r1*c2);i++){
            qu.push(items[i]);
        }
       
        //Thread join
        try {
                for(int i=0;i<tn;i++){
                thread[i].t.join();
                }
            } catch (InterruptedException ex) {
                System.out.println("Inturrpted.");            
            }  
       
        //Output
        System.out.println("After multipication:");
        for(int i=0;i<r1;i++){
            for(int j=0;j<c2;j++){
                System.out.print(C[i][j]+" ");
            }
            System.out.println();
        }      
    }
}