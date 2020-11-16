#include<iostream>
#include<stdio.h>
#include<string.h>
#include <fstream>
#include <vector> 
#include<stdlib.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <unistd.h>
#include <pthread.h>

using namespace std;

#define inf 1e5
#define N 4
vector<int> list;
string initIP = "192.168.0.";
pthread_t thread;
int sockfd,id; 
int bind_flag;
int bytes_received;
socklen_t addrlen;
struct sockaddr_in client;
struct sockaddr_in server;
int neighbour[N];
int bakcupNeighbour[N];
int count[N];
int previous[N];

int getID(string ip)
{
	int id,i=0,cnt=0,j=0;
	string temp="";
	while(ip[i]!='\0')
	{
		if(ip[i]=='.')
			cnt++;
		if(cnt==3 && ip[i]!='.')
		{
			temp+=ip[i];
			j++;
		}
		i++;
	}
	temp[j]='\0';
	return atoi(temp.c_str());
}


struct node
{
	int dist[10];
	int nextNode[10];
	int id;
}router;

void *func(void* arg){
	server.sin_family = AF_INET;
	server.sin_port = htons(4747);
	char *buffer;
	buffer=(char *)arg;
	string temp=buffer;
	if(temp.substr(0,3)=="clk")
	{
		for ( auto &i : list) 
		{
    		string temp=initIP+to_string((int)(i));
    		server.sin_addr.s_addr = inet_addr(temp.c_str());
    		buffer=(char*)&router;
    		sendto(sockfd, buffer, 1024, 0, (struct sockaddr*) &server, sizeof(sockaddr_in));
    		if(previous[(int)i]>=3 && count[(int)i]==0)
    		{
    			cout << "Router " << (int)i << " up." << endl;
    			neighbour[(int)i]=bakcupNeighbour[(int)i];
    			if(router.nextNode[(int)i]==(int)i)
    			{
    				router.dist[(int)i]=bakcupNeighbour[(int)i];
    			}
    		}
    		previous[(int)i]=count[(int)i]++;
    		if(count[(int)i]==3)
    		{
    			cout << "Router " << (int)i << " down." << endl << endl;
    			bakcupNeighbour[(int)i]=neighbour[(int)i];
    			neighbour[(int)i]=inf;
    			if(router.nextNode[(int)i]==(int)i)
    			{
    				router.dist[(int)i]=inf;
    			}
    			for(int j=1;j<=N;j++)
    			{
    				if(j!=id)
    				{
    					if(router.nextNode[j]==(int)i)
    					{
    						router.dist[j]=inf;
    					}
    				}
    			}
    			
    		}
		}
	}
	else if(temp.substr(0,4)=="send")
	{
			unsigned char a[4],b[4],t1=buffer[12],t2=buffer[13];
			for(int i=0;i<4;i++)
				a[i]=buffer[4+i];
			for(int i=0;i<4;i++)
				b[i]=buffer[8+i];
			int size=buffer[12] | buffer[13]<<8;
			string msg="";
			for(int i=0;i<size;i++)
				msg+=buffer[14+i];
			if(b[3]==id)
			{
				cout << msg << " packet reached destination" << endl; 
			}
			else
			{
				if(router.dist[(int)b[3]]<inf)
				{
					cout << msg <<" packet forwarded to "+initIP << router.nextNode[(int)b[3]] << endl;
					string temp=initIP+to_string(router.nextNode[(int)b[3]]);
    				server.sin_addr.s_addr = inet_addr(temp.c_str());
    				strcpy(buffer,"frwd");
    				for(int i=0;i<4;i++)
    					buffer[4+i]=b[i];
    				buffer[8]=t1;
    				buffer[9]=t2;
    				for(int i=0;i<size;i++)
    					buffer[10+i]=msg[i];
    				sendto(sockfd, buffer, 1024, 0, (struct sockaddr*) &server, sizeof(sockaddr_in));
				}
				else
				{
					cout << initIP;
					printf("%d  unreachable.\n",b[3]);
				}
			}
	}
	else if(temp.substr(0,4)=="frwd")
	{
			int size=buffer[8] | buffer[9]<<8;
			unsigned char b[4],t1=buffer[8],t2=buffer[9];
    		for(int i=0;i<4;i++)
				b[i]=buffer[4+i];
			string msg="";
			for(int i=0;i<size;i++)
				msg+=buffer[10+i];
			if((int)buffer[7]==id)
			{
				cout << msg << " packet reached destination" << endl; 
			}
			else
			{
				if(router.dist[(int)buffer[7]]<inf)
				{
					cout << msg <<" packet forwarded to "+initIP << router.nextNode[(int)buffer[7]] << endl;
					string temp=initIP+to_string(router.nextNode[(int)buffer[7]]);
    				server.sin_addr.s_addr = inet_addr(temp.c_str());
    				strcpy(buffer,"frwd");
    				for(int i=0;i<4;i++)
    					buffer[4+i]=b[i];
    				buffer[8]=t1;
    				buffer[9]=t2;
    				for(int i=0;i<size;i++)
    					buffer[10+i]=msg[i];
    				sendto(sockfd, buffer, 1024, 0, (struct sockaddr*) &server, sizeof(sockaddr_in));
				}
				else
				{
					cout << initIP;
					printf("%d  unreachable.\n",buffer[7]);
				}
			}
	}
	else if(temp.substr(0,4)=="cost")
	{
		unsigned char a[4],b[4];
		for(int i=0;i<4;i++)
				a[i]=buffer[4+i];
		for(int i=0;i<4;i++)
				b[i]=buffer[8+i];
		int cost=buffer[12] | buffer[13]<<8;
		int dest;
		if(a[3]==id)
			dest=b[3];
		else 
			dest=a[3];
		if(router.nextNode[dest]==dest)
		{
			router.dist[dest]=cost;
			neighbour[dest]=cost;
		}
		else
		{
			if(router.dist[dest]>cost)
			{
				router.dist[dest]=cost;
				router.nextNode[dest]=dest;
				neighbour[dest]=cost;
			}
		}
	}
	else if(temp.substr(0,4)=="show")
	{
		cout << "destination      next hop       cost" << endl;
		cout << "-----------      --------       ----" << endl;
		for(int i=1;i<=N;i++)
		{
			if(id!=i)
			{
				if(router.nextNode[i]!=-1)
					cout << initIP + to_string(i) + "     " + initIP + to_string(router.nextNode[i]) + "     " << router.dist[i] << endl;
				else
					cout << initIP + to_string(i) + "       " + " - " + "           " << router.dist[i] << endl;
			}
		}
		cout << endl;
	}
	else
	{
		struct node *table;
    	table=(struct node*)buffer;
    	count[table->id]=0;
    	int i=id,j=table->id;
    	for(int k=1;k<=N;k++)
    	{
    		if(k!=id)
    		{
    			if(router.nextNode[k]==j)
    			{
    				router.dist[k]=neighbour[j]+table->dist[k];
    			}
    			if(router.dist[k]>router.dist[j]+table->dist[k])
    			{
    				router.dist[k]=router.dist[j]+table->dist[k];
    				router.nextNode[k]=router.nextNode[j];
    			}
    			if(router.dist[k]>neighbour[k])
    			{
    				router.dist[k]=neighbour[k];
    				router.nextNode[k]=k;
    			}
    		}
    	}
	}

    //return;
}

int main(int argc, char *argv[])
{
	if(argc<=2)
	{
		cout << "enter ip and file as argument" << endl;
		return 0;
	}
	string ip=argv[1];
	id=getID(ip);
	router.id=id;
	for(int i=1;i<=N;i++)
	{
		if(i!=id)
		{
			router.dist[i]=inf;
			router.nextNode[i]=-1;
			neighbour[i]=inf;
		}
		else
		{
			router.dist[i]=0;
			router.nextNode[i]=i;
			neighbour[i]=0;
		}
	}
	ifstream file(argv[2]);
	string ip1,ip2;
	int cost;
	while(file >> ip1 >> ip2 >> cost)
	{
		int id1=getID(ip1),id2=getID(ip2);
		if(id1==id || id2==id)
		{
			if(id1==id)
			{
				router.dist[id2] = cost;
				router.nextNode[id2] = id2;
				list.push_back(id2);
				neighbour[id2]=cost;
			}
			else
			{ 
				router.dist[id1] = cost;
				router.nextNode[id1] = id1;
				list.push_back(id1);
				neighbour[id1]=cost;
			}
		}
	}
	
	//printing initial routing table
	cout << "destination      next hop       cost" << endl;
	cout << "-----------      --------       ----" << endl;
	for(int i=1;i<=N;i++)
	{
		if(id!=i)
		{
			if(router.nextNode[i]!=-1)
				cout << initIP + "." + to_string(i) + "     " + initIP + "." + to_string(router.nextNode[i]) + "     " << router.dist[i] << endl;
			else
				cout << initIP + "." + to_string(i) + "       " + " - " + "           " << router.dist[i] << endl;
		}
	}
	cout << endl;

	//creating server and client for router
	char buffer[1024];
	struct sockaddr_in tempAddress;
	//server
	client.sin_family = AF_INET;
	client.sin_port = htons(4747);
	client.sin_addr.s_addr = inet_addr(argv[1]);
	sockfd = socket(AF_INET, SOCK_DGRAM, 0);
	bind_flag = bind(sockfd, (struct sockaddr*) &client, sizeof(sockaddr_in));
	//server listening 
	while(true)
	{
		bytes_received = recvfrom(sockfd, buffer, 1024, 0, (struct sockaddr*) &tempAddress, &addrlen);
		//printf("[%s:%d]: %s\n", inet_ntoa(tempAddress.sin_addr), ntohs(tempAddress.sin_port), buffer);
		//pthread_create(&thread,NULL,func,(void*)buffer);
		func(buffer);
	}

	return 0;
}
