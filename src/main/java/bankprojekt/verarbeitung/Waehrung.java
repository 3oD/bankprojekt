package bankprojekt.verarbeitung;


import org.decimal4j.util.DoubleRounder;

import java.math.RoundingMode;

/**
 * The Waehrung class represents a currency with its exchange rate to Euros.
 * It provides methods to convert amounts between Euros and the currency defined by the instance of the class.
 */
public enum Waehrung {
    /**
     * Euro
     */
    EUR(1),
    /**
     * Bulgarische Lew
     */
    BGN(1.9558),
    /**
     * Dänische Krone
     */
    DKK(7.4604),
    /**
     * Mazedonischer Denar
     */
    MKD(61.62);

    private final double euroKurs;

    /**
     * Creates a new currency with the specified exchange rate to Euros.
     *
     * @param euroKurs the exchange rate of the currency to Euros
     */
    Waehrung(double euroKurs) {
        this.euroKurs = euroKurs;
    }

    /**
     * Converts the given amount in Euros to the currency defined by the instance of the class.
     *
     * @param betrag the amount in Euros to be converted to the currency
     * @return the converted amount in the currency
     * @throws IllegalArgumentException if the amount is negative or not a finite value
     */
    public double euroInWaehrungUmrechnen(double betrag) throws IllegalArgumentException {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }

        return DoubleRounder.round(betrag * this.euroKurs, 2, RoundingMode.DOWN);
    }

    /**
     * Converts the given amount in the currency defined by the instance of the class to Euros.
     *
     * @param betrag the amount in the currency to be converted to Euros
     * @return the converted amount in Euros
     * @throws IllegalArgumentException if the amount is negative or not a finite value
     */
    public double waehrungInEuroUmrechnen(double betrag) throws IllegalArgumentException {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }
        return DoubleRounder.round(betrag / this.euroKurs, 2, RoundingMode.DOWN);
    }
}
