#include<bits/stdc++.h>

using namespace std;

//initial permutation matrix for data
vector<int> PI = {-1, 58, 50, 42, 34, 26, 18, 10, 2,
                  60, 52, 44, 36, 28, 20, 12, 4,
                  62, 54, 46, 38, 30, 22, 14, 6,
                  64, 56, 48, 40, 32, 24, 16, 8,
                  57, 49, 41, 33, 25, 17, 9, 1,
                  59, 51, 43, 35, 27, 19, 11, 3,
                  61, 53, 45, 37, 29, 21, 13, 5,
                  63, 55, 47, 39, 31, 23, 15, 7
                 };

//initial permutation made on the key
vector<int> CP_1 = {-1, 57, 49, 41, 33, 25, 17, 9,
                    1, 58, 50, 42, 34, 26, 18,
                    10, 2, 59, 51, 43, 35, 27,
                    19, 11, 3, 60, 52, 44, 36,
                    63, 55, 47, 39, 31, 23, 15,
                    7, 62, 54, 46, 38, 30, 22,
                    14, 6, 61, 53, 45, 37, 29,
                    21, 13, 5, 28, 20, 12, 4
                   };

//permutation applied on shifted key to get Ki+1
vector<int> CP_2 = {-1, 14, 17, 11, 24, 1, 5, 3, 28,
                    15, 6, 21, 10, 23, 19, 12, 4,
                    26, 8, 16, 7, 27, 20, 13, 2,
                    41, 52, 31, 37, 47, 55, 30, 40,
                    51, 45, 33, 48, 44, 49, 39, 56,
                    34, 53, 46, 42, 50, 36, 29, 32
                   };

//Expand matrix to get a 48bits matrix of data's to apply the XOR with Ki
vector<int> E = {-1, 32, 1, 2, 3, 4, 5,
                 4, 5, 6, 7, 8, 9,
                 8, 9, 10, 11, 12, 13,
                 12, 13, 14, 15, 16, 17,
                 16, 17, 18, 19, 20, 21,
                 20, 21, 22, 23, 24, 25,
                 24, 25, 26, 27, 28, 29,
                 28, 29, 30, 31, 32, 1
                };

//PI_2 is used to shorten the result of xoring leftmost extended 48 bits and 48 bits of key for that round
vector<int> PI_2 = {-1, 35, 38, 46, 6, 43, 40, 14, 45,
                    33, 19, 26, 15, 23, 8, 22, 10,
                    12, 11, 5, 25, 27, 21, 16, 31,
                    28, 32, 34, 24, 9, 37, 2, 1
                   };

//permutation made after each SBox substitution for each round
vector<int> P = {-1, 16, 7, 20, 21, 29, 12, 28, 17,
                 1, 15, 23, 26, 5, 18, 31, 10,
                 2, 8, 24, 14, 32, 27, 3, 9,
                 19, 13, 30, 6, 22, 11, 4, 25
                };

//final permutation for data's after the 16 rounds
vector<int> PI_1 = {-1, 40, 8, 48, 16, 56, 24, 64, 32,
                    39, 7, 47, 15, 55, 23, 63, 31,
                    38, 6, 46, 14, 54, 22, 62, 30,
                    37, 5, 45, 13, 53, 21, 61, 29,
                    36, 4, 44, 12, 52, 20, 60, 28,
                    35, 3, 43, 11, 51, 19, 59, 27,
                    34, 2, 42, 10, 50, 18, 58, 26,
                    33, 1, 41, 9, 49, 17, 57, 25
                   };

//matrix that determine the shift for each round of keys
vector<int> SHIFT = { -1,1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};



vector<int> toBinary(string s)
{
    vector<int> temp;
    temp.push_back(-1);
    for(int i=0; i<8; i++)
    {
        char c=s[i];
        for(int j=7; j>=0; j--)
        {
            if(c&(1<<j))
                temp.push_back(1);
            else
                temp.push_back(0);
        }
    }
    return temp;
}

vector<int> transpose(vector<int> &matrix, vector<int> &transposeMatrix)
{
    vector<int> temp;
    temp.push_back(-1);
    for(int i=1; i<transposeMatrix.size(); i++)
        temp.push_back(matrix[transposeMatrix[i]]);
    return temp;
}

vector<int> getKey(vector<int> key, int round)
{
    // initial permutation on key 64->56
    key=transpose(key,CP_1);
    vector<int> temp;
    temp.push_back(-1);
    // shifting
    int shift = SHIFT[round+1];
    for(int i=shift+1; i<=28; i++)
        temp.push_back(key[i]);
    for(int i=1; i<=shift; i++)
        temp.push_back(key[i]);
    for(int i=28+shift+1; i<=56; i++)
        temp.push_back(key[i]);
    for(int i=28; i<=28+shift; i++)
        temp.push_back(key[i]);
    key=temp;
    // permutation on shifted key 56->48
    key=transpose(key,CP_2);
    return key;
}

vector<int> XOR(vector<int> a,vector<int> b)
{
    for(int i=1; i<a.size(); i++)
    {
        a[i]^=b[i];
    }
    return a;
}

void debug(vector<int> v,string s)
{
    cout << s << endl;
    for(int i=1;i<v.size();i++)
        cout << v[i] << " ";
    cout << endl;
}

vector<int> func( vector<int> R,vector<int> K)
{
    // expand matrix of R 32->48
    R=transpose(R,E);
    R=XOR(R,K);
    // shorten 48->32
    R=transpose(R,PI_2);
    //permutation made after each SBox substitution for each round
    R=transpose(R,P);
    return R;
}


string encryption(string key, string plain)
{
    vector<int> keyBinary,plainBinary;
    keyBinary=toBinary(key);
    plainBinary=toBinary(plain);

    //initial transposition
    vector<int> result=transpose(plainBinary,PI);

    //iteration
    for(int i=0; i<16; i++)
    {
        vector<int> K = getKey(keyBinary,i);
        vector<int> L,R;
        L.push_back(-1);
        R.push_back(-1);
        for(int i=1; i<=32; i++)
            L.push_back(result[i]);
        for(int i=33; i<=64; i++)
            R.push_back(result[i]);

        for(int i=1; i<=32; i++)
            result[i] = R[i];
        R=func(R,K);
        R=XOR(L,R);
        for(int i=33; i<=64; i++)
            result[i]=R[i-32];
    }

    // 32 bit swap
    vector<int> L,R;
    L.push_back(-1);
    R.push_back(-1);
    for(int i=1; i<=32; i++)
        L.push_back(result[i]);
    for(int i=33; i<=64; i++)
        R.push_back(result[i]);
    for(int i=1; i<=32; i++)
        result[i]=R[i];
    for(int i=33; i<=64; i++)
        result[i]=L[i-32];

    // inverse transposition
    result=transpose(result,PI_1);

    // to string
    string temp="";
    for(int i=0; i<8; i++)
    {
        int t=0;
        for(int j=1; j<=8; j++)
        {
            t=t*2+(result[8*i+j]);
        }
        temp+=char(t);
    }
    return temp;
}


string decryption(string key,string cipher)
{
    vector<int> keyBinary,cipherBinary;
    keyBinary=toBinary(key);
    cipherBinary=toBinary(cipher);

    // initial transposition
    vector<int> result=transpose(cipherBinary,PI);

    // swap
    vector<int> L,R;
    L.push_back(-1);
    R.push_back(-1);
    for(int i=1;i<=32;i++)
        L.push_back(result[i]);
    for(int i=33;i<=64;i++)
        R.push_back(result[i]);
    for(int i=1;i<=32;i++)
        result[i]=R[i];
    for(int i=33;i<=64;i++)
        result[i]=L[i-32];

    // iteration
    for(int i=15;i>=0;i--)
    {
        vector<int> K=getKey(keyBinary,i);

        vector<int> L,R;
        L.push_back(-1);
        R.push_back(-1);
        for(int i=1;i<=32;i++)
            L.push_back(result[i]);
        for(int i=33;i<=64;i++)
            R.push_back(result[i]);

        for(int i=33;i<=64;i++)
            result[i]=L[i-32];
        L=func(L,K);
        L=XOR(L,R);
        for(int i=1;i<=32;i++)
            result[i]=L[i];
    }

    result=transpose(result,PI_1);

    // to string
    string temp="";
    for(int i=0;i<8;i++)
    {
        int t=0;
        for(int j=1;j<=8;j++)
        {
            t=t*2+(result[8*i+j] );
        }
        temp+=char(t);
    }
    return temp;
}




int main()
{
    //freopen("out.txt", "w", stdout);
    string key="",plain="";
    getline(cin,key);
    getline(cin,plain);
    //padding
    while(plain.size()%8 != 0)
    {
        plain += "~";
    }
    //encryption
    string ciphered = "";
    for(int i=0;i<plain.size()/8;i++)
    {
        string temp = "";
        for(int j=0;j<8;j++)
        {
            temp += plain[i*8+j];
        }
        ciphered += encryption(key,temp);
    }
    cout << "Ciphered : " << ciphered << endl;
    //decryption
    string deciphered = "";
    for(int i=0;i<plain.size()/8;i++)
    {
        string temp="";
        for(int j=0; j<8; j++)
        {
            temp+=ciphered[i*8+j];
        }
        deciphered+=decryption(key,temp);
    }
    cout << "Deciphered : " << deciphered << endl;
}
