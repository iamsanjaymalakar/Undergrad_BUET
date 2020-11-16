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

int tableSize;

int hashFunction(string key)
{
    int hash = 7;
    REP(i, key.length())
    {
        hash = (hash * 31 + key.at(i)) % tableSize;
    }
    return hash % tableSize;
}


class SymbolInfo
{
private:
    string name, type;
public:
    SymbolInfo *next;

    SymbolInfo()
    {
        name = "";
        type = "";
        next = NULL;
    }

    SymbolInfo(string name, string type)
    {
        this->name = name;
        this->type = type;
        next = NULL;
    }

    string getName()
    {
        return name;
    }

    void setName(string name)
    {
        SymbolInfo::name = name;
    }

    string getType()
    {
        return type;
    }

    void setType(string type)
    {
        SymbolInfo::type = type;
    }
};


class linkedList
{
public:
    SymbolInfo *head, *tail;
    int count;

    linkedList()
    {
        head = NULL;
        tail = NULL;
        count = 0;
    }
};


class ScopeTable
{
public:
    linkedList *data;
    ScopeTable *parentScope;
    int ID;


    ScopeTable(int n)
    {
        data = new linkedList[n];
        parentScope = NULL;
    }

    ~ScopeTable()
    {
        delete parentScope;
        delete[] data;
    }

    bool insert(string name, string type)
    {
        int hash = hashFunction(name);
        if (data[hash].head == NULL)
        {
            SymbolInfo *newNode = new SymbolInfo(name, type);
            data[hash].head = newNode;
            data[hash].tail = newNode;
            cout << " Inserted in ScopeTable# " << ID << " at position " << hash << ", " << data[hash].count++ << endl
                 << endl;
            return true;
        }
        if (search(name, false) != NULL)
        {
            cout << " <" << name << "," << type << ">already exists in current ScopeTable" << endl << endl;
            return false;
        }
        SymbolInfo *newNode = new SymbolInfo(name, type);
        data[hash].tail->next = newNode;
        data[hash].tail = newNode;
        cout << " Inserted in ScopeTable# " << ID << " at position " << hash << ", " << data[hash].count++ << endl
             << endl;
        return true;
    }

    bool remove(string name)
    {
        SymbolInfo *node, *prev;
        int hash = hashFunction(name), pos = 0;
        node = data[hash].head;
        search(name, true);
        if (node == NULL)
        {
            cout << name << " not found" << endl << endl;
            return false;
        }
        while (node != NULL)
        {
            if (node->getName() == name)
                break;
            prev = node;
            node = node->next;
            pos++;
        }
        if (node == NULL)
        {
            cout << name << " not found" << endl << endl;
            return false;
        }
        if (node == data[hash].head && node == data[hash].tail)
        {
            data[hash].head = NULL;
            data[hash].tail = NULL;
            free(node);
        }
        else if (node == data[hash].head)
        {
            data[hash].head = data[hash].head->next;
            free(node);
        }
        else if (node == data[hash].tail)
        {
            prev->next = NULL;
            data[hash].tail = prev;
            free(node);
        }
        else
        {
            prev->next = node->next;
            free(node);
        }
        data[hash].count--;
        cout << "Deleted entry at " << hash << ", " << pos << " from  current ScopeTable" << endl << endl;
        return true;
    }

    SymbolInfo *search(string name, bool pf)
    {
        int hash = hashFunction(name);
        if (data[hash].head == NULL)
        {
            if (pf)
                cout << " Not found" << endl << endl;
            return NULL;
        }
        SymbolInfo *cur = data[hash].head;
        int pos = 0;
        while (cur != NULL)
        {
            if (cur->getName() == name)
            {
                if (pf)
                    cout << " Found in ScopeTable# " << ID << " at position " << hash << ", " << pos << endl << endl;
                return
                        cur;
            }
            cur = cur->next;
            pos++;
        }
        if (pf)
            cout << " Not found" << endl << endl;
        return NULL;
    }


    void printHash()
    {
        cout << " ScopeTable # " << ID << endl;
        SymbolInfo *cur;
        REP(i, tableSize)
        {
            cout << " " << i << " --> ";
            cur = data[i].head;
            while (cur != NULL)
            {
                cout << " < " << cur->getName() << " : " << cur->getType() << "> ";
                cur = cur->next;
            }
            cout << endl;
        }
        cout << endl;
    }

};

class SymbolTable
{
public:
    ScopeTable *currentTable;

    SymbolTable()
    {
        ScopeTable *temp = new ScopeTable(tableSize);
        temp->parentScope = NULL;
        temp->ID = 1;
        currentTable = temp;
    }
    
    void enterScopre()
    {
        ScopeTable *temp = new ScopeTable(tableSize);
        temp->parentScope = currentTable;
        temp->ID = temp->parentScope->ID + 1;
        currentTable = temp;
        cout << " New ScopeTable with id " << currentTable->ID << " created" << endl << endl;
    }

    void exitScope()
    {
        if (currentTable->ID != 1)
        {
            ScopeTable *temp = currentTable;
            currentTable = currentTable->parentScope;
            cout << " ScopeTable with id " << temp->ID << " removed" << endl << endl;
            delete temp;
        }
        else
        {
            cout << " Can not remove ScopeTable #1" << endl << endl;
        }
    }

    bool insert(string name, string type)
    {
        return currentTable->insert(name, type);
    }

    bool remove(string name)
    {
        return currentTable->remove(name);
    }

    SymbolInfo *search(string name)
    {
        ScopeTable *cur = currentTable;
        while (cur != NULL)
        {
            SymbolInfo *temp = cur->search(name, true);
            if (temp != NULL)
            {
                return temp;
            }
            cur = cur->parentScope;
        }
        return NULL;
    }

    void printCurrentTable()
    {
        currentTable->printHash();
    }

    void printAllTable()
    {
        ScopeTable *cur = currentTable;
        while (cur != NULL)
        {
            cur->printHash();
            cur = cur->parentScope;
        }
    }
};


int main()
{
    //freopen("input.txt", "r", stdin);
    //freopen("output.txt", "w", stdout);
    cin >> tableSize;
    SymbolTable symbolTable;
    char codeLetter;
    while (cin >> codeLetter)
    {
        if (codeLetter == 'I')
        {
            string name, type;
            cin >> name >> type;
            cout << "I " << name << " " << type << endl << endl;
            symbolTable.insert(name, type);
        }
        else if (codeLetter == 'L')
        {
            string name;
            cin >> name;
            cout << "L " << name << endl << endl;
            symbolTable.search(name);
        }
        else if (codeLetter == 'D')
        {
            string name;
            cin >> name;
            cout << "D " << name << endl << endl;
            symbolTable.remove(name);
        }
        else if (codeLetter == 'P')
        {
            string temp;
            cin >> temp;
            cout << "P " << temp << endl << endl;
            if (temp == "A")
            {
                symbolTable.printAllTable();
            }
            else
            {
                symbolTable.printCurrentTable();
            }
        }
        else if (codeLetter == 'S')
        {
            cout << "S" << endl << endl;
            symbolTable.enterScopre();
        }
        else if (codeLetter == 'E')
        {
            cout << "E" << endl << endl;
            symbolTable.exitScope();
        }
    }
}



