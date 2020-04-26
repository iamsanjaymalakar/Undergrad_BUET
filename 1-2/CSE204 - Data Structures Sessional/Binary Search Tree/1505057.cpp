#include<stdio.h>
#include<stdlib.h>

#define FALSE_VALUE 0
#define TRUE_VALUE 1

typedef struct treeNode Node;

struct treeNode
{
    int item;
    struct treeNode * left; //points to left child
    struct treeNode * right; //points to right child
};

struct treeNode * root;

void initializeTree()
{
    root = 0;
}


struct treeNode * searchItem(struct treeNode * node, int item)
{
    if(node==0) return 0;
    if(node->item==item) return node; //found, return node
    struct treeNode * t = 0;
    if(item < node->item)
        t = searchItem(node->left, item); //search in the left sub-tree
    else
        t = searchItem(node->right, item); //search in the right sub-tree
    return t;
};


struct treeNode * makeTreeNode(int item)
{
    struct treeNode * node ;
    node = (struct treeNode *)malloc(sizeof(struct treeNode));
    node->item = item;
    node->left = 0;
    node->right = 0;
    return node;
};

struct treeNode * insertItem(struct treeNode * node, int item)
{
    if(node==0) //insert as the root as the tree is empty
    {
        struct treeNode * newNode = makeTreeNode(item);
        root = newNode;
        return newNode;
    }

    if(node->item==item) return 0; //already an item exists, so return NULL

    if(item<node->item && node->left==0) //insert as the left child
    {
        struct treeNode * newNode = makeTreeNode(item);
        node->left = newNode;
        return newNode;
    }

    if(item>node->item && node->right==0) //insert as the right child
    {
        struct treeNode * newNode = makeTreeNode(item);
        node->right = newNode;
        return newNode;
    }

    if(item<node->item)
        return insertItem(node->left, item); //insert at left sub-tree
    else
        return insertItem(node->right, item); //insert at right sub-tree
}


int calcNodeHeight(struct treeNode * node) //return height of a node
{
    if(node==0) return -1;
    int l, r;
    l = calcNodeHeight(node->left);
    r = calcNodeHeight(node->right);
    if(l>r) return l+1;
    else return r+1;
}


int calcHeight(int item) //return height of an item in the tree
{
    struct treeNode * node = 0;
    node = searchItem(root, item);
    if(node==0) return -1; //not found
    else return calcNodeHeight(node);
}

int getSize(struct treeNode * node)
{
    if(node==0)
        return 0;
    return getSize(node->left)+getSize(node->right)+1;
}

int calcNodeDepth(struct treeNode * node) //return depth of a node
{
    Node * temp = root;
    int depth=0;
    while(temp->item!=node->item)
    {
        if(node->item>temp->item)
            temp=temp->right;
        else
            temp=temp->left;
        depth++;
    }
    return depth;
}


int calcDepth(int item)//return depth of an item in the tree
{
    struct treeNode * node = 0;
    node = searchItem(root, item);
    if(node==0) return -1; //not found
    else return calcNodeDepth(node);
}


int getMinItem() //returns the minimum item in the tree
{
    if(root==0)
        return -1;
    Node * temp=root;
    while(temp->left!=0)
        temp=temp->left;
    return temp->item;
}

Node* getParent(Node* node, int item)
{
    if (node == 0)
        return 0;
    if(node->left == 0 && node->right == 0)
        return 0;
    if( (node->left != 0 && node->left->item == item)|| (node->right != 0 && node->right->item == item))
        return node;
    if(node->item > item)
        return getParent(node->left,item);
    else
        return getParent(node->right,item);
}


int deleteItem(struct treeNode *node, int item)
{
    Node *temp=searchItem(node,item);
    if(temp==0)
        return FALSE_VALUE;
    Node *parent;
    if(temp!=root)
        parent=getParent(root,temp->item);
    if(temp->left!=0 && temp->right!=0) //with both children
    {
        Node *successor=temp->right;
        while(successor->left!=0)
        {
            successor=successor->left;
        }
        int value= successor->item;
        deleteItem(temp->right,successor->item);
        temp->item=value;
    }
    else if(temp->left==0 && temp->right==0) // with no children
    {
        if(temp==root)
        {
            free(temp);
            root=NULL;
        }
        else
        {
            if(parent->left==temp)
                parent->left=NULL;
            else
                parent->right=NULL;
            free(temp);
        }
    }
    else if(temp->left==0) // with right children only
    {
        if(temp==root)
        {
            root=temp->right;
        }
        else
        {
            if(parent->left==temp)
            {
                parent->left=temp->right;
            }
            else
            {
                parent->right=temp->right;
            }
        }
        free(temp);
    }
    else if(temp->right==0) // with left children only
    {
        if(temp==root)
        {
            root=temp->left;
        }
        else
        {
            if(parent->left==temp)
            {
                parent->left=temp->left;
            }
            else
            {
                parent->right=temp->left;
            }
        }
        free(temp);
    }
}

int getMaxItem() //returns the maximum item in the tree
{
    if(root==0)
        return -1;
    Node * temp=root;
    while(temp->right!=0)
        temp=temp->right;
    return temp->item;
}

int rangeSearch(struct treeNode * node,int leftBound,int rightBound)
{
    if(node==0)
        return 0;
    if(node->item>=leftBound && node->item<=rightBound)
        return 1+rangeSearch(node->left,leftBound,rightBound)+rangeSearch(node->right,leftBound,rightBound);
    else
        return rangeSearch(node->left,leftBound,rightBound)+rangeSearch(node->right,leftBound,rightBound);
}


void printInOrder(struct treeNode * node, int height)
{
    if(node==0) return ;
    //print left sub-tree
    printInOrder(node->left, height-1);
    //print item
    for(int i=0; i<height; i++)printf("   ");
    printf("%03d\n",node->item);
    //print right sub-tree
    printInOrder(node->right, height-1);
}



int main(void)
{
    initializeTree();
    while(1)
    {
        printf("1. Insert item. 2. Delete item. 3. Search item. \n");
        printf("4. Print height of tree. 5. Print height of an item. \n");
        printf("6. PrintInOrder. 7. Size of tree. 8. Print depth of an item\n");
        printf("9. Max item. 10. Min item. 11. Range search. 12.Exit\n");
        int ch;
        scanf("%d",&ch);
        if(ch==1)
        {
            int item;
            scanf("%d", &item);
            insertItem(root, item);
        }
        else if(ch==2)
        {
            int item;
            scanf("%d", &item);
            deleteItem(root, item);
        }
        else if(ch==3)
        {
            int item;
            scanf("%d", &item);
            struct treeNode * res = searchItem(root, item);
            if(res!=0) printf("Found.\n");
            else printf("Not found.\n");
        }
        else if(ch==4)
        {
            int height = calcNodeHeight(root);
            printf("Height of tree = %d\n", height);
        }
        else if(ch==5)
        {
            int item;
            scanf("%d", &item);
            int height = calcHeight(item);
            printf("Height of %d = %d\n", item, height);
        }
        else if(ch==6)
        {
            int h = calcNodeHeight(root);
            printf("\n--------------------------------\n");
            printInOrder(root, h);
            printf("--------------------------------\n");
        }
        else if(ch==7)
        {
            printf("Size of tree : %d\n",getSize(root));
        }
        else if(ch==8)
        {
            int item;
            scanf("%d",&item);
            int depth = calcDepth(item);
            printf("Depth of %d = %d\n", item, depth);
        }
        else if(ch==9)
        {
            printf("Max item = %d\n",getMaxItem());
        }
        else if(ch==10)
        {
            printf("Min item = %d\n",getMinItem());
        }
        else if(ch==11)
        {
            int l,h;
            scanf("%d %d",&l,&h);
            printf("%d\n",rangeSearch(root,l,h));
        }
        else if(ch==12)
        {
            break;
        }
    }

}
/* 1 10 1 5 1 20 1 6 1 40 1 30 1 50 1 45 1 60 */
