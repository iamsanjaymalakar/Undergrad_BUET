#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include <windows.h>
#include "glut.h"

#include "Automobile.h"
#include "Derived.h"

#define PLATFORM_REFRESH_TIME 50

#define PI acos(-1.0)
#define min(a, b) ((a) < (b) ? (a) : (b))
#define max(a, b) ((a) > (b) ? (a) : (b))

struct point
{
    double x,y;
};

struct point inner[40];
struct point outer[40];

Automobile *vehicle = new Car();

double x,y,dx,dy;
int cameraMode;

void generatePoints()
{
    int i;
    for(i=0;i<10;i++)
    {
        inner[i].x=350+300*cos(i*10*PI/180.0);
        inner[i].y=350+300*sin(i*10*PI/180.0);
        outer[i].x=350+450*cos(i*10*PI/180.0);
        outer[i].y=350+450*sin(i*10*PI/180.0);
    }
    for(i=0;i<10;i++)
    {
        inner[i+10].x=-350+300*cos((PI/2)+i*10*PI/180.0);
        inner[i+10].y= 350+300*sin((PI/2)+i*10*PI/180.0);
        outer[i+10].x=-350+450*cos((PI/2)+i*10*PI/180.0);
        outer[i+10].y= 350+450*sin((PI/2)+i*10*PI/180.0);
    }
    for(i=0;i<10;i++)
    {
        inner[i+20].x=-350+300*cos((PI)+i*10*PI/180.0);
        inner[i+20].y=-350+300*sin((PI)+i*10*PI/180.0);
        outer[i+20].x=-350+450*cos((PI)+i*10*PI/180.0);
        outer[i+20].y=-350+450*sin((PI)+i*10*PI/180.0);
    }
    for(i=0;i<10;i++)
    {
        inner[i+30].x= 350+300*cos((3*PI/2)+i*10*PI/180.0);
        inner[i+30].y=-350+300*sin((3*PI/2)+i*10*PI/180.0);
        outer[i+30].x= 350+450*cos((3*PI/2)+i*10*PI/180.0);
        outer[i+30].y=-350+450*sin((3*PI/2)+i*10*PI/180.0);
    }
}

void drawPole(struct point p)
{
    glColor3f(0.0,0.0,1.0);
    glLineWidth(3);
    glBegin(GL_LINES);{
		glVertex3f( p.x, p.y,0);
		glVertex3f( p.x, p.y,20);
	}glEnd();

}

void drawCar()
{
    glColor3f(1.0,0.0,0.0);
	glBegin(GL_QUADS);{
		glVertex3f( 40, 20,10);
		glVertex3f( 40,-20,10);
		glVertex3f(-40,-20,10);
		glVertex3f(-40, 20,10);
	}glEnd();
}

void drawArena()
{
    glColor3f(0.0,1.0,0.0);
    int i;
    for(i=0;i<40;i++)
    {
        drawPole(inner[i]);
        drawPole(outer[i]);
        glColor3f(1.0,1.0,0.0);
        glBegin(GL_QUADS);{
            glVertex3f( inner[i].x, inner[i].y,0);
            glVertex3f( inner[(i+1)%40].x, inner[(i+1)%40].y,0);
            glVertex3f( outer[(i+1)%40].x, outer[(i+1)%40].y,0);
            glVertex3f( outer[i].x, outer[i].y,0);
        }glEnd();
        glColor3f(1.0,1.0,1.0);
        glLineWidth(1);
        glBegin(GL_LINES);{
            glVertex3f( inner[i].x, inner[i].y,5);
            glVertex3f( inner[(i+1)%40].x, inner[(i+1)%40].y,5);

            glVertex3f( outer[i].x, outer[i].y,5);
            glVertex3f( outer[(i+1)%40].x, outer[(i+1)%40].y,5);

            glVertex3f( inner[i].x, inner[i].y,10);
            glVertex3f( inner[(i+1)%40].x, inner[(i+1)%40].y,10);

            glVertex3f( outer[i].x, outer[i].y,10);
            glVertex3f( outer[(i+1)%40].x, outer[(i+1)%40].y,10);

            glVertex3f( inner[i].x, inner[i].y,15);
            glVertex3f( inner[(i+1)%40].x, inner[(i+1)%40].y,15);

            glVertex3f( outer[i].x, outer[i].y,15);
            glVertex3f( outer[(i+1)%40].x, outer[(i+1)%40].y,15);
        }glEnd();
    }
}

void keyboardListener(unsigned char key, int x,int y){
    bool fStickShift = (0 == strcmp(vehicle->GetType(), "Bus") || 0 == strcmp(vehicle->GetType(), "Truck"));
	switch(key){

        case '>':
            if (fStickShift)
                ((AutomobileWithManualXmission*)vehicle)->ShiftGearUp();
            break;
        case '<':
            if (fStickShift)
                ((AutomobileWithManualXmission*)vehicle)->ShiftGearDown();
            break;
		case 'w':
			vehicle->IncreaseSpeed();
			break;

        case 's':
			vehicle->DecreaseSpeed();
			break;

        case 'a':
			vehicle->TurnLeft();
			break;

        case 'd':
			vehicle->TurnRight();
			break;

        case 'c':
			cameraMode++;
			break;

		default:
			break;
	}
}


void specialKeyListener(int key, int x,int y){
	switch(key){
		case GLUT_KEY_DOWN:		//down arrow key
			//cameraHeight -= 3.0;
			break;
		case GLUT_KEY_UP:		// up arrow key
			//cameraHeight += 3.0;
			break;

		case GLUT_KEY_RIGHT:
			//cameraAngle += 0.03;
			break;
		case GLUT_KEY_LEFT:
			//cameraAngle -= 0.03;
			break;

		case GLUT_KEY_PAGE_UP:
			break;
		case GLUT_KEY_PAGE_DOWN:
			break;

		case GLUT_KEY_INSERT:
			break;

		case GLUT_KEY_HOME:
			break;
		case GLUT_KEY_END:
			break;

		default:
			break;
	}
}


void mouseListener(int button, int state, int x, int y){	//x, y is the x-y of the screen (2D)
	switch(button){
		case GLUT_LEFT_BUTTON:
			if(state == GLUT_DOWN){		// 2 times?? in ONE click? -- solution is checking DOWN or UP
				//drawaxes=1-drawaxes;
			}
			break;

		case GLUT_RIGHT_BUTTON:
			//........
			break;

		case GLUT_MIDDLE_BUTTON:
			//........
			break;

		default:
			break;
	}
}

void display(){

	//clear the display
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glClearColor(0,0,0,0);	//color black
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	/********************
	/ set-up camera here
	********************/
	//load the correct matrix -- MODEL-VIEW matrix
	glMatrixMode(GL_MODELVIEW);

	//initialize the matrix
	glLoadIdentity();


	vehicle->GetPosition(x,y);
	vehicle->GetDirection(dx,dy);
	x/=100.0;
	y/=100.0;

	//now give three info
	//1. where is the camera (viewer)?
	//2. where is the camera looking?
	//3. Which direction is the camera's UP direction?

	//gluLookAt(100,100,100,	0,0,0,	0,0,1);
	//gluLookAt(200*cos(cameraAngle), 200*sin(cameraAngle), cameraHeight,		0,0,0,		0,0,1);
	//if(cameraMode==0)gluLookAt(500,0,100,	600,0,0,	0,0,1);
	if(cameraMode%3==0)gluLookAt(0,0,1000,	0,0,0,	0,1,0);
	else if(cameraMode%3==1) gluLookAt(x+dx*10,y+dy*10,20,	x+dx*40,y+dy*40,10,	0,0,1);
	else if(cameraMode%3==2) gluLookAt(0,0,300,	x,y,10,	0,0,1);


	//again select MODEL-VIEW
	glMatrixMode(GL_MODELVIEW);


	/****************************
	/ Add your objects from here
	****************************/
	//add objects


	drawArena();




	double angle=atan2(dy,dx);
	angle*=(180/PI);


	glTranslatef(x,y,0);
	glRotatef(angle,0,0,1);
	drawCar();



	//ADD this line in the end --- if you use double buffer (i.e. GL_DOUBLE)
	glutSwapBuffers();
}


void animate(){
    static int animateCount = 0;
    static int anm = 0;

    vehicle->Move();

    animateCount++;
    if (animateCount == 1000 / PLATFORM_REFRESH_TIME)
    {
        Print(vehicle);
        //printf("%d", ++anm);
        animateCount = 0;
    }


	//codes for any changes in Models, Camera
	glutPostRedisplay();
}

void init(){
	//codes for initialization

	vehicle->SetInitialPosition(72000, 0);
    vehicle->SetInitialDirection(0, 1);
    generatePoints();
    cameraMode=0;
	//clear the screen
	glClearColor(0,0,0,0);

	/************************
	/ set-up projection here
	************************/
	//load the PROJECTION matrix
	glMatrixMode(GL_PROJECTION);

	//initialize the matrix
	glLoadIdentity();

	//give PERSPECTIVE parameters
	gluPerspective(80,	1,	1,	1500.0);
	//field of view in the Y (vertically)
	//aspect ratio that determines the field of view in the X direction (horizontally)
	//near distance
	//far distance
}

void timerCallback(int value)
{
    animate();
    glutTimerFunc(PLATFORM_REFRESH_TIME, timerCallback, 123);
}

int main(int argc, char **argv){
	glutInit(&argc,argv);
	glutInitWindowSize(500, 500);
	glutInitWindowPosition(0, 0);
	glutInitDisplayMode(GLUT_DEPTH | GLUT_DOUBLE | GLUT_RGB);	//Depth, Double buffer, RGB color

	glutCreateWindow("My OpenGL Program");

	init();

	glEnable(GL_DEPTH_TEST);	//enable Depth Testing

	glutDisplayFunc(display);	//display callback function
	//glutIdleFunc(animate);		//what you want to do in the idle time (when no drawing is occuring)
    glutTimerFunc(PLATFORM_REFRESH_TIME, timerCallback, 1);

	glutKeyboardFunc(keyboardListener);
	glutSpecialFunc(specialKeyListener);
	glutMouseFunc(mouseListener);

	glutMainLoop();		//The main loop of OpenGL

	return 0;
}
