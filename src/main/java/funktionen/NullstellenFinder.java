package funktionen;


import java.util.function.DoubleFunction;

public class NullstellenFinder {

    private NullstellenFinder(){
        throw new IllegalStateException("Utility Class!");
    }

    public static Double findNullstelle(DoubleFunction<Double> funktion, Double a, Double b) {
        if (a.isInfinite()||a.isNaN()||b.isInfinite()||b.isNaN()){
            throw new IllegalArgumentException("Invalid input: a or b is infinite or NaN");
        }
        if (funktion.apply(a)*funktion.apply(b)<0) {
            Double m = (a + b) / 2.0;
            if (Math.abs(b - a) < 0.01) {
                return m;
            } else if (Math.signum(funktion.apply(a)) != Math.signum(funktion.apply(m))) {
                return findNullstelle(funktion, a, m);
            } else if (Math.signum(funktion.apply(m)) != Math.signum(funktion.apply(b))) {
                return findNullstelle(funktion, m, b);
            } else {
                throw new IllegalArgumentException("Die Funktion hat in dem gegebenen Intervall keine Nullstelle.");
            }
        }
        else {
            throw new IllegalArgumentException("Ungueltige Eingabe: a und b haben das gleiche Vorzeichen");
        }
    }
}
