package funktionen;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NullstellenFinderTest {

    @Test
    void testFindNullstelle() {
        DoubleFunction<Double> f = x -> Math.pow(x, 2) - 25;
        assertEquals(5, NullstellenFinder.findNullstelle(f, -1.0,10.0), 0.01);

        DoubleFunction<Double> g = x -> Math.exp(3*x) - 7;
        assertEquals(0.648, NullstellenFinder.findNullstelle(g, -0.5,5.0), 0.01);

        DoubleFunction<Double> h = x -> Math.sin(Math.pow(x, 2)) - 0.5;
        assertEquals(-0.723, NullstellenFinder.findNullstelle(h, -1.0,2.0), 0.01);

        DoubleFunction<Double> i = x -> -(Math.pow(x,2)) + 31;
        assertEquals(5.567, NullstellenFinder.findNullstelle(i,-5d,7d),0.01);

        try {
            NullstellenFinder.findNullstelle(g, -28.0, 544.0);
        } catch (IllegalArgumentException e) {
            assertEquals("Die Funktion hat in dem gegebenen Intervall keine Nullstelle.", e.getMessage());
        }
    }

    @Test
    void testFindNullstelleInvalidIntervall() {
        DoubleFunction<Double> f = (double x) -> x + 1;
        assertThrows(IllegalArgumentException.class, () -> NullstellenFinder.findNullstelle(f, Double.NaN, 0.0));
        assertThrows(IllegalArgumentException.class, () -> NullstellenFinder.findNullstelle(f, 0.0, Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> NullstellenFinder.findNullstelle(f, Double.POSITIVE_INFINITY, 0.0));
        assertThrows(IllegalArgumentException.class, () -> NullstellenFinder.findNullstelle(f, 0.0, Double.POSITIVE_INFINITY));
    }
}
