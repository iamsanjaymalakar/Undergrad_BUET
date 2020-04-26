#include<iostream>

using namespace std;


int* middle (int *a,int l,int m,int r)
{
    // left
    int ll=m;
    for(int i=m;i>l;i++)
    {
        if(a[i]>=a[i-1])
        {
            ll--;
        }
        else
            break;
    }

    // right
    int rr=m;
    for(int i=m;i<r;i++)
    {
        if(a[i+1]>=a[i])
        {
            rr++;
        }
        else
            break;
    }
    int ans[2];
    ans[0]=ll;
    ans[1]=rr;
    return ans;
}


int* divide(int *a,int l,int r)
{
    int ans[2];
    if(l==r)
    {
        ans[0]=l;
        ans[1]=r;
        cout << "here" <<" " << a[l] << " " << l<< endl;
        return ans;
    }
    if(l<r)
    {
        //cout <<"here";
            int m=(l+r)/2;
            int *a1,*a2,*a3;
            cout << m << "s" << endl;
            if(m>0)
            {
                a1=divide(a,l,m-1);
            }
            if(m==0)
            {
                a1=divide(a,l,l);
            }
            a2=divide(a,m+1,r);
            a3=middle(a,l,m,r);

            int *mm=a1;
            if((a2[1]-a2[0])>(mm[1]-mm[0]))
            {
               mm=a2;
            }
            if((a3[1]-a3[0])>(mm[1]-mm[0]))
            {
                mm=a3;
            }
            cout << "ass" << endl;
            return mm;
    }
}

int main()
{
    int n;
    cin >> n;
    //n=6;
    int a[6]={6,2,4,4,5,1};
    for(int i=0;i<n;i++)
        cin >> a[i];
    int *ans=divide(a,0,n-1);
    for(int i=ans[0];i<=ans[i];i++)
        cout << a[i] << " ";
    cout << endl;
    return 0;
}

