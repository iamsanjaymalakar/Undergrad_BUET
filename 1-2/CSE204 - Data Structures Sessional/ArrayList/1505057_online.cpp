#include<stdio.h>
#include<stdlib.h>

#define LIST_INIT_SIZE 2
#define NULL_VALUE -99999
#define SUCCESS_VALUE 99999

int listMaxSize;
int * list;
int length;

int shrink();
void initializeList()
{
    listMaxSize = LIST_INIT_SIZE;
    list = (int*)malloc(sizeof(int)*listMaxSize) ;
    length = 0 ;
}

int searchItem(int item)
{
    int i = 0;
    for (i = 0; i < length; i++)
    {
        if( list[i] == item ) return i;
    }
    return NULL_VALUE;
}

int insertItem(int newitem)
{
    if(!length)
    {
        initializeList();
    }
    int * tempList ;
    if (length == listMaxSize)
    {
        //allocate new memory space for tempList
        listMaxSize = 2 * listMaxSize ;
        tempList = (int*) malloc (listMaxSize*sizeof(int)) ;
        int i;
        for( i = 0; i < length ; i++ )
        {
            tempList[i] = list[i] ; //copy all items from list to tempList
        }
        free(list) ; //free the memory allocated before
        list = tempList ; //make list to point to new memory
    };

    list[length] = newitem ; //store new item
    length++ ;
    return SUCCESS_VALUE ;
}


int deleteItemAt(int position) //version 2, do not preserve order of items
{
    if ( position >= length ) return NULL_VALUE;
    list[position] = list[length-1] ;
    length-- ;
    shrink();
    return SUCCESS_VALUE ;
}


int deleteItem(int item)
{
    int position;
    position = searchItem(item) ;
    if ( position == NULL_VALUE ) return NULL_VALUE;
    deleteItemAt(position) ;
    shrink();
    return SUCCESS_VALUE ;
}

void printList()
{
    int i;
    for(i=0; i<length; i++)
        printf("%d ", list[i]);
    printf("Current size: %d, current length: %d\n", listMaxSize, length);
}

int getLength()
{
    return length;
}

int removeDuplicate(int item)
{
    int c=0;
    int found=0;
    for(int i=0; i<length; i++)
    {
        if(item==list[i])
        {
            c++;
            if(c>1)
            {
                if(i==(length-1))
                {
                    length--;
                }
                else
                {
                    for(int j=i; j<length-1; j++)
                    {
                        list[j]=list[j+1];
                    }
                    length--;
                }

                found=1;
            }

        }
    }
    shrink();
    if(found)
        return SUCCESS_VALUE;
    else
        return NULL_VALUE;
}
int insertItemAt(int pos, int item)
{
    if(length==listMaxSize)
    {
        int *temp;
        listMaxSize*=2;
        temp=(int*)malloc(sizeof(int)*listMaxSize);
        for(int i=0; i<listMaxSize; i++)
        {
            temp[i]=list[i];
        }
        free(list);
        list=temp;
    }
    if(pos>=length)
    {
        return NULL_VALUE;
    }
    else
    {
        list[length++]=list[pos-1];
        list[pos-1]=item;
        return SUCCESS_VALUE;
    }
}

int shrink()
{
    if(length<=(listMaxSize/2) && listMaxSize!=LIST_INIT_SIZE)
    {
        int * temp;
        listMaxSize/=2;
        temp=(int*) malloc(sizeof(int)*listMaxSize);
        for(int i=0; i<length; i++)
        {
            temp[i]=list[i];
        }
        free(list);
        list=temp;
        return SUCCESS_VALUE;
    }
    else
        return NULL_VALUE;
}

int deleteLast()
{
    if(!length)
        return NULL_VALUE;
    length--;
    int last=list[length];
    shrink();
    return last;
}

int Clear()
{
    if(list)
    {
        free(list);
        list=NULL;
        length=0;
        listMaxSize=0;
        return SUCCESS_VALUE;
    }
}

int deleteAll()
{
    length=0;
    while(listMaxSize>LIST_INIT_SIZE)
    {
        shrink();
    }
    return SUCCESS_VALUE;
}

int main(void)
{
    initializeList();
    while(1)
    {
        printf("\n1. Insert new item.\n2. Delete item at. \n3. Delete item.\n");
        printf("4. Insert new item at a given position.\n5. Lenght. \n6. Print. \n7. exit.\n8. Shrink.\n");
        printf("9. Delete Last Item.\n10. Clear list.\n11. Delete All\n12. Remove duplicate\n\n");
        int ch;
        scanf("%d",&ch);
        if(ch==1)
        {
            int item;
            scanf("%d", &item);
            insertItem(item);
        }
        else if(ch==2)
        {
            int pos;
            scanf("%d", &pos);
            deleteItemAt(pos);
        }
        else if(ch==3)
        {
            int item;
            scanf("%d", &item);
            deleteItem(item);
        }
        else if(ch==4)
        {
            int pos,item;
            printf("Enter the postion and item :\n");
            scanf("%d %d",&pos,&item);
            insertItemAt(pos,item);
        }
        else if(ch==5)
        {
            printf("Lenght of list : %d\n",getLength());
        }
        else if(ch==6)
        {
            printList();
        }
        else if(ch==7)
        {
            break;
        }
        else if(ch==8)
        {
            shrink();
        }
        else if(ch==9)
        {
            deleteLast();
        }
        else if(ch==10)
        {
            Clear();
        }
        else if(ch==11)
        {
            deleteAll();
        }
        else if(ch==12)
        {
            printf("Enter the number to remiove duplicate :\n");
            int item;
            scanf("%d",&item);
            removeDuplicate(item);
        }
    }

}

int main0()
{
    initializeList();
    char exp[50];
    scanf("%s",exp);
    int i=0;
    while(exp[i])
    {
        int num;
        if(exp[i]>=48 && exp[i]<=57)
        {
            num=exp[i]-48;
            insertItem(num);
        }
        else if(exp[i]=='+' || exp[i]=='-' || exp[i]=='*' || exp[i]=='/')
        {
            int a=deleteLast(),b=deleteLast(),temp;
            if(exp[i]=='+')
            {
                temp=a+b;
            }
            else if(exp[i]=='-')
            {
                temp=b-a;
            }
            else if(exp[i]=='*')
            {
                temp=a*b;
            }
            else if(exp[i]=='/')
            {
                temp=b/a;
            }
            insertItem(temp);
        }
        i++;
    }
    int ans=deleteLast();
    printf("%s = %d\n",exp,ans);
    Clear();
}
