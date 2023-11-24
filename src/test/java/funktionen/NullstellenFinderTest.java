package funktionen;

import org.junit.jupiter.api.Test;

import java.util.function.DoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NullstellenFinderTest {

    @Test
    void testenNullstelle() {
        DoubleFunction<Double> f = x -> Math.pow(x, 2) - 25;
        assertEquals(5, NullstellenFinder.suchenNullstelle(f, 0.0,10.0), 0.01);

        DoubleFunction<Double> g = x -> Math.exp(3*x) - 7;
        assertEquals(0.648, NullstellenFinder.suchenNullstelle(g, 0.0,5.0), 0.01);

        DoubleFunction<Double> h = x -> Math.sin(Math.pow(x, 2)) - 0.5;
        assertEquals(0.723, NullstellenFinder.suchenNullstelle(h, 0.0,2.0), 0.01);

        DoubleFunction<Double> i = x -> -(Math.pow(x,2)) + 31;
        assertEquals(5.567, NullstellenFinder.suchenNullstelle(i,0d,7d),0.01);

        DoubleFunction<Double> k = x -> Math.pow(x, 2) + 1;
        try {
            NullstellenFinder.suchenNullstelle(k, -5.0, 5.0);
        } catch (IllegalArgumentException e) {
            assertEquals("Die Funktion hat in dem gegebenen Intervall keine Nullstelle.", e.getMessage());
        }
    }
}
