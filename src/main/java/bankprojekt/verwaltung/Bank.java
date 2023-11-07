package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;

import java.util.*;

/**
 * The Bank class represents a bank with a given bank code (Bankleitzahl). It provides methods for creating accounts,
 * performing transactions, and retrieving account information.
 */
public class Bank {
    private final long bankleitzahl;
    private final Map<Long, Konto> kontoMap = new HashMap<>();
    private static long kontonummerZaehler = 10000000L;
    private static final double STANDARD_DISPO = 1000;

    /**
     * Represents a bank with a specific bank code.
     */
    public Bank(long bankleitzahl) {
        this.bankleitzahl = bankleitzahl;
    }

    /**
     * Retrieves the bank code of the bank.
     *
     * @return the bank code (bankleitzahl)
     */
    public long getBankleitzahl() {
        return bankleitzahl;
    }

    /**
     * Generates a unique account number.
     *
     * @return the generated unique account number
     */
    private synchronized long generiereEindeutigeKontonummer() {
        long kontonummer = kontonummerZaehler++;

        while (kontoMap.containsKey(kontonummer)) {
            kontonummerZaehler++;
        }
        return kontonummer;
    }

    /**
     * Adds a new bank account to the bank's account map.
     *
     * @param neuesKonto the new bank account to be added
     * @return the account number of the newly created bank account
     */
    private long kontoErstellen(Konto neuesKonto) {
        kontoMap.put(neuesKonto.getKontonummer(), neuesKonto);
        return neuesKonto.getKontonummer();
    }

    /**
     * Creates a new girokonto (checking account) for the specified owner.
     *
     * @param inhaber the owner of the girokonto
     * @return the account number of the newly created girokonto
     */
    public long girokontoErstellen(Kunde inhaber) {
        return kontoErstellen(new Girokonto(inhaber, generiereEindeutigeKontonummer(), STANDARD_DISPO));
    }

    /**
     * Creates a new savings account for the specified owner.
     *
     * @param inhaber the owner of the savings account
     * @return the account number of the newly created savings account
     */
    public long sparbuchErstellen(Kunde inhaber) {
        return kontoErstellen(new Sparbuch(inhaber, generiereEindeutigeKontonummer()));
    }

    /**
     * Retrieves a string representation of all bank accounts in the bank.
     *
     * @return a string with the information of all bank accounts
     */
    public String getAlleKonten() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("------------------------------");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Liste aller Konten:");
        stringBuilder.append(System.lineSeparator());

        for (Map.Entry<Long, Konto> entry : kontoMap.entrySet()) {
            Konto konto = entry.getValue();

            stringBuilder.append(formatKontoInformationen(konto));
            stringBuilder.append(System.lineSeparator());
        }
        stringBuilder.append("------------------------------");
        return stringBuilder.toString();
    }

    /**
     * Formats the given account information into a string.
     *
     * @param konto the account whose information should be formatted
     * @return the formatted account information
     */
    private String formatKontoInformationen(Konto konto) {
        return String.format("Kontonummer: %s, Kontostand: %s %s",
                konto.getKontonummer(),
                konto.getKontostand(),
                konto.getAktuelleWaehrung());
    }

    /**
     * Retrieves all account numbers in the bank.
     *
     * @return a list of all account numbers in the bank
     */
    public List<Long> getAlleKontonummern() {
        return new LinkedList<>(kontoMap.keySet());
    }

    /**
     * Validates a bank account with the given account number.
     *
     * @param nummer the account number to be validated
     * @throws KontonummerNichtVorhandenException if the account number does not exist
     * @throws IllegalArgumentException           if the account number is invalid
     */
    private void validiereKonto(long nummer) throws KontonummerNichtVorhandenException {
        if (!kontoMap.containsKey(nummer)) {
            throw new KontonummerNichtVorhandenException(nummer);
        }
    }

    /**
     * Validates the given amount.
     *
     * @param betrag the amount to be validated
     * @throws IllegalArgumentException if the amount is less than or equal to 0, NaN, or infinite
     */
    private void validiereBetrag(double betrag) {

        if (betrag <= 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Der Betrag muss größer als 0 sein.");
        }
    }

    /**
     * Withdraws a specified amount of money from the account with the given account number.
     *
     * @param nummer the account number to withdraw from
     * @param betrag the amount of money to withdraw
     * @return true if the withdrawal was successful, false otherwise
     * @throws KontonummerNichtVorhandenException if the account number does not exist
     * @throws GesperrtException                  if the account is locked
     */
    public boolean geldAbheben(long nummer, double betrag) throws KontonummerNichtVorhandenException, GesperrtException {
        validiereKonto(nummer);
        validiereBetrag(betrag);
        return kontoMap.get(nummer).abheben(betrag);
    }

    /**
     * Deposits the specified amount of money into the account with the given account number.
     *
     * @param auf    the account number to deposit into
     * @param betrag the amount of money to deposit
     * @throws KontonummerNichtVorhandenException if the account number does not exist
     */
    public void geldEinzahlen(long auf, double betrag) throws KontonummerNichtVorhandenException {
        validiereKonto(auf);
        validiereBetrag(betrag);
        kontoMap.get(auf).einzahlen(betrag);
    }

    /**
     * Deletes a bank account with the given account number.
     *
     * @param nummer the account number of the bank account to be deleted
     * @return true if the bank account was successfully deleted, false otherwise
     */
    public boolean kontoLoeschen(long nummer) {
        if (kontoMap.containsKey(nummer)) {
            kontoMap.remove(nummer);
            return true;
        } else
            return false;
    }

    /**
     * Retrieves the current account balance for the specified account number.
     *
     * @param nummer the account number for which to retrieve the balance
     * @return the current account balance
     * @throws KontonummerNichtVorhandenException if the account number does not exist
     */
    public double getKontostand(long nummer) throws KontonummerNichtVorhandenException {
        if (!kontoMap.containsKey(nummer)) {
            throw new KontonummerNichtVorhandenException(nummer);
        }
        return kontoMap.get(nummer).getKontostand();
    }

    /**
     * Transfers specified amount of money from one account to another.
     *
     * @param vonKontonr       the account number to transfer money from
     * @param nachKontonr      the account number to transfer money to
     * @param betrag           the amount of money to transfer
     * @param verwendungszweck the purpose of the transfer
     * @return true if the transfer was successful, false otherwise
     */
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck) {
        Konto sender = kontoMap.get(vonKontonr);
        Konto empfaenger = kontoMap.get(nachKontonr);

        if (sender == null || empfaenger == null) {
            return false;
        }

        if (verwendungszweck.isBlank()) {
            throw new IllegalArgumentException("Bitte geben Sie einen Verwendungszweck an!");
        }

        if (sender instanceof Ueberweisungsfaehig ueberweisungsfaehigSender &&
                empfaenger instanceof Ueberweisungsfaehig ueberweisungsfaehigEmpfaenger) {

            boolean ueberweisungErfolgt = sendeUeberweisung(ueberweisungsfaehigSender,
                    betrag, empfaenger.getInhaber().getName(),
                    empfaenger.getKontonummer(), getBankleitzahl(), verwendungszweck);
            if (ueberweisungErfolgt) {
                empfangeUeberweisung(ueberweisungsfaehigEmpfaenger,
                        betrag, sender.getInhaber().getName(),
                        sender.getKontonummer(), getBankleitzahl(), verwendungszweck);
                return true;
            }

            return false;
        }
        return false;
    }

    /**
     * Sends a money transfer from the sender's account to the specified recipient.
     *
     * @param sender           the account from which to send the money transfer
     * @param betrag           the amount of money to transfer
     * @param empfaenger       the recipient of the money transfer
     * @param nachKontonr      the recipient's account number
     * @param nachBlz          the bank code of the recipient's bank
     * @param verwendungszweck the purpose of the money transfer
     * @return true if the money transfer was successfully sent, false otherwise
     */
    private boolean sendeUeberweisung(Ueberweisungsfaehig sender, double betrag, String empfaenger,
                                      long nachKontonr, long nachBlz, String verwendungszweck) {
        try {
            return sender.ueberweisungAbsenden(betrag, empfaenger, nachKontonr, nachBlz, verwendungszweck);
        } catch (GesperrtException e) {
            return false;
        }
    }

    /**
     * Receives a money transfer for the specified receiver.
     *
     * @param empfaenger       the receiver of the money transfer
     * @param betrag           the amount of money transferred
     * @param vonName          the name of the sender
     * @param vonKontonr       the account number of the sender
     * @param vonBlz           the bank code of the sender's bank
     * @param verwendungszweck the purpose of the money transfer
     */
    private void empfangeUeberweisung(Ueberweisungsfaehig empfaenger, double betrag, String vonName,
                                      long vonKontonr, long vonBlz, String verwendungszweck) {
        empfaenger.ueberweisungEmpfangen(betrag, vonName, vonKontonr, vonBlz, verwendungszweck);
    }
}
