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

#define BLACK 1
#define RED 2

typedef struct node Node;

struct node
{
    int color;
    int key;
    node *left;
    node *right;
    node *parent;
    bool isNul;

    node()
    {
        key = 0;
        color = BLACK;
        isNul = false;
        left = right = parent = NULL;
    }
};


class RedBlackTree
{
public:
    Node *root;

    RedBlackTree()
    {
        root = new Node();
        root->isNul = true;
    }


    void LeftRotate(Node *x)
    {
        Node *y = x->right;
        x->right = y->left;
        if (y->left->isNul == false)
        {
            y->left->parent = x;
        }
        y->parent = x->parent;
        if (x->parent->isNul == true)
        {
            root = y;
        }
        else if (x == x->parent->left)
        {
            x->parent->left = y;
        }
        else
        {
            x->parent->right = y;
        }
        y->left = x;
        x->parent = y;
    }

    void RightRotate(Node *y)
    {
        Node *x = y->left;
        y->left = x->right;
        if (x->right->isNul == false)
        {
            x->right->parent = y;
        }
        x->parent = y->parent;
        x->right = y;
        if (y->parent->isNul == true)
        {
            root = x;
        }
        else if (y == y->parent->left)
        {
            y->parent->left = x;
        }
        else
        {
            y->parent->right = x;
        }
        y->parent = x;
    }

    void insert(int key)
    {
        Node *temp, *parent, *z;
        parent = new Node();
        parent->isNul = true;
        temp = root;
        while (temp->isNul == false)
        {
            parent = temp;
            if (key < temp->key)
            {
                temp = temp->left;
            }
            else
            {
                temp = temp->right;
            }
        }
        if (parent->isNul)
        {
            z = new Node;
            z->key = key;
            z->color = BLACK;
            Node *nil = new Node();
            nil->isNul = true;
            z->parent = z->left = z->right = nil;
            root = z;
        }
        else
        {
            z = new Node();
            z->key = key;
            z->color = RED;
            z->parent = parent;
            Node *nil = new Node();
            nil->isNul = true;
            z->left = z->right = nil;
            if (z->key < parent->key)
            {
                parent->left = z;
            }
            else
            {
                parent->right = z;
            }
        }
        Node *uncle;
        while (z->parent->color == RED)
        {
            if (z->parent == z->parent->parent->left)
            {
                uncle = z->parent->parent->right;
                if (uncle->color == RED) //Case 1
                {
                    z->parent->color = BLACK;
                    uncle->color = BLACK;
                    z->parent->parent->color = RED;
                    z = z->parent->parent;
                }
                else
                {
                    if (z == z->parent->right)   // Case 2
                    {
                        z = z->parent;
                        LeftRotate(z);
                    }
                    z->parent->color = BLACK;   // Case 3
                    z->parent->parent->color = RED;
                    RightRotate(z->parent->parent);
                }
            }
            else
            {
                uncle = z->parent->parent->left;
                if (uncle->color == RED) //Case 1
                {
                    z->parent->color = BLACK;
                    uncle->color = BLACK;
                    z->parent->parent->color = RED;
                    z = z->parent->parent;
                }
                else
                {
                    if (z == z->parent->left)  // Case 2
                    {
                        z = z->parent;
                        RightRotate(z);
                    }
                    z->parent->color = BLACK;   // Case 3
                    z->parent->parent->color = RED;
                    LeftRotate(z->parent->parent);
                }
            }
            root->color = BLACK;
        }
    }

    void Transplant(Node *destination, Node *source)
    {
        if (destination->parent->isNul)
        {
            root = source;
        }
        else if (destination == destination->parent->left)
        {
            destination->parent->left = source;
        }
        else
        {
            destination->parent->right = source;
        }
        if (source)
        {
            source->parent = destination->parent;
        }
    }

    Node *Minimum(Node *node)
    {
        while (node->left->isNul == false)
        {
            node = node->left;
        }
        return node;
    }

    Node *Maximum(Node *node)
    {
        while (node->right->isNul == false)
        {
            node = node->right;
        }
        return node;
    }

    void deleteFix(Node *x)
    {
        while (x != root && x->color == BLACK)
        {
            if (x == x->parent->left)
            {
                Node *s = x->parent->right;
                if (s->color == RED)
                {
                    s->color = BLACK;
                    x->parent->color = RED;
                    LeftRotate(x->parent);
                    s = x->parent->right;
                }
                if (s->right->color == BLACK && s->left->color == BLACK)
                {
                    s->color = RED;
                    x = x->parent;
                    continue;
                }
                else if (s->right->color == BLACK)
                {
                    s->left->color = BLACK;
                    s->color = RED;
                    RightRotate(s);
                    s = x->parent->right;
                }
                s->color = x->parent->color;
                x->parent->color = BLACK;
                s->right->color = BLACK;
                LeftRotate(x->parent);
                x = root;
            }
            else
            {
                Node *s = x->parent->left;
                if (s->color == RED)
                {
                    s->color = BLACK;
                    x->parent->color = RED;
                    RightRotate(x->parent);
                    s = x->parent->left;
                }
                if (s->left->color == BLACK && s->right->color == BLACK)
                {
                    s->color = RED;
                    x = x->parent;
                    continue;
                }
                else if (s->left->color == BLACK)
                {
                    s->right->color = BLACK;
                    s->color = RED;
                    LeftRotate(s);
                    s = x->parent->left;
                }
                s->color = x->parent->color;
                x->parent->color = BLACK;
                s->left->color = BLACK;
                RightRotate(x->parent);
                x = root;
            }
            x->color = BLACK;
        }

    }

    Node *searchNode(int key)
    {
        Node *node = root;
        while (node->isNul == false)
        {
            if (node->key > key)
            {
                node = node->left;
            }
            else if (node->key < key)
            {
                node = node->right;
            }
            else
            {
                return node;
            }
        }
        Node *temp = new Node();
        temp->isNul = true;
        return temp;
    }

    void Delete(Node *z)
    {
        int original = z->color;
        Node *y, *x;
        y = z;
        if (z->left->isNul)
        {
            x = z->right;
            Transplant(z, z->right);
        }
        else if (z->right->isNul)
        {
            x = z->left;
            Transplant(z, z->left);
        }
        else
        {
            node *y = z;
            z = Minimum(y->right);
            original = z->color;
            swap(z->key, y->key);
            x = z->right;
            Transplant(z, x);
        }
        delete z;
        if (original == BLACK)
            deleteFix(x);
    }

    int nodeHeight(Node *node)
    {
        if (node->isNul)
            return -1;
        int l, r;
        l = nodeHeight(node->left);
        r = nodeHeight(node->right);
        if (l > r) return l + 1;
        else return r + 1;
    }

    int treeHeight()
    {
        return nodeHeight(root);
    }

    void printInOrder(Node *node, int height)
    {
        if (node->isNul) return;
        //print left sub-tree
        printInOrder(node->left, height - 1);
        //print item
        for (int i = 0; i < height; i++)printf("   ");
        if (node->color == RED)
            printf("%03dR\n", node->key);
        else
            printf("%03dB\n", node->key);
        //print right sub-tree
        printInOrder(node->right, height - 1);
    }

    void printRoot()
    {
        printInOrder(root, treeHeight());
    }

};

int main()
{
    RedBlackTree rbt;
    int choice;
    while (true)
    {
        cout << "1. Insert   2.Print Tree   3.Delete  4.Search  5.Minimum   6.Maximum  7.Exit" << endl;
        cin >> choice;
        if (choice == 1)
        {
            int key;
            cin >> key;
            rbt.insert(key);
        }
        else if (choice == 2)
        {
            rbt.printRoot();
        }
        else if (choice == 3)
        {
            int key;
            cin >> key;
            Node *temp = rbt.searchNode(key);
            if (temp->isNul)
                continue;
            else
                rbt.Delete(temp);
        }
        else if (choice == 4)
        {
            int key;
            cin >> key;
            Node *temp = rbt.searchNode(key);
            if (temp->isNul)
                cout << "Not found" << endl;
            else
                cout << "Found" << endl;
        }
        else if (choice == 5)
        {
            Node *temp = rbt.Minimum(rbt.root);
            if (temp->isNul)
                continue;
            else
                cout << temp->key << endl;
        }
        else if (choice == 6)
        {
            Node *temp = rbt.Maximum(rbt.root);
            if (temp->isNul)
                continue;
            else
                cout << temp->key << endl;
        }
        else
        {
            break;
        }
    }
}


// 1 10 1 20 1 5 1 3 1 9 1 15 1 20 1 12


