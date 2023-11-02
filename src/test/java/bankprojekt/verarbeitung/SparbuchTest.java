package bankprojekt.verarbeitung;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SparbuchTest {
    Sparbuch sparbuch1, sparbuch2;
    Kunde kunde;

    @BeforeEach
    void setup() {
        kunde = new Kunde("Sebastian", "Gey", "hier", LocalDate.parse("1996-09-15"));

        sparbuch1 = new Sparbuch();
        sparbuch2 = new Sparbuch(kunde, 65165161L);
    }

    @Test
    void testToString() {
        String expectedString = "-- SPARBUCH --" + System.lineSeparator() + "Kontonummer:    1234567"
                + System.lineSeparator() + "Inhaber: Max Mustermann" + System.lineSeparator() + "zuhause"
                + System.lineSeparator() + "02.11.23" + System.lineSeparator() + "Aktueller Kontostand:       0,00 EUR "
                + System.lineSeparator() + "Zinssatz: 3.0%" + System.lineSeparator();

        assertEquals(expectedString, sparbuch1.toString());
    }
}
