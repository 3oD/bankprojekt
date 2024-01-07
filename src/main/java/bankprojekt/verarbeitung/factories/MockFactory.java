package bankprojekt.verarbeitung.factories;

import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;

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
