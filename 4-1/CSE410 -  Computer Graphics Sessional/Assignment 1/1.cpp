#include<bits/stdc++.h>

//#include <windows.h>
#include <GL/glut.h>

using namespace std;

#define pi (2*acos(0.0))

double cameraHeight;
double cameraAngle;
int drawaxes;
double side,maxSide;

struct Point
{
    double x,y,z;
};

typedef struct Point point;

point pos,l,r,u;

point add(point a,point b)
{
    point temp;
    temp.x=a.x+b.x;
    temp.y=a.y+b.y;
    temp.z=a.z+b.z;
    return temp;
}

point sub(point a,point b)
{
    point temp;
    temp.x=a.x-b.x;
    temp.y=a.y-b.y;
    temp.z=a.z-b.z;
    return temp;
}

point rot(point v,point r,double sign)
{
    point n,temp;
    double angle=0.1*sign;
    n.x=r.y*v.z-r.z*v.y;
    n.y=r.z*v.x-r.x*v.z;
    n.z=r.x*v.y-r.y*v.x;
    temp.x=v.x*cos(angle)+n.x*sin(angle);
    temp.y=v.y*cos(angle)+n.y*sin(angle);
    temp.z=v.z*cos(angle)+n.z*sin(angle);
    return temp;
}


void drawAxes()
{
    if(drawaxes==1)
    {
        glColor3f(1, 1, 1);
        glBegin(GL_LINES);
        {
            glVertex3f( 100,0,0);
            glVertex3f(-100,0,0);

            glVertex3f(0,-100,0);
            glVertex3f(0, 100,0);

            glVertex3f(0,0, 100);
            glVertex3f(0,0,-100);
        }
        glEnd();
    }
}

void drawSquare(double a)
{
    glBegin(GL_QUADS);
    {
        glVertex3f(a,a,0);
        glVertex3f(a,-a,0);
        glVertex3f(-a,-a,0);
        glVertex3f(-a,a,0);
    }
    glEnd();
}

void drawOneEighthSphere(double radius, int slices, int stacks)
{
    point points[100][100];
    double h,r;
    //generate points
    for (int i=0; i<=stacks; i++)
    {
        h=radius*sin(((double)i/(double)stacks)*(pi/2));
        r=radius*cos(((double)i/(double)stacks)*(pi/2));
        for(int j=0;j<=slices;j++)
        {
            points[i][j].x=r*cos(((double)j/(double)slices)*pi/2);
            points[i][j].y=r*sin(((double)j/(double)slices)*pi/2);
            points[i][j].z=h;
        }
    }
    //draw quads using generated points
    for(int i=0;i<stacks;i++)
    {
        glColor3f(1, 0, 0);
        for(int j=0;j<slices;j++)
        {
            glBegin(GL_QUADS);
            {
                //upper hemisphere
                glVertex3f(points[i][j].x,points[i][j].y,points[i][j].z);
                glVertex3f(points[i][j+1].x,points[i][j+1].y,points[i][j+1].z);
                glVertex3f(points[i+1][j+1].x, points[i+1][j+1].y,points[i+1][j+1].z);
                glVertex3f(points[i+1][j].x, points[i+1][j].y, points[i+1][j].z);
            }
            glEnd();
        }
    }
}

void drawEightSpheres()
{
    double p=side;
	double r=maxSide-side;
	double slices=50;
	double stacks=50;
	// + + +
	glPushMatrix();
	{
		glTranslatef(p,p,p);
		drawOneEighthSphere(r,slices,stacks);
    }
	glPopMatrix();
	// + - +
	glPushMatrix();
	{
		glTranslatef(p,-p,p);
		glRotated(-90, 0, 0, 1);
		drawOneEighthSphere(r,slices,stacks);
	}
	glPopMatrix();
	// - + +
    glPushMatrix();
	{
		glTranslatef(-p,p,p);
		glRotated(90, 0, 0, 1);
		drawOneEighthSphere(r,slices,stacks);
	}
	glPopMatrix();
	// - - +
    glPushMatrix();
	{
		glTranslatef(-p,-p,p);
		glRotated(180, 0, 0, 1);
		drawOneEighthSphere(r,slices,stacks);
	}
	glPopMatrix();
	// below z
	// + + -
	glPushMatrix();
	{
		glTranslatef(p,p,-p);
		glRotated(180,1,1,0);
		drawOneEighthSphere(r,slices,stacks);
    }
    glPopMatrix();
    // + - -
	glPushMatrix();
	{
		glTranslatef(p,-p,-p);
		glRotated(-90,0,0,1);
		glRotated(180,1,1,0);
		drawOneEighthSphere(r,slices,stacks);
	}
	glPopMatrix();
	// - + -
    glPushMatrix();
	{
		glTranslatef(-p,p,-p);
		glRotated(90,0,0,1);
		glRotated(180,1,1,0);
		drawOneEighthSphere(r,slices,stacks);
	}
	glPopMatrix();
	// - - -
    glPushMatrix();
	{
		glTranslatef(-p,-p,-p);
		glRotated(180,0,0,1);
		glRotated(180,1,1,0);
		drawOneEighthSphere(r,slices,stacks);
	}
	glPopMatrix();
}

void drawSixSquares()
{
    // z
    glPushMatrix();
    glTranslated(0,0,maxSide);
	drawSquare(side);
	glPopMatrix();
	// -z
    glPushMatrix();
    glTranslated(0,0,-maxSide);
	drawSquare(side);
	glPopMatrix();
	// x
    glPushMatrix();
    glTranslated(maxSide,0,0);
    glRotated(90,0,1,0);
	drawSquare(side);
	glPopMatrix();
	// -x
    glPushMatrix();
    glTranslated(-maxSide,0,0);
    glRotated(90,0,1,0);
	drawSquare(side);
	glPopMatrix();
	// y
    glPushMatrix();
    glTranslated(0,maxSide,0);
	glRotated(90,1,0,0);
	drawSquare(side);
	glPopMatrix();
	// -y
    glPushMatrix();
    glTranslated(0,-maxSide,0);
    glRotated(90,1,0,0);
	drawSquare(side);
	glPopMatrix();
}

void drawOneFourthCylinder(double radius,double height,int slices,int stacks)
{
    point points[100][100];
	double h;
	//generate points
	for(int i=0;i<=stacks;i++)
	{
		h=height*sin(((double)i/(double)stacks)*(pi/2));
		for(int j=0;j<=slices;j++)
		{
			points[i][j].x=radius*cos(((double)j/(double)slices)*pi/2);
			points[i][j].y=radius*sin(((double)j/(double)slices)*pi/2);
			points[i][j].z=h;
		}
	}
	//draw quads using generated points
	for(int i=0;i<stacks;i++)
	{
		for(int j=0;j<slices;j++)
		{
			glBegin(GL_QUADS);{
				//upper hemisphere
				glVertex3f(points[i][j].x,points[i][j].y,points[i][j].z);
				glVertex3f(points[i][j+1].x,points[i][j+1].y,points[i][j+1].z);
				glVertex3f(points[i+1][j+1].x,points[i+1][j+1].y,points[i+1][j+1].z);
				glVertex3f(points[i+1][j].x,points[i+1][j].y,points[i+1][j].z);
				//lower hemisphere
				glVertex3f(points[i][j].x,points[i][j].y,-points[i][j].z);
				glVertex3f(points[i][j+1].x,points[i][j+1].y,-points[i][j+1].z);
				glVertex3f(points[i+1][j+1].x,points[i+1][j+1].y,-points[i+1][j+1].z);
				glVertex3f(points[i+1][j].x,points[i+1][j].y,-points[i+1][j].z);
			}glEnd();
		}
	}
}

void drawTwelveCylinders()
{
    double h=side;
    double r=maxSide-side;
    // z axis
    glPushMatrix();
    glTranslatef(h,h,0);
    drawOneFourthCylinder(r,h,50,50);
    glPopMatrix();

    glPushMatrix();
    glTranslatef(h,-h,0);
    glRotated(-90,0,0,1);
    drawOneFourthCylinder(r,h,50,50);
    glPopMatrix();

    glPushMatrix();
    glTranslatef(-h,h,0);
    glRotated(90,0,0,1);
    drawOneFourthCylinder(r,h,50,50);
    glPopMatrix();

    glPushMatrix();
    glTranslatef(-h,-h,0);
    glRotated(180,0,0,1);
    drawOneFourthCylinder(r,h,50,50);
    glPopMatrix();

    // x axis
    glPushMatrix();
    glTranslatef(0,h,h);
    glRotated(-90,0,1,0);
    drawOneFourthCylinder(r,h,50,50);
	glPopMatrix();

    glPushMatrix();
    glTranslatef(0,-h,h);
    glRotated(-90,0,1,0);
    glRotated(-90,0,0,1);
    drawOneFourthCylinder(r,h,50,50);
	glPopMatrix();

    glPushMatrix();
    glTranslatef(0,h,-h);
    glRotated(90,0,1,0);
    drawOneFourthCylinder(r,h,50,50);
	glPopMatrix();

	glPushMatrix();
    glTranslatef(0,-h,-h);
    glRotated(90,0,1,0);
    glRotated(-90,0,0,1);
    drawOneFourthCylinder(r,h,50,50);
	glPopMatrix();

    // y axis
    glPushMatrix();
    glTranslatef(h,0,h);
    glRotated(90,1,0,0);
    drawOneFourthCylinder(r,h,50,50);
	glPopMatrix();

    glPushMatrix();
    glTranslatef(-h,0,h);
    glRotated(90,1,0,0);
    glRotated(90,0,0,1);
    drawOneFourthCylinder(r,h,50,50);
	glPopMatrix();

    glPushMatrix();
    glTranslatef(h,0,-h);
    glRotated(-90,1,0,0);
    drawOneFourthCylinder(r,h,50,50);
	glPopMatrix();

    glPushMatrix();
    glTranslatef(-h,0,-h);
    glRotated(-90,1,0,0);
    glRotated(90,0,0,1);
    drawOneFourthCylinder(r,h,50,50);
	glPopMatrix();


}


/*
1 - rotate/look left
2 - rotate/look right
3 - look up
4 - look down
5 - tilt clockwise
6 - tilt counterclockwise
*/
void keyboardListener(unsigned char key, int x,int y)
{
    switch(key)
    {

    case '1':
        l=rot(l,u,1);
        r=rot(r,u,1);
        break;
    case '2':
        l=rot(l,u,-1);
        r=rot(r,u,-1);
        break;
    case '3':
        l=rot(l,r,-1);
        u=rot(u,r,-1);
        break;
    case '4':
        l=rot(l,r,1);
        u=rot(u,r,1);
        break;
    case '5':
        u=rot(u,l,-1);
        r=rot(r,l,-1);
        break;
    case '6':
        u=rot(u,l,1);
        r=rot(r,l,1);
        break;
    default:
        break;
    }
}

/*
up arrow - move forward
down arrow - move backward
right arrow - move right
left arrow - move left
PgUp - move up
PgDn - move down
*/
void specialKeyListener(int key, int x,int y)
{
    switch(key)
    {
    case GLUT_KEY_DOWN:
        pos=sub(pos,l);
        break;
    case GLUT_KEY_UP:
        pos=add(pos,l);
        break;
    case GLUT_KEY_RIGHT:
        pos=add(pos,r);
        break;
    case GLUT_KEY_LEFT:
        pos=sub(pos,r);
        break;
    case GLUT_KEY_PAGE_UP:
        pos=add(pos,u);
        break;
    case GLUT_KEY_PAGE_DOWN:
        pos=sub(pos,u);
        break;
    case GLUT_KEY_INSERT:
        break;
    case GLUT_KEY_HOME:
        side-=0.5;
		side=max(side,0.0);
		break;
	case GLUT_KEY_END:
		side+=0.5;
		side=min(side,maxSide);
		break;

    default:
        break;
    }
}


void mouseListener(int button, int state, int x, int y)
{
    switch(button)
    {
    case GLUT_LEFT_BUTTON:
        if(state == GLUT_DOWN)
        {
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



void display()
{

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
    gluLookAt(pos.x, pos.y, pos.z, pos.x + l.x, pos.y + l.y, pos.z + l.z, u.x, u.y, u.z);


    //again select MODEL-VIEW
    glMatrixMode(GL_MODELVIEW);


    /****************************
    / Add your objects from here
    ****************************/
    //add objects

    drawAxes();
    glColor3f(1,0,0);
    drawEightSpheres();
    glColor3f(1,1,1);
    drawSixSquares();
    glColor3f(0,1,0);
    drawTwelveCylinders();
    //
    //ADD this line in the end --- if you use double buffer (i.e. GL_DOUBLE)
    glutSwapBuffers();
}


void animate()
{
    //codes for any changes in Models, Camera
    glutPostRedisplay();
}

void init()
{
    pos = { 0.0, -85.0, 0.0 };
    l = { 0.0, 1.0, 0.0 };
    r = { 1.0, 0.0, 0.0 };
    u = { 0.0, 0.0, 1.0 };
    side=12;
    maxSide=20;
    drawaxes=1;
    cameraHeight=150.0;
    cameraAngle=1.0;

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

int main(int argc, char **argv)
{
    glutInit(&argc,argv);
    glutInitWindowSize(500, 500);
    glutInitWindowPosition(0, 0);
    glutInitDisplayMode(GLUT_DEPTH | GLUT_DOUBLE | GLUT_RGB);	//Depth, Double buffer, RGB color

    glutCreateWindow("Offline One");

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
