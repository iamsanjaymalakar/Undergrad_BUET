# include "iGraphics.h"
#include<windows.h>

int h1[7],h2[7],m1[7],m2[7],s1[7],s2[7];
int hh1,hh2,mm1,mm2,ss1,ss2;
int x = 300, y = 300, r = 20;

double xh[6],yh[6],xv[6],yv[6];

void gettime(int t[]) {
    time_t ctime;
    struct tm * current;
    time ( &ctime );
    current = localtime ( &ctime );
    t[0] = current->tm_hour;
    t[1] = current->tm_min;
    t[2] = current->tm_sec;
}

void arrayfix(int a[],int n);

void timeout()
{
    int t[3] = {};
    gettime(t);
    hh1=t[0]/10;
    arrayfix(h1,hh1);
    hh2=t[0]%10;
    arrayfix(h2,hh2);
    mm1=t[1]/10;
    arrayfix(m1,mm1);
    mm2=t[1]%10;
    arrayfix(m2,mm2);
    ss1=t[2]/10;
    arrayfix(s1,ss1);
    ss2=t[2]%10;
    arrayfix(s2,ss2);
}

void arrayfix(int a[],int n)
{
    if(n==1)
        {
            a[0]=0;
            a[1]=0;
            a[2]=1;
            a[3]=0;
            a[4]=0;
            a[5]=1;
            a[6]=0;
        }
    else if(n==2)
        {
            a[0]=0;
            a[1]=1;
            a[2]=1;
            a[3]=1;
            a[4]=1;
            a[5]=0;
            a[6]=1;
        }
    else if(n==3)
        {
            a[0]=0;
            a[1]=1;
            a[2]=1;
            a[3]=1;
            a[4]=0;
            a[5]=1;
            a[6]=1;
        }
    else if(n==4)
        {
            a[0]=1;
            a[1]=0;
            a[2]=1;
            a[3]=1;
            a[4]=0;
            a[5]=1;
            a[6]=0;
        }
    else if(n==5)
        {
            a[0]=1;
            a[1]=1;
            a[2]=0;
            a[3]=1;
            a[4]=0;
            a[5]=1;
            a[6]=1;
        }
    else if(n==6)
        {
            a[0]=1;
            a[1]=1;
            a[2]=0;
            a[3]=1;
            a[4]=1;
            a[5]=1;
            a[6]=1;
        }
    else if(n==7)
        {
            a[0]=0;
            a[1]=1;
            a[2]=1;
            a[3]=0;
            a[4]=0;
            a[5]=1;
            a[6]=0;
        }
    else if(n==8)
        {
            a[0]=1;
            a[1]=1;
            a[2]=1;
            a[3]=1;
            a[4]=1;
            a[5]=1;
            a[6]=1;
        }
    else if(n==9)
        {
            a[0]=1;
            a[1]=1;
            a[2]=1;
            a[3]=1;
            a[4]=0;
            a[5]=1;
            a[6]=0;
        }
    else if(n==0)
        {
            a[0]=1;
            a[1]=1;
            a[2]=1;
            a[3]=0;
            a[4]=1;
            a[5]=1;
            a[6]=1;
        }
}


void digit(int a,int b, int arr[])
{
    if(arr[0])
        iFilledRectangle(a,300,10,50);
    if(arr[1])
        iFilledRectangle(a,352,50,10);
    if(arr[2])
        iFilledRectangle(b,300,10,50);
    if(arr[3])
        iFilledRectangle(a,288,50,10);
    if(arr[4])
        iFilledRectangle(a,236,10,50);
    if(arr[5])
        iFilledRectangle(b,236,10,50);
    if(arr[6])
        iFilledRectangle(a,224,50,10);
}

void colon(int x)
{
    iFilledRectangle(x,310,10,10);
    iFilledRectangle(x,265,10,10);
}

void iDraw() {
	//place your drawing codes here
	iClear();
	iSetColor(25,110,200);
	if(ss1==0 && ss2==0)
        iSetColor(110,225,200);
	system("cls");
	timeout();
	digit(50,90,h1);
	digit(110,150,h2);
	if(ss2%2==0)
        colon(170);
	digit(190,230,m1);
	digit(250,290,m2);
	iSetTimer(500);
	if(ss2%2==0)
        colon(310);
	digit(330,370,s1);
	digit(390,430,s2);
	iSetColor(20, 200, 0);
	iText(40, 40, "Digital Clock");
}

/*
	function iMouseMove() is called when the user presses and drags the mouse.
	(mx, my) is the position where the mouse pointer is.
	*/
void iMouseMove(int mx, int my) {
	printf("x = %d, y= %d\n",mx,my);
	//place your codes here
}

/*
	function iMouse() is called when the user presses/releases the mouse.
	(mx, my) is the position where the mouse pointer is.
	*/
void iMouse(int button, int state, int mx, int my) {
	if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {
		//place your codes here
		//	printf("x = %d, y= %d\n",mx,my);
		x += 10;
		y += 10;
	}
	if (button == GLUT_RIGHT_BUTTON && state == GLUT_DOWN) {
		//place your codes here
		x -= 10;
		y -= 10;
	}


}

/*
	function iKeyboard() is called whenever the user hits a key in keyboard.
	key- holds the ASCII value of the key pressed.
	*/
void iKeyboard(unsigned char key) {
	if (key == 'q') {
		exit(0);
	}
	//place your codes for other keys here
}

/*
	function iSpecialKeyboard() is called whenver user hits special keys like-
	function keys, home, end, pg up, pg down, arraows etc. you have to use
	appropriate constants to detect them. A list is:
	GLUT_KEY_F1, GLUT_KEY_F2, GLUT_KEY_F3, GLUT_KEY_F4, GLUT_KEY_F5, GLUT_KEY_F6,
	GLUT_KEY_F7, GLUT_KEY_F8, GLUT_KEY_F9, GLUT_KEY_F10, GLUT_KEY_F11, GLUT_KEY_F12,
	GLUT_KEY_LEFT, GLUT_KEY_UP, GLUT_KEY_RIGHT, GLUT_KEY_DOWN, GLUT_KEY_PAGE UP,
	GLUT_KEY_PAGE DOWN, GLUT_KEY_HOME, GLUT_KEY_END, GLUT_KEY_INSERT
	*/
void iSpecialKeyboard(unsigned char key) {

	if (key == GLUT_KEY_END) {
		exit(0);
	}
	//place your codes for other keys here
}


int main() {
	//place your own initialization codes here.
	iInitialize(500,400, "Digital Clock");
	return 0;
}
