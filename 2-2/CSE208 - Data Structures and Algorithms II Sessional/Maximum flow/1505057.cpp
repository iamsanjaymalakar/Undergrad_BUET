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


#define FOR(i, a, b) for (int i = a; i <= b; i++)
#define REP(i, n) for (int i = 0; i < n; i++)
#define ll long long
#define ull unsigned long long
#define inf 1e9
#define sf(a) scanf("%d",&a);
#define sf2(a,b) scanf("%d %d",&a,&b);
#define sf3(a,b,c) scanf("%d %d %d",&a,&b,&c);
#define pf printf
#define mset(a,b) memset(a,b,sizeof(a))
#define itf it->first
#define its it->second

int n,m,s,t;
int g[1000][1000],rg[1000][1000];
bool visited[1000];
int p[1000];

bool bfs()
{
    mset(visited,false);
    queue<int> q;
    q.push(s);
    visited[s]=true;
    while(!q.empty())
    {
        int u=q.front();
        q.pop();
        FOR(v,1,n)
        {
            if(!visited[v] && rg[u][v]>0)
            {
                q.push(v);
                visited[v]=true;
                p[v]=u;
            }
        }
    }
    if(visited[t])
        return true;
    return false;
}

void dfs(int s)
{
    visited[s]=true;
    FOR(i,1,n)
    {
        if(!visited[i] && rg[s][i])
        {
            dfs(i);
        }
    }
}


void ff()
{
    int mf=0;
    while(bfs())
    {
        int mpf=inf,u;
        for(int v=t;v!=s;v=p[v])
        {
            u=p[v];
            mpf=min(mpf,rg[u][v]);
        }
        for(int v=t;v!=s;v=p[v])
        {
            u=p[v];
            rg[u][v]-=mpf;
            rg[v][u]+=mpf;
        }
        mf+=mpf;
    }
    cout << "Max flow = " << mf << endl << endl;
}

void mc()
{
    mset(visited,false);
    dfs(s);
    FOR(i,1,n)
    {
        FOR(j,1,n)
        {
            if(visited[i] && !visited[j] && g[i][j])
            {
                cout << i << "->" << j << " = " << g[i][j]  << endl;
            }
        }
    }
}

int main()
{
    freopen("input.txt","r",stdin);
    sf2(n,m);
    int u,v,w;
    REP(i,m)
    {
        sf3(u,v,w);
        g[u][v]=w;
        rg[u][v]=w;
    }
    sf2(s,t);
    ff();
    REP(i,n)
    {
        REP(j,n)
        {
            if(g[i+1][j+1]>0)
            {
                cout << i+1 << "->" << j+1 << " = " << g[i+1][j+1]-rg[i+1][j+1]  << endl;
            }
        }
    }
    cout << endl;
    mc();
}


/*
6
10
1 2 16
1 3 13
2 3 10
3 2 4
2 4 12
4 3 9
3 5 14
5 4 7
4 6 20
5 6 4
1
6
*/
