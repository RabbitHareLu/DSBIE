package com.dsbie.rearend;

import com.dsbie.rearend.exception.KToolException;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        KToolsContext instance = KToolsContext.getInstance();


        Properties properties = instance.getProperties();
        properties.forEach((k, v) -> {
            System.out.println(k + "--->" + v);
        });
    }
}