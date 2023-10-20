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
    private double zinssatz;

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
        super(inhaber, kontonummer, Waehrung.EUR);
        zinssatz = 0.03;
    }

    @Override
    public String toString() {
        String ausgabe = "-- SPARBUCH --" + System.lineSeparator() +
                super.toString()
                + "Zinssatz: " + this.zinssatz * 100 + "%" + System.lineSeparator();
        return ausgabe;
    }

    @Override
    public boolean abheben(double betrag) throws GesperrtException {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }
        if (this.isGesperrt()) {
            throw new GesperrtException(this.getKontonummer());
        }

        LocalDate heute = LocalDate.now();
        if (heute.getMonth() != zeitpunkt.getMonth() || heute.getYear() != zeitpunkt.getYear()) {
            this.bereitsAbgehoben = 0;
        }

        if (getKontostand() - betrag >= this.getAktuelleWaehrung().euroInWaehrungUmrechnen(Sparbuch.MIN_KONTOSTAND) &&
                bereitsAbgehoben + betrag <= this.getAktuelleWaehrung().euroInWaehrungUmrechnen(Sparbuch.ABHEBESUMME)) {
            setKontostand(getKontostand() - betrag);
            bereitsAbgehoben += betrag;
            this.zeitpunkt = LocalDate.now();
            return true;
        } else
            return false;
    }

    @Override
    public boolean abheben(double betrag, Waehrung waehrung) throws GesperrtException {
        double betragInEUR = waehrung.waehrungInEuroUmrechnen(betrag);
        double betragInKontoWaehrung = getAktuelleWaehrung().euroInWaehrungUmrechnen(betragInEUR);

        return abheben(betragInKontoWaehrung);
    }


    /**
     * Konvertiert den Kontostand und den bereits abgehobenen Betrag in die neue Währung.
     * Ändert die Kontowährung in die neue Währung.
     *
     * @param neu Währung, die festgelegt werden soll
     */
    @Override
    public void waehrungswechsel(Waehrung neu) {
        double kontostandInEUR = getAktuelleWaehrung().waehrungInEuroUmrechnen(getKontostand());
        double bereitsAbgehobenInEUR = getAktuelleWaehrung().waehrungInEuroUmrechnen(bereitsAbgehoben);

        setKontostand(neu.euroInWaehrungUmrechnen(kontostandInEUR));
        bereitsAbgehoben = neu.euroInWaehrungUmrechnen(bereitsAbgehobenInEUR);

        super.waehrungswechsel(neu);
    }

}
