#include<stdio.h>
#include<stdlib.h>
#define NULL_VALUE -999999
#define INFINITY 999999
#define WHITE 1
#define GREY 2
#define BLACK 3

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


//******************Graph class starts here**************************
class Graph
{
    int nVertices, nEdges ;
    bool directed ;
    int ** matrix ; //adjacency matrix to store the graph
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
    matrix = 0 ;
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
    if(color!=0) delete[] color;
    if(parent!=0) delete[] parent;
    if(dist!=0) delete[] dist;
    if(startTime!=0) delete[] startTime;
    if(endTime!=0) delete[] endTime;
    //allocate space for the matrix
    matrix = new int*[nVertices];
    for(int i=0; i<nVertices; i++)
    {
        matrix[i] = new int[nVertices];
        for(int j=0; j<nVertices; j++)
            matrix[i][j] = 0; //initialize the matrix cells to 0
    }
    //
    color = new int[nVertices];
    parent = new int[nVertices];
    dist= new int[nVertices];
    startTime = new int[nVertices];
    endTime = new int[nVertices];
}

void Graph::addEdge(int u, int v)
{
    //write your code here
    if(u<0 || u>=nVertices || v<0 || v>=nVertices) return;
    matrix[u][v] = 1;
    if(!directed) matrix[v][u] = 1;
    nEdges++;
}

void Graph::removeEdge(int u, int v)
{
    //write this function
    if(isEdge(u,v))
    {
        if(!directed)
        {
            matrix[u][v]=0;
            matrix[v][u]=0;
        }
        else
        {
            matrix[u][v]=0;
        }
        nEdges--;
    }
    else
    {
        printf("Edge doesn't exists.\n");
    }
}

bool Graph::isEdge(int u, int v)
{
    //returns true if (u,v) is an edge, otherwise should return false
    if(u<0 || v<0 || u>=nVertices || v>=nVertices)
        return false;
    if(matrix[u][v] || matrix[v][u])
        return true;
    return false;
}

int Graph::getDegree(int u)
{
    //returns the degree of vertex u
    if(u<0 || u>=nVertices)
        return -1;
    if(!directed)
    {
        int s=0;
        for(int i=0; i<nVertices; i++)
        {
            s+=matrix[u][i];
        }
        return s;
    }

}

bool Graph::hasCommonAdjacent(int u, int v)
{
    //returns true if vertices u and v have common adjacent vertices
    if(u>=0 && v>=0 && u<nVertices && v<nVertices)
        {
            for(int i=0; i<nVertices; i++)
            {
                    if(matrix[u][i]==matrix[v][i] && matrix[u][i] )
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
        for(int i=0;i<nVertices; i++)
        {
            if(matrix[u][i])
            {
                if(color[i]==WHITE)
                {
                    color[i]=GREY;
                    dist[i]=dist[u]+1;
                    parent[i]=u;
                    q.enqueue(i);
                }
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
    for(int i=0; i<nVertices; i++)
    {
        if(matrix[u][i])
        {
            if(color[i]==WHITE)
            {
                parent[i]=u;
                dfs_visit(i);
            }
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
    if(u<0 || u>=nVertices || v<0 || v>=nVertices)
        return -1;
    bfs(u);
    return dist[v] ;
}


void Graph::printGraph()
{
    printf("\nNumber of vertices: %d, Number of edges: %d\n", nVertices, nEdges);
    for(int i=0; i<nVertices; i++)
    {
        printf("%d:", i);
        for(int j=0; j<nVertices; j++)
        {
            if(matrix[i][j]==1)
                printf(" %d", j);
        }
        printf("\n");
    }
}

Graph::~Graph()
{
    //write your destructor here
    for(int i=0;i<nVertices;i++)
        delete matrix[i];
    if(matrix)
        delete[] matrix;
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
    matrix=0;
    color=0;
    parent=0;
    startTime=0;
    endTime=0;
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
