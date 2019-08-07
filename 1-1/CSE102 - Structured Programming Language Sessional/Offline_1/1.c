#include<stdio.h>

int main()
{
    int q,m,y,h,Y;
    printf("Enter year: ");
    scanf("%d",&y);
    printf("Enter month (1-12): ");
    scanf("%d",&m);
    printf("Enter day: ");
    scanf("%d",&q);
    if(m==1)
        m=13;
    if(m==2)
        m=14;
    if(m==13 || m==14)
    {
        Y=y-1;
        h=(q+(26*(m+1))/10+Y+Y/4+6*(Y/100)+Y/400)%7;
    }
    else
        h=(q+(26*(m+1))/10+y+y/4+6*(y/100)+y/400)%7;
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

    printf(" %d,%d is ",q,y);

    switch(h)
    {
    case 0 :
        printf("Saturday");
        break;
    case 1 :
        printf("Sunday");
        break;
    case 2 :
        printf("Monday");
        break;
    case 3 :
        printf("Tuesday");
        break;
    case 4 :
        printf("Wednesday");
        break;
    case 5 :
        printf("Thursday");
        break;
    default :
        printf("Friday");
    }

    getch();
    return 0;

}
