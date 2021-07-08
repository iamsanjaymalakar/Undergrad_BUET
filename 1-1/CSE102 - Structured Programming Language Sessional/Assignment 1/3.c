// what was i smoking lol

#include<stdio.h>
#include<limits.h>

int max(int a,int b)
{
    if(a>b)
        return a;
    else
        return b;
}

int mss(int arr[],int n)
{
	int ans = INT_MIN,i,start_index,sub_array_size;
	for(sub_array_size = 1;sub_array_size <= n; ++sub_array_size)
	{
		for(start_index = 0;start_index < n; ++start_index)
		{
			if(start_index+sub_array_size > n)
				break;
			int sum = 0;
			for(i = start_index; i < (start_index+sub_array_size); i++)
				sum+= arr[i];
			ans = max(ans,sum);
		}
	}
	return ans;
}

int main()
{
    int N,i,start_index,sub_array_size;
    printf("Enter array size :");
    scanf("%d",&N);
    int arr[N];
    for(i=0; i<N; i++)
        scanf("%d",&arr[i]);
    printf("Maximum sum is %d",mss(arr,N));

    int ans = INT_MIN;
	for(sub_array_size = 1;sub_array_size <= N; ++sub_array_size)
	{
		for(start_index = 0;start_index < N; ++start_index)
		{
			if(start_index+sub_array_size > N)
				break;
			int sum = 0;
			for(i = start_index; i < (start_index+sub_array_size); i++)
				{sum+= arr[i];
				if(mss(arr,N)==sum)
                    printf("\n%d %d",start_index,i);
				}
		}
	}

    getch();
    return 0;
}
