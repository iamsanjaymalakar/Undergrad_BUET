//============================================================================
// Author       : Sanjay Malakar
//============================================================================

#include <algorithm>
#include <bitset>
#include <cctype>
#include <cmath>
#include <cstdio>
#include <cstring>
#include <iostream>
#include <map>
#include <queue>
#include <set>
#include <stack>
#include <string>
#include <vector>

using namespace std;

#define NULL_VALUE -999999
#define INF 999999
#define WHITE 1
#define GREY 2
#define BLACK 3
#define MAX_HEAP_SIZE 100000
#define MAXREAL 999999.0

const int mn = 1000001;

#define FOR(i, a, b) for (int i = a; i <= b; i++)
#define REP(i, n) for (int i = 0; i < n; i++)
#define ll long long
#define ull unsigned long long




// Heap

class HeapItem
{
public:
    int data; //actual data that is stored
    float key; //key value of the data, heap is constructed based on key
};

//MinHeap class, minimum item stored at the root of heap tree
class MinHeap
{
public:
    HeapItem * A; //stores heap items, e.g., nodes
    int heapLength;
    int * map;

    MinHeap() //constructor
    {
        A = new HeapItem[MAX_HEAP_SIZE];
        map = new int[MAX_HEAP_SIZE];
        heapLength=0;
    }

    ~MinHeap() //destructor
    {
        if(map) delete [] map;
        if(A) delete [] A;
        map = 0; //set to NULL after deletion
        A = 0; //set to NULL after deletion
    }

    //Fills the heap with an array of integers
    //key values do not maintain heap property
    //May be used in some algorithms such as dijkstra's shortest path
    void initialize(int v[], int n)
    {
        heapLength = n;
        for(int i=0; i<n; i++) //nodes are stored from index 1 instead of 0 in the heap
        {
            A[i+1].data = v[i];
            A[i+1].key = MAXREAL;
            map[v[i]] = i+1; //map tracks which vertex is stored at which heap node
        }
    }

    //this function inserts a new (data,key) pair in the heap
    //call to buheapify is required
    void insertItem(int data, int key)
    {
        //Write your codes here
        if(data>=0)
        {
            heapLength++;
            A[heapLength].data=data;
            A[heapLength].key=key;
            map[data]=heapLength;
            buHeapify(heapLength);
        }
        else
        {
            printf("Data can't be negative.\n");
        }
    }

    //this function removes (and returns) the node which contains the minimum key value
    HeapItem removeMin()
    {
        //write your codes here
        HeapItem temp;
        if(heapLength==0)
        {
            printf("Underflow.\n");
            temp.data=-1;
            return temp;
        }
        else
        {
            temp.data=A[1].data;
            temp.key=A[1].key;
            A[1]=A[heapLength--];
            map[A[1].data]=1;
            heapify(1);
            return temp;
        }
    }


    //The function updates the key value of an existing data
    //stored in the heap
    //Note that updates can result in an increase or decrease of key value
    //Call to heapify or buheapify is required
    void updateKey(int data, float key)
    {
        //Write your codes here.
        if(map[data]>=1 && map[data]<=heapLength)
        {
            float temp=A[map[data]].key;
            A[map[data]].key=key;
            if(key<temp)
                buHeapify(map[data]);
            else
                heapify(map[data]);
        }
        else
        {
            printf("%d not found.\n",data);
        }
    }


    //This function returns the key value of a data stored in heap
    float getKey(int data)
    {
        int i = map[data];
        return A[i].key;
    }

    //This function heapifies the heap
    //When a key value of ith node is increased (because of update), then calling
    //this function will restore heap property
    void heapify(int i)
    {
        int l,r,smallest;
        while(1)
        {
            l=2*i;      //left child index
            r=2*i+1;    //right child index
            smallest=i;

            if(l>heapLength && r>heapLength)
                break; //nothing to do, we are at bottom
            else if(r>heapLength)
                smallest = l;
            else if(l>heapLength)
                smallest = r;
            else if( A[l].key < A[r].key )
                smallest = l;
            else
                smallest = r;
            if(A[i].key <= A[smallest].key)
                break;	//we are done heapifying
            else
            {
                //swap nodes with smallest child, adjust map array accordingly
                HeapItem t;
                t=A[i];
                A[i]=A[smallest];
                map[A[i].data]=i;
                A[smallest]=t;
                map[A[smallest].data]=smallest;
                i=smallest;
            }

        }
    }

    //This function heapifies the heap form bottom to up
    //When a key value of ith node is decreased (because of update), then calling
    //this function will restore heap property
    //In addition, when a new item is inserted at the end of the heap, then
    //calling this function restores heap property
    void buHeapify(int i)
    {
        if(i==1)
            return;
        if(i<=heapLength)
        {
            if(A[i/2].key>A[i].key)
            {
                HeapItem temp;
                temp.data=A[i/2].data;
                temp.key=A[i/2].key;
                A[i/2]=A[i];
                map[A[i/2].data]=i/2;
                A[i]=temp;
                map[A[i].data]=i;
            }
        }
        buHeapify(i/2);
    }

    void printHeap()
    {
        printf("Heap length: %d\n", heapLength);
        for(int i=1; i<=heapLength; i++)
        {
            printf("(%d,%.2f,%d) ", A[i].data, A[i].key, map[A[i].data]);
        }
        printf("\n");
    }

    bool Empty()
    {
        if(heapLength==0)return true;
        else return false;
    }

    bool contains(int data)
    {
        if(map[data]>=1 && map[data]<=heapLength)
            return true;
        return false;
    }
};

// heap end

class Queue
{
    int queueInitSize ;
    int queueMaxSize;
    int * data;
    int length;
    int front;
    int rear;
public:
    Queue();
    ~Queue();
    void enqueue(int item); //insert item in the queue
    int dequeue(); //returns the item according to FIFO
    bool empty(); //return true if Queue is empty
};

Queue::Queue()
{
    queueInitSize = 2 ;
    queueMaxSize = queueInitSize;
    data = new int[queueMaxSize] ; //allocate initial memory
    length = 0 ;
    front = 0;
    rear = 0;
}


void Queue::enqueue(int item)
{
    if (length == queueMaxSize)
    {
        int * tempData ;
        //allocate new memory space for tempList
        queueMaxSize = 2 * queueMaxSize ;
        tempData = new int[queueMaxSize] ;
        int i, j;
        j = 0;
        for( i = rear; i < length ; i++ )
        {
            tempData[j++] = data[i] ; //copy items from rear
        }
        for( i = 0; i < rear ; i++ )
        {
            tempData[j++] = data[i] ; //copy items before rear
        }
        rear = 0 ;
        front = length ;
        delete[] data ; //free the memory allocated before
        data = tempData ; //make list to point to new memory
    }

    data[front] = item ; //store new item
    front = (front + 1) % queueMaxSize ;
    length++ ;
}


bool Queue::empty()
{
    if(length == 0) return true ;
    else return false ;
}


int Queue::dequeue()
{
    if(length == 0) return NULL_VALUE ;
    int item = data[rear] ;
    rear = (rear + 1) % queueMaxSize ;
    length-- ;
    return item ;
}


Queue::~Queue()
{
    if(data) delete[] data; //deallocate memory
    data = 0; //set to NULL
}

//****************Queue class ends here************************

//****************Dynamic ArrayList class based************************
class ArrayList
{
    int * list;
    int length;
    int listMaxSize ;
    int listInitSize ;
public:
    ArrayList() ;
    ~ArrayList() ;
    int searchItem(int item) ;
    void insertItem(int item) ;
    void removeItem(int item) ;
    void removeItemAt(int item);
    int getItem(int position) ;
    int getLength();
    bool empty();
    void printList();
} ;


ArrayList::ArrayList()
{
    listInitSize = 2 ;
    listMaxSize = listInitSize ;
    list = new int[listMaxSize] ;
    length = 0 ;
}

void ArrayList::insertItem(int newitem)
{
    int * tempList ;
    if (length == listMaxSize)
    {
        //allocate new memory space for tempList
        listMaxSize = 2 * listMaxSize ;
        tempList = new int[listMaxSize] ;
        int i;
        for( i = 0; i < length ; i++ )
        {
            tempList[i] = list[i] ; //copy all items from list to tempList
        }
        delete[] list ; //free the memory allocated before
        list = tempList ; //make list to point to new memory
    };

    list[length] = newitem ; //store new item
    length++ ;
}

int ArrayList::searchItem(int item)
{
    int i = 0;
    for (i = 0; i < length; i++)
    {
        if( list[i] == item ) return i;
    }
    return NULL_VALUE;
}

void ArrayList::removeItemAt(int position) //do not preserve order of items
{
    if ( position < 0 || position >= length ) return ; //nothing to remove
    list[position] = list[length-1] ;
    length-- ;
}


void ArrayList::removeItem(int item)
{
    int position;
    position = searchItem(item) ;
    if ( position == NULL_VALUE ) return ; //nothing to remove
    removeItemAt(position) ;
}


int ArrayList::getItem(int position)
{
    if(position < 0 || position >= length) return NULL_VALUE ;
    return list[position] ;
}

int ArrayList::getLength()
{

    return length ;
}

bool ArrayList::empty()
{
    if(length==0)return true;
    else return false;
}

void ArrayList::printList()
{
    int i;
    for(i=0; i<length; i++)
        printf("%d ", list[i]);
    printf("Current size: %d, current length: %d\n", listMaxSize, length);
}

ArrayList::~ArrayList()
{
    if(list) delete [] list;
    list = 0 ;
}

//******************ArrayList class ends here*************************

//******************Graph class starts here**************************

int c=0;

//struct Edge
//{
//    int u,v,w;
//};


class Graph
{
public:
    int nVertices,nEdges ;
    bool directed;
    ArrayList *adjList ;
    //Edge *edge;
    int **weight;
    //define other variables required for bfs such as color, parent, and dist
    //you must use pointers and dynamic allocation
    int *color;
    int *parent;
    int *dist;
    int *startTime;
    int *endTime;
    // for distance dijsktra

    Graph(bool dir = false);
    ~Graph();
    void setVerticesAndEdges(int n,int m);
    void addEdge(int u, int v,int w,int f);
    void removeEdge(int u, int v);
    bool isEdge(int u, int v);
    int getDegree(int u);
    bool hasCommonAdjacent(int u, int v);
    int getDist(int u, int v);
    void printGraph();
    void bfs(int source); //will run bfs in the graph
    void dfs_visit(int u); //will run dfs in the graph
    void dfs();
    void dijkstra(int source);
};


Graph::Graph(bool dir)
{
    nVertices = 0 ;
    nEdges = 0 ;
    adjList = 0 ;
    directed = dir ; //set direction of the graph
    //define other variables to be initialized
    color=0;
    dist=0;
    parent=0;
    startTime=0;
    endTime=0;
    //edge=0;
    weight=0;
    c=0;
}

void Graph::setVerticesAndEdges(int n,int m)
{
    this->nVertices = n ;
    this->nEdges = m;
    //delete previous list
    if(adjList!=0) delete[] adjList ;
    if(color!=0) delete[] color;
    if(parent!=0) delete[] parent;
    if(dist!=0) delete[] dist;
    if(startTime!=0) delete[] startTime;
    if(endTime!=0) delete[] endTime;
    //if(edge!=0)
        //delete[] edge;
    if(weight)
        delete[] weight;
    // new
    adjList = new ArrayList[nVertices+1] ;
    color = new int[nVertices+1];
    parent = new int[nVertices+1];
    dist= new int[nVertices+1];
    //startTime = new int[nVertices];
    //endTime = new int[nVertices];
    //
    //edge= new Edge[nEdges];
    weight = new int*[nVertices+1];
    for(int i=0; i<(nVertices+1); i++)
        weight[i] = new int[nVertices+1];
    //memset
    for(int i=0; i<nVertices; i++)
    {
        for(int j=0; j<nVertices; j++)
        {
            weight[i+1][j+1]=INT_MAX;
        }
    }
    for(int i=1; i<=nVertices; i++)
        dist[i]=INF;

}



void Graph::addEdge(int u,int v,int w,int f)
{
    if(u<1 || v<1 || u>nVertices || v>nVertices) return; //vertex out of range
    adjList[u].insertItem(v) ;
    if(!directed) adjList[v].insertItem(u);
//    edge[c].u=u;
//    edge[c].v=v;
//    edge[c].w=w;
//    c++;
//    if(f)
//        nEdges++;
    // weight mapping
    if(directed)
    {
        if(w<weight[u][v])
            weight[u][v]=w;
    }
    else
    {
        if(w<weight[u][v])
            weight[u][v]=w;
        if(w<weight[v][u])
            weight[v][u]=w;
    }

}

void Graph::removeEdge(int u, int v)
{
    //write this function
    // if(isEdge(u,v))
    {
        if(!directed)
        {
            adjList[u].removeItem(v);
            adjList[v].removeItem(u);
        }
        else
        {
            adjList[u].removeItem(v);
        }
//        REP(i,c)
//        {
//            if(edge[i].u==u && edge[i].v==v)
//            {
//                //cout << edge[i].u << " " << edge[i].u << " " << i <<endl;
//                edge[i].u=edge[c-1].u;
//                edge[i].v=edge[c-1].v;
//                edge[i].w=edge[c-1].w;
//                break;
//            }
//        }
//        c--;
        nEdges--;
    }
}

bool Graph::isEdge(int u, int v)
{
    //returns true if (u,v) is an edge, otherwise should return false
    if(u<1 || v<1 || u>nVertices || v>nVertices)
        return false;
    if(adjList[u].searchItem(v)!=NULL_VALUE || adjList[v].searchItem(u)!=NULL_VALUE)
        return true;
    else
        return false;
}

int Graph::getDegree(int u)
{
    //returns the degree of vertex u
    if(u<1 || u>nVertices)
        return -1;
    if(!directed)
        return adjList[u].getLength();
    return -1;
}


void Graph::bfs(int source)
{
    if(source<1 || source>nVertices)
        return;
    //complete this function
    //initialize BFS variables
    for(int i=1; i<=nVertices; i++)
    {
        color[i] = WHITE ;
        parent[i] = -1 ;
        dist[i] = INF ;
    }
    color[source] = GREY;
    dist[source] = 0;
    parent[source] = 0;
    Queue q ;
    q.enqueue(source) ;
    while(!q.empty())
    {
        int u = q.dequeue();
        for(int i=0; i<adjList[u].getLength(); i++)
        {
            if(color[adjList[u].getItem(i)]==WHITE)
            {
                color[adjList[u].getItem(i)]=GREY;
                dist[adjList[u].getItem(i)]=dist[u]+1;
                parent[adjList[u].getItem(i)]=u;
                q.enqueue(adjList[u].getItem(i));
            }
        }
        color[u]=BLACK;
    }
}


int Graph::getDist(int u, int v)
{
    //returns the shortest path distance from u to v
    //must call bfs using u as the source vertex, then use distance array to find the distance
    bfs(u);
    return dist[v] ;
}

void Graph::printGraph()
{
    printf("\nNumber of vertices: %d, Number of edges: %d\n", nVertices, nEdges);
    for(int i=1; i<=nVertices; i++)
    {
        printf("%d:", i);
        for(int j=0; j<adjList[i].getLength(); j++)
        {
            printf(" %d", adjList[i].getItem(j));
        }
        printf("\n");
    }
}

Graph::~Graph()
{
    //write your destructor here
    if(adjList!=0)
        delete[] adjList ; //delete previous list
    if(color!=0)
        delete[] color;
    if(parent!=0)
        delete[] parent;
    if(dist!=0)
        delete[] dist;
    if(startTime!=0)
        delete[] startTime;
    if(endTime!=0)
        delete[] endTime;
//    if(edge!=0)
        //delete[] edge;
    if(weight!=0)
    {
        for(int i=0; i<(nVertices+1); i++)
        {
            if(weight[i]!=0)
                delete[] weight[i];
        }
        delete[] weight;
    }
    //
    adjList = 0;
    color = 0;
    parent = 0;
    dist = 0;
    startTime = 0;
    endTime = 0;
//    edge=0;
    weight=0;
}

void Graph::dijkstra(int source)
{
    MinHeap heap;
    for(int i=1; i<=nVertices; i++)
    {
        heap.insertItem(i,INF);
    }
    //heap.printHeap();
    heap.updateKey(source,0);
    dist[source]=0;
    parent[source]=0;
    while(!heap.Empty())
    {
        HeapItem u=heap.removeMin();
        for(int i=0; i<adjList[u.data].getLength(); i++)
        {
            if((dist[adjList[u.data].getItem(i)])>(dist[u.data]+weight[u.data][adjList[u.data].getItem(i)]))
            {
                if(heap.contains(adjList[u.data].getItem(i)))
                {
                    dist[adjList[u.data].getItem(i)] = dist[u.data] + weight[u.data][adjList[u.data].getItem(i)];
                    heap.updateKey(adjList[u.data].getItem(i),dist[adjList[u.data].getItem(i)]);
                    parent[adjList[u.data].getItem(i)]=u.data;
                }
            }
        }
    }
}


int main()
{
    int n,m,u,v,w;
    //int cs;
    //cin >> cs;
    Graph g(true);
   // REP(css,cs)
   // {

        freopen("input.txt","r",stdin);
        //printf("Enter number of vertices: ");
        cin >> n;
        //printf("Enter number of edges: ");
        cin >> m;
        g.setVerticesAndEdges(n,m);
        REP(i,m)
        {
            cin >> u >> v >> w;
            g.addEdge(u,v,w,0);
        }
        int source;
        cin >> source;
        cout << "The source? " << source << endl;
        cout << endl;
        g.dijkstra(source);
       //  cout << "   V      v.d     v.p   "<< endl;
        for(int i=0; i<g.nVertices; i++)
        {
            cout << "   " << i+1 <<  "   " << g.dist[i+1] << "   "  << g.parent[i+1] << endl;
        }
        cout << endl;
   // }
}

/*


*/

