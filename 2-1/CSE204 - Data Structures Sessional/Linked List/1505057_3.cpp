#include<stdio.h>
#include<stdlib.h>


#define NULL_VALUE -99999
#define SUCCESS_VALUE 99999

struct listNode
{
    int item;
    struct listNode * next;
};

struct listNode * list;
struct listNode * tail;

void initializeList()
{
    list = 0;  //initially set to NULL
    tail = 0;
}

struct listNode * searchItem(int item)
{
    struct listNode * temp ;
    temp = list ; //start at the beginning
    while (temp != 0)
    {
        if (temp->item == item) return temp ;
        temp = temp->next ; //move to next node
    }
    return 0 ; //0 means invalid pointer in C, also called NULL value in C
}

void printList()
{
    struct listNode * temp;
    temp = list;
    while(temp!=0)
    {
        printf("%d->", temp->item);
        temp = temp->next;
    }
    printf("\n");
}

//add required codes to insert item at the beginning, remember to properly set the tail pointer!

int insertItem(int item) //insert at the beginning of the linked list
{
    struct listNode * newNode ;
    newNode = (struct listNode*) malloc (sizeof(struct listNode)) ;
    newNode->item = item ;
    if(!list)
    {
        newNode->next=NULL;
        list=newNode;
        tail=newNode;
    }
    else
    {
        newNode->next=list;
        list=newNode;
    }
    return SUCCESS_VALUE ;
}

int insertItem(int oldItem, int newItem)
{
    struct listNode *temp,*prev,*newNode;
    temp=list;
    prev=NULL;
    newNode=(struct listNode*)malloc(sizeof(struct listNode));
    newNode->item=newItem;
    int f=0;
    if(!list)
    {
        newNode->next=NULL;
        list=newNode;
        tail=newNode;
        return SUCCESS_VALUE;
    }
    while(temp!=NULL && temp->item!=oldItem)
    {
        prev=temp;
        temp=temp->next;
        if(temp!=NULL && temp->item==oldItem)
        {
            f=1;
            break;
        }
    }
    if(list->item==oldItem)
    {
        newNode->next=list;
        list=newNode;
        return SUCCESS_VALUE;
    }
    else
    {
        if(f)
        {
            prev->next=newNode;
            newNode->next=temp;
            return SUCCESS_VALUE;
        }
        else
        {
            newNode->next=list;
            list=newNode;
            return SUCCESS_VALUE;
        }
    }
}



//add required codes to delete item, remember to properly set the tail pointer!
int deleteItem(int item)
{
    struct listNode *temp, *prev ;
    temp = list ; //start at the beginning
    if(!list)
        return NULL_VALUE;
    while (temp != 0)
    {
        if (temp->item == item) break ;
        prev = temp;
        temp = temp->next ; //move to next node
    }
    if (temp == 0) return NULL_VALUE ; //item not found to delete
    if(temp==list && temp==tail)
    {
        initializeList();
    }
    else if (temp == list) //delete the first node
    {
        list = list->next ;
        free(temp) ;
    }
    else if(temp==tail)
    {
        prev->next=NULL;
        tail=prev;
        free(temp);
    }
    else
    {
        prev->next = temp->next ;
        free(temp);
    }
    return SUCCESS_VALUE ;
}

int deleteAfter(int item)
{
    struct listNode *cur,*temp;
    cur=list;
    if(!list)
        return NULL_VALUE;
    while(cur!=NULL && cur->next!=NULL)
    {
        if(cur->item==item)
        {
            temp=cur->next;
            cur->next=cur->next->next;
            if(temp==tail)
                tail=cur;
            free(temp);
            return SUCCESS_VALUE;
        }
        cur=cur->next;
    }
    return NULL_VALUE;
}

int insertLast(int item)
{
    struct listNode *temp,*newItem;
    temp=list;
    newItem=(struct listNode*)malloc(sizeof(struct listNode));
    newItem->item=item;
    if(!list)
    {
        newItem->next=NULL;
        list=newItem;
        tail=newItem;
        return SUCCESS_VALUE;
    }
    else
    {
        newItem->next=NULL;
        tail->next=newItem;
        tail=newItem;
    }
    return SUCCESS_VALUE;
}


int main(void)
{
    initializeList();
    while(1)
    {
        printf("1. Insert new item. \n2. Delete item. \n3. Search item. \n");
        printf("4. Insert Before\n5. Delete after.\n6. Insert last.\n7. Print. \n8. exit.\n");

        int ch;
        scanf("%d",&ch);
        if(ch==1)
        {
            int item;
            scanf("%d",&item);
            insertItem(item);
        }
        else if(ch==4)
        {
            int Old,New;
            scanf("%d %d",&Old,&New);
            insertItem(Old,New);
        }
        else if(ch==2)
        {
            int item;
            scanf("%d", &item);
            deleteItem(item);
        }
        else if(ch==3)
        {
            int item;
            scanf("%d", &item);
            struct listNode * res = searchItem(item);
            if(res!=0) printf("Found.\n");
            else printf("Not found.\n");
        }
        else if(ch==5)
        {
            int item;
            scanf("%d",&item);
            deleteAfter(item);
        }
        else if(ch==6)
        {
            int item;
            scanf("%d",&item);
            insertLast(item);
        }
        else if(ch==7)
        {
            printList();
        }
        else if(ch==8)
        {
            break;
        }
       // printf("%d %d\n",list->item,tail->item);
    }

}
