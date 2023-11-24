package bankprojekt.verwaltung;

/**
 * Signals that a given account number does not exist.
 */
public class KontonummerDoesNotExistException extends Exception{
    /**
     * Signals that a given account number does not exist.
     * @param kontonummer account number that does not exist
     */
    public KontonummerDoesNotExistException(long kontonummer)
    {
        super("Die Kontonummer: " + kontonummer + " existiert nicht!");
    }
}
