package funktionen;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.DoubleFunction;

import static funktionen.NullstellenFinder.findNullstellen;
import static org.junit.jupiter.api.Assertions.*;

class NullstellenFinderTest {
    @Test
    void findNullstellenFx() {
        DoubleFunction<Double> f = x -> x * x - 25;   // Roots at x=-5 and x=5
        List<Double> nullstellen = findNullstellen(f, -10, 10);

        double epsilon = 0.01;  // Tolerance for root comparison

        boolean rootNegativeFiveExists = nullstellen.stream()
                .anyMatch(root -> Math.abs(root + 5) < epsilon);
        boolean rootFiveExists = nullstellen.stream()
                .anyMatch(root -> Math.abs(root - 5) < epsilon);

        assertTrue(rootNegativeFiveExists, "Root -5 is not found");
        assertTrue(rootFiveExists, "Root 5 is not found");
        assertEquals(2, nullstellen.size());
    }

    @Test
    void findNullstellenGx() {
        DoubleFunction<Double> g = x -> Math.pow(Math.E, 3 * x) - 7;  // The exact roots cannot be expressed in a finite form
        // Scanning from -2 to 1 should yield a single root
        List<Double> nullstellen = findNullstellen(g, -2, 1);
        assertEquals(1, nullstellen.size(), "Finding root failed for g(x)");
    }

    @Test
    void findNullstellenHx() {
        DoubleFunction<Double> h = x -> Math.sin(x * x) - 0.5;   // Many roots
        List<Double> nullstellen = findNullstellen(h, -4, 4);
        assertTrue(nullstellen.size() > 2, "Finding roots failed for h(x)");
    }

    @Test
    void findNullstellenKx() {
        DoubleFunction<Double> k = x -> x * x + 1;    // Non-real roots
        List<Double> nullstellen = findNullstellen(k, -2, 2);
        assertTrue(nullstellen.isEmpty(), "Finding non-existent roots failed for k(x)");
    }
}
