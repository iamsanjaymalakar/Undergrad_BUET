#include<vector>
#include<algorithm>
#include<iostream>
#include<fstream>
#include<cstring>
#include<bitset>
#include<chrono>
#include<cstdlib>
#include<ctime>

using namespace std;

const int MAX_VERTEX = 30, SECONDS_TO_RUN = 20;

vector< vector<int> > solve( vector<int> perm, int **edge_mat, int vertex )
{
    vector< vector<int> > sol;
    for( int i = 0; i < vertex; i++ )
    {
        int ind = -1;
        for( int j = 0; j < sol.size(); j++ )
        {
            int f = 1;
            for( int k = 0; k < sol[j].size(); k++ )
            {
                if( edge_mat[i][ sol[j][k] ] == 0 )
                {
                    f = 0;
                    break;
                }
            }
            if(f)
            {
                ind = j;
                break;
            }
        }
        if(ind != -1)
        {
            sol[ind].push_back(i);
        }
        else
        {
            vector<int> vec = {i};
            sol.push_back(vec);
        }
    }
    return sol;
}

int main()
{
    srand(time(0));

    ifstream fin;
    //input file: vertex number start from 0
    //vertex edge
    //each edge in one line
    fin.open("in0.txt");

    ofstream fout;
    fout.open("out_ig0.txt");

    int vertex, edge;

    fin >> vertex >> edge;

    int **edge_mat;
    edge_mat = new int*[vertex];
    vector<int> perm;


    for( int i = 0; i < vertex; i++ )
    {
        perm.push_back(i);
        edge_mat[i] = new int[vertex];
    }

    for( int i = 0; i < vertex; i++ )
    {
        for( int j = 0; j < vertex; j++ )
        {
            edge_mat[i][j] = 0;
        }
    }

    for( int i = 0; i < edge; i++ )
    {
        int a, b;
        fin >> a >> b;
        edge_mat[a][b] = 1;
        edge_mat[b][a] = 1;
    }

    random_shuffle(perm.begin(), perm.end());

    auto start = chrono::high_resolution_clock::now();

    int ans = 1e9, duration = 0, it = 0;

    while(duration <= SECONDS_TO_RUN*1000)
    {
        vector< vector<int> > sol = solve(perm, edge_mat, vertex);

        int cur_sol = sol.size();
        ans = min( ans, cur_sol );


        int ind = rand()%cur_sol;
        swap( sol[0], sol[ind] );


        perm.clear();
        for( int i = 0; i < cur_sol; i++ )
        {
            for( int j = 0; j < sol[i].size(); j++ )
            {
                perm.push_back( sol[i][j] );
            }
        }

        it++;

        auto stop = chrono::high_resolution_clock::now();
        duration = chrono::duration_cast<chrono::milliseconds>(stop - start).count();

        if(it%100000==0)
        {
            cout << "Iteration No: " << it << endl;
        }
    }

    cout << "Clique Cover Number is: " << ans << endl;
    cout << "Time For Execution: " << duration << " ms" << endl;

    fout << ans << " " << duration << endl;


    return 0;
}
