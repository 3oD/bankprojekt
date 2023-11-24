package funktionen;


import java.util.function.DoubleFunction;

public class NullstellenFinder {

    public static Double suchenNullstelle(DoubleFunction<Double> funktion, Double a, Double b) {
        Double m = (a + b) / 2.0;
        if (Math.abs(b - a) < 0.01) {
            return m;
        } else if (Math.signum(funktion.apply(a)) != Math.signum(funktion.apply(m))) {
            return suchenNullstelle(funktion, a, m);
        } else if (Math.signum(funktion.apply(m)) != Math.signum(funktion.apply(b))) {
            return suchenNullstelle(funktion, m, b);
        } else {
            throw new IllegalArgumentException("Die Funktion hat in dem gegebenen Intervall keine Nullstelle.");
        }
    }
}
