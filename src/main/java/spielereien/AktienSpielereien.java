package spielereien;

import bankprojekt.verarbeitung.Aktie;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class AktienSpielereien {
    private static final Logger LOGGER = Logger.getLogger("AktienSpielereien");
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final String BLUE_START = "\u001B[34m";
    private static final String GREEN_START = "\u001B[32m";
    private static final String WHITE_START = "\u001B[37m";
    private static final String COLOR_END = "\u001B[0m";

    public static void main(String[] args) throws Exception {
        Aktie google = new Aktie("Google", "GOOGL", 2000);
        Aktie apple = new Aktie("Apple", "AAPL", 300);
        Aktie microsoft = new Aktie("Microsoft", "MSFT", 250);

        Konto konto = new Girokonto();
        konto.einzahlen(1000000);
        System.out.println("Kontostand: " + konto.getKontostand());


        Runnable getKurse = () -> LOGGER.info(BLUE_START + System.lineSeparator() +
                "GOOGL: " + google.getKurs() + System.lineSeparator() +
                "AAPL: " + apple.getKurs() + System.lineSeparator() +
                "MSFT: " + microsoft.getKurs() + System.lineSeparator() +
                COLOR_END);

        scheduler.scheduleAtFixedRate(getKurse, 0, 1, TimeUnit.SECONDS);

        Future<Double> kauf1 = konto.kaufauftrag(google, 10, 1900);
        Future<Double> kauf2 = konto.kaufauftrag(apple, 3, 295);
        Future<Double> kauf3 = konto.kaufauftrag(microsoft, 5, 245);

        LOGGER.info(GREEN_START + "Kaufpreis von GOOGL: " + kauf1.get() + COLOR_END);
        LOGGER.info(GREEN_START + "Kaufpreis von AAPL: " + kauf2.get() + COLOR_END);
        LOGGER.info(GREEN_START + "Kaufpreis von MSFT: " + kauf3.get() + COLOR_END);

        Future<Double> verkauf1 = konto.verkaufauftrag(google.getWertpapierNr(), 2100);
        Future<Double> verkauf2 = konto.verkaufauftrag(apple.getWertpapierNr(), 305);
        Future<Double> verkauf3 = konto.verkaufauftrag(microsoft.getWertpapierNr(), 255);

        LOGGER.info(BLUE_START + "Aktie: " + "Verkaufspreis von GOOGL: " + verkauf1.get() + COLOR_END);
        LOGGER.info(BLUE_START + "Aktie: " + "Verkaufspreis von AAPL: " + verkauf2.get() + COLOR_END);
        LOGGER.info(BLUE_START + "Aktie: " + "Verkaufspreis von MSFT: " + verkauf3.get() + COLOR_END);


        LOGGER.info(WHITE_START + String.format("Gesamtkaufpreis für Google-Aktien: %.2f%n", kauf1.get()) + COLOR_END);
        LOGGER.info(WHITE_START + String.format("Gesamtkaufpreis für Apple-Aktien: %.2f%n", kauf2.get()) + COLOR_END);
        LOGGER.info(WHITE_START + String.format("Gesamtkaufpreis für Microsoft-Aktien: %.2f%n", kauf3.get()) + COLOR_END);
        LOGGER.info(WHITE_START + String.format("Gesamteinnahmen aus dem Verkauf von Google-Aktien: %.2f%n", verkauf1.get()) + COLOR_END);
        LOGGER.info(WHITE_START + String.format("Gesamteinnahmen aus dem Verkauf von Apple-Aktien: %.2f%n", verkauf2.get()) + COLOR_END);
        LOGGER.info(WHITE_START + String.format("Gesamteinnahmen aus dem Verkauf von Microsoft-Aktien: %.2f%n", verkauf3.get()) + COLOR_END);

        LOGGER.info(WHITE_START + "Alle Aufträge abgeschlossen" + COLOR_END);
        microsoft.shutdown();
        LOGGER.info("#####  " + microsoft.getName() + "(" + microsoft.getWertpapierNr() + ") wurde beendet  #####");
        apple.shutdown();
        LOGGER.info("#####  " + apple.getName() + "(" + apple.getWertpapierNr() + ") wurde beendet  #####");
        google.shutdown();
        LOGGER.info("#####  " + google.getName() + "(" + google.getWertpapierNr() + ") wurde beendet  #####");

        scheduler.shutdown();
    }
}
