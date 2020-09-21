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

using namespace std;

int ccc;

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

#define NULLVALUE -inf

#define tableSize 10000
#define wordSize 10000
#define wordLength 5

typedef struct node Node;
typedef struct headPointer hp;

const char alphabet[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
const int alphabetSize = sizeof(alphabet) - 1;
string word = "AAAAA";


void genWord()
{
    REP(i, wordLength)
    {
        word[i] = alphabet[rand() % alphabetSize];
    }
}

int hashOne(string key)
{
    int hash = 0;
    REP(i, wordLength)
    {
        hash += key[i] * pow(32, i);
    }
    return hash % tableSize;
}

int hashTwo(string key)
{
    int hash = 7;
    for (int i = 0; i < wordLength; i++)
    {
        hash = hash * 31 + key[i];
    }
    return hash % tableSize;
}

int hashThree(string key)
{
    int hash = 0;
    REP(i, wordLength)
    {
        hash += key[i] * pow(64, i);
    }
    return hash % tableSize;
}


struct node
{
    string key;
    int value;
    Node *next;

    node()
    {
        key = "AAAAA";
        value = inf;
        next = NULL;
    }
};

struct headPointer
{
    Node *head;
    int count;

    headPointer()
    {
        count = 0;
        head = NULL;
    }
};

Node *createNode(string key, int value)
{
    Node *temp = (Node *) malloc(sizeof(Node));
    temp->key = key;
    temp->value = value;
    temp->next = NULL;
    return temp;
}

class HashLP
{
public:
    int collision;
    Node *data;
    int value;

    HashLP()
    {
        value = 1;
        collision = 0;
        data = new Node[tableSize];
    }

    void insert(string key, int (*hashFunc)(string))
    {
        if (value > tableSize)
        {
            cout << "Hash memory full !" << endl;
            return;
        }
        int hash = hashFunc(key);
        if (data[hash].value == inf)
        {
            data[hash].key = key;
            data[hash].value = value++;
        }
        else
        {
            if (data[hash].key != key)
            {
                FOR(i, 1, tableSize)
                {
                    hash = (hashFunc(key) + i) % tableSize;
                    if (data[hash].key == key)
                    {
                        collision++;
                        break;
                    }
                    if (data[hash].value == inf)
                    {
                        data[hash].key = key;
                        data[hash].value = value++;
                        break;
                    }
                }
            }
            collision++;
        }
    }

    int remove(string key, int (*hashFunc)(string))
    {
        int hash = search(key, hashFunc);
        if (hash != NULLVALUE)
        {
            data[hash].key = "";
            data[hash].value = inf;
            return hash;
        }
        return NULLVALUE;
    }

    int search(string key, int (*hashFunc)(string))
    {
        int hashP = hashFunc(key), hash;
        if (data[hashP].value != inf)
        {
            REP(i, tableSize)
            {
                hash = (hashP + i) % tableSize;
                if (data[hash].key == key)
                {
                    return hash;
                }
                if (data[hash].value == inf)
                {
                    return NULLVALUE;
                }
            }
        }
        return NULLVALUE;
    }

    int getCollision()
    {
        return collision;
    }

    int getValue()
    {
        return value;
    }

    void printHash()
    {
        REP(i, tableSize)
        {
            cout << i << "  ->  (" << data[i].key << " , " << data[i].value << ")" << endl;
        }
    }
};

class HashSC
{
public:
    int collision;
    hp *data;
    int value;

    HashSC()
    {
        collision = 0;
        value = 1;
        data = new hp[tableSize];
    }

    void insert(string key, int (*hashFunc)(string))
    {
        int hash = hashFunc(key);
        if (data[hash].head == NULL)
        {
            Node *newNode = new Node;
            newNode->value = value++;
            newNode->next = NULL;
            newNode->key = key;
            data[hash].head = newNode;
            data[hash].count++;
            return;
        }
        if (search(key, hashFunc) != NULLVALUE)
            return;
        Node *newNode = new Node;
        newNode->value = value++;
        newNode->next = NULL;
        newNode->key = key;
        newNode->next = data[hash].head;
        data[hash].head = newNode;
        data[hash].count++;
        collision++;
    }

    int remove(string key, int (*hashFunc)(string))
    {
        int hash = search(key, hashFunc);
        if (hash == NULLVALUE)
            return NULLVALUE;
        if (data[hash].head->key == key)
        {
            Node *temp = data[hash].head;
            data[hash].head = data[hash].head->next;
            free(temp);
            data[hash].count--;
            return hash;
        }
        Node *cur = data[hash].head;
        while (cur->next->key != key && cur->next != NULL)
        {
            cur = cur->next;
        }
        Node *temp = cur->next;
        cur->next = cur->next->next;
        free(temp);
        data[hash].count--;
        return hash;
    }

    int search(string key, int (*hashFunc)(string))
    {
        int hash = hashFunc(key);
        if (data[hash].head == NULL)
            return NULLVALUE;
        Node *cur = data[hash].head;
        while (cur != NULL)
        {
            if (cur->key == key)
            {
                return hash;
            }
            cur = cur->next;
        }
        return NULLVALUE;
    }


    int getCollision()
    {
        return collision;
    }

    int getValue()
    {
        return value;
    }

    void printHash()
    {
        Node *cur;
        REP(i, tableSize)
        {
            cout << i << " [" << data[i].count << "] " << " : ";
            cur = data[i].head;
            while (cur != NULL)
            {
                cout << "(" << cur->key << "," << cur->value << ")" << " -> ";
                cur = cur->next;
            }
            cout << endl;
        }
    }
};


int main()
{
    srand(time(NULL));
    string words[wordSize];
    double stime, etime;
    HashLP hashLP[3];
    HashSC hashSC[3];
    REP(i, wordSize)
    {
        genWord();
        hashLP[0].insert(word, hashOne);
        hashLP[1].insert(word, hashTwo);
        hashLP[2].insert(word, hashThree);
        hashSC[0].insert(word, hashOne);
        hashSC[1].insert(word, hashTwo);
        hashSC[2].insert(word, hashThree);
    }
    REP(i, wordSize)
    {
        genWord();
        words[i] = word;
    }
    cout << "Linear probing :" << endl;
    stime = clock();
    REP(i, wordSize)
    {
        hashLP[0].search(words[i], hashOne);
    }
    etime = clock();
    cout << "Hash #1 :  Time-" << (etime - stime) / CLK_TCK << "s     Collision-" << hashLP[0].getCollision() << endl;

    stime = clock();
    REP(i, wordSize)
    {
        hashLP[1].search(words[i], hashTwo);
    }
    etime = clock();
    cout << "Hash #2 :  Time-" << (etime - stime) / CLK_TCK << "s     Collision-" << hashLP[1].getCollision() << endl ;

    stime = clock();
    REP(i, wordSize)
    {
        hashLP[2].search(words[i], hashThree);
    }
    etime = clock();
    cout << "Hash #3 :  Time-" << (etime - stime) / CLK_TCK << "s     Collision-" << hashLP[2].getCollision() << endl << endl;

    cout << "Separate Chaining :" << endl;
    stime = clock();
    REP(i, wordSize)
    {
        hashSC[0].search(words[i], hashOne);
    }
    etime = clock();
    cout << "Hash #1 :  Time-" << (etime - stime) / CLK_TCK << "s     Collision-" << hashSC[0].getCollision() << endl ;

    stime = clock();
    REP(i, wordSize)
    {
        hashSC[1].search(words[i], hashTwo);
    }
    etime = clock();
    cout << "Hash #2 :  Time-" << (etime - stime) / CLK_TCK << "s     Collision-" << hashSC[1].getCollision() << endl ;

    stime = clock();
    REP(i, wordSize)
    {
        hashSC[2].search(words[i], hashThree);
    }
    etime = clock();
    cout << "Hash #3 :  Time-" << (etime - stime) / CLK_TCK << "s     Collision-" << hashSC[2].getCollision() << endl ;
    return 0;
}
