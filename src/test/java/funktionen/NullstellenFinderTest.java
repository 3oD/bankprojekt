package funktionen;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NullstellenFinderTest {
    @Test
    void testFindeNullstelle() {
        assertEquals(5, Math.abs(NullstellenFinder.suchenNullstelle(x -> x * x - 25, -10d, 10d)), 0.01);
        assertEquals(Math.log(7)/3, NullstellenFinder.suchenNullstelle(x -> Math.pow(Math.E, 3 * x) - 7, 0d, 2d), 0.01);
        assertEquals(Math.sqrt(Math.asin(0.5)), NullstellenFinder.suchenNullstelle(x -> Math.sin(x * x) - 0.5, 0d, 2d), 0.01);
        assertThrows(IllegalArgumentException.class, () -> NullstellenFinder.suchenNullstelle(x -> x * x + 1, 0d, 2d));
    }

    @Test
    void testNullstelle() {
        assertEquals(5, Math.abs(NullstellenFinder.suchenNullstelle((x -> x * x - 25), -10d, 10d)),0.01);
        assertEquals(Math.log(7)/3, NullstellenFinder.suchenNullstelle((x -> Math.exp(3 * x) - 7), 0d, 1d),0.01);
        assertEquals(-1.7320508075688772, NullstellenFinder.suchenNullstelle((x -> Math.sin(x * x) - 0.5), -2d, 2d));

        // Die Funktion k(x) = x² + 1 hat keine Nullstelle im Intervall [-1, 1].
        assertEquals(Double.NaN, NullstellenFinder.suchenNullstelle((x -> x * x + 1), -1d, 1d));
    }

    @Test
    void testenNullstelle() {
        DoubleFunction<Double> f = x -> Math.pow(x, 2) - 25;
        assertEquals(5, Math.abs(NullstellenFinder.suchenNullstelle(f, -10.0,10.0)), 0.01);

        DoubleFunction<Double> g = x -> Math.exp(3*x) - 7;
        assertEquals(0.694, NullstellenFinder.suchenNullstelle(g, 0.0,5.0), 0.01);

        DoubleFunction<Double> h = x -> Math.sin(Math.pow(x, 2)) - 0.5;
        assertEquals(0.824, NullstellenFinder.suchenNullstelle(h, 0.0,2.0), 0.01);

        DoubleFunction<Double> k = x -> Math.pow(x, 2) + 1;
        try {
            NullstellenFinder.suchenNullstelle(k, -5.0, 5.0);
        } catch (IllegalArgumentException e) {
            assertEquals("Die Funktion hat in dem gegebenen Intervall keine Nullstelle.", e.getMessage());
        }
    }
}
