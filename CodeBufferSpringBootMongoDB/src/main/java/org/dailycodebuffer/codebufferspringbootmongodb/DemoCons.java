package org.dailycodebuffer.codebufferspringbootmongodb;

public class DemoCons {
    public static void main(String[] args) {
        Dog d=new Dog();
    }
}

class Animal{
    Animal(){
        System.out.println("Animal is Created");
    }
}

class Dog extends Animal{
    Dog(){
        System.out.println("Dog is Created");
    }
}
