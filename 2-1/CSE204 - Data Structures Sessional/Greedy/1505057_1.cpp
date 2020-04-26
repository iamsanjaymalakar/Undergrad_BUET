#include <iostream>

using namespace std;

void merge(int* a,int* b,int l,int m,int r)
{
    int ln=m-l+1,rn=r-m;
    int L[ln],R[rn],Lb[ln],Rb[rn];
    for (int i=0;i<ln;i++)
        L[i]=a[l+i],Lb[i]=b[l+i];
    for (int i=0;i<rn;i++)
        R[i]=a[m+1+i],Rb[i]=b[m+1+i];
    int i=0,j=0,k=l;
    while (i<ln && j<rn)
    {
        if (L[i]<=R[j])
        {
            a[k]=L[i];
            b[k]=Lb[i];
            i++;
        }
        else
        {
            a[k] = R[j];
            b[k]=Rb[j];
            j++;
        }
        k++;
    }

    while (i<ln)
    {
        a[k]=L[i];
        b[k]=Lb[i];
        i++;
        k++;
    }
    while (j<rn)
    {
        a[k]=R[j];
        b[k]=Rb[j];
        j++;
        k++;
    }
}

void mergeSort(int* a,int *b,int l,int r)
{
    if( l <  r)
    {
        int m = (l+r)/2;
        mergeSort(a,b,l,m);
        mergeSort(a,b,m+1,r);
        merge(a,b,l,m,r);
    }
}

int main()
{
    int n;
    cin >> n;
    int a[n],b[n];
    for(int i=0;i<n;i++)
        cin >> a[i];
    for(int i=0;i<n;i++)
        b[i]=i+1;
    mergeSort(a,b,0,n-1);
    for(int i=0;i<n;i++)
        cout << b[i] << " ";
    cout << endl;
    return 0;
}

// 11 10 24 5 11 67 21 8 97 32 9 41
