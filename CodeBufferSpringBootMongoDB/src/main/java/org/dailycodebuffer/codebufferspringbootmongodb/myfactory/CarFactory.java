package org.dailycodebuffer.codebufferspringbootmongodb.myfactory;

public class CarFactory extends MotorVehicleFactory {
    @Override
    protected MotorVehicle createMotorVehicle() {
        return new Car();
    }
}
