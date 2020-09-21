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
#include <ctime>
#include <sstream>

using namespace std;

#define FOR(i, a, b) for (int i = a; i <= b; i++)
#define REP(i, n) for (int i = 0; i < n; i++)
#define ll long long
#define ull unsigned long long
#define inf 1e9
#define sf(a) scanf("%d",&a);
#define sf2(a, b) scanf("%d %d",&a,&b);
#define sf3(a, b, c) scanf("%d %d %d",&a,&b,&c);
#define pf printf
#define mset(a, b) memset(a,b,sizeof(a))
#define itf it->first
#define its it->second

typedef struct node Node;

int N, M;
Node *root;

struct node
{
public:
    node *parent;
    node *left;
    node *right;
    set<int> uni;
    vector<set<int> > remset;
    int rem;
    double bound;
    int picked;
};

class comparator
{
public:
    int operator()(const Node *a, const Node *b)
    {
        return a->bound > b->bound;
    }
};

int main()
{
    stringstream stringStream;
    string temp;
    getline(cin, temp);
    stringStream << temp;
    stringStream >> N;
    stringStream >> M;
    stringStream.clear();
    set<int> subSet[M];
    //root node
    Node *tempNode = new Node();
    tempNode->parent = nullptr;
    tempNode->left = nullptr;
    tempNode->right = nullptr;
    tempNode->bound = inf;
    tempNode->picked = 0;
    tempNode->rem = M;
    FOR(i, 1, N)
    {
        tempNode->uni.insert(i);
    }
    REP(k, M)
    {
        getline(cin, temp);
        stringStream << temp;
        set<int> tempSet;
        while (!stringStream.eof())
        {
            int tempInt;
            stringStream >> tempInt;
            tempSet.insert(tempInt);
        }
        tempNode->remset.push_back(tempSet);
        subSet[k] = tempSet;
        stringStream.clear();
    }
    root = tempNode;
    // root end
    priority_queue<Node *, vector<Node *>, comparator> minHeap;
    minHeap.push(root);
    int ans=inf;
    Node *ansNode;
    //
    while (!minHeap.empty())
    {
        //poping the node
        Node *tempNode = minHeap.top();
        minHeap.pop();
        if(tempNode->uni.empty())
        {
            if(tempNode->picked<ans)
            {
                ans=tempNode->picked,ansNode=tempNode;
            }
            continue;
        }
        if(tempNode->rem==0 || tempNode->bound>ans)
            continue;
        int setIndex = 0;
        Node *cur = tempNode;
        while (cur != root)
        {
            setIndex++;
            cur = cur->parent;
        }
        //left
        Node *leftNode = new Node();
        leftNode->parent = tempNode;
        tempNode->left = leftNode;
        leftNode->left = nullptr;
        leftNode->right = nullptr;
        leftNode->rem = leftNode->parent->rem - 1;
        leftNode->picked = leftNode->parent->picked + 1;
        // update universal set
        set<int> tempSet = tempNode->uni;
        set<int>::iterator itset;
        for (itset = subSet[setIndex].begin(); itset != subSet[setIndex].end(); itset++)
        {
            tempSet.erase(*itset);
        }
        leftNode->uni = tempSet;
        //updating the subsets
        for (int i = 1; i < (int) tempNode->remset.size(); i++)
        {
            tempSet = tempNode->remset.at(i);
            for (itset = subSet[setIndex].begin(); itset != subSet[setIndex].end(); itset++)
            {
                tempSet.erase(*itset);
            }
            leftNode->remset.push_back(tempSet);
        }
        // max cardiality
        int maxCar = -inf;
        for (int i = 0; i < (int) leftNode->remset.size(); i++)
        {
            if ((int) leftNode->remset.at(i).size() > maxCar)
            {
                maxCar = leftNode->remset.at(i).size();
            }
        }
        //calculating the bound
        // bound  = total picked items + no of elements in the u set/max cardiality of remaining sets;
        if (maxCar == -inf && leftNode->uni.size() != 0)
        {
            leftNode->bound = inf;
        }
        else
        {
            leftNode->bound = (leftNode->picked) + (leftNode->uni.size() * 1.00) / maxCar;
        }
        // adding the node to heap
        minHeap.push(leftNode);
        //right
        Node *rightNode = new Node();
        rightNode->parent = tempNode;
        tempNode->right = rightNode;
        rightNode->left = nullptr;
        rightNode->right = nullptr;
        rightNode->picked = rightNode->parent->picked;
        rightNode->rem = rightNode->parent->rem - 1;
        //same u set
        rightNode->uni = tempNode->uni;
        // updating subsets
        for (int i = 1; i < (int) tempNode->remset.size(); i++)
        {
            rightNode->remset.push_back(tempNode->remset.at(i));
        }
        // max cardiality
        maxCar = -inf;
        for (int i = 0; i < (int) rightNode->remset.size(); i++)
        {
            if ((int) rightNode->remset.at(i).size() > maxCar)
            {
                maxCar = rightNode->remset.at(i).size();
            }
        }
        // bound
        if (maxCar == -inf && rightNode->uni.size() != 0)
        {
            rightNode->bound = inf;
        }
        else
        {
            rightNode->bound = (rightNode->picked) + (rightNode->uni.size() * 1.00) / maxCar;
        }
        // adding the node to heap
        minHeap.push(rightNode);
    }
    vector<int> ansSets;
    while (ansNode != root)
    {
        if (ansNode->parent->left == ansNode)
        {
            ansSets.push_back(M - ansNode->rem - 1);
        }
        ansNode = ansNode->parent;
    }
    cout << ans << endl;
    vector<int>::reverse_iterator itv;
    set<int>::iterator itset;
    for (itv = ansSets.rbegin(); itv != ansSets.rend(); itv++)
    {
        for (itset = subSet[*itv].begin(); itset != subSet[*itv].end(); itset++)
        {
            cout << *itset << " ";
        }
        cout << endl;
    }

}


/*
5 4
1 2 3
2 4
3 4
4 5
*/


/*
14 5
1 2 3 4 5 6 7 8
1 3 5 7 9 11 13
13 14
2 4 6 8 10 12 14
9 10 11 12
*/
