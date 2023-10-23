package bankprojekt.verarbeitung;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class KontoTest {
    Girokonto girokonto;
    Sparbuch sparbuch;
    Kunde kunde;
    Waehrung waehrungMKD = Waehrung.MKD;
    Waehrung waehrungEUR = Waehrung.EUR;

    @BeforeEach
    void setup() {
        kunde = new Kunde("Sebastian", "Gey", "hier", LocalDate.parse("1996-09-15"));
        girokonto = new Girokonto(kunde, 1234, 1000.0);
        sparbuch = new Sparbuch(kunde, 123456);

        girokonto.einzahlen(1000);
        sparbuch.einzahlen(1000);
    }

    @Test
    public void testWaehrungswechsel() {
        girokonto.waehrungswechsel(Waehrung.BGN);
        assertEquals(1955.79, girokonto.getKontostand());
        sparbuch.waehrungswechsel(Waehrung.DKK);
        assertEquals(7460.39, sparbuch.getKontostand());
    }

    @Test
    public void testEuroInWaehrungUmrechnen() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            waehrungEUR.euroInWaehrungUmrechnen(-156);
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            waehrungEUR.euroInWaehrungUmrechnen(Double.POSITIVE_INFINITY);
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            waehrungEUR.euroInWaehrungUmrechnen(Double.NaN);
        });
    }

    @Test
    public void testWaehrungInEuroUmrechnen() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            waehrungEUR.waehrungInEuroUmrechnen(-156);
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            waehrungEUR.waehrungInEuroUmrechnen(Double.POSITIVE_INFINITY);
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            waehrungEUR.waehrungInEuroUmrechnen(Double.NaN);
        });
    }

    @Test
    public void testAbheben() throws GesperrtException {
        assertTrue(girokonto.abheben(200));
        assertFalse(girokonto.abheben(5000));

        assertTrue(sparbuch.abheben(13));
        assertFalse(sparbuch.abheben(2001));
    }

    @Test
    public void testAbhebenGirokontoZuViel() throws GesperrtException {
        assertFalse(girokonto.abheben(5000));
    }

    @Test
    public void testAbhebenSparbuchZuViel() throws GesperrtException {
        assertFalse(sparbuch.abheben(2051));
    }

    @Test
    public void testAbhebenSparbuchBetragUngueltig() throws GesperrtException {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            assertFalse(sparbuch.abheben(Double.NaN));
        });
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            assertFalse(sparbuch.abheben(Double.POSITIVE_INFINITY));
        });
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            assertFalse(sparbuch.abheben(-189));
        });
    }

    @Test
    public void testAbhebenSparbuchGesperrt() {
        sparbuch.sperren();
        Assertions.assertThrowsExactly(GesperrtException.class, () -> {
           sparbuch.abheben(15);
        });
    }

    @Test
    public void testAbhebenSparbuchInAndererWaehrung() throws GesperrtException {
        sparbuch.waehrungswechsel(waehrungMKD);
        assertFalse(sparbuch.abheben(88136513, waehrungEUR));
        assertTrue(sparbuch.abheben(12, waehrungEUR));
    }

    @Test
    public void testAbhebenInFremdwaehrung() throws GesperrtException {
        assertTrue(girokonto.abheben(200, waehrungMKD));
        assertTrue(sparbuch.abheben(15,waehrungMKD));
    }
}
