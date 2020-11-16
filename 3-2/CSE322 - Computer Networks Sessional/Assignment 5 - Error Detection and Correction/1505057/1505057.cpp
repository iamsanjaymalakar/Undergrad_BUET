#include<iostream>
#include<cmath>
#include<cstring>
#include<bitset>
#include <windows.h>
#include<ctime>
#include<stdio.h>

using namespace std;

#define GREEN 10
#define RED 12
#define CYAN 11

void setColor(int color)
{
    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE),color );
}

void resetColor()
{
    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE),15 );
}

int powerOfTwo(int n)
{
    if((n != 0) && ((n &(n - 1)) == 0))
        return 1;
    return 0;
}

void reverseArray(int arr[], int s, int e)
{
    if (s >= e)
        return;

    int temp = arr[s];
    arr[s] = arr[e];
    arr[e] = temp;
    reverseArray(arr, s + 1, e - 1);
}


int main()
{
    string data;
    int m,poly;
    double p;
    cout << "enter data string: ";
    getline(cin,data);
    cout << "enter number of data bytes in a row <m>: ";
    cin >> m;
    cout << "enter probability <p>: ";
    cin >> p;
    cout << "enter generator polynomial: ";
    cin >> poly;
    if(data.length()%m!=0)
    {
        for(int i=0; i<data.length()%m; i++)
            data+="~";
    }
    cout << endl << "data string after padding: " << data << endl << endl;
    int d[data.length()/m][m*8+1];
    for(int i=0,idx=0; i<data.length(); i=i+m,idx++)
    {
        for(int j=i; j<i+m; j++)
        {
            for(int k=7; k>=0; k--)
            {
                d[idx][(j%m+1)*8-k] = (data[j] & (1 << k)) ? 1 : 0;
            }
        }
    }
    cout << "data block <ascii code of m character per row>:" << endl ;
    for(int i=0; i<data.length()/m; i++)
    {
        for(int j=1; j<=m*8; j++)
            cout << d[i][j];
        cout << endl;
    }
    // calculating rb
    int rb=0,mb=m*8;
    while (mb + rb + 1 > pow (2, rb))
    {
        rb++;
    }
    int dcb[data.length()/m][mb+rb+1],idx,dc;
    for(int i=0; i<data.length()/m; i++)
    {
        idx=1;
        dc=0;
        for (int j = 1; j <= mb + rb; j++)
        {
            if (j == pow (2, dc))
            {
                dcb[i][j] = 0;
                dc++;
            }
            else
            {
                dcb[i][j] = d[i][idx];
                idx++;
            }
        }
    }
    //adding hamming code
    for(int i=0; i<data.length()/m; i++)
    {
        for(int j=1; j<=mb+rb; j++)
        {
            if(powerOfTwo(j))
                continue;
            if(dcb[i][j])
            {
                int temp=j;
                for(int k=0; k<rb; k++)
                {
                    if(temp&1)
                    {
                        dcb[i][(int)pow(2,k)]^=1;
                    }
                    temp>>=1;
                }
            }
        }
    }
    cout << endl << "data blocks after adding check bits: " << endl;
    for(int i=0; i<data.length()/m; i++)
    {
        for(int j=1; j<=mb+rb; j++)
        {
            if(powerOfTwo(j))
            {
                setColor(GREEN);
                cout << dcb[i][j];
                resetColor();
            }
            else
                cout << dcb[i][j];
        }
        cout << endl;
    }
    //column wise serialization
    int gs=0,temp=poly;
    while(temp)
    {
        gs++;
        temp/=10;
    }
    int g[gs],i=0;
    temp=poly;
    while(temp)
    {
        g[i]=temp%10;
        temp/=10;
        i++;
    }
    reverseArray(g,0,gs-1);
    int msg=(mb+rb)*data.length()/m;
    int frame[msg+gs-1];
    idx=0;
    for(int i=1; i<=mb+rb; i++)
    {
        for(int j=0; j<data.length()/m; j++)
        {
            frame[idx++]=dcb[j][i];
        }
    }
    for(int i=idx; i<msg+gs-1; i++)
        frame[i]=0;
    int tempFrame[msg+gs-1];
    for(int i=0; i<msg+gs-1; i++)
        tempFrame[i]=frame[i];
    //division
    for(i=0; i<msg; i++)
    {
        int j=0;
        int k=i;
        if (tempFrame[k]>=g[j])
        {
            for(j=0,k=i; j<gs; j++,k++)
            {
                tempFrame[k]=tempFrame[k]^g[j];
            }
        }
    }
    for(int i=0,j=msg; i<gs-1; i++,j++)
    {
        frame[j]=tempFrame[j];
    }
    //print
    cout << endl << "data bits after column wise serialization:" << endl;
    for(int i=0; i<msg; i++)
    {
        cout << frame[i];
    }
    cout << endl << "\ndata bits after appending CRC checksum <sent frame>:" << endl;
    for(int i=0; i<msg+gs-1; i++)
    {
        if(i>=msg)
        {
            setColor(CYAN);
            cout << frame[i];
            resetColor();
        }
        else
            cout << frame[i];
    }
    srand(time(NULL));
    cout << endl << "\nreceived frame:" << endl;
    bool erbit[msg+gs-1];
    for(int i=0; i<msg+gs-1; i++)
    {
        int rnd = rand()%100;
        if(rnd<(p*100))
        {
            frame[i]^=1;
            erbit[i]=true;
            setColor(RED);
            cout << frame[i];
            resetColor();
        }
        else
        {
            erbit[i]=false;
            cout << frame[i];
        }
    }
    //receiver
    for(int i=0; i<msg+gs-1; i++)
    {
        tempFrame[i]=frame[i];
    }
    for(int i=0; i<msg; i++)
    {
        int j=0;
        int k=i;
        if (tempFrame[k]>=g[j])
        {
            for(j=0,k=i; j<gs; j++,k++)
            {
                tempFrame[k]=tempFrame[k]^g[j];
            }
        }
    }
    int rem[20];
    for (int i=msg,j=0; i<msg+gs-1; i++,j++)
    {
        rem[j]= tempFrame[i];
    }
    bool f=0;
    for(i=0; i<gs-1; i++)
    {
        if(rem[i]!=0)
        {
            f=1;
            break;
        }
    }
    cout << "\n\nresult of CRC checksum matching: ";
    if(f)
    {
        cout << "error detected\n";
    }
    else
    {
        cout << "no error detected\n";
    }
    //removing crc bits
    int rdcb[data.length()/m][mb+rb];
    bool rerbit[data.length()/m][mb+rb];
    for(int i=0; i<msg; i++)
    {
        rdcb[i%(data.length()/m)][i/(data.length()/m)]=frame[i];
        rerbit[i%(data.length()/m)][i/(data.length()/m)]=erbit[i];
    }
    for(int i=0; i<data.length()/m; i++)
    {
        for(int j=0; j<mb+rb; j++)
        {
            if(rerbit[i][j])
            {
                setColor(RED);
                cout << rdcb[i][j];
                resetColor();
            }
            else
                cout << rdcb[i][j];
        }
        cout << endl;
    }
    //calculating hamming bits again
    for(int i=0; i<data.length()/m; i++)
    {
        for(int j=1; j<=mb+rb; j++)
        {
            if(powerOfTwo(j))
                continue;
            if(rdcb[i][j-1])
            {
                int temp=j;
                for(int k=0; k<rb; k++)
                {
                    if(temp&1)
                    {
                        rdcb[i][(int)pow(2,k)-1]^=1;
                    }
                    temp>>=1;
                }
            }
        }
    }
    //error handle
    for(int i=0; i<data.length()/m; i++)
    {
        int pos=0;
        for(int j=mb+rb-1; j>=0; j--)
        {
            if(powerOfTwo(j+1))
            {
                if(rdcb[i][j])
                {
                    pos+=(j+1);
                }
            }
        }
        if(pos && pos<(mb+rb)) // flip bits
        {
            rdcb[i][pos-1]^=1;
        }
    }
    int rd[data.length()/m][m*8];
    //copy
    for(int i=0; i<data.length()/m; i++)
    {
        int idx=0;
        for(int j=0; j<mb+rb; j++)
        {
            if(powerOfTwo(j+1))
                continue;
            rd[i][idx]=rdcb[i][j];
            idx++;
        }
    }

    cout << "\ndata block after removing check bits:\n";
    for(int i=0; i<data.length()/m; i++)
    {
        for(int j=0; j<m*8; j++)
        {
            cout << rd[i][j];
        }
        cout << endl;
    }
    cout << "\noutput frame: ";
    char c=0;
    for(int i=0; i<data.length()/m; i++)
    {
        for(int j=0; j<m; j++)
        {
            c=0;
            for(int k=8*j; k<(j+1)*8; k++)
            {
                if(rd[i][k])
                    c|=1;
                if(k!=(j+1)*8-1)
                    c<<=1;
            }
            cout << c ;
        }
    }
}

