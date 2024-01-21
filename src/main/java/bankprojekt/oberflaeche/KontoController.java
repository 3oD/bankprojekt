package bankprojekt.oberflaeche;

import bankprojekt.verarbeitung.Girokonto;

public class KontoController {
    private KontoOberflaeche view;
    private Girokonto model;

    public KontoController() {
        this.view = new KontoOberflaeche();
        this.model = new Girokonto();
    }
}
