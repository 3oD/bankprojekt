package bankprojekt.verarbeitung.factories;

import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;

/**
 * A factory class for creating instances of the {@link Konto} class.
 * <p>
 * This class is used to create mock objects for testing purposes.
 */
public class MockFactory extends KontoFactory {

    private final Konto mockKonto;

    public MockFactory(Konto mockKonto) {
        this.mockKonto = mockKonto;
    }
    @Override
    public Konto createKonto(Kunde inhaber, long nummer) {
        return mockKonto;
    }
}
