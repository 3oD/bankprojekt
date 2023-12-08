package spielereien;

import bankprojekt.verarbeitung.Aktie;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;

import java.util.concurrent.Future;

public class AktienSpielereien {
    public static void main(String[] args) throws Exception {
        Aktie google = new Aktie("Google", "GOOGL", 2000);
        Aktie apple = new Aktie("Apple", "AAPL", 300);
        Aktie microsoft = new Aktie("Microsoft", "MSFT", 250);

        Konto konto = new Girokonto();  // Ergänzen Sie diese Zeile je nach Ihrer Klassenstruktur
        konto.einzahlen(10000);
        System.out.println("Kontostand: " + konto.getKontostand());

        Future<Double> kauf1 = konto.kaufauftrag(google, 1, 2000);
        Future<Double> kauf2 = konto.kaufauftrag(apple, 3, 290);
        Future<Double> kauf3 = konto.kaufauftrag(microsoft, 5, 250);

        Future<Double> verkauf1 = konto.verkaufauftrag(google.getWertpapierNr(), 2050);
        Future<Double> verkauf2 = konto.verkaufauftrag(apple.getWertpapierNr(), 310);
        Future<Double> verkauf3 = konto.verkaufauftrag(microsoft.getWertpapierNr(), 260);

        while (!(kauf1.isDone() && kauf2.isDone() && kauf3.isDone() && verkauf1.isDone() && verkauf2.isDone() && verkauf3.isDone())) {
            Thread.sleep(1000);  // Warten Sie 1 Sekunde zwischen den Kontrollen
            konto.getDepot();
            if (kauf1.isDone()) {
                System.out.println("Kauf von Google-Aktien abgeschlossen");
            }
            else {
                System.out.println("Kauf von Google-Aktien noch nicht abgeschlossen");
            }
            if (kauf2.isDone()) {
                System.out.println("Kauf von Apple-Aktien abgeschlossen");
            } else {
                System.out.println("Kauf von Apple-Aktien noch nicht abgeschlossen");
            }
            if (kauf3.isDone()) {
                System.out.println("Kauf von Microsoft-Aktien abgeschlossen");
            } else {
                System.out.println("Kauf von Microsoft-Aktien noch nicht abgeschlossen");
            }


            if (verkauf1.isDone()) {
                System.out.println("Verkauf von Google-Aktien abgeschlossen");
            } else {
                System.out.println("Verkauf von Google-Aktien noch nicht abgeschlossen");
            }
            if (verkauf2.isDone()) {
                System.out.println("Verkauf von Apple-Aktien abgeschlossen");
            } else {
                System.out.println("Verkauf von Apple-Aktien noch nicht abgeschlossen");
            }
            if (verkauf3.isDone()) {
                System.out.println("Verkauf von Microsoft-Aktien abgeschlossen");
            } else {
                System.out.println("Verkauf von Microsoft-Aktien noch nicht abgeschlossen");
            }
        }

        System.out.printf("Gesamtkaufpreis für Google-Aktien: %.2f%n", kauf1.get());
        System.out.printf("Gesamtkaufpreis für Apple-Aktien: %.2f%n", kauf2.get());
        System.out.printf("Gesamtkaufpreis für Microsoft-Aktien: %.2f%n", kauf3.get());
        System.out.printf("Gesamteinnahmen aus dem Verkauf von Google-Aktien: %.2f%n", verkauf1.get());
        System.out.printf("Gesamteinnahmen aus dem Verkauf von Apple-Aktien: %.2f%n", verkauf2.get());
        System.out.printf("Gesamtergebnisse aus dem Verkauf von Microsoft-Aktien: %.2f%n", verkauf3.get());

        if (kauf1.isDone() && kauf2.isDone() && kauf3.isDone() && verkauf1.isDone() && verkauf2.isDone() && verkauf3.isDone()) {
            System.out.println("Alle Aufträge abgeschlossen");
            konto.getDepot();
        } else {
            System.out.println("Nicht alle Aufträge abgeschlossen");
        }
    }
}
