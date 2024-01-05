package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;
import bankprojekt.verarbeitung.factories.KontoFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * The Bank class represents a bank with a given bank code (Bankleitzahl). It provides methods for creating accounts,
 * performing transactions, and retrieving account information.
 */
public class Bank implements Cloneable, Serializable {
    private final long bankleitzahl;
    private final Map<Long, Konto> kontoMap = new HashMap<>();
    private long kontonummerZaehler = 10000000L;
    private static final long MINIMUM_KONTONUMMER = 10000000L;

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

    /*
     * ###############################################
     * Methods for creating bank accounts
     * ###############################################
     */

    /**
     * Creates a new bank account using the given KontoFactory.
     *
     * @param factory the factory to create the account
     * @return the account number of the newly created account
     */
    public long kontoErstellen(KontoFactory factory, Kunde inhaber) {
        Konto neuesKonto = factory.createKontoMitInhaber(inhaber, generiereEindeutigeKontonummer());
        kontoMap.put(neuesKonto.getKontonummer(), neuesKonto);
        return neuesKonto.getKontonummer();
    }

    /**
     * Inserts a mock bank account into the bank's account map.
     *
     * @param k the bank account to be inserted
     * @return the account number of the inserted bank account
     */
    public long mockEinfuegen(Konto k) {
        long kontonummer = generiereEindeutigeKontonummer();

        kontoMap.put(kontonummer, k);
        return kontonummer;
    }


    /*
     * ###############################################
     * Methods for retrieving account information
     * ###############################################
     */

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
     * Retrieves the current account balance for the specified account number.
     *
     * @param nummer the account number for which to retrieve the balance
     * @return the current account balance
     * @throws KontonummerDoesNotExistException if the account number does not exist
     */
    public double getKontostand(long nummer) throws KontonummerDoesNotExistException {
        validiereKonto(nummer);
        return kontoMap.get(nummer).getKontostand();
    }

    /**
     * Retrieves a list of customers with a bank account balance equal to or greater than the specified minimum amount.
     *
     * @param minimum the minimum balance to filter by
     * @return a list of customers with a balance equal to or greater than the minimum amount
     * @throws IllegalArgumentException if the minimum amount is NaN or infinite
     */
    public List<Kunde> getKundenMitVollemKonto(double minimum) {
        if (Double.isNaN(minimum) || Double.isInfinite(minimum)) {
            throw new IllegalArgumentException("Unguelitge Einfage von minimum");
        }

        return kontoMap.values().stream()
                .filter(konto -> konto.getKontostand() >= minimum)
                .map(Konto::getInhaber)
                .toList();
    }

    /**
     * Retrieves the name and addresses of all customers associated with bank accounts in this bank.
     *
     * @return a string containing the addresses of all customers, separated by a new line
     */
    public String getKundenadressen() {
        return kontoMap.values().stream()
                .map(Konto::getInhaber)
                .distinct()
                .sorted(Comparator.comparing(Kunde::getVorname))
                .map(kunde -> kunde.getName() + ": " + kunde.getAdresse())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Retrieves a list of account numbers with gaps in the range of existing account numbers.
     *
     * @return a list of account numbers with gaps
     */
    public List<Long> getKontonummernLuecken() {
        long upperBound = kontonummerZaehler - 1;
        return LongStream.rangeClosed(MINIMUM_KONTONUMMER, upperBound)
                .boxed()
                .filter(num -> !kontoMap.containsKey(num))
                .toList();
    }

    /**
     * Retrieves a list of customers with a bank account balance equal to or greater than the specified minimum amount.
     *
     * @param minimum the minimum balance to filter by
     * @return a list of customers with a balance equal to or greater than the minimum amount
     * @throws IllegalArgumentException if the minimum amount is NaN or infinite
     */
    public List<Kunde> getAlleReichenKunden(double minimum) {
        if (Double.isNaN(minimum) || Double.isInfinite(minimum)) {
            throw new IllegalArgumentException("Invalid minimum value");
        }

        return kontoMap.values().stream()
                .collect(Collectors.groupingBy(Konto::getInhaber))
                .entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(),
                        e.getValue().stream()
                                .mapToDouble(Konto::getKontostand)
                                .sum()))
                .filter(e -> e.getValue() > minimum)
                .map(Map.Entry::getKey)
                .toList();
    }


    /*
     * ###############################################
     * Methods for performing transactions
     * ###############################################
     */

    /**
     * Validates the existence of a specified bank account number.
     *
     * @param nummer the bank account number to be validated
     * @throws KontonummerDoesNotExistException if the bank account number does not exist
     */
    private void validiereKonto(long nummer) throws KontonummerDoesNotExistException {
        if (!kontoMap.containsKey(nummer)) {
            throw new KontonummerDoesNotExistException(nummer);
        }
    }

    /**
     * Validates a specified amount to ensure it is a valid value.
     *
     * @param betrag the amount to be validated
     * @throws IllegalArgumentException if the amount is less than or equal to 0, NaN, or infinite
     */
    private void validiereBetrag(double betrag) {

        if (betrag <= 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Invalid amount");
        }
    }

    /**
     * Withdraws a specified amount of money from the account with the given account number.
     *
     * @param nummer the account number to withdraw from
     * @param betrag the amount of money to withdraw
     * @return true if the withdrawal was successful, false otherwise
     * @throws KontonummerDoesNotExistException if the account number does not exist
     * @throws GesperrtException                if the account is locked
     */
    public boolean geldAbheben(long nummer, double betrag) throws KontonummerDoesNotExistException, GesperrtException {
        validiereKonto(nummer);
        validiereBetrag(betrag);
        return kontoMap.get(nummer).abheben(betrag);
    }

    /**
     * Deposits the specified amount of money into the account with the given account number.
     *
     * @param auf    the account number to deposit into
     * @param betrag the amount of money to deposit
     * @throws KontonummerDoesNotExistException if the account number does not exist
     */
    public void geldEinzahlen(long auf, double betrag) throws KontonummerDoesNotExistException {
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
     * Transfers the specified amount of money from one account to another.
     *
     * @param vonKontonr       the account number for the sender
     * @param nachKontonr      the account number for the recipient
     * @param betrag           the amount of money to transfer
     * @param verwendungszweck the purpose of the money transfer
     * @return true if the money transfer was successful, false otherwise
     * @throws IllegalArgumentException if the amount is negative, zero, infinit or NaN, or if the purpose is blank
     */
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck) throws IllegalArgumentException {
        Konto sender = kontoMap.get(vonKontonr);
        Konto empfaenger = kontoMap.get(nachKontonr);

        if (sender == null || empfaenger == null) {
            return false;
        }

        validiereBetrag(betrag);
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
    boolean sendeUeberweisung(Ueberweisungsfaehig sender, double betrag, String empfaenger,
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
    void empfangeUeberweisung(Ueberweisungsfaehig empfaenger, double betrag, String vonName,
                              long vonKontonr, long vonBlz, String verwendungszweck) {
        empfaenger.ueberweisungEmpfangen(betrag, vonName, vonKontonr, vonBlz, verwendungszweck);
    }

    /**
     * Locks all bank accounts with a negative account balance.
     * Once locked, the account cannot be accessed or modified.
     * This method iterates over all bank accounts in the bank and filters out
     * the ones with a negative account balance. It then calls the {@link Konto#sperren()} method
     * for each filtered account to lock it.
     */
    public void pleitegeierSperren() {
        kontoMap.values().stream()
                .filter(konto -> konto.getKontostand() < 0)
                .forEach(Konto::sperren);
    }

    /**
     * Creates a clone of the current Bank object.
     *
     * @return a clone of the current Bank object
     * @throws CloneNotSupportedException if cloning is not supported for the Bank object
     */
    @Override
    public Bank clone() throws CloneNotSupportedException {
        byte[] byteData;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
            oos.flush();
            byteData = bos.toByteArray();
        } catch (IOException e) {
            throw new CloneNotSupportedException("Bank could not be cloned");
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Bank) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new CloneNotSupportedException("Bank could not be cloned");
        }
    }
}
