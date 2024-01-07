package bankprojekt.verarbeitung.factories;

import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;

/**
 * A factory class for creating instances of the {@link Konto} class.
 */
public abstract class KontoFactory {

    /**
     * Creates a new Konto with the given owner.
     *
     * @param inhaber the owner of the account
     * @return a new Konto object
     */
    public abstract Konto createKonto(Kunde inhaber, long nummer);
}
