package org.dailycodebuffer.codebufferspringbootmongodb.myfactory;

public class MotorcycleFactory extends MotorVehicleFactory {
    @Override
    protected MotorVehicle createMotorVehicle() {
        return new Motorcycle();
    }
}
