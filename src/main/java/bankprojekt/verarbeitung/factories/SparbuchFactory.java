package bankprojekt.verarbeitung.factories;

import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;
import bankprojekt.verarbeitung.Sparbuch;

public class SparbuchFactory extends KontoFactory {
    @Override
    public Konto createKontoMitInhaber(Kunde inhaber, long nummer) {
        return new Sparbuch(inhaber, nummer);
    }
}
