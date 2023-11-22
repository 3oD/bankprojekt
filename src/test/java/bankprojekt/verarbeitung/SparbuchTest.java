package bankprojekt.verarbeitung;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SparbuchTest {
    Sparbuch sparbuch1, sparbuch2;
    Kunde kunde;

    @BeforeEach
    void setup() throws GesperrtException {
        kunde = new Kunde("Sebastian", "Gey", "hier", LocalDate.parse("1996-09-15"));

        sparbuch1 = new Sparbuch();
        sparbuch2 = new Sparbuch(kunde, 65165161L);
    }

    @Test
    void testToString() {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        String expectedString = "-- SPARBUCH --" + System.lineSeparator() + "Kontonummer:    1234567"
                + System.lineSeparator() + "Inhaber: Max Mustermann" + System.lineSeparator() + "zuhause"
                + System.lineSeparator() + df.format(LocalDate.now()) + System.lineSeparator() + "Aktueller Kontostand: "
                + String.format("%10.2f %s", sparbuch1.getKontostand(), sparbuch1.getAktuelleWaehrung()) + " "
                + System.lineSeparator() + "Zinssatz: 3.0%" + System.lineSeparator();

        assertEquals(expectedString, sparbuch1.toString());
    }
}
