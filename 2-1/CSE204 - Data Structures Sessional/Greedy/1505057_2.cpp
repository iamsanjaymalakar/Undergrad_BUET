#include<stdio.h>
#include<iostream>

using namespace std;

#define MAX_HEAP_SIZE 100000

#define MAXREAL 999999.0

class HeapItem
{
public:
    int data; //actual data that is stored
    float key; //key value of the data, heap is constructed based on key
    bool leaf;
    HeapItem* left;
    HeapItem* right;
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
    void insertItem(int data, int key,bool leaf)
    {
        //Write your codes here
        if(data>=0)
        {
            heapLength++;
            A[heapLength].data=data;
            A[heapLength].key=key;
            A[heapLength].leaf=leaf;
            A[heapLength].left=0;
            A[heapLength].right=0;
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
            temp=A[1];
            A[1]=A[heapLength];
            heapLength--;
            heapify(1);
            return temp;
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
                A[i]=temp;
            }
        }
        buHeapify(i/2);
    }

    void printHeap()
    {
        printf("Heap length: %d\n", heapLength);
        for(int i=1; i<=heapLength; i++)
        {
            printf("(%d,%.2f) ", A[i].data, A[i].key);
        }
        printf("\n");
    }

    bool Empty()
    {
        if(heapLength==0)return true;
        else return false;
    }

    // additional functions
    void insertItem(HeapItem item)
    {
        heapLength++;
        A[heapLength]=item;
        buHeapify(heapLength);
    }

    int getLength()
    {
        return heapLength;
    }

    // creates pointer from item
    HeapItem* createPointer(HeapItem item)
    {
        HeapItem* temp = new HeapItem;
        temp->data=item.data;
        temp->key=item.key;
        temp->leaf=item.leaf;
        temp->right=item.right;
        temp->left=item.left;
        return temp;
    }
};

char ch[26],code[26][26];
int j;
char temp[26];

void printCodes(HeapItem *item,int level)
{
    if(item->leaf)
    {
        ch[j]=item->data;
        for(int i=0;i<level;i++)
        {
            code[j][i]=temp[i];
        }
        if(level==0)
        {
            code[j][level]='0';
            level++;
        }
        code[j][level]='\0';
        j++;
        return;
    }
    temp[level]='0';
    printCodes(item->left,level+1);
    temp[level]='1';
    printCodes(item->right,level+1);
}

void huffman(char c[],int f[],int n)
{
    MinHeap heap;
    for(int i=0;i<n;i++)
        heap.insertItem(c[i],f[i],true);
    while(heap.getLength()>1)
    {
        HeapItem a=heap.removeMin();
        HeapItem b=heap.removeMin();
        HeapItem temp;
        temp.data='$';
        temp.key=a.key+b.key;
        temp.leaf=false;
        temp.left=heap.createPointer(a);
        temp.right=heap.createPointer(b);
        heap.insertItem(temp);
    }
    printCodes(heap.createPointer(heap.removeMin()),0);
}

int main()
{
    int n;
    cin >> n;
    char c[n];
    int f[n];
    for(int i=0;i<n;i++)
    {
            cin >> c[i] >> f[i];
    }
    huffman(c,f,n);
    for(int i=0;i<j;i++)
    {
        cout << ch[i] << " : " << code[i] << endl;
    }
    return 0;
}

// 6 a 45 b 13 c 12 d 16 e 9 f 5
