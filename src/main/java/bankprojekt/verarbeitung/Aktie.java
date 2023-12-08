package bankprojekt.verarbeitung;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;
import java.util.logging.Logger;

public class Aktie {
    private final String name;
    private final String wertpapierNr;
    private final Random rand = new Random();
    private final Lock lock = new ReentrantLock();
    private final Condition kursChanged = lock.newCondition();
    private final Condition aktieExists = lock.newCondition();
    private final AtomicReference<Double> kurs = new AtomicReference<>();
    private static final Logger logger = Logger.getLogger(Aktie.class.getName());

    public Aktie(String name, String wkn, double kurs) {
        this.name = name;
        this.wertpapierNr = wkn;
        this.kurs.set(kurs);

        Executors.newScheduledThreadPool(0).scheduleAtFixedRate(() -> {
            lock.lock();
            try {
                double percentChange = rand.nextDouble() * 6 - 3;
                this.kurs.set(this.kurs.get() + this.kurs.get() * percentChange / 100);
                kursChanged.signalAll();

                // Logging des aktuellen Kurses
                logger.info(String.format("Aktueller Kurswert für %s (%s): %.2f", name, wertpapierNr, this.kurs.get()));
            } finally {
                lock.unlock();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public double getKurs() {
        lock.lock();
        try {
            return kurs.get();
        } finally {
            lock.unlock();
        }
    }

    public String getName() {
        return name;
    }

    public String getWertpapierNr() {
        return wertpapierNr;
    }

    public void awaitKursChange() throws InterruptedException {
        lock.lock();
        try {
            kursChanged.await();
        } finally {
            lock.unlock();
        }
    }

    public void awaitAktieExists() throws InterruptedException {
        lock.lock();
        try {
            aktieExists.await();
        } finally {
            lock.unlock();
        }
    }
}
