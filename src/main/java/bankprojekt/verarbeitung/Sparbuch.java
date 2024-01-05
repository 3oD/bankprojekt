package bankprojekt.verarbeitung;

import java.time.LocalDate;

/**
 * ein Sparbuch, d.h. ein Konto, das nur recht eingeschränkt genutzt
 * werden kann. Insbesondere darf man monatlich nur höchstens 2000€
 * abheben, wobei der Kontostand nie unter 0,50€ fallen darf.
 *
 * @author Doro
 */
public class Sparbuch extends Konto {
    /**
     * Zinssatz, mit dem das Sparbuch verzinst wird. 0,03 entspricht 3%
     */
    private final double zinssatz;

    /**
     * Monatlich erlaubter Gesamtbetrag für Abhebungen
     */
    public static final double ABHEBESUMME = 2000;

    /**
     * Minimaler Kontostand
     */
    public static final double MIN_KONTOSTAND = 0.50;

    /**
     * Betrag, der im aktuellen Monat bereits abgehoben wurde
     */
    private double bereitsAbgehoben = 0;

    /**
     * Monat und Jahr der letzten Abhebung
     */
    private LocalDate zeitpunkt = LocalDate.now();

    /**
     * ein Standard-Sparbuch
     */
    public Sparbuch() {
        super();
        zinssatz = 0.03;
    }

    /**
     * ein Standard-Sparbuch, das inhaber gehört und die angegebene Kontonummer hat
     *
     * @param inhaber     der Kontoinhaber
     * @param kontonummer die Wunsch-Kontonummer
     * @throws IllegalArgumentException wenn inhaber null ist
     */
    public Sparbuch(Kunde inhaber, long kontonummer) {
        super(inhaber, kontonummer);
        zinssatz = 0.03;
    }

    /**
     * Gibt den Geldbetrag zurück, der bereits von dem Konto abgehoben wurde.
     *
     * @return der Geldbetrag, der bereits abgehoben wurde
     */
    public double getBereitsAbgehoben() {
        return bereitsAbgehoben;
    }

    @Override
    public String toString() {
        return "-- SPARBUCH --" + System.lineSeparator() +
                super.toString()
                + "Zinssatz: " + this.zinssatz * 100 + "%" + System.lineSeparator();
    }

    // TODO Änderung von bereitsAbgehoben in eine andere Methode auslagern
    @Override
    protected boolean pruefeAbheben(double betrag) {
        LocalDate heute = LocalDate.now();
        if (heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear()) {
            this.bereitsAbgehoben = 0;
        }

        return getKontostand() - betrag >= this.getAktuelleWaehrung().euroInWaehrungUmrechnen(Sparbuch.MIN_KONTOSTAND) &&
                bereitsAbgehoben + betrag <= this.getAktuelleWaehrung().euroInWaehrungUmrechnen(Sparbuch.ABHEBESUMME);
    }

    @Override
    protected  void kontoAenderung(double betrag){
        LocalDate heute = LocalDate.now();
        if (heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear()) {
            this.bereitsAbgehoben = 0;
        }

        this.bereitsAbgehoben += betrag;
        this.zeitpunkt = heute;
    }
    /**
     * Konvertiert den Kontostand und den bereits abgehobenen Betrag in die neue Währung.
     * Ändert die Kontowährung in die neue Währung.
     *
     * @param neu Währung, die festgelegt werden soll
     */
    @Override
    public void waehrungswechsel(Waehrung neu) {
        double bereitsAbgehobenInEUR = getAktuelleWaehrung().waehrungInEuroUmrechnen(bereitsAbgehoben);
        bereitsAbgehoben = neu.euroInWaehrungUmrechnen(bereitsAbgehobenInEUR);

        super.waehrungswechsel(neu);
    }

    @Override
    public boolean equals(Object other){
        return super.equals(other);
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

}
