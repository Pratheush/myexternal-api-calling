package org.dailycodebuffer.codebufferspringbootmongodb.myfactory;

public class Motorcycle implements MotorVehicle {
    @Override
    public void build() {
        System.out.println("Build Motorcycle");
    }
}
