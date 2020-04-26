#include<iostream>

using namespace std;

#define Inf 1e9

void merger(int *a,int *b,int l,int m,int r)
{
    int ln = m-l+1,rn = r-m;
    int L[ln],R[rn];
    int Lb[ln],Rb[rn];
    for(int i=0; i<ln; i++)
    {
        L[i]=a[l+i];
        Lb[i]=b[l+i];
    }
    for(int i=0; i<rn; i++)
    {
        R[i]=a[m+1+i];
        Rb[i]=b[m+1+i];
    }
    int i=0,j=0,k=l;
    while(i<ln && j<rn)
    {
        if(L[i] <= R[j])
        {
            if(L[i]==R[j])
            {
                if(Lb[i]<=Rb[j])
                {
                    a[k]=L[i];
                    b[k]=Lb[i];
                    i++;
                }
                else
                {
                    a[k]=R[j];
                    b[k]=Rb[j];
                    j++;
                }
            }
            else
            {
                a[k]=L[i];
                b[k]=Lb[i];
                i++;
            }
        }
        else
        {
            a[k]=R[j];
            b[k]=Rb[j];
            j++;
        }
        k++;
    }
    while(i<ln)
    {
        a[k]=L[i];
        b[k]=Lb[i];
        k++;
        i++;
    }
    while(j<rn)
    {
        a[k]=R[j];
        b[k]=Rb[j];
        k++;
        j++;
    }
}

void mergeSort(int *a,int *b,int l,int r)
{
    if(l<r)
    {
        int m = (l+r)/2;
        mergeSort(a,b,l,m);
        mergeSort(a,b,m+1,r);
        merger(a,b,l,m,r);
    }
}

void calc(int *a,int *b,int l,int m,int r)
{
    int ln=m-l+1,rn=r-m;
    int L[ln],R[rn];
    for(int i=0; i<ln; i++)
    {
        L[i]=b[l+i];
    }
    for(int i=0; i<rn; i++)
    {
        R[i]=b[m+1+i];
    }
    //minimum of left array
    int min=L[0];
    for(int i=1; i<ln; i++)
    {
        if(L[i]<min)
            min=L[i];
    }
    //cout << min << endl;
    //comparing minimum with right array
    for(int i=0; i<rn; i++)
    {
        if(R[i]>=min)
        {
            b[i+m+1]=Inf;
            a[i+m+1]=Inf;
        }
    }

}

void calcPoints(int *a,int *b,int l,int r)
{
    if(l<r)
    {
        int m = (l+r)/2;
        calcPoints(a,b,l,m);
        calcPoints(a,b,m+1,r);
        calc(a,b,l,m,r);
    }
}

int main()
{
    int n;
    cin >> n;
    int a[n],b[n];
    for(int i=0; i<n; i++)
    {
        cin >> a[i] >> b[i];
    }

    //sorting x
    mergeSort(a,b,0,n-1);
    //removing same points
//    for(int i=0;i<n-1;i++)
//    {
//        if(a[i]==a[i+1] && b[i]==b[i+1])
//        {
//            a[i]=Inf;
//            b[i]=Inf;
//            a[i+1]=Inf;
//            b[i+1]=Inf;
//        }
//    }

    // calculating non-dominant points
    calcPoints(a,b,0,n-1);
    for(int i=0; i<n; i++)
    {
        if(a[i]!=Inf && b[i]!=Inf)
            cout << a[i] << " " << b[i] << endl;
    }
}


//11 9 2 1 8 3 7 10 5 8 5 6 8 2 5 4 4 11 7 7 3 5 6

