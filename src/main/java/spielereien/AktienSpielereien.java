package spielereien;

import bankprojekt.verarbeitung.Aktie;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;

import java.util.concurrent.Future;
import java.util.logging.Logger;

public class AktienSpielereien {
    private static final Logger logger = Logger.getLogger("AktienSpielereien");

    public static void main(String[] args) throws Exception {
        Aktie google = new Aktie("Google", "GOOGL", 2000);
        Aktie apple = new Aktie("Apple", "AAPL", 300);
        Aktie microsoft = new Aktie("Microsoft", "MSFT", 250);

        Konto konto = new Girokonto();
        konto.einzahlen(1000000);
        System.out.println("Kontostand: " + konto.getKontostand());

        Future<Double> kauf1 = konto.kaufauftrag(google, 10, 1900);
        Future<Double> kauf2 = konto.kaufauftrag(apple, 3, 290);
        Future<Double> kauf3 = konto.kaufauftrag(microsoft, 5, 240);

        logger.info("\u001B[32m" + "Kaufpreis von GOOGL: " + kauf1.get() + "\u001B[0m");
        logger.info("\u001B[32m" + "Kaufpreis von AAPL: " + kauf2.get() + "\u001B[0m");
        logger.info("\u001B[32m" + "Kaufpreis von MSFT: " + kauf3.get() + "\u001B[0m");

        Future<Double> verkauf1 = konto.verkaufauftrag(google.getWertpapierNr(), 2100);
        Future<Double> verkauf2 = konto.verkaufauftrag(apple.getWertpapierNr(), 310);
        Future<Double> verkauf3 = konto.verkaufauftrag(microsoft.getWertpapierNr(), 260);

        logger.info("\u001B[34m" + "Aktie: " + "Verkaufspreis von GOOGL: " + verkauf1.get() + "\u001B[0m");
        logger.info("\u001B[34m" + "Aktie: " + "Verkaufspreis von AAPL: " + verkauf2.get() + "\u001B[0m");
        logger.info("\u001B[34m" + "Aktie: " + "Verkaufspreis von MSFT: " + verkauf3.get() + "\u001B[0m");

        System.out.printf("Gesamtkaufpreis für Google-Aktien: %.2f%n", kauf1.get());
        System.out.printf("Gesamtkaufpreis für Apple-Aktien: %.2f%n", kauf2.get());
        System.out.printf("Gesamtkaufpreis für Microsoft-Aktien: %.2f%n", kauf3.get());
        System.out.printf("Gesamteinnahmen aus dem Verkauf von Google-Aktien: %.2f%n", verkauf1.get());
        System.out.printf("Gesamteinnahmen aus dem Verkauf von Apple-Aktien: %.2f%n", verkauf2.get());
        System.out.printf("Gesamtergebnisse aus dem Verkauf von Microsoft-Aktien: %.2f%n", verkauf3.get());

        if (kauf1.isDone() && kauf2.isDone() && kauf3.isDone() && verkauf1.isDone() && verkauf2.isDone() && verkauf3.isDone()) {
            System.out.println("Alle Aufträge abgeschlossen");
            microsoft.shutdown();
            apple.shutdown();
            google.shutdown();
        } else {
            System.out.println("Nicht alle Aufträge abgeschlossen");
        }
    }
}
