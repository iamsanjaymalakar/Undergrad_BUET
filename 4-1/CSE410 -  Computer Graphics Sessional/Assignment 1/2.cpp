#include<stdio.h>
#include<stdlib.h>
#include<math.h>

//#include <windows.h>
#include <GL/glut.h>

#define pi (2*acos(0.0))
#define wheelRadius 20
#define wheelRotateAngle 2
#define wheelMoveDistance 2

double cameraHeight;
double cameraAngle;
int drawgrid;
int drawaxes;
double rAngle;
double wAngle;
double distX,distY;
double unitAngle;

struct point
{
	double x,y,z;
};


void drawAxes()
{
	if(drawaxes==1)
	{
		glColor3f(1.0, 1.0, 1.0);
		glBegin(GL_LINES);{
		    glColor3f(1,0,0);
			glVertex3f( 100,0,0);
			glColor3f(0,1,0);
			glVertex3f(-100,0,0);

            glColor3f(1,0,0);
			glVertex3f(0,-100,0);
			glColor3f(0,1,0);
			glVertex3f(0, 100,0);

            glColor3f(1,0, 0);
			glVertex3f(0,0, 100);
			glColor3f(0,1,0);
			glVertex3f(0,0,-100);
		}glEnd();
	}
}


void drawGrid()
{
	int i;
	if(drawgrid==1)
	{
		glColor3f(0.6, 0.6, 0.6);
		glBegin(GL_LINES);{
			for(i=-8;i<=8;i++){
				glVertex3f(i*10, -90, 0);
				glVertex3f(i*10,  90, 0);

				glVertex3f(-90, i*10, 0);
				glVertex3f( 90, i*10, 0);
			}
		}glEnd();
	}
}

void drawSquare(double a)
{
	glBegin(GL_QUADS);{
		glVertex3f( a, a,2);
		glVertex3f( a,-a,2);
		glVertex3f(-a,-a,2);
		glVertex3f(-a, a,2);
	}glEnd();
}


/*
w - move forward
s - move backward
a - rotate left
d - rotate right
*/
void keyboardListener(unsigned char key, int x,int y){
	switch(key){
        case 'w':
            distX-=(wheelMoveDistance*cos(wAngle*pi/180));
            distY-=(wheelMoveDistance*sin(wAngle*pi/180));
            rAngle-=unitAngle;
            break;
        case 's':
            distX+=(wheelMoveDistance*cos(wAngle*pi/180));
            distY+=(wheelMoveDistance*sin(wAngle*pi/180));
            rAngle+=unitAngle;
            break;
		case 'a':
			wAngle+=wheelRotateAngle;
			break;
        case 'd':
            wAngle-=wheelRotateAngle;
            break;
		default:
			break;
	}
}


void specialKeyListener(int key, int x,int y){
	switch(key){
		case GLUT_KEY_DOWN:
			cameraHeight-=2.0;
			break;
		case GLUT_KEY_UP:
			cameraHeight+=2.0;
			break;
		case GLUT_KEY_RIGHT:
			cameraAngle+=0.02;
			break;
		case GLUT_KEY_LEFT:
			cameraAngle-=0.02;
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

void mouseListener(int button, int state, int x, int y){
	switch(button){
		case GLUT_LEFT_BUTTON:
			if(state == GLUT_DOWN){
				drawaxes=1-drawaxes;
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


void drawWheel(double radius, int segments, int d)
{
     struct point points[100];
    //generate points to draw in x-z plane
    for(int i=0;i<segments;i++)
    {
        points[i].x=radius*cos(((double)i/(double)segments)*2*pi);
        points[i].z=radius*sin(((double)i/(double)segments)*2*pi);
    }
    double x=.3;
    for(int i = 0; i < segments; i++)
    {
        glBegin(GL_QUADS);
        {
            if(x>.7)
                x=.3;
            else
                x+=.02;
            glColor3f(x,x,x);
            //glColor3f((double)i/(double)segments,(double)i/(double)segments,(double)i/(double)segments);
            glVertex3f(points[i].x,-d,points[i].z);
            glVertex3f(points[i].x,d, points[i].z);
            glVertex3f(points[(i+1)%segments].x,d,points[(i+1)%segments].z);
            glVertex3f(points[(i+1)%segments].x,-d,points[(i+1)%segments].z);
        }
        glEnd();
    }

    // rims
    glColor3f(0.5,0.5,0.5);
    glBegin(GL_QUADS);
    {
        glVertex3f(wheelRadius,-d/2,0);
        glVertex3f(wheelRadius,d/2,0);
        glVertex3f(-wheelRadius,d/2,0);
        glVertex3f(-wheelRadius,-d/2,0);
    }
    glEnd();
    glBegin(GL_QUADS);
    {
        glVertex3f(0,-d/2,wheelRadius);
        glVertex3f(0,d/2,wheelRadius);
        glVertex3f(0,d/2,-wheelRadius);
        glVertex3f(0,-d/2,-wheelRadius);
    }
    glEnd();
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

	//now give three info
	//1. where is the camera (viewer)?
	//2. where is the camera looking?
	//3. Which direction is the camera's UP direction?

	//gluLookAt(100,100,100,	0,0,0,	0,0,1);
	gluLookAt(120*cos(cameraAngle),120*sin(cameraAngle), cameraHeight, 0,0,0, 0,0,1);

	//again select MODEL-VIEW
	glMatrixMode(GL_MODELVIEW);


	/****************************
	/ Add your objects from here
	****************************/
	//add objects

	drawAxes();
	drawGrid();

    glTranslated(distX,distY,0);
	glTranslatef(0,0,wheelRadius);
	glRotated(wAngle,0,0,1);
	glRotated(rAngle,0,1,0);
    drawWheel(20,50,4);


	//ADD this line in the end --- if you use double buffer (i.e. GL_DOUBLE)
	glutSwapBuffers();
}


void animate(){
	glutPostRedisplay();
}

void init(){
	//codes for initialization
	drawgrid=1;
	drawaxes=0;
	cameraHeight=100.0;
	cameraAngle=1.0;
	rAngle=0;
	wAngle=0;
    distX=0;
    distY=0;
    unitAngle=360.0/double(2*pi*wheelRadius);
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
	gluPerspective(80,	1,	1,	1000.0);
	//field of view in the Y (vertically)
	//aspect ratio that determines the field of view in the X direction (horizontally)
	//near distance
	//far distance
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
	glutIdleFunc(animate);		//what you want to do in the idle time (when no drawing is occuring)

	glutKeyboardFunc(keyboardListener);
	glutSpecialFunc(specialKeyListener);
	glutMouseFunc(mouseListener);

	glutMainLoop();		//The main loop of OpenGL

	return 0;
}
