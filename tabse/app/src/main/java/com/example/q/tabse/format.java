package com.example.q.tabse;

public class format {
    public static String StringToPhone(String s) {
        if(s.length()==9) {
            return s.substring(0, 2) + "-" + s.substring(2, 5) + "-" + s.substring(5, 9);
        }
        if(s.length()==10) {
            return s.substring(0, 3) + "-" + s.substring(3, 6) + "-" + s.substring(6, 10);
        }
        if(s.length()==11) {
            return s.substring(0, 3) + "-" + s.substring(3, 7) + "-" + s.substring(7, 11);
        }
        return s;
    }
}
