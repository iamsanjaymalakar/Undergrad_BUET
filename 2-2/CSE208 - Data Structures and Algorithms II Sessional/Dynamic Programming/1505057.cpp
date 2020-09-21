#include<iostream>

using namespace std;

int b[10000][10000];

void backtrack(int i,int j,char &t)
{
    if(i==j)
    {
        cout << t++;
        return;
    }
    cout << "(";
    backtrack(i,b[i][j],t);
    backtrack(b[i][j]+1,j,t);
    cout << ")";
}

void mcm(int p[],int n)
{
    int a[n][n];

    for(int i=1;i<n;i++)
    {
        a[i][i]=0;
    }
    for(int l=2;l<n;l++)
    {
        for(int i=1;i<n-l+1;i++)
        {
            int j=i+l-1;
            a[i][j]=INT_MAX;
            for(int k=i;k<=j-1;k++)
            {
                int q=a[i][k]+a[k+1][j]+p[i-1]*p[k]*p[j];
                if(q<a[i][j])
                {
                    a[i][j]=q;
                    b[i][j]=k;
                }
            }
        }
    }
    cout << a[1][n-1] << endl;
    char t='A';
    backtrack(1,n-1,t);
}

int main()
{
   int n;
   cin >> n;
   int p[n+1];
   int u,v;
   for(int i=0;i<n;i++)
   {
       cin >> u >> v;
       p[i]=u;
   }
   p[n]=v;
    mcm(p,n+1);
}

/*
4
40 20
20 30
30 10
10 30
*/
