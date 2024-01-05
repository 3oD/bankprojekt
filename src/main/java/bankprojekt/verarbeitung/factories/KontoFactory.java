package bankprojekt.verarbeitung.factories;

import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;

public abstract class KontoFactory {
    /**
     * Creates a new Konto with the given owner.
     *
     * @param inhaber the owner of the Konto
     * @return a new Konto object
     */
    public abstract Konto createKontoMitInhaber(Kunde inhaber,long nummer);
}
