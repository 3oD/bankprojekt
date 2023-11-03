package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Kunde;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {
    Bank b1, b2;
    Kunde kunde1, kunde2;
    long kontoNummer1, kontoNummer2;

    @BeforeEach
    void setup() throws KontonummerNichtVorhandenException {
        b1 = new Bank(12312L);
        b2 = new Bank(145351L);

        kunde1 = new Kunde("Max", "Mustermann", "Home", LocalDate.parse("2001-10-29"));
        kunde2 = new Kunde("John", "Doe", "Work", LocalDate.parse("1985-07-12"));

        kontoNummer1 = b1.girokontoErstellen(kunde1);
        kontoNummer2 = b1.girokontoErstellen(kunde2);

        b1.geldEinzahlen(kontoNummer1, 500);
    }

    @Test
    void testGetBankleitzahl() {
        assertEquals(12312L, b1.getBankleitzahl());
    }

    // TODO: Eigener Test für getAlleKonten
    // TODO: Eigener Test für getAlleKontonummern

    @Test
    void testGeldEinzahlen() throws KontonummerNichtVorhandenException {
        b1.geldEinzahlen(kontoNummer1, 500);

        assertEquals(1000, b1.getKontostand(kontoNummer1));
    }

    @Test
    void testGeldEinzahlenKontonummerNichtVorhanden() {
        Assertions.assertThrowsExactly(KontonummerNichtVorhandenException.class, () -> b1.geldEinzahlen(8521464L, 1000));
    }

    @Test
    void testGeldEinzahlenUngueltigerBetrag() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> b1.geldEinzahlen(kontoNummer2, 0));
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> b1.geldEinzahlen(kontoNummer1, -5));
    }

    @Test
    void testGeldAbheben() throws GesperrtException, KontonummerNichtVorhandenException {
        b1.geldAbheben(kontoNummer1, 100);
        assertEquals(400, b1.getKontostand(kontoNummer1));
    }

    @Test
    void testKontoLoeschen() {
        long kontonummer = b1.getAlleKontonummern().get(0);
        b1.kontoLoeschen(kontonummer);
        assertFalse(b1.getAlleKontonummern().contains(kontonummer));
        System.out.println("------------------------------");
        System.out.println("Konto " + b1.getAlleKontonummern().get(0) + " wurde erfolgreich gelöscht");
        System.out.println("------------------------------");
    }

    @Test
    void testKontoLoeschenNichtVorhanden() {
        assertFalse(b1.kontoLoeschen(1891561L));
    }

    @Test
    void testKontoNichtVorhanden() {
        Assertions.assertThrowsExactly(KontonummerNichtVorhandenException.class, () -> b1.geldEinzahlen(156846L, 33.6));
    }

    @Test
    void testGeldueberweisen() {
        b1.geldUeberweisen(kontoNummer1, kontoNummer2, 100, "Test");
        assertEquals(100, b1.getKontostand(kontoNummer2));
        assertEquals(400, b1.getKontostand(kontoNummer1));
    }
}
