#include<bits/stdc++.h>

using namespace std;

#define pi acos(-1)

stack<pair<double**,bool>> Stack;
ofstream s1,s2,s3;
double** V;
double** P;

void debug(double** a)
{
    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            cout << fixed << setprecision(7) << a[j][i] << " ";
        }
        cout << endl;
    }
    cout << endl << endl << endl;
}

struct point
{
    double x;
    double y;
    double z;
};

double** zeroMatrix()
{
    double **temp=new double*[4];
    for(int i=0;i<4;i++)
    {
        temp[i]=new double[4];
    }
    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            temp[i][j]=0;
        }
    }
    return temp;
}

double** identityMatrix()
{
    double **temp=zeroMatrix();
    for(int i=0;i<4;i++)
    {
        temp[i][i]=1;
    }
    return temp;
}

double** multiply(double** a,double** b)
{
    double** temp=zeroMatrix();
    for(int i=0;i<4;i++)
    {
        for(int j=0;j<4;j++)
        {
            for(int k=0;k<4;k++)
            {
                temp[i][j]+=a[i][k]*b[k][j];
            }
        }
    }
    return temp;
}

point cross(point a,point b)
{
    point temp;
    temp.x=a.y*b.z-a.z*b.y;
    temp.y=a.z*b.x-a.x*b.z;
    temp.z=a.x*b.y-a.y*b.x;
    return temp;
}

point normalize(point a)
{
    point temp;
    double len=sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
    temp.x=a.x/len;
    temp.y=a.y/len;
    temp.z=a.z/len;
    return temp;
}

void multiplyTop(double** a)
{
    double** temp=multiply(Stack.top().first,a);
    Stack.push(make_pair(temp,false));
}

void translate(point t)
{
    double** temp=identityMatrix();
    temp[0][3]=t.x;
    temp[1][3]=t.y;
    temp[2][3]=t.z;
    multiplyTop(temp);
}

void scale(point s)
{
    double** temp=identityMatrix();
    temp[0][0]=s.x;
    temp[1][1]=s.y;
    temp[2][2]=s.z;
    multiplyTop(temp);
}

void rotate(double angle,point a)
{
    double** temp=zeroMatrix();

    double cost=cos(pi*angle/180.0);
    double sint=sin(pi*angle/180.0);

    point u=normalize(a);
    temp[0][0]=cost+u.x*u.x*(1-cost);
    temp[0][1]=u.x*u.y*(1-cost)-u.z*sint;
    temp[0][2]=u.x*u.z*(1-cost)+u.y*sint;

    temp[1][0]=u.x*u.y*(1-cost)+u.z*sint;
    temp[1][1]=cost+u.y*u.y*(1-cost);
    temp[1][2]=u.y*u.z*(1-cost)-u.x*sint;

    temp[2][0]=u.x*u.z*(1-cost)-u.y*sint;
    temp[2][1]=u.y*u.z*(1-cost)+u.x*sint;
    temp[2][2]=cost+u.z*u.z*(1-cost);

    temp[3][3]=1;

    multiplyTop(temp);
}

double** makeTriangle(point a,point b,point c)
{
    double** temp=zeroMatrix();
    point points[]={a,b,c};
    for(int i=0;i<3;i++)
    {
        temp[0][i]=points[i].x;
        temp[1][i]=points[i].y;
        temp[2][i]=points[i].z;
        temp[3][i]=1;
    }
    temp[0][3]=temp[1][3]=temp[2][3]=temp[3][3]=1.0;
    return multiply(Stack.top().first,temp);
}

void push()
{
    Stack.push(make_pair(Stack.top().first,true));
}

void pop()
{
    while(!Stack.top().second)
    {
        Stack.pop();
    }
    Stack.pop();
}

void init()
{
    Stack.push(make_pair(identityMatrix(),false));
    s1.open("stage1.txt");
    s2.open("stage2.txt");
    s3.open("stage3.txt");
    point eye,look,up,l,r,u;
    double fovY,ar,near,far,fovX,tp,rp;
    double** T;
    double** R;

    cin >> eye.x >> eye.y >> eye.z;
    cin >> look.x >> look.y >> look.z;
    cin >> up.x >> up.y >> up.z;
    cin >> fovY >> ar >> near >> far;

    //view trans
    l.x=look.x-eye.x;
    l.y=look.y-eye.y;
    l.z=look.z-eye.z;
    l=normalize(l);

    r=normalize(cross(l,up));

    u=normalize(cross(r,l));

    T=identityMatrix();
    T[0][3]=-eye.x,T[1][3]=-eye.y,T[2][3]=-eye.z;

    R=zeroMatrix();
    R[0][0]=r.x,R[0][1]=r.y,R[0][2]=r.z;
    R[1][0]=u.x,R[1][1]=u.y,R[1][2]=u.z;
    R[2][0]=-l.x,R[2][1]=-l.y,R[2][2]=-l.z;
    R[3][3] = 1.0;

    V=multiply(R,T);

    //projections trans
    fovX=fovY*ar;
    tp=near*tan(fovY/2*pi/180.0);
    rp=near*tan(fovX/2*pi/180.0);

    P=zeroMatrix();
    P[0][0]=near/rp;
    P[1][1]=near/tp;
    P[2][2]=-(far+near)/(far-near);
    P[2][3]=-(2*far*near)/(far-near);
    P[3][2]=-1.0;
}

int main()
{
    freopen("scene.txt","r",stdin);
    init();
    string choice;

    while(true)
    {
        cin >> choice;
        if(choice=="triangle")
        {
            point a,b,c;
            cin >> a.x >> a.y >> a.z;
            cin >> b.x >> b.y >> b.z;
            cin >> c.x >> c.y >> c.z;
            double** triangle=makeTriangle(a,b,c);
            //stage 1
            for(int i=0;i<3;i++)
            {
                for(int j=0;j<3;j++)
                {
                    if(j==2)
                    {
                        s1 << fixed << setprecision(7) << triangle[j][i];
                    }
                    else
                    {
                        s1 << fixed << setprecision(7) << triangle[j][i] << " ";
                    }
                }
                s1 << endl;
            }
            s1 << endl;

            //stage 2
            triangle=multiply(V,triangle);
            debug(triangle);
            for(int i=0;i<3;i++)
            {
                for(int j=0;j<3;j++)
                {
                    if(j==2)
                    {
                        s2 << fixed << setprecision(7) << triangle[j][i];
                    }
                    else
                    {
                        s2 << fixed << setprecision(7) << triangle[j][i] << " ";
                    }
                }
                s2 << endl;
            }
            s2 << endl;

            //stage 3
            triangle=multiply(P,triangle);
            for(int i=0;i<3;i++)
            {
                for(int j=0;j<3;j++)
                {
                    if(j==2)
                    {
                        s3 << fixed << setprecision(7) << triangle[j][i]/triangle[3][i];
                    }
                    else
                    {
                        s3 << fixed << setprecision(7) << triangle[j][i]/triangle[3][i] << " ";
                    }
                }
                s3 << endl;
            }
            s3 << endl;
        }
        else if(choice=="translate")
        {
            point t;
            cin >> t.x >> t.y >> t.z;
            translate(t);
        }
        else if(choice=="scale")
        {
            point s;
            cin >> s.x >> s.y >> s.z;
            scale(s);
        }
        else if(choice=="rotate")
        {
            double angle;
            point a;
            cin >> angle >> a.x >> a.y >> a.z;
            rotate(angle,a);
        }
        else if(choice=="push")
        {
            push();
        }
        else if(choice=="pop")
        {
            pop();
        }
        else if(choice=="end")
        {
            break;
        }
        else
        {
            cout << "Unknown choice" << endl;
        }
    }
}
