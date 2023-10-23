package de.sgey;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class Dividieren {
    public static void main(String[] args) {
        BigDecimal number1;
        BigDecimal number2;
        Scanner scanner1 = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);


        System.out.println("Bitte geben Sie eine Zahl ein:");
        try {
            number1 = scanner1.nextBigDecimal();
        } catch (Exception e){
            throw new IllegalArgumentException("Bitte geben Sie eine gültige Zahl ein");
        }

        System.out.println("Geben Sie nun die Zahl ein, durch welche Sie teilen wollen:");
        try {
            number2 = scanner2.nextBigDecimal();
        } catch (Exception e){
            throw new IllegalArgumentException("Bitte geben Sie eine gültige Zahl ein");
        }

        System.out.println(number1.divide(number2,2, RoundingMode.UP));
    }


}
