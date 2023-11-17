package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class BankMockingTest {
    private Bank bank;
    private Girokonto sender, empfaenger;
    private long kontonummerSender, kontonummerEmpfaenger;
    ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<String> stringArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> stringArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Long> longArgumentCaptor1 = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Long> longArgumentCaptor2 = ArgumentCaptor.forClass(Long.class);

    @BeforeEach
    void setUp() {
        bank = new Bank(12345678L);
        sender = mock(Girokonto.class);
        empfaenger = mock(Girokonto.class);
        Kunde senderKunde = mock(Kunde.class);
        Kunde empfaengerKunde = mock(Kunde.class);

        when(senderKunde.getName()).thenReturn("Mustermann");
        when(empfaengerKunde.getName()).thenReturn("Musterfrau");

        when(sender.getInhaber()).thenReturn(senderKunde);
        when(empfaenger.getInhaber()).thenReturn(empfaengerKunde);

        kontonummerSender = bank.mockEinfuegen(sender);
        kontonummerEmpfaenger = bank.mockEinfuegen(empfaenger);

    }

    @Test
    void testGeldUeberweisen() throws GesperrtException {
        double betrag = 200.0;
        String verwendungszweck = "Testzweck";

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);
        when(sender.ueberweisungAbsenden(betrag, empfaenger.getInhaber().getName(), kontonummerEmpfaenger, bank.getBankleitzahl(), verwendungszweck)).thenReturn(true);

        assertTrue(bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(sender, times(1)).ueberweisungAbsenden(doubleArgumentCaptor.capture(), stringArgumentCaptor1.capture(), longArgumentCaptor1.capture(), longArgumentCaptor2.capture(), stringArgumentCaptor2.capture());
        verify(empfaenger, times(1)).ueberweisungEmpfangen(betrag, sender.getInhaber().getName(), kontonummerSender, bank.getBankleitzahl(), verwendungszweck);


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

        when(sender.getKontonummer()).thenReturn(kontonummerSender);
        when(empfaenger.getKontonummer()).thenReturn(kontonummerEmpfaenger);
        // when(sender.isGesperrt()).thenReturn(true);
        // sender muss GesperrtException werfen und nicht prüfen, ob es gesperrt ist

        assertThrowsExactly(GesperrtException.class, () -> bank.geldUeberweisen(sender.getKontonummer(), empfaenger.getKontonummer(), betrag, verwendungszweck));

        verify(sender, times(0)).ueberweisungAbsenden(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
        verify(empfaenger, times(0)).ueberweisungEmpfangen(anyDouble(), anyString(), anyLong(), anyLong(), anyString());
    }

    // TODO Kontolöschen -> Liste aller Kontonummern zurückgeben lassen und prüfen, ob die Nummer nicht mehr vorhanden ist oder mit Konto interagieren
    //

}
