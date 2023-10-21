package bankprojekt.verarbeitung;

/**
 * Ein Girokonto, d.h. ein Konto mit einem Dispo und der Fähigkeit,
 * Überweisungen zu senden und zu empfangen.
 * Grundsätzlich sind Überweisungen und Abhebungen möglich bis
 * zu einem Kontostand von -this.dispo
 *
 * @author Doro
 */
public class Girokonto extends Konto implements Ueberweisungsfaehig {
    /**
     * Wert, bis zu dem das Konto überzogen werden darf
     */
    private double dispo;

    /**
     * erzeugt ein leeres, nicht gesperrtes Standard-Girokonto
     * von Max MUSTERMANN
     */
    public Girokonto() {
        super(Kunde.MUSTERMANN, 99887766);
        this.dispo = 500;
    }

    /**
     * erzeugt ein Girokonto mit den angegebenen Werten
     *
     * @param inhaber Kontoinhaber
     * @param nummer  Kontonummer
     * @param dispo   Dispo
     * @throws IllegalArgumentException wenn der inhaber null ist oder der angegebene dispo negativ bzw. NaN ist
     */
    public Girokonto(Kunde inhaber, long nummer, double dispo) {
        super(inhaber, nummer);
        if (dispo < 0 || Double.isNaN(dispo) || Double.isInfinite(dispo))
            throw new IllegalArgumentException("Der Dispo ist nicht gültig!");
        this.dispo = dispo;
    }

    /**
     * liefert den Dispo
     *
     * @return Dispo von this
     */
    public double getDispo() {
        return dispo;
    }

    /**
     * setzt den Dispo neu
     *
     * @param dispo muss größer sein als 0
     * @throws IllegalArgumentException wenn dispo negativ bzw. NaN ist
     */
    public void setDispo(double dispo) {
        if (dispo < 0 || Double.isNaN(dispo) || Double.isInfinite(dispo))
            throw new IllegalArgumentException("Der Dispo ist nicht gültig!");
        this.dispo = dispo;
    }

    @Override
    public boolean ueberweisungAbsenden(double betrag,
                                        String empfaenger, long nachKontonr,
                                        long nachBlz, String verwendungszweck)
            throws GesperrtException {
        if (this.isGesperrt())
            throw new GesperrtException(this.getKontonummer());
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag) || empfaenger == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        if (getKontostand() - betrag >= -dispo) {
            setKontostand(getKontostand() - betrag);
            return true;
        } else
            return false;
    }

    @Override
    public void ueberweisungEmpfangen(double betrag, String vonName, long vonKontonr, long vonBlz, String verwendungszweck) {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag) || vonName == null || verwendungszweck == null)
            throw new IllegalArgumentException("Parameter fehlerhaft");
        setKontostand(getKontostand() + betrag);
    }

    @Override
    public String toString() {
        String ausgabe = "-- GIROKONTO --" + System.lineSeparator() +
                super.toString()
                + "Dispo: " + this.dispo + " " + getAktuelleWaehrung() + System.lineSeparator();
        return ausgabe;
    }

    @Override
    public boolean abheben(double betrag) throws GesperrtException {
        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }
        if (this.isGesperrt())
            throw new GesperrtException(this.getKontonummer());
        if (getKontostand() - betrag >= -dispo) {
            setKontostand(getKontostand() - betrag);
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

    @Override
    public void waehrungswechsel(Waehrung neu) {
        /** rechne Kontostand in Euro um */
        double kontostandInEUR = getAktuelleWaehrung().waehrungInEuroUmrechnen(getKontostand());
        double dispoInEUR = getAktuelleWaehrung().waehrungInEuroUmrechnen(getDispo());

        /** rechne den umgerechneten Kontostand in die gewünschte neue Währung um und setze als Kontostand/Dispo */
        setKontostand(neu.euroInWaehrungUmrechnen(kontostandInEUR));
        setDispo(neu.euroInWaehrungUmrechnen(dispoInEUR));

        /** setze Währung auf neue um */
        super.waehrungswechsel(neu);
    }

}
