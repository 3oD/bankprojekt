package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

public class BankMockingTest {
    private Bank bank;
    private Konto sender, empfaenger;
    Girokonto girokonto;
    private Ueberweisungsfaehig ueberweisungsfaehigSender, ueberweisungsfaehigEmpfaenger;
    private Kunde inhaber;

    @BeforeEach
    void setUp() throws KontonummerNichtVorhandenException {
        bank = new Bank(12345678L);
        sender = Mockito.mock(Girokonto.class);
        empfaenger = Mockito.mock(Girokonto.class);
        inhaber = Mockito.mock(Kunde.class);

        Mockito.when(inhaber.getName()).thenReturn("Mustermann");
        Mockito.when(bank.mockEinfuegen(sender)).thenReturn(10000000L);
        Mockito.when(bank.mockEinfuegen(empfaenger)).thenReturn(20000000L);

        long kontonummerSender = bank.mockEinfuegen(sender);
        long kontonummerEmpfaenger = bank.mockEinfuegen(empfaenger);

        Mockito.when(bank.getKontostand(kontonummerSender)).thenReturn(500.0);
        Mockito.when(bank.getKontostand(kontonummerEmpfaenger)).thenReturn(1000.0);

    }

    @Test
    void testMockEinfuegen() {
        long kontonummer1 = bank.mockEinfuegen(sender);
        long kontonummer2 = bank.mockEinfuegen(empfaenger);

        assertEquals(10000000L, kontonummer1);
        assertEquals(20000000L, kontonummer2);
    }

    @Test
    void testGetKontostand() throws KontonummerNichtVorhandenException {
        double kontostand1 = bank.getKontostand(10000000L);
        double kontostand2 = bank.getKontostand(20000000L);

        assertEquals(500.0, kontostand1);
        assertEquals(1000.0, kontostand2);
    }

}
