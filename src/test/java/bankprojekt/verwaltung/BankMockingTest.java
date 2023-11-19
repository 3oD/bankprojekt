package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BankMockingTest {
    private Bank bank;
    private Konto sender, empfaenger, kontoNichtUeberweisungsfaehig;
    private long kontonummerSender, kontonummerEmpfaenger, kontonummerEmpfaengerNichtUeberweisungsfaehig;
    ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<String> stringArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> stringArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Long> longArgumentCaptor1 = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Long> longArgumentCaptor2 = ArgumentCaptor.forClass(Long.class);

    @BeforeEach
    void setUp() {
        bank = new Bank(12345678L);
        sender = mock(Konto.class, withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        empfaenger = mock(Konto.class, withSettings().extraInterfaces(Ueberweisungsfaehig.class));
        kontoNichtUeberweisungsfaehig = mock(Konto.class);
        Kunde senderKunde = mock(Kunde.class);
        Kunde empfaengerKunde = mock(Kunde.class);

        when(senderKunde.getName()).thenReturn("Mustermann");
        when(empfaengerKunde.getName()).thenReturn("Musterfrau");

        when(sender.getInhaber()).thenReturn(senderKunde);
        when(empfaenger.getInhaber()).thenReturn(empfaengerKunde);
        when(kontoNichtUeberweisungsfaehig.getInhaber()).thenReturn(empfaengerKunde);

        kontonummerSender = bank.mockEinfuegen(sender);
        kontonummerEmpfaenger = bank.mockEinfuegen(empfaenger);
        kontonummerEmpfaengerNichtUeberweisungsfaehig = bank.mockEinfuegen(kontoNichtUeberweisungsfaehig);

    }

    @Test
    void testGeldUeberweisen() throws GesperrtException {
        double betrag = 200.0;
        String verwendungszweck = "Testzweck";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);
        when(ueberweisungsfaehigSender.ueberweisungAbsenden(betrag, empfaenger.getInhaber().getName(), kontonummerEmpfaenger, bank.getBankleitzahl(), verwendungszweck)).thenReturn(true);

        assertTrue(bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigSender, times(1)).ueberweisungAbsenden(doubleArgumentCaptor.capture(), stringArgumentCaptor1.capture(), longArgumentCaptor1.capture(), longArgumentCaptor2.capture(), stringArgumentCaptor2.capture());
        verify(ueberweisungsfaehigEmpfaenger, times(1)).ueberweisungEmpfangen(betrag, sender.getInhaber().getName(), kontonummerSender, bank.getBankleitzahl(), verwendungszweck);


        assertEquals(betrag, doubleArgumentCaptor.getValue());
        assertEquals(empfaenger.getInhaber().getName(), stringArgumentCaptor1.getValue());
        assertEquals(empfaenger.getKontonummer(), longArgumentCaptor1.getValue());
        assertEquals(bank.getBankleitzahl(), longArgumentCaptor2.getValue());
        assertEquals(verwendungszweck, stringArgumentCaptor2.getValue());
    }

    @Test
    void testGeldUeberweisenAbsendenFalse() throws GesperrtException {
        double betrag = 200.0;
        String verwendungszweck = "Testzweck";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);
        when(ueberweisungsfaehigSender.ueberweisungAbsenden(betrag, empfaenger.getInhaber().getName(), kontonummerEmpfaenger, bank.getBankleitzahl(), verwendungszweck)).thenReturn(false);

        assertFalse(bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigSender, times(1)).ueberweisungAbsenden(doubleArgumentCaptor.capture(), stringArgumentCaptor1.capture(), longArgumentCaptor1.capture(), longArgumentCaptor2.capture(), stringArgumentCaptor2.capture());
        verify(ueberweisungsfaehigEmpfaenger, times(0)).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());


        assertEquals(betrag, doubleArgumentCaptor.getValue());
        assertEquals(empfaenger.getInhaber().getName(), stringArgumentCaptor1.getValue());
        assertEquals(empfaenger.getKontonummer(), longArgumentCaptor1.getValue());
        assertEquals(bank.getBankleitzahl(), longArgumentCaptor2.getValue());
        assertEquals(verwendungszweck, stringArgumentCaptor2.getValue());
    }

    @Test
    void testGeldUeberweisenMitKontoGesperrt() throws GesperrtException {
        double betrag = 200.0;
        String verwendungszweck = "Testzweck";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);
        when(ueberweisungsfaehigSender.ueberweisungAbsenden(betrag, empfaenger.getInhaber().getName(), kontonummerEmpfaenger, bank.getBankleitzahl(), verwendungszweck)).thenThrow(new GesperrtException(kontonummerSender));

        assertFalse(bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigSender, times(1)).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
        verify(ueberweisungsfaehigEmpfaenger, times(0)).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenOhneVerwendungszweck() throws GesperrtException {
        double betrag = 200.0;
        String verwendungszweckLeerString = "";
        String verwendungszweckLeerzeichen = " ";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweckLeerString));
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweckLeerzeichen));

        verify(ueberweisungsfaehigSender, times(0)).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
        verify(ueberweisungsfaehigEmpfaenger, times(0)).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenInvaliderBetrag() throws GesperrtException {
        double betragNegativ = -200.0;
        double betragNaN = Double.NaN;
        double betragInfinite = Double.POSITIVE_INFINITY;
        double betragNull = 0.0;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betragNegativ, verwendungszweck));
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betragNaN, verwendungszweck));
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betragInfinite, verwendungszweck));
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betragNull, verwendungszweck));

        verify(ueberweisungsfaehigSender, times(0)).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
        verify(ueberweisungsfaehigEmpfaenger, times(0)).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenEmpfaengerNichtUeberweisungsfaehig() throws GesperrtException {
        double betrag = 200.0;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(kontoNichtUeberweisungsfaehig.getKontonummer()).thenReturn(kontonummerEmpfaengerNichtUeberweisungsfaehig);

        assertFalse(bank.geldUeberweisen(sender.getKontonummer(), kontoNichtUeberweisungsfaehig.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigSender, times(0)).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenSenderNichtUeberweisungsfaehig() {
        double betrag = 200.0;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);
        when(kontoNichtUeberweisungsfaehig.getKontonummer()).thenReturn(kontonummerEmpfaengerNichtUeberweisungsfaehig);

        assertFalse(bank.geldUeberweisen(kontoNichtUeberweisungsfaehig.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigEmpfaenger, times(0)).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenSenderNichtVorhanden() {
        double betrag = 200.0;
        long kontonummer = 999999L;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);

        assertFalse(bank.geldUeberweisen(kontonummer, empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigEmpfaenger, times(0)).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenEmpfaengerNichtVorhanden() throws GesperrtException {
        double betrag = 200.0;
        long kontonummer = 999999L;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);

        assertFalse(bank.geldUeberweisen(sender.getKontonummer(), kontonummer, betrag, verwendungszweck));

        verify(ueberweisungsfaehigSender, times(0)).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testKontoLoeschen() {
        assertTrue(bank.kontoLoeschen(kontonummerEmpfaenger));
        Assertions.assertThrowsExactly(KontonummerNichtVorhandenException.class, () -> bank.geldAbheben(kontonummerEmpfaenger, 20.0));
        assertFalse(bank.getAlleKontonummern().contains(kontonummerEmpfaenger));
    }

    @Test
    void testKontoLoeschenKontoNichtVorhanden() {
        assertFalse(bank.kontoLoeschen(1564879498L));
    }

}
