#include<stdio.h>

int main()
{
    int q=1,m,y,h,Y,i,j;
    int mday[]= {0,0,0,31,30,31,30,31,31,30,31,30,31,31,28};

    printf("Enter year: ");
    scanf("%d",&y);
    printf("Enter month (1-12): ");
    scanf("%d",&m);

    if(m==1)
        m=13;
    if(m==2)
        m=14;

    if(y%400==0 || y%4==0&&y%100!=0)
    {
        mday[14]=29;
    }

    if(m==13 || m==14)
    {
        Y=y-1;
        h=(q+(26*(m+1))/10+Y+Y/4+6*(Y/100)+Y/400)%7;
    }
    else
        h=(q+(26*(m+1))/10+y+y/4+6*(y/100)+y/400)%7;

    printf("\nCalender for: \n\n");

    switch(m)
    {
    case 13:
        printf("January");
        break;
    case 14:
        printf("February");
        break;
    case 3:
        printf("March");
        break;
    case 4:
        printf("April");
        break;
    case 5:
        printf("May");
        break;
    case 6:
        printf("June");
        break;
    case 7:
        printf("July");
        break;
    case 8:
        printf("August");
        break;
    case 9:
        printf("September");
        break;
    case 10:
        printf("October");
        break;
    case 11:
        printf("November");
        break;
    case 12:
        printf("December");
        break;
    }

    printf(" %d\n\n",y);

    printf("SAT\tSUN\tMON\tTEU\tWED\tTHU\tFRI\n");
    for(i=0; i<h; i++)
    {
        printf("\t");
    }
    for(i=1,j=h+1;i<=mday[m];i++,j++)
    {
        printf("%d\t",i);
        if(j==7)
        {
            printf("\n");
        }
        if(j>7)
            j=(j+3)%7+4;
    }
    getch();
    return 0;
}
