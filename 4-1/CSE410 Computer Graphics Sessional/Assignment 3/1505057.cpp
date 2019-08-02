#include <bits/stdc++.h>
#include <windows.h>
#include <GL/glut.h>
#include "bitmap_image.hpp"

using namespace std;

#define pi (2*acos(0.0))
#define EPSILON 0.000001
#define windowHeight 500
#define windowWidth 500
#define FOV 90
#define nearPlane 1
#define farPlane 1000

int imageHeight,imageWidth,maxLevel;

void drawAxes()
{
    glBegin(GL_LINES);
    {
        glColor3f(1,0,0);
        glVertex3f(100,0,0);
        glVertex3f(-100,0,0);
        glColor3f(0,1,0);
        glVertex3f(0,-100,0);
        glVertex3f(0,100,0);
        glColor3f(0,0,1);
        glVertex3f(0,0,100);
        glVertex3f(0,0,-100);
    }
    glEnd();
}


struct Point
{
    double x,y,z;
    Point(double x,double y,double z)
    {
        this->x=x;
        this->y=y;
        this->z=z;
    }
    Point()
    {

    }
};

Point eye,l,r,u;
vector <Point> Lights;

Point add(Point a,Point b)
{
    Point temp;
    temp.x=a.x+b.x;
    temp.y=a.y+b.y;
    temp.z=a.z+b.z;
    return temp;
}

Point sub(Point a,Point b)
{
    Point temp;
    temp.x=a.x-b.x;
    temp.y=a.y-b.y;
    temp.z=a.z-b.z;
    return temp;
}

Point scaleUp(Point a,double s)
{
    Point temp;
    temp.x=a.x*s;
    temp.y=a.y*s;
    temp.z=a.z*s;
    return temp;
}

Point scaleDown(Point a,double s)
{
    Point temp;
    temp.x=a.x/s;
    temp.y=a.y/s;
    temp.z=a.z/s;
    return temp;
}

Point normalize(Point a)
{
    Point temp;
    double len=sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
    temp.x=a.x/len;
    temp.y=a.y/len;
    temp.z=a.z/len;
    return temp;
}

double PointLen(Point a)
{
    return sqrt(a.x*a.x+a.y*a.y+a.z*a.z);
}

double dotProduct(Point a,Point b)
{
    return a.x*b.x+a.y*b.y+a.z*b.z;
}

Point crossProduct(Point a,Point b)
{
    Point temp;
    temp.x=a.y*b.z-a.z*b.y;
    temp.y=a.z*b.x-a.x*b.z;
    temp.z=a.x*b.y-a.y*b.x;
    return temp;
}

Point rot(Point v,Point r,double sign)
{
    Point n,temp;
    double angle=0.05*sign;
    n.x=r.y*v.z-r.z*v.y;
    n.y=r.z*v.x-r.x*v.z;
    n.z=r.x*v.y-r.y*v.x;
    temp.x=v.x*cos(angle)+n.x*sin(angle);
    temp.y=v.y*cos(angle)+n.y*sin(angle);
    temp.z=v.z*cos(angle)+n.z*sin(angle);
    return temp;
}

void debug(Point a)
{
    cout << "(" << a.x << "," << a.y << "," << a.z << ")" << endl;
}


struct Color
{
    double r;
    double g;
    double b;

    Color(double r,double g,double b)
    {
        this->r=r;
        this->g=g;
        this->b=b;
    }

    Color()
    {

    }
};

Color addColor(Color a,Color b)
{
    Color temp;
    temp.r=a.r+b.r;
    temp.g=a.g+b.g;
    temp.b=a.b+b.b;
    return temp;
}

Color mulColor(Color a,double m)
{
    Color temp;
    temp.r=a.r*m;
    temp.g=a.g*m;
    temp.b=a.b*m;
    return temp;
}

struct Coefficient
{
    double ambient;
    double diffuse;
    double specular;
    double reflection;
};

struct Ray
{
    Point source;
    Point dir;
    Ray(Point source,Point dir)
    {
        this->source=source;
        this->dir=normalize(dir);
    }
    Ray()
    {

    }
};

class Object
{
public:
    Color color;
    Coefficient coefficient;
    double exponent;
    Object()
    {

    }

    virtual void draw() = 0;
    virtual double intersectionParameter(Ray r) = 0;
    virtual Point normal(Point p) = 0;

    Point reflectedRay(Ray a,Point n)
    {
        Point r=sub(a.dir,scaleUp(n,2*dotProduct(a.dir,n)));
        return normalize(r);
    }

    Color setColor(Ray r,double t,int level);

};

class Sphere : public Object
{
public:
    Point c;
    double r;

    Sphere(Point c,double r)
    {
        this->c=c;
        this->r=r;
    }

    void draw()
    {
        glColor3f(color.r,color.g,color.b);
        glPushMatrix();
        glTranslatef(c.x,c.y,c.z);
        glutSolidSphere(r,100,100);
        glPopMatrix();
    }

    //t2 dot(B,B)+ 2t dot(B,A-C)+ dot(A-C,A-C)-r2=0
    double intersectionParameter(Ray r)
    {
        Point C=this->c;
        Point A=r.source;
        Point B=r.dir;
        Point OC=sub(A,C);
        double a=dotProduct(B,B); // a=dot(B,B)
        double b=2*dotProduct(OC,B); // b=2dot(B,A-C)
        double c=dotProduct(OC,OC)-this->r*this->r; // c=dot(A-C,A-C)-r2
        double discriminant=b*b-4*a*c;
        if(discriminant<0)
            return -1;
        double t1=(-b+sqrt(discriminant))/(2*a);
        double t2=(-b-sqrt(discriminant))/(2*a);
        double t=min(t1,t2);
        if(t>=nearPlane && t<=farPlane)
            return t;
        return -1;
    }

    Point normal(Point p)
    {
        return normalize(sub(p,this->c));
    }
};

class Triangle : public Object
{
public:
    Point a,b,c,N;
    double D;
    Triangle(Point a,Point b,Point c)
    {
        this->a=a;
        this->b=b;
        this->c=c;
        Point AB=sub(b,a);
        Point AC=sub(c,a);
        N=crossProduct(AB,AC);
        D=dotProduct(N,a);
    }

    void draw()
    {
        glColor3f(color.r,color.g,color.b);
        glBegin(GL_TRIANGLES);
        {
            glVertex3f(a.x,a.y,a.z);
            glVertex3f(b.x,b.y,b.z);
            glVertex3f(c.x,c.y,c.z);
        }
        glEnd();
    }

    double intersectionParameter(Ray r)
    {
        Point vertex0=a;
        Point vertex1=b;
        Point vertex2=c;
        Point edge1,edge2,h,s,q;
        double a,f,u,v;
        edge1=sub(vertex1,vertex0);
        edge2=sub(vertex2,vertex0);
        h=crossProduct(r.dir,edge2);
        a=dotProduct(edge1,h);
        if(a>-EPSILON && a<EPSILON)
            return -1;
        f=1.0/a;
        s=sub(r.source,vertex0);
        u=f*dotProduct(s,h);
        if(u<0.0 || u>1.0)
            return -1;
        q=crossProduct(s,edge1);
        v=f*dotProduct(r.dir,q);
        if(v<0.0 || u+v>1.0)
            return -1;
        double t=f*dotProduct(edge2,q);
        if(t>EPSILON && t>=nearPlane && t<=farPlane)
        {
            return t;
        }
        else
            return -1;
    }


    Point normal(Point p)
    {
        return normalize(N);
    }
};

class Floor : public Object
{
public:
    Point c;
    double w,tSize;
    int tCount;

    Floor(double w,double tSize)
    {
        this->w=w;
        this->tSize=tSize;
        this->c=Point(-w/2.0,-w/2.0,0);
        this->tCount=w/tSize;
    }

    void draw()
    {
        glBegin(GL_QUADS);
        {
            for(int i=0; i<tCount; i++)
            {
                for(int j=0; j<tCount; j++)
                {
                    glColor3f((i+j)%2,(i+j)%2,(i+j)%2);
                    glVertex3f(c.x+tSize*i,c.y+tSize*j,c.z);
                    glVertex3f(c.x+tSize*(i+1),c.y+tSize*j,c.z);
                    glVertex3f(c.x+tSize*(i+1),c.y+tSize*(j+1),c.z);
                    glVertex3f(c.x+tSize*i,c.y+tSize*(j+1),c.z);
                }
            }
        }
        glEnd();
    }

    double intersectionParameter(Ray r)
    {
        if(r.dir.z==0) // in x y plane
            return -1;
        double t=-(r.source.z/r.dir.z);
        Point intersectionPoint=add(r.source,scaleUp(r.dir,t));
        double x=intersectionPoint.x-c.x;
        double y=intersectionPoint.y-c.y;
        if(x<0||x>w)
            return -1;
        if(y<0||y>w)
            return -1;
        int tileX=x/tSize;
        int tileY=y/tSize;
        if(tileX<0||tileX>tCount)
            return -1;
        if(tileY<0||tileY>tCount)
            return -1;
        int c=(tileX+tileY)%2;
        color.r=c;
        color.g=c;
        color.b=c;
        if(t>=nearPlane && t<=farPlane)
            return t;
        return -1;
    }

    Point normal(Point p)
    {
        return Point(0,0,1);
    }
};

vector <Object *> Objects;


pair<double,double> getNearestObjectT(Ray r)
{
    int object=-1;
    double minT=9999999;
    for(unsigned int i=0; i<Objects.size(); i++)
    {
        double temp=Objects[i]->intersectionParameter(r);
        if(temp<=0)
            continue;
        else if(temp<minT)
            minT=temp,object=i;
    }
    return make_pair(object,minT);
}

Color Object::setColor(Ray ray,double t,int level=1)
{
    if(level>maxLevel)
        return Color(0,0,0);
    Color tempColor(0,0,0);
    double diffuse=0,specular=0;
    Point intersectionP=add(ray.source,scaleUp(ray.dir,t));
    Point N=normal(intersectionP);
    Point eyeR=reflectedRay(ray,N);
    for(unsigned int i=0; i<Lights.size(); i++)
    {
        Point dir=normalize(sub(Lights[i],intersectionP));
        Point source=add(intersectionP,scaleUp(dir,EPSILON*1000));
        Ray L(source,dir);
        Point R=normalize(sub(scaleUp(N,2*dotProduct(L.dir,N)),L.dir));
        Point V=normalize(sub(ray.source,intersectionP));
        bool obsecured=false;
        for(unsigned int j=0; j<Objects.size(); j++)
        {
            double temp=Objects[j]->intersectionParameter(L);
            if(temp>0)
            {
                obsecured=true;
                break;
            }
        }
        if(!obsecured)
        {
            double cosThetha=dotProduct(L.dir,N);
            diffuse+=max(cosThetha,0.0)*coefficient.diffuse;
            double cosPhi=dotProduct(R,V);
            specular+=pow(max(cosPhi,0.0),exponent)*coefficient.specular;
        }
    }
    tempColor=addColor(tempColor,mulColor(color,coefficient.ambient+diffuse));
    tempColor=addColor(tempColor,mulColor(Color(1,1,1),specular));
    //cout << tempColor.r << " " << tempColor.g << " " << tempColor.b << endl;
    Point eyeRStart=add(intersectionP,scaleUp(eyeR,EPSILON));
    Ray rRay(eyeRStart,eyeR);
    pair<double,double> nearestObject=getNearestObjectT(rRay);
    int nObject=nearestObject.first;
    double minT=nearestObject.second;
    if(nObject!=-1)
    {
        tempColor=addColor(tempColor,mulColor(Objects[nObject]->setColor(rRay,minT,level+1),coefficient.reflection));
    }
    if(tempColor.r<0)
        tempColor.r=0;
    if(tempColor.r>1)
        tempColor.r=1;
    if(tempColor.g<0)
        tempColor.g=0;
    if(tempColor.g>1)
        tempColor.g=1;
    if(tempColor.b<0)
        tempColor.b=0;
    if(tempColor.b>1)
        tempColor.b=1;
    return tempColor;
}



void createFloor()
{
    Object *floor=new Floor(1000,30);
    floor->coefficient.ambient=0.25;
    floor->coefficient.diffuse=0.25;
    floor->coefficient.specular=0.25;
    floor->coefficient.reflection=0.25;
    floor->exponent=10;
    Objects.push_back(floor);
}

void drawLightSource(Point a)
{
    glColor3f(1,1,0);
    glPushMatrix();
    glTranslatef(a.x,a.y,a.z);
    glutSolidSphere(3,60,60);
    glPopMatrix();
}

void capture()
{
    Color** frameColors;
    frameColors=new Color*[imageWidth];
    for(int i=0; i<imageWidth; i++)
    {
        frameColors[i]=new Color[imageHeight];
    }
    double distance=(windowHeight/2)/tan(FOV*pi/360);
    Point topLeft=add(eye,add(sub(scaleUp(l,distance),scaleUp(r,windowWidth/2)),scaleUp(u,windowHeight/2)));
    cout << "Eye : ";
    debug(eye);
    cout << "Distance :" << distance << endl;
    cout << "Top left : ";
    debug(topLeft);
    cout << "Saving...." << endl;
    debug(topLeft);
    double dw=(double)windowWidth/(double)imageWidth;
    double dh=(double)windowHeight/(double)imageHeight;
    cout << "TEST" << dw << " " << dh << endl;
    for(int i=0; i<imageWidth; i++)
    {
        for(int j=0; j<imageHeight; j++)
        {
            Point temp=sub(add(topLeft,scaleUp(r,i*dw)),scaleUp(u,j*dh));
            //cout << "Point : ";
            //debug(temp);
            Ray ray(eye,sub(temp,eye));
            Color tempColor= {0,0,0};
            pair<double,double> nearestObject=getNearestObjectT(ray);
            int nObject=nearestObject.first;
            double minT=nearestObject.second;
            if(nObject!=-1)
            {
                tempColor=Objects[nObject]->setColor(ray,minT,1);
            }
            frameColors[i][j]=tempColor;
        }
    }

    bitmap_image image(imageWidth,imageHeight);
    for(int i=0; i<imageWidth; i++)
    {
        for(int j=0; j<imageHeight; j++)
        {
            Color color=frameColors[i][j];
            image.set_pixel(i,j,color.r*255,color.g*255,color.b*255);
        }
    }
    image.save_image("out.bmp");

    cout << "Saved" << endl;

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
    case '0':
        capture();
    default:
        break;
    }
}


void specialKeyListener(int key, int x,int y)
{
    switch(key)
    {
    case GLUT_KEY_DOWN:
        eye=sub(eye,scaleUp(l,3));
        break;
    case GLUT_KEY_UP:
        eye=add(eye,scaleUp(l,3));
        break;
    case GLUT_KEY_RIGHT:
        eye=add(eye,scaleUp(r,3));
        break;
    case GLUT_KEY_LEFT:
        eye=sub(eye,scaleUp(r,3));
        break;
    case GLUT_KEY_PAGE_UP:
        eye=add(eye,scaleUp(u,3));
        break;
    case GLUT_KEY_PAGE_DOWN:
        eye=sub(eye,scaleUp(u,3));
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


void mouseListener(int button, int state, int x, int y)
{
    switch(button)
    {
    case GLUT_LEFT_BUTTON:
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
    gluLookAt(eye.x,eye.y,eye.z,eye.x+l.x,eye.y+l.y,eye.z+l.z,u.x,u.y,u.z);


    //again select MODEL-VIEW
    glMatrixMode(GL_MODELVIEW);


    /****************************
    / Add your objects from here
    ****************************/
    //add objects
    drawAxes();
    // draw objects
    for(unsigned int i=0; i<Objects.size(); i++)
        Objects[i]->draw();
    for(unsigned int i=0; i<Lights.size(); i++)
        drawLightSource(Lights[i]);

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
    eye= {0,-70,30};
    l= {0,1,0};
    r= {1,0,0};
    u= {0,0,1};

    createFloor();
    // read data
    ifstream fin("description.txt");
    int objectsCount,lightCount;
    string type;
    Object *object;
    fin >> maxLevel;
    fin >> imageHeight;
    imageWidth=imageHeight;
    fin >> objectsCount;
    for(int i=0; i<objectsCount; i++)
    {
        fin >> type;
        if(type=="sphere")
        {
            Point c;
            double r;
            fin >> c.x >> c.y >> c.z;
            fin >> r;
            object = new Sphere(c,r);
            fin >> object->color.r >> object->color.g >> object->color.b;
            fin >> object->coefficient.ambient >> object->coefficient.diffuse;
            fin >> object->coefficient.reflection >> object->coefficient.specular;
            fin >> object->exponent;
            Objects.push_back(object);
        }
        else if(type=="pyramid")
        {
            Point base;
            double len,height,exponet;
            Color color;
            Coefficient coefficient;
            fin >> base.x >> base.y >> base.z;
            fin >> len >> height;
            fin >> color.r >> color.g >> color.b;
            fin >> coefficient.ambient >> coefficient.diffuse;
            fin >> coefficient.specular >> coefficient.reflection;
            fin >> exponet;
            // add six triangles
            Point triangleP[6][3];
            triangleP[0][0]=Point(base.x,base.y,base.z); //1
            triangleP[0][1]=Point(base.x+len,base.y,base.z);
            triangleP[0][2]=Point(base.x+len/2,base.y+len/2,height);
            triangleP[1][0]=Point(base.x+len,base.y,base.z); //2
            triangleP[1][1]=Point(base.x+len,base.y+len,base.z);
            triangleP[1][2]=triangleP[0][2];
            triangleP[2][0]=Point(base.x+len,base.y+len,base.z); //3
            triangleP[2][1]=Point(base.x,base.y+len,base.z);
            triangleP[2][2]=triangleP[0][2];
            triangleP[3][0]=Point(base.x,base.y+len,base.z); //4
            triangleP[3][1]=Point(base.x,base.y,base.z);
            triangleP[3][2]=triangleP[0][2];
            triangleP[4][0]=Point(base.x,base.y,base.z); //5 base
            triangleP[4][1]=Point(base.x+len,base.y,base.z);
            triangleP[4][2]=Point(base.x+len,base.y+len,base.z);
            triangleP[5][0]=Point(base.x+len,base.y+len,base.z); //6 base
            triangleP[5][1]=Point(base.x,base.y+len,base.z);
            triangleP[5][2]=Point(base.x,base.y,base.z);
            for(int i=0; i<6; i++)
            {
                object = new Triangle(triangleP[i][0],triangleP[i][1],triangleP[i][2]);
                object->coefficient=coefficient;
                object->color=color;
                object->exponent=exponet;
                Objects.push_back(object);
            }
        }
    }
    fin >> lightCount;
    for(int i=0; i<lightCount; i++)
    {
        Point temp;
        fin >> temp.x >> temp.y >> temp.z;
        Lights.push_back(temp);
    }
    //debug
    for(unsigned int i=0;i<Objects.size();i++)
    {
        cout << "Object " << i << endl;
        cout << Objects[i]->coefficient.ambient << " " << Objects[i]->coefficient.diffuse << " " << Objects[i]->coefficient.specular << " " << Objects[i]->coefficient.reflection << endl;
        cout << Objects[i]->exponent << endl;
    }

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
    gluPerspective(FOV,	1,	1,	1000.0);
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

    glutCreateWindow("Ray");

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
