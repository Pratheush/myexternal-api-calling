package org.dailycodebuffer.codebufferspringbootmongodb;

public class MyFactorial {
    public static Integer compute(Integer i){
        if (i == 0|| i==1) return 1;
        else if (i>1) return i*compute(i-1);
        else throw new IllegalArgumentException("Factorial of a negative number is undefined");
    }
}
