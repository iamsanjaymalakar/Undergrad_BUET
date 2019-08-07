#include<stdio.h>
#include<pthread.h>
#include<semaphore.h>
#include<queue>
#include<unistd.h>
#include<iostream>

#define SLEEP 0
#define cCake 5
#define vCake 5

using namespace std;


typedef struct cake Cake;

//semaphores and mutex
sem_t q1empty,q1full,q2empty,q2full,q3empty,q3full;
pthread_mutex_t consoleLock,q1Lock,q2Lock,q3Lock;

queue<Cake> q1,q2,q3;

struct cake
{
	int type,id; // 1 or 2
	cake(int t,int d)
	{
		type=t;
		id=d;
	}
};

void initSemMutex()
{
	sem_init(&q1empty,0,5);
	sem_init(&q1full,0,0);
	sem_init(&q2empty,0,5);
	sem_init(&q2full,0,0);
	sem_init(&q3empty,0,5);
	sem_init(&q3full,0,0);
	pthread_mutex_init(&q1Lock,0);
	pthread_mutex_init(&q2Lock,0);
	pthread_mutex_init(&q3Lock,0);
	pthread_mutex_init(&consoleLock,0);
}

void* funcChefX(void* arg)
{
	pthread_mutex_lock(&consoleLock);
	cout << "Chef X started making chocolate cakes" << endl;
	pthread_mutex_unlock(&consoleLock);
	for(int i=1;i<=cCake;i++)
	{
		pthread_mutex_lock(&consoleLock);
		cout << "Chef X wants to access queue 1" << endl;
		pthread_mutex_unlock(&consoleLock);
		sem_wait(&q1empty);
		pthread_mutex_lock(&consoleLock);
		cout << "Chef X accessed queue 1" << endl;
		pthread_mutex_unlock(&consoleLock);
		Cake tempCake(1,i);
		pthread_mutex_lock(&q1Lock);
		sleep(SLEEP);	
		q1.push(tempCake);
		pthread_mutex_unlock(&q1Lock);
		pthread_mutex_lock(&consoleLock);
		cout << "Chef X inserted chocolate cake " << i << " to queue 1" << endl;
		pthread_mutex_unlock(&consoleLock);	
		sem_post(&q1full);
	}
	pthread_mutex_lock(&consoleLock);
	cout << "Chef X finished working" << endl;
	pthread_mutex_unlock(&consoleLock);
	return NULL;
}

void* funcChefY(void* arg)
{
	pthread_mutex_lock(&consoleLock);
	cout << "Chef Y started making vanilla cakes" << endl;
	pthread_mutex_unlock(&consoleLock);
	for(int i=1;i<=vCake;i++)
	{
		pthread_mutex_lock(&consoleLock);
		cout << "Chef Y wants to access queue 1" << endl;
		pthread_mutex_unlock(&consoleLock);
		sem_wait(&q1empty);
		pthread_mutex_lock(&consoleLock);
		cout << "Chef Y accessed queue 1" << endl;
		pthread_mutex_unlock(&consoleLock);
		Cake tempCake(2,i);
		pthread_mutex_lock(&q1Lock);	
		sleep(SLEEP);
		q1.push(tempCake);	
		pthread_mutex_unlock(&q1Lock);
		pthread_mutex_lock(&consoleLock);
		cout << "Chef Y inserted vanilla cake " << i << " to queue 1" << endl;
		pthread_mutex_unlock(&consoleLock);
		sem_post(&q1full);
	}
	pthread_mutex_lock(&consoleLock);
	cout << "Chef Y finished working" << endl;
	pthread_mutex_unlock(&consoleLock);
	return NULL;
}

void* funcChefZ(void* arg)
{
	pthread_mutex_lock(&consoleLock);
	cout << "Chef Z started collecting cakes" << endl;
	pthread_mutex_unlock(&consoleLock);
	for(int i=1;i<=(cCake+vCake);i++)
	{	
		pthread_mutex_lock(&consoleLock);
		cout << "Chef Z wants to access queue 1" << endl;
		pthread_mutex_unlock(&consoleLock);
		sem_wait(&q1full);
		pthread_mutex_lock(&consoleLock);
		cout << "Chef Z accessed queue 1" << endl;
		pthread_mutex_unlock(&consoleLock);
		pthread_mutex_lock(&q1Lock);
		sleep(SLEEP);	
		Cake item = q1.front();
		q1.pop();
		pthread_mutex_unlock(&q1Lock);
		if(item.type==1)
		{
			pthread_mutex_lock(&consoleLock);
			cout << "Chef Z popped chocolate cake " << item.id << " from queue 1" << endl;
			pthread_mutex_unlock(&consoleLock);
		}
		else if(item.type==2)
		{
			pthread_mutex_lock(&consoleLock);
			cout << "Chef Z popped vanila cake " << item.id << " from queue 1" << endl;
			pthread_mutex_unlock(&consoleLock);
		}		
		sem_post(&q1empty);
		if(item.type==1)
		{
			pthread_mutex_lock(&consoleLock);
			cout << "Chef Z wants to access queue 3" << endl;
			pthread_mutex_unlock(&consoleLock);
			sem_wait(&q3empty);
			pthread_mutex_lock(&consoleLock);
			cout << "Chef Z accessed queue 3" << endl;
			pthread_mutex_unlock(&consoleLock);
			pthread_mutex_lock(&q3Lock);
			sleep(SLEEP);	
			q3.push(item);
			pthread_mutex_unlock(&q3Lock);
			pthread_mutex_lock(&consoleLock);
			cout << "Chef Z inserted chocolate cake " << item.id << " to queue 3" << endl;
			pthread_mutex_unlock(&consoleLock);	
			sem_post(&q3full);
		}
		else if(item.type==2)
		{
			pthread_mutex_lock(&consoleLock);
			cout << "Chef Z wants to access queue 2" << endl;
			pthread_mutex_unlock(&consoleLock);
			sem_wait(&q2empty);
			pthread_mutex_lock(&consoleLock);
			cout << "Chef Z accessed queue 2" << endl;
			pthread_mutex_unlock(&consoleLock);
			pthread_mutex_lock(&q2Lock);
			sleep(SLEEP);	
			q2.push(item);
			pthread_mutex_unlock(&q2Lock);
			pthread_mutex_lock(&consoleLock);
			cout << "Chef Z inserted vanilla cake " << item.id << " to queue 2" << endl;
			pthread_mutex_unlock(&consoleLock);	
			sem_post(&q2full);
		}
	}
	pthread_mutex_lock(&consoleLock);
	cout << "Chef Z finished working" << endl;
	pthread_mutex_unlock(&consoleLock);
	return NULL;
}

void* funcWaiter1(void* arg)
{
	pthread_mutex_lock(&consoleLock);
	cout << "Waiter 1 started collecting chocolate cakes" << endl;
	pthread_mutex_unlock(&consoleLock);
	for(int i=1;i<=cCake;i++)
	{	
		pthread_mutex_lock(&consoleLock);
		cout << "Waiter 1 wants to access queue 3" << endl;
		pthread_mutex_unlock(&consoleLock);
		sem_wait(&q3full);
		pthread_mutex_lock(&consoleLock);
		cout << "Waiter 1 accessed queue 3" << endl;
		pthread_mutex_unlock(&consoleLock);
		pthread_mutex_lock(&q3Lock);
		sleep(SLEEP);
		Cake item = q3.front();
		q3.pop();
		pthread_mutex_unlock(&q3Lock);
		pthread_mutex_lock(&consoleLock);
		cout << "Waiter 1 popped chocolate cake " << item.id << " from queue 3" << endl;
		pthread_mutex_unlock(&consoleLock);		
		sem_post(&q1empty);	
	}
	pthread_mutex_lock(&consoleLock);
	cout << "Waiter 1 finished working" << endl;
	pthread_mutex_unlock(&consoleLock);
	return NULL;
}

void* funcWaiter2(void* arg)
{
	pthread_mutex_lock(&consoleLock);
	cout << "Waiter 2 started collecting vanilla cakes" << endl;
	pthread_mutex_unlock(&consoleLock);
	for(int i=1;i<=vCake;i++)
	{	
		pthread_mutex_lock(&consoleLock);
		cout << "Waiter 2 wants to access queue 2" << endl;
		pthread_mutex_unlock(&consoleLock);
		sem_wait(&q2full);
		pthread_mutex_lock(&consoleLock);
		cout << "Waiter 2 accessed queue 2" << endl;
		pthread_mutex_unlock(&consoleLock);
		pthread_mutex_lock(&q2Lock);
		sleep(SLEEP);
		Cake item = q2.front();
		q2.pop();
		pthread_mutex_unlock(&q2Lock);
		pthread_mutex_lock(&consoleLock);
		cout << "Waiter 2 popped vanilla cake " << item.id << " from queue 3" << endl;
		pthread_mutex_unlock(&consoleLock);		
		sem_post(&q2empty);	
	}
	pthread_mutex_lock(&consoleLock);
	cout << "Waiter 2 finished working" << endl;
	pthread_mutex_unlock(&consoleLock);
	return NULL;
}

int main()
{
	//

	//threads
	pthread_t chefX;
	pthread_t chefY;
	pthread_t chefZ;
	pthread_t waiter1;
	pthread_t waiter2;

	initSemMutex();

	//creating threads
	pthread_create(&chefX,NULL,funcChefX,NULL);
	pthread_create(&chefY,NULL,funcChefY,NULL);
	pthread_create(&chefZ,NULL,funcChefZ,NULL);
	pthread_create(&waiter1,NULL,funcWaiter1,NULL);
	pthread_create(&waiter2,NULL,funcWaiter2,NULL);
	
	//joing threads
	pthread_join(chefX,NULL);
	pthread_join(chefY,NULL);
	pthread_join(chefZ,NULL);
	pthread_join(waiter1,NULL);
	pthread_join(waiter2,NULL);

	cout << "main ending" << endl;
}
