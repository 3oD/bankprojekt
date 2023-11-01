package bankprojekt.verwaltung;

/**
 * Signals that a given account number does not exist.
 */
public class KontonummerNichtVorhandenException  extends Exception{
    /**
     * Signals that a given account number does not exist.
     * @param kontonummer account number that does not exist
     */
    public KontonummerNichtVorhandenException(long kontonummer)
    {
        super("Die Kontonummer: " + kontonummer + " existiert nicht!");
    }
}
