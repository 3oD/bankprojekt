package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;

import java.security.SecureRandom;
import java.util.*;

/**
 * The Bank class represents a bank.
 */
public class Bank {
    private final long bankleitzahl;
    private final Map<Long, Konto> kontoMap = new HashMap<>();
    private final SecureRandom zahlengenerator = new SecureRandom();
    private static final double STANDARD_DISPO = 1000;

    /**
     * Represents a bank with a given bank code (Bankleitzahl).
     * @param bankleitzahl bank code to be given
     */
    public Bank(long bankleitzahl) {
        this.bankleitzahl = bankleitzahl;
    }

    /**
     * Retrieves the bank code associated with this bank.
     *
     * @return the bank code
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
        long kontonummer;
        kontonummer = Math.abs(10000000L + zahlengenerator.nextLong(90000000));

        if (kontoMap.containsKey(kontonummer)) {
            generiereEindeutigeKontonummer();
        }
        return kontonummer;
    }

    /**
     * Adds a new account to the bank and returns the account number of the newly created account.
     *
     * @param neuesKonto the new account to be added to the bank
     * @return the account number of the newly created account
     */
    private long kontoErstellen(Konto neuesKonto) {
        kontoMap.put(neuesKonto.getKontonummer(), neuesKonto);
        return neuesKonto.getKontonummer();
    }

    /**
     * Creates a new Girokonto account for the specified owner.
     *
     * @param inhaber the owner of the Girokonto account
     * @return the account number of the newly created Girokonto account
     */
    public long girokontoErstellen(Kunde inhaber) {
        return kontoErstellen(new Girokonto(inhaber, generiereEindeutigeKontonummer(), STANDARD_DISPO));
    }

    /**
     * Creates a new Sparbuch account for the specified owner.
     *
     * @param inhaber the owner of the Sparbuch account
     * @return the account number of the newly created Sparbuch account
     */
    public long sparbuchErstellen(Kunde inhaber) {
        return kontoErstellen(new Sparbuch(inhaber, generiereEindeutigeKontonummer()));
    }

    /**
     * Retrieves a formatted string containing a list of all bank accounts.
     *
     * @return a formatted string with a list of all bank accounts
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
     * Formats the given Konto object's information into a readable string.
     *
     * @param konto the Konto object to format the information for
     * @return the formatted Konto information
     */
    private String formatKontoInformationen(Konto konto) {
        return String.format("Kontonummer: %s, Kontostand: %s %s",
                konto.getKontonummer(),
                konto.getKontostand(),
                konto.getAktuelleWaehrung());
    }

    /**
     * Retrieves a list of all account numbers in the bank.
     *
     * @return a list of all account numbers
     */
    public List<Long> getAlleKontonummern() {
        return new LinkedList<>(kontoMap.keySet());
    }

    /**
     * Validates the given account number and amount.
     *
     * @param nummer the account number to validate
     * @param betrag the amount to validate
     * @throws KontonummerNichtVorhandenException if the account number does not exist
     * @throws IllegalArgumentException if the amount is less than or equal to zero
     */
    private void validiereKontoUndBetrag(long nummer, double betrag) throws KontonummerNichtVorhandenException {
        if (!kontoMap.containsKey(nummer)) {
            throw new KontonummerNichtVorhandenException(nummer);
        }
        if (betrag <= 0) {
            throw new IllegalArgumentException("Der Betrag muss größer als 0 sein.");
        }
    }

    /**
     * Withdraws the specified amount of money from the account with the given account number.
     *
     * @param nummer the account number to withdraw from
     * @param betrag the amount of money to withdraw
     * @return true if the withdrawal was successful, false otherwise
     * @throws KontonummerNichtVorhandenException if the account number does not exist
     * @throws GesperrtException if the account is locked and the withdrawal is not allowed
     */
    public boolean geldAbheben(long nummer, double betrag) throws KontonummerNichtVorhandenException, GesperrtException {
        validiereKontoUndBetrag(nummer, betrag);
        return kontoMap.get(nummer).abheben(betrag);
    }

    /**
     * Deposits a specified amount of money into the account with the given account number.
     *
     * @param auf    the account number to deposit into
     * @param betrag the amount of money to deposit
     * @throws KontonummerNichtVorhandenException if the account number does not exist
     */
    public void geldEinzahlen(long auf, double betrag) throws KontonummerNichtVorhandenException {
        validiereKontoUndBetrag(auf, betrag);
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
     * @param nummer the account number to retrieve the balance for
     * @return the current account balance
     */
    public double getKontostand(long nummer) {
        return kontoMap.get(nummer).getKontostand();
    }

    /**
     * Transfers money from one account to another.
     *
     * @param vonKontonr the account number to transfer money from
     * @param nachKontonr the account number to transfer money to
     * @param betrag the amount of money to transfer
     * @param verwendungszweck the purpose of the transfer
     *
     * @return true if the money transfer was successful, false otherwise
     */
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck) {
        //TODO: Implementieren
        return false;
    }
}
