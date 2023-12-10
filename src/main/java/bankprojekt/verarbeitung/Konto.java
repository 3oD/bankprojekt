package bankprojekt.verarbeitung;

import com.google.common.primitives.Doubles;

import java.util.Map;
import java.util.concurrent.*;

/**
 * stellt ein allgemeines Bank-Konto dar
 */
public abstract class Konto implements Comparable<Konto> {
    /**
     * der Kontoinhaber
     */
    private Kunde inhaber;

    /**
     * die Kontonummer
     */
    private final long nummer;

    /**
     * der aktuelle Kontostand
     */
    private double kontostand;

    /**
     * die aktuelle Währung
     */
    private Waehrung waehrung = Waehrung.EUR;

    /**
     * Wenn das Konto gesperrt ist (gesperrt = true), können keine Aktionen daran mehr vorgenommen werden,
     * die zum Schaden des Kontoinhabers wären (abheben, Inhaberwechsel)
     */
    private boolean gesperrt;

    /**
     * Depot is a private ConcurrentHashMap that represents the stock portfolio of an account.
     * The keys in the map are Aktie objects, which represent individual stocks, and the values are
     * integers representing the quantity of each stock in the portfolio.
     */
    private final ConcurrentHashMap<Aktie, Integer> depot = new ConcurrentHashMap<>();

    /**
     * An ExecutorService that manages a fixed thread pool of size 10.
     *
     * @see Executors#newFixedThreadPool(int)
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    /**
     * Constructs a new Konto object with the specified owner and account number.
     *
     * @param inhaber The owner of the account
     * @param kontonummer The account number
     */
    Konto(Kunde inhaber, long kontonummer) {
        setInhaber(inhaber);
        this.nummer = kontonummer;
        this.kontostand = 0;
        this.gesperrt = false;
    }

    /**
     * setzt alle Eigenschaften des Kontos auf Standardwerte
     */
    Konto() {
        this(Kunde.MUSTERMANN, 1234567);
    }

    /**
     * liefert den Kontoinhaber zurück
     *
     * @return der Inhaber
     */
    public final Kunde getInhaber() {
        return this.inhaber;
    }

    /**
     * Sets the owner of the account.
     *
     * @param kinh the owner of the account
     * @throws IllegalArgumentException if the owner is null
     */
    public final void setInhaber(Kunde kinh) {
        if (kinh == null)
            throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
        this.inhaber = kinh;

    }

    /**
     * liefert den aktuellen Kontostand
     *
     * @return Kontostand
     */
    public final double getKontostand() {
        return kontostand;
    }

    /**
     * setzt den aktuellen Kontostand
     *
     * @param kontostand neuer Kontostand
     */
    protected void setKontostand(double kontostand) {
        this.kontostand = kontostand;
    }

    /**
     * liefert die Kontonummer zurück
     *
     * @return Kontonummer
     */
    public final long getKontonummer() {
        return nummer;
    }

    /**
     * liefert zurück, ob das Konto gesperrt ist oder nicht
     *
     * @return true, wenn das Konto gesperrt ist
     */
    public final boolean isGesperrt() {
        return gesperrt;
    }

    /**
     * Erhöht den Kontostand um den eingezahlten Betrag.
     *
     * @param betrag double
     * @throws IllegalArgumentException wenn der betrag negativ ist
     */
    public void einzahlen(double betrag) {
        if (betrag < 0 || !Doubles.isFinite(betrag)) {
            throw new IllegalArgumentException("Falscher Betrag");
        }
        setKontostand(getKontostand() + betrag);
    }

    @Override
    public String toString() {
        String ausgabe;
        ausgabe = "Kontonummer: " + this.getKontonummerFormatiert()
                + System.lineSeparator();
        ausgabe += "Inhaber: " + this.inhaber;
        ausgabe += "Aktueller Kontostand: " + getKontostandFormatiert() + " ";
        ausgabe += this.getGesperrtText() + System.lineSeparator();
        return ausgabe;
    }

    /**
     * Mit dieser Methode wird der geforderte Betrag vom Konto abgehoben, wenn es nicht gesperrt ist
     * und die speziellen Abheberegeln des jeweiligen Kontotyps die Abhebung erlauben
     *
     * @param betrag double
     * @return true, wenn die Abhebung geklappt hat,
     * false, wenn sie abgelehnt wurde
     * @throws GesperrtException        wenn das Konto gesperrt ist
     * @throws IllegalArgumentException wenn der betrag negativ oder unendlich oder NaN ist
     */
    public abstract boolean abheben(double betrag)
            throws GesperrtException;

    /**
     * sperrt das Konto, Aktionen zum Schaden des Benutzers sind nicht mehr möglich.
     */
    public final void sperren() {
        this.gesperrt = true;
    }

    /**
     * entsperrt das Konto, alle Kontoaktionen sind wieder möglich.
     */
    public final void entsperren() {
        this.gesperrt = false;
    }


    /**
     * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
     *
     * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
     */
    public final String getGesperrtText() {
        if (this.gesperrt) {
            return "GESPERRT";
        } else {
            return "";
        }
    }

    /**
     * liefert die ordentlich formatierte Kontonummer
     *
     * @return auf 10 Stellen formatierte Kontonummer
     */
    public String getKontonummerFormatiert() {
        return String.format("%10d", this.nummer);
    }

    /**
     * liefert den ordentlich formatierten Kontostand
     *
     * @return formatierter Kontostand mit 2 Nachkommastellen und Währungssymbol
     */
    public String getKontostandFormatiert() {
        return String.format("%10.2f %s", this.getKontostand(), waehrung);
    }

    /**
     * Vergleich von this mit other; Zwei Konten gelten als gleich,
     * wenn sie die gleiche Kontonummer haben
     *
     * @param other das Vergleichskonto
     * @return true, wenn beide Konten die gleiche Nummer haben
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        return this.nummer == ((Konto) other).nummer;
    }

    @Override
    public int hashCode() {
        return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
    }

    @Override
    public int compareTo(Konto other) {
        return Long.compare(this.getKontonummer(), other.getKontonummer());
    }

    /**
     * Diese Methode wird verwendet, um einen bestimmten Betrag in einer bestimmten Währung auf das Konto einzuzahlen.
     *
     * @param betrag   den zu hinterlegenden Betrag
     * @param waehrung die Währung, in der der Betrag angegeben ist
     * @throws IllegalArgumentException wenn der Betrag negativ oder kein endlicher Wert ist
     */
    public void einzahlen(double betrag, Waehrung waehrung) {
        double betragInEUR = waehrung.waehrungInEuroUmrechnen(betrag);
        double betragInKontoWaehrung = getAktuelleWaehrung().euroInWaehrungUmrechnen(betragInEUR);

        einzahlen(betragInKontoWaehrung);
    }

    /**
     * Diese Methode wird verwendet, um einen bestimmten Betrag in einer bestimmten Währung vom Konto abzuheben.
     *
     * @param betrag   der abzuhebende Betrag
     * @param waehrung die Währung, in der der Betrag angegeben ist
     * @return true, wenn die Rücknahme erfolgreich war, sonst false
     * @throws GesperrtException        wenn das Konto gesperrt ist
     * @throws IllegalArgumentException wenn der Betrag negativ oder unendlich oder NaN ist
     */
    public boolean abheben(double betrag, Waehrung waehrung) throws GesperrtException {
        double betragInEUR = waehrung.waehrungInEuroUmrechnen(betrag);
        double betragInKontoWaehrung = getAktuelleWaehrung().euroInWaehrungUmrechnen(betragInEUR);

        return abheben(betragInKontoWaehrung);
    }

    /**
     * Ruft die aktuelle Währung ab, die mit diesem Konto verbunden ist.
     *
     * @return die aktuelle Währung des Kontos
     */
    public Waehrung getAktuelleWaehrung() {
        return waehrung;
    }

    /**
     * Ändert die mit dem Konto verbundene Währung.
     *
     * @param neu die neue Währung, die mit dem Konto verknüpft werden soll
     */
    public void waehrungswechsel(Waehrung neu) {
        double kontostandInEUR = getAktuelleWaehrung().waehrungInEuroUmrechnen(getKontostand());
        setKontostand(neu.euroInWaehrungUmrechnen(kontostandInEUR));

        this.waehrung = neu;
    }

    /**
     * Executes a buy order for the specified stock at the maximum price.
     *
     * @param aktie      the stock to buy
     * @param anzahl     the number of stocks to buy
     * @param maxPreis   the maximum price to buy the stocks at
     * @return a Future representing the cost of the buy order, or 0 if the account balance is insufficient
     */
    public Future<Double> kaufauftrag(Aktie aktie, int anzahl, double maxPreis) {
        return executorService.submit(() -> {
            double kosten;
            synchronized (this) {
                double kurs = aktie.getKurs();
                while (kurs > maxPreis) {
                    aktie.awaitKursChange();
                    kurs = aktie.getKurs();
                }
                kosten = kurs * anzahl;
                if (kontostand >= kosten) {
                    kontostand -= kosten;
                    depot.put(aktie, depot.getOrDefault(aktie, 0) + anzahl);
                    System.out.println("Kaufauftrag für " + aktie.getName() + " (" + aktie.getWertpapierNr() + ") ausgeführt."  + " gekauft bei: " + aktie.getKurs());
                } else {
                    kosten = 0;
                }
            }
            return kosten;
        });
    }

    /**
     * Executes a selling order for stocks with a given security number and minimum price.
     *
     * @param wertpapierNr The security number of the stocks to sell.
     * @param minimalpreis The minimum price required for selling the stocks.
     * @return A future representing the total profit from the selling order.
     */
    public Future<Double> verkaufauftrag(String wertpapierNr, double minimalpreis) {
        return executorService.submit(() -> {
            double gesamtErtrag = 0;
            synchronized (this) {
                for (Map.Entry<Aktie, Integer> entry : depot.entrySet()) {
                    Aktie aktie = entry.getKey();
                    Integer anzahl = entry.getValue();

                    if (aktie.getWertpapierNr().equals(wertpapierNr) && anzahl > 0) {
                        double aktienKurs = aktie.getKurs();

                        while (aktienKurs < minimalpreis) {
                            aktie.awaitKursChange();
                            aktienKurs = aktie.getKurs();
                        }
                            double ertrag = aktienKurs * anzahl;
                            kontostand += ertrag;
                            depot.remove(aktie);
                            gesamtErtrag += ertrag;
                            System.out.println("Verkaufsauftrag für " + aktie.getName() + " (" + aktie.getWertpapierNr() + ") wurde ausgeführt. Kontostand: " + kontostand);
                    }
                }
            }
            return gesamtErtrag;
        });
    }
}
