#pragma once
#include <cstring>

using namespace std;

class Automobile
{
private:
    char strType[10]; // Stores the type of the automobile

protected:
    double posX;    // Distance between origin and center position of automobile, along X-axis (in cm)
    double posY;    // Distance between origin and center position of automobile, along Y-axis (in cm)
    double directionX; // Direction vector's X coefficient
    double directionY; // Direction vector's Y coefficient
    double speed;   // Stores speed of the automobile
    double fuel;    // Stores remaining fuel

public:
    Automobile(char *type)
    {
        strcpy(strType, type);
    }

    void SetInitialPosition(double x, double y)
    {
        posX = x;
        posY = y;
    }

    void SetInitialDirection(double x, double y)
    {
        directionX = x;
        directionY = y;
    }

    const char* GetType()
    {
        return strType;
    }

    double GetFuel()
    {
        return fuel;
    }

    double GetSpeed()
    {
        return speed;
    }

    void GetPosition(double &x, double &y)
    {
        x = posX;
        y = posY;
    }

    void GetDirection(double &x, double &y)
    {
        x = directionX;
        y = directionY;
    }

    //
    // The following functions should be overridden in derived classes.
    //

    // Turn left (counter clockwise) by angle mentioned in the chart, without moving forward.
    virtual void TurnLeft() = 0;

    // Turn right (clockwise) by angle mentioned in the chart, without moving forward.
    virtual void TurnRight() = 0;

    // Increase speed by amount mentioned in the chart, but don't move forward yet
    virtual void IncreaseSpeed() = 0;

    // Decrease speed by amount mentioned in the chart, but don't move forward yet
    virtual void DecreaseSpeed() = 0;

    // move forward 1 second's worth of distance
    virtual void Move() = 0;
};

class AutomobileWithManualXmission : public Automobile
{
protected:
    int gearPosition;   // Stores the current gear position

public:
    AutomobileWithManualXmission(char *type) : Automobile(type)
    {
        gearPosition = 0;
    }

    void ShiftGearUp()
    {
        if (gearPosition < 5)
            gearPosition++;
    }
    void ShiftGearDown()
    {
        if (gearPosition > 0)
            gearPosition--;
    }

    int GetGearPosition()
    {
        return gearPosition;
    }
};

void Print(Automobile* v)
{
    double x, y, directionX, directionY, speed, fuel;

    v->GetPosition(x, y);
    v->GetDirection(directionX, directionY);

    system("cls");

    printf("===============================================================\n");
    printf("type     : %s\n", v->GetType());
    printf("Position : (%.4f, %.4f)\n", x, y);
    printf("Direction: (%.4f, %.4f)\n", directionX, directionY);
    printf("Speed    : %.4f\n", v->GetSpeed());
    if (0 == strcmp(v->GetType(), "Bus") || 0 == strcmp(v->GetType(), "Truck"))
    {
        printf("Gear     : %d\n", ((AutomobileWithManualXmission*)v)->GetGearPosition());
    }
    printf("Fuel left: %.4f\n", v->GetFuel());
    printf("===============================================================\n\n");
}
