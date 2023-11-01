package bankprojekt.verarbeitung;

public class KontonummerNichtVorhandenException  extends Exception{
    public KontonummerNichtVorhandenException(long kontonummer)
    {
        super("Die Kontonummer: " + kontonummer + " existiert nicht!");
    }
}
