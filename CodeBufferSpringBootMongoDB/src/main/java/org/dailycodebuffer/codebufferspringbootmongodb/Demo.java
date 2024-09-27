package org.dailycodebuffer.codebufferspringbootmongodb;

import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        String str="Hi there, Hello World; How are you";
        String[] strarray = str.split("[,;]");

        System.out.println(Arrays.toString(strarray));
        for(String st:strarray) System.out.println(st);
    }
}
