package bankprojekt.verarbeitung;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GiroTest {
    Girokonto girokonto1, girokonto2;
    Kunde kunde;

    @BeforeEach
    void setup() {
        kunde = new Kunde("Sebastian", "Gey", "hier", LocalDate.parse("1996-09-15"));

        girokonto1 = new Girokonto();
        girokonto2 = new Girokonto(kunde, 65165161L, 1000);
    }

    @Test
    void testConstructorUngueltigerDispo() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            Girokonto girokonto = new Girokonto(kunde, 6546546L, -156);
        });
    }

    @Test
    void testSetDispo() {
        girokonto1.setDispo(200);
        assertEquals(200, girokonto1.getDispo());
    }
}
