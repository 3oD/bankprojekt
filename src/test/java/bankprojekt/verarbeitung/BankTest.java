package bankprojekt.verarbeitung;

import bankprojekt.verwaltung.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {
    Bank b = new Bank(12312L);
    Kunde[] kundenArray = {
            new Kunde("Max","Mustermann","Home", LocalDate.parse("2001-10-29")),
            new Kunde("John","Doe","Work", LocalDate.parse("1985-07-12")),
            new Kunde("Jane","Doe","Home", LocalDate.parse("1986-08-13")),
            new Kunde("Alice","Johnson","School", LocalDate.parse("1999-11-14")),
            new Kunde("Bob","Smith","Home", LocalDate.parse("1978-04-25")),
            new Kunde("Charlie","Brown","Work", LocalDate.parse("1990-01-01")),
            new Kunde("Lucy","Van Pelt","Home", LocalDate.parse("1990-02-02")),
            new Kunde("Linus","Van Pelt","Work", LocalDate.parse("1990-03-03")),
            new Kunde("Peppermint","Patty","Home", LocalDate.parse("1990-04-04")),
            new Kunde("Snoopy","Dog","Work", LocalDate.parse("1990-05-05"))
    };
    Girokonto girokonto = new Girokonto(kundenArray[0],6789L,1000 );
    Sparbuch sparbuch = new Sparbuch(kundenArray[1],4567L);

    @BeforeEach
    void setup(){
        for (Kunde kunde : kundenArray) {
            b.girokontoErstellen(kunde);
            b.sparbuchErstellen(kunde);
        }
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

        b.geldEinzahlen(kontoNum,100.0);
        System.out.println("Geld einzahlen: " + b.getKontostand(kontoNum));
        b.geldAbheben(kontoNum,99.0);
        System.out.println("Geld abheben: " + b.getKontostand(kontoNum));
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
}
