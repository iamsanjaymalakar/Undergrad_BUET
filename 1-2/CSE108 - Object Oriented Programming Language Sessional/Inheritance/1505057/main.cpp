#include <iostream>
#include <cstdio>
#include <cmath>
#include <cstring>
#include <cstdlib>
#include <string>
#include <algorithm>
#include <vector>
#include <map>

#include "Automobile.h"

#define PI 2*acos(0.0)

using namespace std;

typedef long long unsigned llu;
typedef long long ll;

double Deg2Rad(double degree)
{
    return (degree*2*PI)/360.0;
}

double Rad2Deg(double radian)
{
    return (radian*360)/(2.0*PI);
}

double SpeedConv(double speed)
{
    return speed*100000.0/3600.0; // at cm/s
}

double dAngle(double x, double y)
{
    if(x>=0 && y>=0)
    {
        if(x==0 && y!=0)
            return (90);
        else if(x==0 && y==0)
            return 0;
        else
            return Rad2Deg(atan(y/x));
    }
    else if(x<0 && y>=0)
    {
        x=-x;
        if(abs(x)==0 && abs(y)!=0)
            return 180-90;
        else
            return 180-Rad2Deg(atan(y/x));
    }
    else if(x<0 && y<0)
    {
        x=-x;
        y=-y;
        return 180+Rad2Deg(atan(y/x));
    }
    else if(x>=0 && y<0)
    {
        y=-y;
        return 360-Rad2Deg(atan(y/x));
    }
}

double rAngle(double x,double y)
{
    return Deg2Rad(dAngle(x,y));
}

class Car : public Automobile
{
public:
    double inAngle,newAngle,angle,r,newX,newY,Distance=0;
    int turnAngle=5,incdec=10,maxSpeed=120,fuelEff=8;
    double dx,dy;
    Car() : Automobile("Car")
    {
        speed=0;
        fuel=30;
    }

    void TurnLeft()
    {
        newX=0,newY=0;
        angle=Deg2Rad(turnAngle);
        newX=directionX*cos(angle)-directionY*sin(angle);
        newY=directionX*sin(angle)+directionY*cos(angle);
        if(abs(newX-0)<=0.000001)
            newX=0;
        if(abs(newY-0)<=0.000001)
            newY=0;
        directionX=newX;
        directionY=newY;
    }

    void TurnRight()
    {
        newX=0,newY=0;
        angle=Deg2Rad(-turnAngle);
        newX=directionX*cos(angle)-directionY*sin(angle);
        newY=directionX*sin(angle)+directionY*cos(angle);
        if(abs(newX-0)<=0.000001)
            newX=0;
        if(abs(newY-0)<=0.000001)
            newY=0;
        directionX=newX;
        directionY=newY;
    }

    void IncreaseSpeed()
    {
        if((speed+incdec)<=maxSpeed)
            speed+=incdec;
        else
        {
            speed=maxSpeed;
            cout << "You've reached the maximum speed !" << endl;
        }
    }

    void DecreaseSpeed()
    {
        if((speed-incdec)>=0)
            speed-=incdec;
        else
        {
            speed=0;
            cout << "You're already at the minimum speed !" << endl;
        }
    }

    void Move()
    {
        if(fuel)
        {
            Distance=SpeedConv(speed)*1;
            dx=Distance*cos(rAngle(directionX,directionY));
            dy=Distance*sin(rAngle(directionX,directionY));
            if(abs(dx-0)<=0.000001)
                dx=0;
            if(abs(dy-0)<=0.000001)
                dy=0;
            posX+=dx;
            posY+=dy;
            fuel=fuel-((double)Distance/(double)(fuelEff*100000));
             if(fuel<0)
                fuel=0;
        }
        else
            cout << "Your tank is empty" << endl;
    }

};

class SUV : public Automobile
{
public:
    double inAngle,newAngle,angle,r,newX,newY,Distance=0;
    int turnAngle=10,incdec=8,maxSpeed=100,fuelEff=6;
    double dx,dy;
    SUV() : Automobile("SUV")
    {
        speed=0;
        fuel=50;
    }

    void TurnLeft()
    {
        newX=0,newY=0;
        angle=Deg2Rad(turnAngle);
        newX=directionX*cos(angle)-directionY*sin(angle);
        newY=directionX*sin(angle)+directionY*cos(angle);
        if(abs(newX-0)<=0.000001)
            newX=0;
        if(abs(newY-0)<=0.000001)
            newY=0;
        directionX=newX;
        directionY=newY;
    }

    void TurnRight()
    {
        newX=0,newY=0;
        angle=Deg2Rad(-turnAngle);
        newX=directionX*cos(angle)-directionY*sin(angle);
        newY=directionX*sin(angle)+directionY*cos(angle);
        if(abs(newX-0)<=0.000001)
            newX=0;
        if(abs(newY-0)<=0.000001)
            newY=0;
        directionX=newX;
        directionY=newY;
    }

    void IncreaseSpeed()
    {
        if((speed+incdec)<=maxSpeed)
            speed+=incdec;
        else
        {
            speed=maxSpeed;
            cout << "You've reached the maximum speed !" << endl;
        }
    }

    void DecreaseSpeed()
    {
        if((speed-incdec)>=0)
            speed-=incdec;
        else
        {
            speed=0;
            cout << "You're already at the minimum speed !" << endl;
        }
    }

   void Move()
    {
        if(fuel)
        {
            Distance=SpeedConv(speed)*1;
            dx=Distance*cos(rAngle(directionX,directionY));
            dy=Distance*sin(rAngle(directionX,directionY));
            if(abs(dx-0)<=0.000001)
                dx=0;
            if(abs(dy-0)<=0.000001)
                dy=0;
            posX+=dx;
            posY+=dy;
            fuel=fuel-((double)Distance/(double)(fuelEff*100000));
             if(fuel<0)
                fuel=0;
        }
        else
            cout << "Your tank is empty" << endl;
    }
};

class Bus : public AutomobileWithManualXmission
{
public:
    double inAngle,newAngle,angle,r,newX,newY,Distance=0;
    int turnAngle=15,incdec=6,maxSpeed=80,fuelEff=4;
    double dx,dy;
    Bus() : AutomobileWithManualXmission("Bus")
    {
        speed=0;
        fuel=80;
    }

    void TurnLeft()
    {
        newX=0,newY=0;
        angle=Deg2Rad(turnAngle);
        newX=directionX*cos(angle)-directionY*sin(angle);
        newY=directionX*sin(angle)+directionY*cos(angle);
        if(abs(newX-0)<=0.000001)
            newX=0;
        if(abs(newY-0)<=0.000001)
            newY=0;
        directionX=newX;
        directionY=newY;
    }

    void TurnRight()
    {
        newX=0,newY=0;
        angle=Deg2Rad(-turnAngle);
        newX=directionX*cos(angle)-directionY*sin(angle);
        newY=directionX*sin(angle)+directionY*cos(angle);
        if(abs(newX-0)<=0.000001)
            newX=0;
        if(abs(newY-0)<=0.000001)
            newY=0;
        directionX=newX;
        directionY=newY;
    }

    void IncreaseSpeed()
    {
        if(fuel)
        {
            if(gearPosition==0)
            {
                cout << "Shift up your gear to increase speed" << endl;
            }
            else if(gearPosition==1)
            {
                if(speed>=0 && speed<=20)
                {
                    if(speed+incdec>20)
                    {
                        speed=20;
                        cout << "Shift up your gear to increase speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
            else if(gearPosition==2)
            {
                if(speed>=10 && speed<=30)
                {
                    if(speed+incdec>30)
                    {
                        speed=30;
                        cout << "Shift up your gear to increase speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
            else if(gearPosition==3)
            {
                if(speed>=25 && speed<=45)
                {
                    if(speed+incdec>45)
                    {
                        speed=45;
                        cout << "Shift up your gear to increase speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
            else if(gearPosition==4)
            {
                if(speed>=35 && speed<=60)
                {
                    if(speed+incdec>60)
                    {
                        speed=60;
                        cout << "Shift up your gear to increase speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
            else if(gearPosition==5)
            {
                if(speed>=50)
                {
                    if(speed+incdec>maxSpeed)
                    {
                        speed=maxSpeed;
                        cout << "You are already at your highest speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
        }
        else
            cout << "Your tank is empty" << endl;
    }

    void DecreaseSpeed()
    {
        if(fuel)
        {
            if(gearPosition==0)
            {
                cout << "You are already at your  lowest speed" << endl;
            }
            else if(gearPosition==1)
            {
                if(speed>=0 && speed<=20)
                {
                    if(speed-incdec<0)
                    {
                        speed=0;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            else if(gearPosition==2)
            {
                if(speed>=10 && speed<=30)
                {
                    if(speed-incdec<10)
                    {
                        speed=10;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            else if(gearPosition==3)
            {
                if(speed>=25 && speed<=45)
                {
                    if(speed-incdec<25)
                    {
                        speed=25;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            else if(gearPosition==4)
            {
                if(speed>=35 && speed<=60)
                {
                    if(speed-incdec<35)
                    {
                        speed=35;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            else if(gearPosition==5)
            {
                if(speed>=50)
                {
                    if(speed+incdec<50)
                    {
                        speed=50;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            if(speed<0)
                speed=0;
        }
        else
            cout << "Your tank is empty" << endl;
    }

    void Move()
    {
        if(fuel)
        {
            Distance=SpeedConv(speed)*1;
            dx=Distance*cos(rAngle(directionX,directionY));
            dy=Distance*sin(rAngle(directionX,directionY));
            if(abs(dx-0)<=0.000001)
                dx=0;
            if(abs(dy-0)<=0.000001)
                dy=0;
            posX+=dx;
            posY+=dy;
            fuel=fuel-((double)Distance/(double)(fuelEff*100000));
            if(fuel<0)
                fuel=0;
        }
        else
            cout << "Your tank is empty" << endl;
    }

};

class Truck : public AutomobileWithManualXmission
{
public:
    double inAngle,newAngle,angle,r,newX,newY,Distance=0;
    int turnAngle=25,incdec=4,maxSpeed=60,fuelEff=2;
    double dx,dy;
    Truck() : AutomobileWithManualXmission("Truck")
    {
        speed=0;
        fuel=100;
    }

    void TurnLeft()
    {
        newX=0,newY=0;
        angle=Deg2Rad(turnAngle);
        newX=directionX*cos(angle)-directionY*sin(angle);
        newY=directionX*sin(angle)+directionY*cos(angle);
        if(abs(newX-0)<=0.000001)
            newX=0;
        if(abs(newY-0)<=0.000001)
            newY=0;
        directionX=newX;
        directionY=newY;
    }

    void TurnRight()
    {
        newX=0,newY=0;
        angle=Deg2Rad(-turnAngle);
        newX=directionX*cos(angle)-directionY*sin(angle);
        newY=directionX*sin(angle)+directionY*cos(angle);
        if(abs(newX-0)<=0.000001)
            newX=0;
        if(abs(newY-0)<=0.000001)
            newY=0;
        directionX=newX;
        directionY=newY;
    }

    void IncreaseSpeed()
    {
        if(fuel)
        {
            if(gearPosition==0)
            {
                cout << "Shift up your gear to increase speed" << endl;
            }
            else if(gearPosition==1)
            {
                if(speed>=0 && speed<=20)
                {
                    if(speed+incdec>20)
                    {
                        speed=20;
                        cout << "Shift up your gear to increase speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
            else if(gearPosition==2)
            {
                if(speed>=10 && speed<=30)
                {
                    if(speed+incdec>30)
                    {
                        speed=30;
                        cout << "Shift up your gear to increase speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
            else if(gearPosition==3)
            {
                if(speed>=25 && speed<=45)
                {
                    if(speed+incdec>45)
                    {
                        speed=45;
                        cout << "Shift up your gear to increase speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
            else if(gearPosition==4)
            {
                if(speed>=35 && speed<=60)
                {
                    if(speed+incdec>60)
                    {
                        speed=60;
                        cout << "Shift up your gear to increase speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
            else if(gearPosition==5)
            {
                if(speed>=50)
                {
                    if(speed+incdec>maxSpeed)
                    {
                        speed=maxSpeed;
                        cout << "You are already at your highest speed" << endl;
                    }
                    else
                        speed+=incdec;
                }
                else
                    speed+=incdec;
            }
        }
        else
            cout << "Your tank is empty" << endl;
    }

    void DecreaseSpeed()
    {
        if(fuel)
        {
            if(gearPosition==0)
            {
                cout << "You are already at your  lowest speed" << endl;
            }
            else if(gearPosition==1)
            {
                if(speed>=0 && speed<=20)
                {
                    if(speed-incdec<0)
                    {
                        speed=0;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            else if(gearPosition==2)
            {
                if(speed>=10 && speed<=30)
                {
                    if(speed-incdec<10)
                    {
                        speed=10;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            else if(gearPosition==3)
            {
                if(speed>=25 && speed<=45)
                {
                    if(speed-incdec<25)
                    {
                        speed=25;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            else if(gearPosition==4)
            {
                if(speed>=35 && speed<=60)
                {
                    if(speed-incdec<35)
                    {
                        speed=35;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            else if(gearPosition==5)
            {
                if(speed>=50)
                {
                    if(speed+incdec<50)
                    {
                        speed=50;
                        cout << "Shift down your gear to decrease speed" << endl;
                    }
                    else
                        speed-=incdec;
                }
                else
                    speed-=incdec;
            }
            if(speed<0)
                speed=0;
        }
        else
            cout << "Your tank is empty" << endl;
    }

    void Move()
    {
        if(fuel)
        {
            Distance=SpeedConv(speed)*1;
            dx=Distance*cos(rAngle(directionX,directionY));
            dy=Distance*sin(rAngle(directionX,directionY));
            if(abs(dx-0)<=0.000001)
                dx=0;
            if(abs(dy-0)<=0.000001)
                dy=0;
            posX+=dx;
            posY+=dy;
            fuel=fuel-((double)Distance/(double)(fuelEff*100000));
             if(fuel<0)
                fuel=0;
        }
        else
            cout << "Your tank is empty" << endl;
    }
};

int main()
{
    double  posX;
    double posY;
    double directionX;
    double directionY;
    char ch;
    Bus c;
    c.SetInitialDirection(1,0);
    c.SetInitialPosition(0,0);
    while(1)
    {
        cout<<c.GetType()<<endl;
        c.GetPosition(posX,posY);
        cout<<"Position ( "<<posX<<", "<<posY<<" )"<<endl;
        c.GetDirection(directionX,directionY);
        cout<<"Direction ( "<<directionX<<", "<<directionY<<" )"<<endl;
        cout<<"Gear = "<<c.GetGearPosition()<<endl;
        cout<<"Speed = "<<c.GetSpeed()<<endl;
        cout<<"Fuel = "<<c.GetFuel()<<endl;
        cin>>ch;
        if(ch=='q')
            break;
        switch(ch)
        {
        case 'w':
            c.IncreaseSpeed();
            break;
        case 's':
            c.DecreaseSpeed();
            break;
        case 'a':
            c.TurnLeft();
            break;
        case 'd':
            c.TurnRight();
            break;
        case '<':
            c.ShiftGearUp();
            break;
        case '>':
            c.ShiftGearDown();
            break;
        default:
            c.Move();
            break;
        }
        cout << endl << endl ;
    }
    return 0;
}



