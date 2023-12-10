package funktionen;


import java.util.List;
import java.util.function.DoubleFunction;
import java.util.stream.Stream;

public class NullstellenFinder {

    private NullstellenFinder() {
        throw new IllegalStateException("Utility Class!");
    }

    public static final double EPSILON = 0.01;

    public static List<Double> findNullstellen(DoubleFunction<Double> funktion, double a, double b) {
        final double[] x = {a};
        final boolean[] positive = {funktion.apply(a) > 0};

        return Stream.iterate(x[0], n -> n < b, n -> n + EPSILON)
                .filter(n -> {
                    boolean nowPositive = funktion.apply(n) > 0;
                    if (nowPositive != positive[0]) {
                        positive[0] = nowPositive; // Update the sign
                        return true;
                    }
                    return false;
                })
                .map(n -> findNullstelle(funktion, n - EPSILON, n))
                .toList();
    }

    private static double findNullstelle(DoubleFunction<Double> funktion, double a, double b) {
        double m = (a + b) / 2.0;
        // if the interval is small enough, return the midpoint
        if (Math.abs(b - a) < EPSILON) {
            return m;
        }
        // else divide the interval at the midpoint and continue
        if (Math.signum(funktion.apply(a)) != Math.signum(funktion.apply(m))) {
            return findNullstelle(funktion, a, m);
        } else {
            return findNullstelle(funktion, m, b);
        }
    }
}
