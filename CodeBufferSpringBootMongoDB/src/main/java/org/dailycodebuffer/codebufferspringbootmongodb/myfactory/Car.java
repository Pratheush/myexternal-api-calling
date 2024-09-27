package org.dailycodebuffer.codebufferspringbootmongodb.myfactory;

public class Car implements MotorVehicle {
    @Override
    public void build() {
        System.out.println("Build Car");
    }
}
