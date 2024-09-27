package org.dailycodebuffer.codebufferspringbootmongodb.myfactory;

public class FactoryMain {
    public static void main(String[] args) {
        /*MotorVehicle mcycle=new Motorcycle();
        mcycle.build();

        MotorVehicle car=new Car();
        car.build();*/

        MotorVehicleFactory motorcycleFactory=new MotorcycleFactory();
        //motorcycleFactory.createMotorVehicle();
        motorcycleFactory.create();

        MotorVehicleFactory carFactory=new CarFactory();
        //carFactory.createMotorVehicle();
        carFactory.create();
    }
}
