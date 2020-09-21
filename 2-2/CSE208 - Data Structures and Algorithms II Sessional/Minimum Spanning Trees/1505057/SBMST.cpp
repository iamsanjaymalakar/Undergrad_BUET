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
#define INFINITY 999999
#define WHITE 1
#define GREY 2
#define BLACK 3

const int mn = 1000001;

#define FOR(i, a, b) for (int i = a; i <= b; i++)
#define REP(i, n) for (int i = 0; i < n; i++)
#define ll long long
#define ull unsigned long long


//global variable time
int time;

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
    //if(list) delete [] list;
    //list = 0 ;
}

//******************ArrayList class ends here*************************

//******************Graph class starts here**************************

struct Edge
{
    int u,v,w;
};


class Graph
{
public:
    int nVertices, nEdges ;
    bool directed ;
    ArrayList  * adjList ;
    Edge *edge;
    //define other variables required for bfs such as color, parent, and dist
    //you must use pointers and dynamic allocation
    int *color;
    int *parent;
    int *dist;
    int *startTime;
    int *endTime;

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
    //if(startTime!=0) delete[] startTime;
    //if(endTime!=0) delete[] endTime;
    // new
    adjList = new ArrayList[nVertices+1] ;
    color = new int[nVertices+1];
    parent = new int[nVertices+1];
    dist= new int[nVertices+1];
    //startTime = new int[nVertices];
    //endTime = new int[nVertices];
    //
    edge= (struct Edge*)malloc(nEdges*sizeof(struct Edge));
}

int c=0;

void Graph::addEdge(int u,int v,int w,int f)
{
    if(u<1 || v<1 || u>nVertices || v>nVertices) return; //vertex out of range
    adjList[u].insertItem(v) ;
    if(!directed) adjList[v].insertItem(u);
    edge[c].u=u;
    edge[c].v=v;
    edge[c].w=w;
    c++;
    if(f)
        nEdges++;
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
        REP(i,c)
        {
            if(edge[i].u==u && edge[i].v==v)
            {
                //cout << edge[i].u << " " << edge[i].u << " " << i <<endl;
                edge[i].u=edge[c-1].u;
                edge[i].v=edge[c-1].v;
                edge[i].w=edge[c-1].w;
                break;
            }
        }
        c--;
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
    if(u<0 || u>=nVertices)
        return -1;
    if(!directed)
        return adjList[u].getLength();
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
        dist[i] = INFINITY ;
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
    //if(startTime!=0)
    //  delete[] startTime;
    //if(endTime!=0)
    //  delete[] endTime;

    //
    adjList = 0;
    color = 0;
    parent = 0;
    dist = 0;
    startTime = 0;
    endTime = 0;
}


//**********************Graph class ends here******************************


//**********************Disjoint Class starts here*************************

struct Node
{
    int data;
    int parent;
    int rank;
};

void makeSet(struct Node *node,int data)
{
    // node[data].data = data;
    node[data].parent = data;
    node[data].rank = 0;
}

int findSet(struct Node *node, int data)
{
    if (node[data].parent != data)
        node[data].parent = findSet(node,node[data].parent);
    return node[data].parent;
}

void Union(struct Node *node,int u,int v)
{
    int uRoot = findSet(node,u);
    int vRoot = findSet(node,v);
    if(node[uRoot].rank == node[vRoot].rank)
    {
        node[vRoot].parent = uRoot;
        node[uRoot].rank++;
    }
    else if(node[uRoot].rank < node[vRoot].rank)
        node[uRoot].parent = vRoot;
    else
        node[vRoot].parent = uRoot;
}

// comparator
int cmp(struct Edge a,struct Edge b)
{
    return a.w < b.w;
}

//kruskal
int ca=0;

Edge* kruskal(Graph &G)
{
    Edge *res = new Edge[G.nEdges];
    struct Node *node =(struct Node*)malloc(G.nVertices*sizeof(struct Node));
    //make set
    for(int i=1; i<=G.nVertices; i++)
    {
        makeSet(node,i);
    }
    // sort
    sort(G.edge,G.edge+G.nEdges,cmp);
    int i=0;
    ca=0;
    while (ca<(G.nVertices-1))
    {
        if(i>=G.nEdges)
            break;
        struct Edge min= G.edge[i++];
        int ru = findSet(node,min.u);
        int rv = findSet(node,min.v);
        if (ru!=rv)
        {
            res[ca++]=min;
            Union(node,ru,rv);
        }
    }
    return res;
}
//Graph g;
//Edge * SBMST(Graph G)
//{
//    // find mst
//    Edge *mst = kruskal(&G);
//    int mintotal=0,temptotal=0;
//    Edge *ans;
//    int cnt=ca;
//    int flag=0;
//    REP(i,cnt)
//    {
//        Edge temp = mst[i];
//        printf("here");
//        G.removeEdge(temp.u,temp.v);
//        printf("here");
//        G.printGraph();
//        G.bfs(1);
//        REP(j,G.nVertices)
//        {
//            cout << G.dist[j+1] << " ";
//        }
//        cout << endl;
//        REP(j,G.nVertices)
//        {
//            if(G.dist[j+1]==INFINITY)
//            {
//                flag=1;
//                break;
//            }
//        }
//        if(flag)
//        {
//            flag=0;
//            break;
//        }
//        Edge *tempmst=kruskal(&G);
//        temptotal=0;
//        for(int i=0; i<ca; i++)
//        {
//            temptotal+=tempmst[i].w;
//        }
//        if(!i)
//        {
//            mintotal=temptotal;
//            ans=tempmst;
//        }
//        if(temptotal<mintotal)
//        {
//            mintotal=temptotal;
//            ans=tempmst;
//        }
//      //  G.addEdge(temp.u,temp.v,temp.w);
//    }
//    return ans;
//
//}


//******main function to test your code*************************
int main()
{
    int n,m,u,v,w;
    Graph g;
    printf("Enter number of vertices: ");
    cin >> n;
    printf("Enter number of edges: ");
    cin >> m;
    g.setVerticesAndEdges(n,m);
    REP(i,m)
    {
        cin >> u >> v >> w;
        g.addEdge(u,v,w,0);
    }
    cout << endl;
    //mst usong cruskal
    struct Edge mst[g.nEdges];
    struct Node *node =(struct Node*)malloc(g.nVertices*sizeof(struct Node));
    //make set
    for(int i=1; i<=g.nVertices; i++)
    {
        makeSet(node,i);
    }
    // sort
    sort(g.edge,g.edge+g.nEdges,cmp);
    int i=0;
    ca=0;
    while (ca<(g.nVertices-1))
    {
        if(i>=g.nEdges)
            break;
        struct Edge min= g.edge[i++];
        int ru = findSet(node,min.u);
        int rv = findSet(node,min.v);
        if (ru!=rv)
        {
            mst[ca++]=min;
            Union(node,ru,rv);
        }
    }
    //
    int cnt=ca;
    struct Edge temp;
    struct Edge tempres[g.nEdges];
    struct Edge *sbmst;
    int sbw;
    int sbc=0;
    cout << ca << endl;
    // loop for sbmst
    REP(k,cnt)
    {
        temp = mst[k];
        g.removeEdge(temp.u,temp.v);

        //make set
        for(int i=1; i<=g.nVertices; i++)
        {
            makeSet(node,i);
        }
        // sort
        sort(g.edge,g.edge+g.nEdges,cmp);

        i=0;
        ca=0;
        while (ca<(g.nVertices-1))
        {
            if(i>=g.nEdges)
                break;
            struct Edge min= g.edge[i++];
            int ru = findSet(node,min.u);
            int rv = findSet(node,min.v);
            if (ru!=rv)
            {
                tempres[ca++]=min;
                Union(node,ru,rv);
            }
        }
        //cout << ca << endl;
        g.addEdge(temp.u,temp.v,temp.w,1);
        // check
        if(ca!=(g.nVertices-1))
        {
            //cout << "forest" << endl;
            continue;
        }
        int tw=0;
        for(int j=0; j<ca; j++)
        {
            tw+=tempres[j].w;
        }
        //cout << tw << endl;
        if(!k)
        {
            sbmst=tempres;
            sbw=tw;
        }
        if(tw<sbw)
        {
            sbw=tw;
            sbmst=tempres;
        }
        sbc=ca;
    }

    for(int m=0;m<sbc;m++)
    {
        cout << sbmst[m].u << " " << sbmst[m].v << endl;
    }
     cout << sbw << endl;
    return 0;
}

// 6 9 1 2 4 1 3 1 1 4 3 2 3 4 2 4 4 3 4 2 3 6 4 4 6 6 5 6 2

