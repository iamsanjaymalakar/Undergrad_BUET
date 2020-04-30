package offline;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class WorkItem{
    public int[] rV;
    public int[] cV;
    public int i,j;
    public WorkItem(int i,int j,int[][] A,int[][] B){
        rV=new int[A[0].length];
        cV=new int[B.length];
        this.i=i;
        this.j=j;
        for(int a=0;a<A[0].length;a++){
            rV[a]=A[i][a];
        }
        for(int b=0;b<B.length;b++){
            cV[b]=B[b][j];
        }
    }    
}

class queue
{
    WorkItem[] q;
    int items;
    int head;
    int tail;
    queue(int a)
    {
        q=new WorkItem[a];
        items=a;
        head=tail=0;
    }
    void push(WorkItem w)
    {
        q[tail++]=w;
    }
    WorkItem front()
    {
        return q[head++];
    }
}


class W extends Thread{
    public int tt,index;
    public int[][] C;
    public WorkItem item;
    Thread t;
    queue obj;
    
    public W(int[][] C,int tt,int index,queue obj){
        this.C=C;
        this.tt=tt;
        this.index=index;
        this.obj=obj;
    }
    
    public int dot(WorkItem x){
    int sum=0;
    for(int a=0;a<x.rV.length;a++){
        sum+=(x.rV[a]*x.cV[a]);
    }
    return sum;
    }
    
    @Override
    public void run(){
        synchronized(obj)
        {
            for(int a=0;a<tt;a++){
            item=obj.front();
            int temp=dot(item);
            C[item.i][item.j]=temp;
            index++;
            }
        }  
    } 
}

 
public class Main {
    
    public static void main(String[] args) {
        System.out.println("Enter row and coloumn of first matrix:");
        Scanner sc= new Scanner(System.in);
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
        System.out.println("Enter number of threads:");
        int tn=sc.nextInt();
       
        //WorkItem Initialization
        WorkItem items[]=new WorkItem[r1*c2];
        for(int i=0;i<r1;i++){
           for(int j=0;j<c2;j++){
               items[i*c2+j]=new WorkItem(i,j,A,B);
           }
        }
        
        //Queue
        queue qu=new queue(r1*c2);
        for(int i=0;i<(r1*c2);i++){
            qu.push(items[i]);
        }
        
        
        //Thread
        W[] thread=new W[tn];
        int index,tc=0;
        int tt=(r1*c2)/tn;
        for(int i=0;i<r1;i++){
            for(int j=0;j<c2;j++){
                index=i*c2+j;
                if(index%tt==0){
                    thread[tc]=new W(C,tt,index,qu);
                    thread[tc].setName("Thread  "+tc);
                    thread[tc].start();
                    tc++;
                }
            }  
        }
        
        //Thread join
        try {
                for(int i=0;i<tn;i++){
                thread[i].join();
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
    
    

