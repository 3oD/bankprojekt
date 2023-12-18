package spielereien;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

/**
 * The FormatierungSpielereien class contains methods for reading long and double inputs from the user and performing various formatting operations.
 */
public class FormatierungSpielereien {

    /**
     * Reads a long input from the user using the provided Scanner object.
     *
     * @param scanner the Scanner object to use for reading input
     * @return the long value entered by the user
     */
    private static long readLongInput(Scanner scanner) {
        System.out.println("Bitte geben Sie eine Ganzzahl ein: ");
        if(!scanner.hasNextLong()){
            System.out.println("Invalid input provided. Expecting a Long number.");
            return readLongInput(scanner);
        }
        return scanner.nextLong();
    }

    /**
     * Reads a double input from the user using the provided Scanner object.
     *
     * @param scanner the Scanner object to use for reading input
     * @return the double value entered by the user
     */
    private static double readDoubleInput(Scanner scanner) {
        System.out.println("Bitte geben Sie eine Gleitkommazahl ein: ");
        if(!scanner.hasNextDouble()){
            System.out.println("Invalid input provided. Expecting a Double number.");
            return readDoubleInput(scanner);
        }
        return scanner.nextDouble();
    }

    /**
     * The main method reads a long and a double input from the user and performs various formatting operations.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long longVar = 0;
        double doubleVar = 0.0;

        try (Scanner scanner = new Scanner(System.in)) {
            longVar = readLongInput(scanner);
            doubleVar = readDoubleInput(scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (FileWriter fileWriter = new FileWriter("output.txt");
             PrintWriter printWriter = new PrintWriter(fileWriter);) {


            printWriter.printf("%d%n", longVar); //1
            printWriter.printf("%012d%n", longVar); //2
            printWriter.printf("%,+d%n", longVar); //3
            printWriter.printf("%X%n", longVar); //4

            printWriter.printf("%f%n", doubleVar); //5
            printWriter.printf("%+.5f%n", doubleVar); //6
            printWriter.printf("%e%n", doubleVar); //7
            printWriter.printf(Locale.US, "%.2f%n", doubleVar); //8

            LocalDate today = LocalDate.now();
            printWriter.printf("%te %tb %tY (%ta)%n", today, today, today, today); //9

            Calendar calendar = Calendar.getInstance(Locale.ITALIAN);
            printWriter.printf("%1$td %1$tm %1$tY %1$tA %n", calendar); //10

            LocalTime now = LocalTime.now();
            printWriter.printf("%1$tI:%1$tM %1$tp %n", now); //11

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
