#include<stdio.h>
#include<stdlib.h>
#define NULL_VALUE -999999
#define INFINITY 999999
#define WHITE 1
#define GREY 2
#define BLACK 3

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
    if(list) delete [] list;
    list = 0 ;
}

//******************ArrayList class ends here*************************

//******************Graph class starts here**************************
class Graph
{
    int nVertices, nEdges ;
    bool directed ;
    ArrayList  * adjList ;
    //define other variables required for bfs such as color, parent, and dist
    //you must use pointers and dynamic allocation
    int *color;
    int *parent;
    int *dist;
    int *startTime;
    int *endTime;
public:
    Graph(bool dir = false);
    ~Graph();
    void setnVertices(int n);
    void addEdge(int u, int v);
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

void Graph::setnVertices(int n)
{
    this->nVertices = n ;
    if(adjList!=0) delete[] adjList ; //delete previous list
    if(color!=0) delete[] color;
    if(parent!=0) delete[] parent;
    if(dist!=0) delete[] dist;
    if(startTime!=0) delete[] startTime;
    if(endTime!=0) delete[] endTime;
    adjList = new ArrayList[nVertices] ;
    //
    color = new int[nVertices];
    parent = new int[nVertices];
    dist= new int[nVertices];
    startTime = new int[nVertices];
    endTime = new int[nVertices];
}

void Graph::addEdge(int u, int v)
{
    if(u<0 || v<0 || u>=nVertices || v>=nVertices) return; //vertex out of range
    this->nEdges++ ;
    adjList[u].insertItem(v) ;
    if(!directed) adjList[v].insertItem(u) ;
}

void Graph::removeEdge(int u, int v)
{
    //write this function
    if(isEdge(u,v))
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
        nEdges--;
    }
}

bool Graph::isEdge(int u, int v)
{
    //returns true if (u,v) is an edge, otherwise should return false
    if(u<0 || v<0 || u>=nVertices || v>=nVertices)
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
    {
        return adjList[u].getLength();
    }
}

bool Graph::hasCommonAdjacent(int u, int v)
{
    //returns true if vertices u and v have common adjacent vertices
    if(u>=0 && v>=0 && u<nVertices && v<nVertices)
    {
        for(int i=0; i<adjList[u].getLength(); i++)
            for(int j=0; j<adjList[v].getLength(); j++)
            {
                if(adjList[u].getItem(i)==adjList[v].getItem(j))
                    return true;
            }
    }
    return false;
}

void Graph::bfs(int source)
{
    if(source<0 || source>=nVertices)
        return;
    //complete this function
    //initialize BFS variables
    for(int i=0; i<nVertices; i++)
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

void Graph::dfs()
{
    //write this function
    for(int i=0; i<nVertices; i++)
    {
        color[i] = WHITE ;
        parent[i] = -1 ;
    }
    time=0;
    for(int i=0; i<nVertices; i++)
    {
        if(color[i]==WHITE)
        {
            dfs_visit(i);
        }
    }
    for(int i=0; i<nVertices; i++)
    {
        printf("%d  : %d//%d\n",i,startTime[i],endTime[i]);
    }
}

void Graph::dfs_visit(int u)
{
    time=time+1;
    startTime[u] = time;
    color[u] = GREY;
    for(int i=0; i<adjList[u].getLength(); i++)
    {
        if(color[adjList[u].getItem(i)]==WHITE)
        {
            parent[adjList[u].getItem(i)]=u;
            dfs_visit(adjList[u].getItem(i));
        }
    }
    color[u]=BLACK;
    time=time+1;
    endTime[u]=time;
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
    for(int i=0; i<nVertices; i++)
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
	
	//
	adjList = 0;
	color = 0;
	parent = 0;
	dist = 0;
	startTime = 0;
	endTime = 0;
}


//**********************Graph class ends here******************************


//******main function to test your code*************************
int main(void)
{
    int n;
    Graph g;
    printf("Enter number of vertices: ");
    scanf("%d", &n);
    g.setnVertices(n);
    while(1)
    {
        printf("1. Add edge. 2. Remove edge. 3. isEdge. 4. Degree. \n");
        printf("5. Common adjacent. 6.Shortest path. 7.DFS. 8. Print Graph  9. Exit.\n");

        int ch;
        scanf("%d",&ch);
        if(ch==1)
        {
            int u, v;
            scanf("%d%d", &u, &v);
            g.addEdge(u, v);
        }
        else if(ch==2)
        {
            printf("Enter two vertices of edge :\n");
            int u,v;
            scanf("%d %d",&u,&v);
            // here check edge or not
            if(g.isEdge(u,v))
                g.removeEdge(u,v);
        }
        else if(ch==3)
        {
            printf("Enter two vertices of edge :\n");
            int u,v;
            scanf("%d %d",&u,&v);
            if(g.isEdge(u,v))
                printf("Yes.\n");
            else
                printf("No.\n");

        }
        else if(ch==4)
        {
            int u;
            scanf("%d",&u);
            printf("Degree of %d : %d\n",u,g.getDegree(u));
        }
        else if(ch==5)
        {
            printf("Enter two vertices :\n");
            int u,v;
            scanf("%d %d",&u,&v);
            if(g.hasCommonAdjacent(u,v))
                printf("Yes.\n");
            else
                printf("No.\n");
        }
        else if(ch==6)
        {
            printf("Enter two vertices :\n");
            int u,v;
            scanf("%d %d",&u,&v);
            printf("Distance from %d to %d : %d\n",u,v,g.getDist(u,v));
        }
        else if(ch==7)
        {
            g.dfs();
        }
        else if(ch==8)
        {
            g.printGraph();
        }
        else if(ch==9)
        {
            break;
        }
    }

}

//4 1 0 1 1 0 2 1 1 3 1 2 3 1 0 3
