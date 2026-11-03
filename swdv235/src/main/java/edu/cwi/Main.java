package edu.cwi;

import me.gosimple.nbvcxz.resources.Generator;

public class Main {
    public static void main(String[] args) {
        String pass1 = Generator.generatePassphrase("-", 5);
        System.out.println("Your new passphrase is: " + pass1);
    }
}