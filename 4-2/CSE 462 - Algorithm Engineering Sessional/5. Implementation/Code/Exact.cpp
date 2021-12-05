#include<vector>
#include<algorithm>
#include<iostream>
#include<fstream>
#include<cstring>
#include<bitset>
#include <chrono>

using namespace std;

//0 - based
const int MAX_VERTEX = 30, MOD1 = 1e9+7, MOD2 = 1e9+9;

void find_clique( int vertex, int cliques[], int *edge_mat )
{
    int tot = 1<<vertex;

    for( int flag = 1; flag < tot; flag++ )
    {
        cliques[flag] = 1;
        for( int i = 0; i < vertex and cliques[flag] == 1; i++ )
        {
            for( int j = 0; j < vertex; j++ )
            {
                if( i != j and (flag&(1<<i)) != 0  and (flag&(1<<j)) != 0 and edge_mat[i*vertex+j] == 0 )
                {
                    cliques[flag] = 0;
                    break;
                }
            }
        }
    }
    return;
}

void sos_dp(int vertex, int cliques[])
{
    int tot = 1<<vertex;

    for( int i = 0; i < vertex; i++ )
    {
        for( int mask = 0; mask < tot; mask++ )
        {
            if( mask&(1<<i) )
            {
                cliques[mask] += cliques[mask^(1<<i)];
            }
        }
    }

    return;
}

//a^b
int expo( int a, int b, int mod )
{
    int val = 1;
    while(b > 0)
    {
        if( b&1 )
            val = (1LL*val*a)%mod;
        a = (1LL*a*a)%mod;
        b /= 2;
    }
    return val;
}

bool KCliqueSolvable(int vertex, int zetaClique[], int k)
{
    long long ans1 = 0, ans2 = 0, tot = (1<<vertex)-1;

    for( int i = 1; i < tot; i++ )
    {
        if( zetaClique[i] == 0 ) continue;

        int bit = vertex - __builtin_popcount(i);

        if( bit&1 )
        {
            ans1 += expo( zetaClique[i], k, MOD1 );
            ans2 += expo( zetaClique[i], k, MOD2 );
        }
        else
        {
            ans1 -= expo( zetaClique[i], k, MOD1 );
            ans2 -= expo( zetaClique[i], k, MOD2 );
        }
        ans1 %= MOD1;
        ans2 %= MOD2;
    }

    int sum = zetaClique[tot];
    long long rem1 = (expo(sum, k, MOD1) - ans1)%MOD1, rem2 = (expo(sum, k, MOD2) - ans2)%MOD2;

    return rem1 != 0 and rem2 != 0;

}

int cliqueCoverNumber(int vertex, int zetaClique[])
{
    int lo = 0, hi = vertex;
    while(lo < hi)
    {
        int m = (lo+hi)/2;
        int f = KCliqueSolvable(vertex, zetaClique, m);
        if(f)
        {
            hi = m;
        }
        else
        {
            lo = m+1;
        }
    }
    return hi;
}

int main()
{
    ifstream fin;
    //input file: vertex number start from 0
    //vertex edge
    //each edge in one line
    fin.open("in0.txt");

    ofstream fout;
    fout.open("out_exact0.txt");


    auto start = chrono::high_resolution_clock::now();

    int vertex, edge;

    fin >> vertex >> edge;

    int tot = (1<<vertex);

    int *cliques = new int[tot];
    int edge_mat[vertex][vertex];
    memset( edge_mat, 0, sizeof edge_mat );
    memset( cliques, 0, sizeof cliques );

    for( int i = 0; i < edge; i++ )
    {
        int a, b;
        fin >> a >> b;
        edge_mat[a][b] = 1;
        edge_mat[b][a] = 1;
    }

    find_clique(vertex, cliques, edge_mat[0]);

    cout << "All clique find done!" << endl;

    sos_dp(vertex, cliques);

    cout << "SOS Dp done!" << endl;

    int ans = cliqueCoverNumber(vertex, cliques);

    auto stop = chrono::high_resolution_clock::now();
    int duration = chrono::duration_cast<chrono::milliseconds>(stop - start).count();


    cout << "Clique Cover Number is: " << ans << endl;
    cout << "Time For Execution: " << duration << " ms" << endl;

    fout << ans << " " << duration << endl;
    return 0;
}
