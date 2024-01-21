package bankprojekt.verarbeitung;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;

class KontoTest {
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
    void testSetInhaber() {
        Kunde expectedKunde = new Kunde("Max", "Mustermann", "dort", LocalDate.parse("1990-01-01"));
        girokonto.setInhaber(expectedKunde);

        assertEquals(expectedKunde, girokonto.getInhaber());
    }

    @Test
    void testSetInhaberNull() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> girokonto.setInhaber(null));
    }

    @Test
    void testSetKontostand() {
        PropertyChangeListener mockListener = Mockito.mock(PropertyChangeListener.class);
        girokonto.anmelden(mockListener);

        double oldKontostand = girokonto.getKontostand();
        double einzahlenSumme = 200.0;
        girokonto.einzahlen(einzahlenSumme);

        ArgumentCaptor<PropertyChangeEvent> argument = ArgumentCaptor.forClass(PropertyChangeEvent.class);
        verify(mockListener).propertyChange(argument.capture());

        assertEquals("kontostand", argument.getValue().getPropertyName());
        assertEquals(oldKontostand, argument.getValue().getOldValue());
        assertEquals(1200d, argument.getValue().getNewValue());
    }

    @Test
    void testWerbetext() {
        assertEquals("ganz hoher Dispo", Kontoart.GIROKONTO.getWerbetext());
        assertEquals("ganz vioele Zinsen", Kontoart.SPARBUCH.getWerbetext());
        assertEquals("kommt später...", Kontoart.FESTGELDKONTO.getWerbetext());
    }

    @Test
    void testWaehrungswechsel() throws GesperrtException {
        double expectedKontostand = girokonto.getKontostand();
        double expectedDispo = girokonto.getDispo();
        System.out.println(expectedKontostand);

        girokonto.waehrungswechsel(Waehrung.BGN);
        assertEquals(expectedKontostand * 1.9558, girokonto.getKontostand(), 0.01);
        assertEquals(expectedDispo * 1.9558, girokonto.getDispo(), 0.01);
        girokonto.waehrungswechsel(Waehrung.MKD);
        assertEquals(expectedKontostand * 61.62, girokonto.getKontostand(), 0.7);
        assertEquals(expectedDispo * 61.62, girokonto.getDispo(), 0.7);

        sparbuch.abheben(200);
        double betragNachWechsel = Waehrung.DKK.euroInWaehrungUmrechnen(200);
        sparbuch.waehrungswechsel(Waehrung.DKK);
        assertEquals(5968.3, sparbuch.getKontostand(), 0.02);
        assertEquals(betragNachWechsel, sparbuch.getBereitsAbgehoben());
    }

    @Test
    void testEuroInWaehrungUmrechnen() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> waehrungEUR.euroInWaehrungUmrechnen(-156));

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> waehrungEUR.euroInWaehrungUmrechnen(Double.POSITIVE_INFINITY));

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> waehrungEUR.euroInWaehrungUmrechnen(Double.NaN));
    }

    @Test
    void testWaehrungInEuroUmrechnen() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> waehrungEUR.waehrungInEuroUmrechnen(-156));

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> waehrungEUR.waehrungInEuroUmrechnen(Double.POSITIVE_INFINITY));

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> waehrungEUR.waehrungInEuroUmrechnen(Double.NaN));
    }

    @Test
    void testAbheben() throws GesperrtException {
        assertTrue(girokonto.abheben(200, Waehrung.DKK));
        assertFalse(girokonto.abheben(5000000, Waehrung.MKD));

        assertTrue(sparbuch.abheben(13, Waehrung.DKK));
        assertFalse(sparbuch.abheben(2001, Waehrung.BGN));
    }

    @Test
    void testAbhebenMitFremdwaehrung() throws GesperrtException {
        girokonto.waehrungswechsel(Waehrung.DKK);
        assertTrue(girokonto.abheben(200, Waehrung.DKK));
        assertTrue(girokonto.abheben(20, Waehrung.EUR));
        assertTrue(girokonto.abheben(20, Waehrung.BGN));
        assertFalse(girokonto.abheben(5000000, Waehrung.MKD));

        sparbuch.waehrungswechsel(Waehrung.DKK);
        assertTrue(sparbuch.abheben(13, Waehrung.DKK));
        assertTrue(sparbuch.abheben(20, Waehrung.EUR));
        assertTrue(sparbuch.abheben(20, Waehrung.BGN));
        assertFalse(sparbuch.abheben(2001, Waehrung.BGN));
    }

    @Test
    void testAbhebenGirokontoZuViel() throws GesperrtException {
        assertFalse(girokonto.abheben(5000000, Waehrung.DKK));
    }

    @Test
    void testAbhebenSparbuchZuViel() throws GesperrtException {
        assertFalse(sparbuch.abheben(2051));
    }

    @Test
    void testAbhebenSparbuchBetragUngueltig() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> assertFalse(sparbuch.abheben(Double.NaN)));
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> assertFalse(sparbuch.abheben(Double.POSITIVE_INFINITY)));
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> assertFalse(sparbuch.abheben(-189)));
    }

    @Test
    void testAbhebenSparbuchGesperrt() {
        sparbuch.sperren();
        Assertions.assertThrowsExactly(GesperrtException.class, () -> sparbuch.abheben(15));
    }

    @Test
    void testAbhebenSparbuchInAndererWaehrung() throws GesperrtException {
        sparbuch.waehrungswechsel(waehrungMKD);
        assertFalse(sparbuch.abheben(88136513, waehrungEUR));
        assertTrue(sparbuch.abheben(12, waehrungEUR));
    }

    @Test
    void testAbhebenInFremdwaehrung() throws GesperrtException {
        assertTrue(girokonto.abheben(200, waehrungMKD));
        assertTrue(sparbuch.abheben(15, waehrungMKD));
    }

    /**
     * Test of sperren method, of class Konto.
     * Method: sperren()
     */
    @Test
    public void testSperren() {
        Konto instance = new Girokonto();
        Assertions.assertFalse(instance.isGesperrt());

        instance.sperren();

        Assertions.assertTrue(instance.isGesperrt());
    }

    @Test
    public void testEntsperren() {
        Kunde kunde = new Kunde("Max", "Mustermann", "zuhause", LocalDate.parse("1990-01-01"));

        Konto konto = new Sparbuch(kunde, 123456L);

        konto.sperren();
        assertTrue(konto.isGesperrt());

        konto.entsperren();

        assertFalse(konto.isGesperrt());
    }
}
