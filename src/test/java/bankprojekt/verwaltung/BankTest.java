package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Kunde;
import bankprojekt.verarbeitung.Sparbuch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {
    Bank b = new Bank(12312L);
    Bank b2 = new Bank(145351L);
    Kunde[] kundenArray = {
            new Kunde("Max", "Mustermann", "Home", LocalDate.parse("2001-10-29")),
            new Kunde("John", "Doe", "Work", LocalDate.parse("1985-07-12")),
            new Kunde("Jane", "Doe", "Home", LocalDate.parse("1986-08-13")),
            new Kunde("Alice", "Johnson", "School", LocalDate.parse("1999-11-14")),
            new Kunde("Bob", "Smith", "Home", LocalDate.parse("1978-04-25")),
            new Kunde("Charlie", "Brown", "Work", LocalDate.parse("1990-01-01")),
            new Kunde("Lucy", "Van Pelt", "Home", LocalDate.parse("1990-02-02")),
            new Kunde("Linus", "Van Pelt", "Work", LocalDate.parse("1990-03-03")),
            new Kunde("Peppermint", "Patty", "Home", LocalDate.parse("1990-04-04")),
            new Kunde("Snoopy", "Dog", "Work", LocalDate.parse("1990-05-05"))
    };
    Girokonto girokonto = new Girokonto(kundenArray[0], 6789L, 1000);
    Sparbuch sparbuch = new Sparbuch(kundenArray[1], 4567L);
    long kontoNummer1, kontoNummer2;

    @BeforeEach
    void setup() throws KontonummerNichtVorhandenException {
        for (Kunde kunde : kundenArray) {
            b.girokontoErstellen(kunde);
            b.sparbuchErstellen(kunde);
        }

        kontoNummer1 = b.getAlleKontonummern().get(5);
        kontoNummer2 = b.getAlleKontonummern().get(9);

        b.geldEinzahlen(kontoNummer1, 500);
    }

    @Test
    void testGetBankleitzahl(){
        assertEquals(12312L, b.getBankleitzahl());
    }

    @Test
    void brutTest() throws GesperrtException, KontonummerNichtVorhandenException {
        for (Kunde kunde : kundenArray) {
            b.girokontoErstellen(kunde);
            b.sparbuchErstellen(kunde);
        }
        System.out.println(b.getAlleKonten());
        System.out.println(b.getAlleKontonummern());
        long kontoNum = b.getAlleKontonummern().get(1);

        b.geldEinzahlen(kontoNum, 100.0);
        System.out.println("Geld einzahlen: " + b.getKontostand(kontoNum));
        b.geldAbheben(kontoNum, 99.0);
        System.out.println("Geld abheben: " + b.getKontostand(kontoNum));
    }

    @Test
    void testGeldEinzahlen() throws KontonummerNichtVorhandenException {
        long kontonummer = b.getAlleKontonummern().get(4);
        b.geldEinzahlen(kontonummer, 500);

        assertEquals(500, b.getKontostand(kontonummer));
    }

    @Test
    void testGeldEinzahlenKontonummerNichtVorhanden() {
        Assertions.assertThrowsExactly(KontonummerNichtVorhandenException.class, () -> {
            b.geldEinzahlen(8521464L, 500);
        });
    }

    @Test
    void testGeldEinzahlenMitBetrag0() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            long kontonummer = b.getAlleKontonummern().get(4);
            b.geldEinzahlen(kontonummer, 0);
        });
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            long kontonummer = b.getAlleKontonummern().get(4);
            b.geldEinzahlen(kontonummer, -5);
        });
    }

    @Test
    void testGeldAbheben(){

    }

    @Test
    void testKontoLoeschen() {
        long kontonummer = b.getAlleKontonummern().get(0);
        b.kontoLoeschen(kontonummer);
        assertFalse(b.getAlleKontonummern().contains(kontonummer));
        System.out.println("------------------------------");
        System.out.println("Konto " + b.getAlleKontonummern().get(0) + " wurde erfolgreich gelöscht");
        System.out.println("------------------------------");
    }

    @Test
    void testKontoLoeschenNichtVorhanden() {
        assertFalse(b.kontoLoeschen(1891561L));
    }

    @Test
    void testKontoNichtVorhanden() {
        Assertions.assertThrowsExactly(KontonummerNichtVorhandenException.class, () -> {
            b.geldEinzahlen(156846L, 33.6);
        });
    }
}
