package bankprojekt.verarbeitung;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * The Aktie class represents a stock with a name, a security number (WKN), and a current price.
 * Once created, the stock's price will be periodically updated based on a random percentage change.
 * The class provides methods to get the stock's name, security number, and current price,
 * as well as an awaitKursChange() method to pause the execution until the price of the stock changes.
 * It also provides a shutdown() method to stop the periodic price updates.
 */
public class Aktie {
    private final String name;
    private final String wertpapierNr;
    private final Lock lock = new ReentrantLock();
    private final Condition kursChanged = lock.newCondition();
    private final AtomicReference<Double> kurs = new AtomicReference<>();

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(0);


    /**
     * Creates a new Aktie object with the given name, security number (WKN), and initial price.
     * The price of the Aktie will be periodically updated based on a random percentage change.
     *
     * @param name The name of the Aktie.
     * @param wkn The security number (WKN) of the Aktie.
     * @param kurs The initial price of the Aktie.
     */
    public Aktie(String name, String wkn, double kurs) {
        this.name = name;
        this.wertpapierNr = wkn;
        this.kurs.set(kurs);

        updateKurs();
    }

    /**
     * Retrieves the current price (kurs) of the stock.
     *
     * @return The current price of the stock.
     */
    public double getKurs() {
        lock.lock();
        try {
            return kurs.get();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Updates the kurs (price) of the stock periodically based on a random percentage change.
     * The kurs is updated every second using a scheduled executor service.
     * The kurs is changed by a random percentage between -3% and 3%.
     * After updating the kurs, the method signals all threads that are waiting for kurs changes.
     * The method also logs the current kurs value.
     */
    private void updateKurs() {
        executorService.scheduleAtFixedRate(() -> {
            lock.lock();
            try {
                double percentChange = ThreadLocalRandom.current().nextDouble(-3, 3);
                this.kurs.set(this.kurs.get() + this.kurs.get() * percentChange / 100);
                kursChanged.signalAll();
            } finally {
                lock.unlock();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Retrieves the name of the object.
     *
     * @return The name of the object.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the security number (WertpapierNr) of the stock.
     *
     * @return The security number of the stock.
     */
    public String getWertpapierNr() {
        return wertpapierNr;
    }

    /**
     * Waits for a change in the kurs (price) of the stock. If the kurs changes,
     * the method will resume execution. This method should be called within a
     * synchronized block to ensure thread-safe access to the stock object.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void awaitKursChange() throws InterruptedException {
        lock.lock();
        try {
            kursChanged.await();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Shuts down the Aktie instance.
     *
     * This method stops the execution of the Aktie instance by shutting down the executor service.
     * It also logs a shutdown message with the name and wertpapierNr of the Aktie.
     *
     * only implemented to stop AktienSpielereien automatically after all verkaufauftraege are done.
     */
    public void shutdown() {
        executorService.shutdownNow();
    }
}
