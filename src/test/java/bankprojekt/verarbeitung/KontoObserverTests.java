package bankprojekt.verarbeitung;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KontoObserverTests {
    PropertyChangeListener mockListener;
    Konto konto;

    @BeforeEach
    public void setup() {
        konto = new Konto() {
            @Override
            protected boolean pruefeAbheben(double betrag) {
                return true;
            }
        };
        konto.einzahlen(500);
        konto.anmelden(event -> {
            switch (event.getPropertyName()) {
                case "kontostand":
                    System.out.println("Account balance changed: " + event.getNewValue());
                    break;
                case "waehrung":
                    System.out.println("The Currency has been changed " + event.getNewValue());
                    break;
                case "gesperrt":
                    System.out.println("The account has been suspended ");
                    break;
                case "entsperren":
                    System.out.println("The account has been unsuspended ");
                    break;
                default:
                    System.out.println("An unknown property was changed.");
                    break;
            }
        });
        this.mockListener = mock(PropertyChangeListener.class);
        konto.anmelden(this.mockListener);

        System.out.println("#############################################");
    }

    @AfterAll
    public static void teardown() {
        System.out.println("#############################################");
    }

    @Test
    void testEinzahlen() {
        System.out.println("Testing deposit:");
        konto.einzahlen(100);
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    void testAbheben() throws GesperrtException {
        System.out.println("Testing withdraw");
        konto.abheben(100);
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    void waehrungWechsel() {
        System.out.println("Testing currency change");
        ArgumentCaptor<PropertyChangeEvent> eventCaptor = ArgumentCaptor.forClass(PropertyChangeEvent.class);

        konto.waehrungswechsel(Waehrung.DKK);
        verify(mockListener, times(2)).propertyChange(eventCaptor.capture());
        List<PropertyChangeEvent> actualEvents = eventCaptor.getAllValues();

        Optional<PropertyChangeEvent> waehrungEvent = actualEvents.stream()
                .filter(e -> "waehrung".equals(e.getPropertyName()))
                .findFirst();

        assertTrue(waehrungEvent.isPresent());
        assertEquals(Waehrung.DKK, waehrungEvent.get().getNewValue());
    }

    @Test
    void kontoSperren() {
        System.out.println("Testing account suspension");
        konto.sperren();
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

    @Test
    void kontoEntsperren() {
        System.out.println("Testing account unsuspension");
        konto.entsperren();
        verify(mockListener, times(1)).propertyChange(any(PropertyChangeEvent.class));
    }

}
