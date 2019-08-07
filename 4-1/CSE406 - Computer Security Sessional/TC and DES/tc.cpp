#include<bits/stdc++.h>

using namespace std;

int N;
map<int,int> keyMap;

int countPairs(string s1,string s2)
{
    int f1[26] = { 0 };
    int f2[26] = { 0 };
    int cnt = 0;
    for (int i = 0; i < s1.length(); i++)
        f1[s1[i] - 'A']++;
    for (int i = 0; i < s2.length(); i++)
        f2[s2[i] - 'A']++;
    for (int i = 0; i < 26; i++)
        cnt += (min(f1[i], f2[i]));
    return cnt;
}


int main()
{
    freopen("transposition-57.txt","r",stdin);
    freopen("output.txt","w",stdout);
    string s;
    string words[10];
    for(int i=0; i<10; i++)
        words[i]="";
    getline(cin,s);
    cout << "Cipher : " << s << endl;
    string word;
    getline(cin,word);
    getline(cin,word);
    for(int i=0; i<word.length(); i++)
    {
        if(word[i]==',')
            N++,i++;
        else if(word[i]==' ')
            continue;
        else
            words[N].push_back(word[i]);
    }
    N++;
    cout << "Words : ";
    for(int i=0; i<N; i++)
    {
        cout << words[i] << " ";
        transform(words[i].begin(),words[i].end(),words[i].begin(),::toupper);
    }
    cout << endl << endl;
    for(int k=2; k<=10; k++)
    {
        cout << "Key length : " << k << endl;
        int l=ceil(s.length()/k);
        for(int i=0; i<l; i++)
        {
            string temp="";
            for(int j=0; j<k; j++)
            {
                temp.push_back(s[i+j*l]);
            }
            for(int m=0; m<words[0].length()-k+1; m++)
            {
                int key[k],used[k];
                if(countPairs(words[0].substr(m,k),temp)==k)
                {
                    string tt=words[0].substr(m,k);
                    cout << words[0] <<" " << words[0].substr(m,k)<<" " << temp;
                    for(int i=0; i<k; i++)
                        used[i]=0;
                    for(int g=0; g<k; g++)
                    {
                        for(int h=0; h<k; h++)
                        {
                            if(tt[g]==temp[h] && !used[h])
                            {
                                key[g]=h+1,used[h]=1;
                                break;
                            }
                        }
                    }
                    for(int i=0; i<k; i++)
                        cout << " " <<  key[i] << " ";
                    cout << endl;
                    //
                    for(int i=0; i<k; i++)
                    {
                        keyMap[key[i]]=i+1;
                    }
                    //
                    string plain="";
                    for(int m=0; m<l; m++)
                    {
                        for(int j=0; j<k; j++)
                        {
                            plain.push_back(s[m+(key[j]-1)*l]);
                        }
                    }
                    cout << plain << endl;
                    // check for other words
                    int c=0;
                    for(int w=1; w<N; w++)
                    {
                        for(int i=0; i<plain.length()-words[w].length()+1; i++)
                        {
                            if(!plain.substr(i,words[w].length()).compare(words[w]))
                                cout << plain.substr(i,words[w].length()) << " matched" << endl,c++;
                        }
                    }
                    if(c==N-1)
                    {
                        cout << "Plain text : " << endl;
                        transform(plain.begin(), plain.end(), plain.begin(), ::tolower);
                        cout << plain << endl;
                        cout << "Key length : " << k << endl;
                        cout << "Order : " ;
                        for(int i=0; i<k; i++)
                            cout << key[i] << " ";
                        cout << endl;
                        cout << "Encoded using found key : ";
                        int l=ceil(plain.length()/k);
                        string encoded="";
                        //
                        cout << endl;
                        for(int i=0; i<k; i++)
                        {
                            cout << keyMap[i+1] << " ";
                        }
                        cout << endl;
                        //
                        for(int j=0; j<k; j++)
                        {
                            for(int i=0; i<plain.length(); i=i+k)
                            {
                                encoded.push_back(plain[i+(keyMap[j+1]-1)]-'a'+'A');
                            }
                        }
                        cout << encoded << endl;
                        int cnt=0;
                        for(int i=0; i<encoded.length(); i++)
                        {
                            if(s[i]==encoded[i])
                                cnt++;
                        }
                        cout << "Accuracy : " << cnt/encoded.length()*100 << "%" << endl;
                        return 0;
                    }
                    else
                    {
                        cout << c << " words matched" << endl;
                    }
                    cout << endl;
                }
            }
        }
        cout << endl << endl;
    }
}


