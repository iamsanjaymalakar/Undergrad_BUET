#include <cmath>

#include "Automobile.h"

using namespace std;

#define PI acos(-1.0)
#define min(a, b) ((a) < (b) ? (a) : (b))
#define max(a, b) ((a) > (b) ? (a) : (b))

void rotateVector(double &x, double &y, double angleDeg)
{
    double angleRad = PI * angleDeg / 180.0;
    double xNew, yNew;

    xNew = x * cos(angleRad) - y * sin(angleRad);
    yNew = y * cos(angleRad) + x * sin(angleRad);

    double tmp=sqrt(xNew*xNew+yNew*yNew);

    x = xNew/tmp;
    y = yNew/tmp;
}

void moveAlongVector(double &x, double &y, double directionX, double directionY, double dist)
{
    x = x + dist * directionX;
    y = y + dist * directionY;
}

class Car : public Automobile
{
public:
    Car() : Automobile("car")
    {
        speed = 0;
        fuel = 30;
    }

    void TurnLeft()
    {
        rotateVector(directionX, directionY, 5);
    }

    void TurnRight()
    {
        rotateVector(directionX, directionY, -5);
    }

    void IncreaseSpeed()
    {
        speed += 10;
        speed = min(speed, 120);
    }

    void DecreaseSpeed()
    {
        speed -= 10;
        speed = max(speed, 0);
    }

    void Move()
    {
        //
        // Compute the distance traveled and fuel consumed in 1 second
        //

        double dist = speed * 1000 * 100 / 3600;
        double fuelUsed = speed / 3600 / 8;

        if (fuel > 0)
        {
            fuel -= fuelUsed;
            fuel = max(0, fuel);
            moveAlongVector(posX, posY, directionX, directionY, dist);
        }
    }
};

class Bus : public AutomobileWithManualXmission
{
public:
    Bus() : AutomobileWithManualXmission("Bus")
    {
        speed = 0;
        fuel = 80;
    }

    void TurnLeft()
    {
        rotateVector(directionX, directionY, 15);
    }

    void TurnRight()
    {
        rotateVector(directionX, directionY, -15);
    }

    void IncreaseSpeed()
    {
        const int gearMaxSpeed[] = {0, 20, 30, 45, 60, 80};

        speed += 6;
        speed = min(speed, gearMaxSpeed[gearPosition]);
    }

    void DecreaseSpeed()
    {
        const int gearMinSpeed[] = {0, 0, 10, 25, 35, 50};

        speed -= 6;
        speed = max(speed, gearMinSpeed[gearPosition]);
    }

    void Move()
    {
        //
        // Compute the distance traveled and fuel consumed in 1 second
        //

        double dist = speed * 1000 * 100 / 3600;
        double fuelUsed = speed / 3600 / 4;

        if (fuel > 0)
        {
            fuel -= fuelUsed;
            fuel = max(0, fuel);
            moveAlongVector(posX, posY, directionX, directionY, dist);
        }
    }
};
