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
    void testWerbetext(){
        assertEquals("ganz hoher Dispo", Kontoart.GIROKONTO.getWerbetext());
        assertEquals("ganz vioele Zinsen", Kontoart.SPARBUCH.getWerbetext());
        assertEquals("kommt später...", Kontoart.FESTGELDKONTO.getWerbetext());
    }

    @Test
    public void testWaehrungswechsel() throws GesperrtException {
        girokonto.waehrungswechsel(Waehrung.BGN);
        assertEquals(1955.8, girokonto.getKontostand(),0.01);
        assertEquals(1955.8,girokonto.getDispo(),0.01);
        girokonto.waehrungswechsel(Waehrung.EUR);
        assertEquals(1000, girokonto.getKontostand(),0.01);
        assertEquals(1000, girokonto.getDispo(),0.01);

        sparbuch.abheben(200);
        double betragNachWechsel = Waehrung.DKK.euroInWaehrungUmrechnen(200);
        sparbuch.waehrungswechsel(Waehrung.DKK);
        assertEquals(5968.3, sparbuch.getKontostand(),0.02);
        assertEquals(betragNachWechsel, sparbuch.getBereitsAbgehoben());
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
        assertTrue(girokonto.abheben(200, Waehrung.DKK));
        assertFalse(girokonto.abheben(5000000, Waehrung.MKD));

        assertTrue(sparbuch.abheben(13, Waehrung.DKK));
        assertFalse(sparbuch.abheben(2001, Waehrung.BGN));
    }

    @Test
    public void testAbhebenMitFremdwaehrung() throws GesperrtException {
        girokonto.waehrungswechsel(Waehrung.DKK);
        assertTrue(girokonto.abheben(200, Waehrung.DKK));
        assertTrue(girokonto.abheben(20,Waehrung.EUR));
        assertTrue(girokonto.abheben(20,Waehrung.BGN));
        assertFalse(girokonto.abheben(5000000, Waehrung.MKD));

        sparbuch.waehrungswechsel(Waehrung.DKK);
        assertTrue(sparbuch.abheben(13, Waehrung.DKK));
        assertTrue(sparbuch.abheben(20,Waehrung.EUR));
        assertTrue(sparbuch.abheben(20,Waehrung.BGN));
        assertFalse(sparbuch.abheben(2001, Waehrung.BGN));
    }

    @Test
    public void testAbhebenGirokontoZuViel() throws GesperrtException {
        assertFalse(girokonto.abheben(5000000, Waehrung.DKK));
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
