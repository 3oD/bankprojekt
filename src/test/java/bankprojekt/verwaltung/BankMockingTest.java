package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;
import bankprojekt.verarbeitung.factories.GirokontoFactory;
import bankprojekt.verarbeitung.factories.KontoFactory;
import bankprojekt.verarbeitung.factories.MockFactory;
import bankprojekt.verarbeitung.factories.SparbuchFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

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
        when(senderKunde.getVorname()).thenReturn("Max");
        when(senderKunde.getAdresse()).thenReturn("Musterstrasse 1");
        when(empfaengerKunde.getName()).thenReturn("Musterfrau");
        when(empfaengerKunde.getVorname()).thenReturn("Maria");
        when(empfaengerKunde.getAdresse()).thenReturn("Musterstrasse 2");

        when(sender.getInhaber()).thenReturn(senderKunde);
        when(empfaenger.getInhaber()).thenReturn(empfaengerKunde);
        when(kontoNichtUeberweisungsfaehig.getInhaber()).thenReturn(empfaengerKunde);

        kontonummerSender = bank.kontoErstellen(new MockFactory(sender), senderKunde);
        kontonummerEmpfaenger = bank.kontoErstellen(new MockFactory(empfaenger), empfaengerKunde);
        kontonummerEmpfaengerNichtUeberweisungsfaehig = bank.kontoErstellen(new MockFactory(kontoNichtUeberweisungsfaehig), empfaengerKunde);
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

        verify(ueberweisungsfaehigSender, times(1)).ueberweisungAbsenden(betrag, "Musterfrau", empfaenger.getKontonummer(), bank.getBankleitzahl(), verwendungszweck);
        verify(ueberweisungsfaehigEmpfaenger, times(1)).ueberweisungEmpfangen(betrag, sender.getInhaber().getName(), kontonummerSender, bank.getBankleitzahl(), verwendungszweck);
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
        verify(ueberweisungsfaehigEmpfaenger, never()).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());


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
        verify(ueberweisungsfaehigEmpfaenger, never()).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
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

        verify(ueberweisungsfaehigSender, never()).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
        verify(ueberweisungsfaehigEmpfaenger, never()).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
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

        verify(ueberweisungsfaehigSender, never()).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
        verify(ueberweisungsfaehigEmpfaenger, never()).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenEmpfaengerNichtUeberweisungsfaehig() throws GesperrtException {
        double betrag = 200.0;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(kontoNichtUeberweisungsfaehig.getKontonummer()).thenReturn(kontonummerEmpfaengerNichtUeberweisungsfaehig);

        assertFalse(bank.geldUeberweisen(sender.getKontonummer(), kontoNichtUeberweisungsfaehig.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigSender, never()).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenSenderNichtUeberweisungsfaehig() {
        double betrag = 200.0;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);
        when(kontoNichtUeberweisungsfaehig.getKontonummer()).thenReturn(kontonummerEmpfaengerNichtUeberweisungsfaehig);

        assertFalse(bank.geldUeberweisen(kontoNichtUeberweisungsfaehig.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigEmpfaenger, never()).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenSenderNichtVorhanden() {
        double betrag = 200.0;
        long kontonummer = 999999L;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger = (Ueberweisungsfaehig) empfaenger;

        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);

        assertFalse(bank.geldUeberweisen(kontonummer, empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(ueberweisungsfaehigEmpfaenger, never()).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testGeldUeberweisenEmpfaengerNichtVorhanden() throws GesperrtException {
        double betrag = 200.0;
        long kontonummer = 999999L;
        String verwendungszweck = "Testzwecke";
        Ueberweisungsfaehig ueberweisungsfaehigSender = (Ueberweisungsfaehig) sender;

        when(sender.getKontonummer()).thenReturn(kontonummerSender);

        assertFalse(bank.geldUeberweisen(sender.getKontonummer(), kontonummer, betrag, verwendungszweck));

        verify(ueberweisungsfaehigSender, never()).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    @Test
    void testKontoLoeschen() {
        assertTrue(bank.kontoLoeschen(kontonummerEmpfaenger));
        Assertions.assertThrowsExactly(KontonummerDoesNotExistException.class, () -> bank.geldAbheben(kontonummerEmpfaenger, 20.0));
        assertFalse(bank.getAlleKontonummern().contains(kontonummerEmpfaenger));
    }

    @Test
    @DisplayName("Test checks if an account that does not exist can be deleted from the bank.")
    void testKontoLoeschenKontoNichtVorhanden() {
        assertFalse(bank.kontoLoeschen(1564879498L));
    }

    @Test
    @DisplayName("Test checks if the account does not gets locked if the balance is below 0")
    void testPleitegeierSperren() {
        when(sender.getKontostand()).thenReturn(-1000d);

        bank.pleitegeierSperren();

        verify(sender).sperren();
    }

    @Test
    @DisplayName("Test checks if the account does not get locked if the balance is over 0")
    void testPleitegeierSperrenKontostandUeberNull() {
        when(sender.getKontostand()).thenReturn(1000d);

        bank.pleitegeierSperren();

        verify(sender, never()).sperren();
    }

    @Test
    @DisplayName("Test checks if the account does not get locked if the balance is 0")
    void testPleitegeierSperrenKontostandGenauNull() {
        when(sender.getKontostand()).thenReturn(1000d);

        bank.pleitegeierSperren();

        verify(sender, never()).sperren();
    }

    /**
     * Test checks if unused account numbers are correctly identified.
     */
    @Test
    @DisplayName("Test checks if unused account numbers are correctly identified")
    void testGetKontonummernLuecken() throws KontonummerDoesNotExistException {
        List<Long> accountedNums = bank.getKontonummernLuecken();
        assertEquals(0, accountedNums.size(), "Expect initial unused account list to be empty");

        bank.kontoLoeschen(10000000L); // remove account
        accountedNums = bank.getKontonummernLuecken();
        assertEquals(1, accountedNums.size(), "Expect unused account list to have 1 number after an account is deleted");
        assertEquals(10000000L, accountedNums.get(0), "Expect 10000000L to be the unused account number");
    }

    @Test
    void testGetKundenadressenUnterschiedlicheAdresse() {
        Kunde kunde1 = mock(Kunde.class);
        Kunde kunde2 = mock(Kunde.class);
        Kunde kunde3 = mock(Kunde.class);

        when(sender.getInhaber()).thenReturn(kunde1);
        when(empfaenger.getInhaber()).thenReturn(kunde2);
        when(kontoNichtUeberweisungsfaehig.getInhaber()).thenReturn(kunde3);

        when(kunde1.getAdresse()).thenReturn("Musterstrasse 1");
        when(kunde2.getAdresse()).thenReturn("Musterstrasse 2");
        when(kunde3.getAdresse()).thenReturn("Musterstrasse 3");

        when(kunde1.getVorname()).thenReturn("Max");
        when(kunde1.getName()).thenReturn("Mustermann, Max");
        when(kunde2.getVorname()).thenReturn("Maria");
        when(kunde2.getName()).thenReturn("Musterfrau, Maria");
        when(kunde3.getVorname()).thenReturn("John");
        when(kunde3.getName()).thenReturn("Doe, John");

        String actual = bank.getKundenadressen();
        String expected = "Doe, John: Musterstrasse 3" + System.lineSeparator() + "Musterfrau, Maria: Musterstrasse 2" + System.lineSeparator() + "Mustermann, Max: Musterstrasse 1";

        assertEquals(expected, actual);
    }

    @Test
    void testGetKundenadressenGleicheAdresse() {
        Kunde kunde1 = mock(Kunde.class);

        when(sender.getInhaber()).thenReturn(kunde1);
        when(empfaenger.getInhaber()).thenReturn(kunde1);
        when(kontoNichtUeberweisungsfaehig.getInhaber()).thenReturn(kunde1);

        when(kunde1.getAdresse()).thenReturn("Musterstrasse 1");

        when(kunde1.getVorname()).thenReturn("Max");
        when(kunde1.getName()).thenReturn("Mustermann, Max");

        String actual = bank.getKundenadressen();
        String expected = "Mustermann, Max: Musterstrasse 1";

        assertEquals(expected, actual);
    }

    /**
     * Test to check whether `getKundenMitVollemKonto` method returns correct result.
     */
    @Test
    void testGetKundenMitVollemKonto() {
        Kunde kunde1 = mock(Kunde.class);
        Kunde kunde2 = mock(Kunde.class);
        Konto konto1 = mock(Konto.class);
        Konto konto2 = mock(Konto.class);

        when(konto1.getInhaber()).thenReturn(kunde1);
        when(konto2.getInhaber()).thenReturn(kunde2);
        when(konto1.getKontostand()).thenReturn(2000.0);
        when(konto2.getKontostand()).thenReturn(500.0);

        bank.kontoErstellen(new MockFactory(konto1), kunde1);
        bank.kontoErstellen(new MockFactory(konto2), kunde2);

        List<Kunde> result = bank.getKundenMitVollemKonto(1500);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(result.get(0), kunde1);
    }

    @Test
    void testGetAlleReichenKunden() {
        Kunde mockCustomer1 = mock(Kunde.class);
        Kunde mockCustomer2 = mock(Kunde.class);
        Kunde mockCustomer3 = mock(Kunde.class);

        Konto mockKonto1 = mock(Konto.class);
        Konto mockKonto2 = mock(Konto.class);
        Konto mockKonto3 = mock(Konto.class);

        when(mockKonto1.getInhaber()).thenReturn(mockCustomer1);
        when(mockKonto2.getInhaber()).thenReturn(mockCustomer2);
        when(mockKonto3.getInhaber()).thenReturn(mockCustomer3);

        when(mockKonto1.getKontostand()).thenReturn(1500.0);
        when(mockKonto2.getKontostand()).thenReturn(2500.0);
        when(mockKonto3.getKontostand()).thenReturn(3500.0);

        bank.kontoErstellen(new MockFactory(mockKonto1), mockCustomer1);
        bank.kontoErstellen(new MockFactory(mockKonto2), mockCustomer2);
        bank.kontoErstellen(new MockFactory(mockKonto3), mockCustomer3);

        List<Kunde> reicheKunden = bank.getAlleReichenKunden(2000);

        assertEquals(2, reicheKunden.size());

        assertFalse(reicheKunden.contains(mockCustomer1));
        assertTrue(reicheKunden.contains(mockCustomer2));
        assertTrue(reicheKunden.contains(mockCustomer3));
    }

    @Test
    void testGetAlleReichenKundenExtremfaelle() {
        assertThrows(IllegalArgumentException.class, () -> bank.getAlleReichenKunden(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> bank.getAlleReichenKunden(Double.NaN));
    }
}
