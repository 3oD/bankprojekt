package bankprojekt.oberflaeche;

import bankprojekt.verarbeitung.Konto;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Eine Oberfläche für ein einzelnes Konto. Man kann einzahlen
 * und abheben und sperren und die Adresse des Kontoinhabers
 * ändern
 *
 * @author Doro
 */
public class KontoOberflaeche extends BorderPane {
    private Text ueberschrift;
    private GridPane anzeige;
    private Text txtNummer;
    /**
     * Anzeige der Kontonummer
     */
    private Text nummer;
    private Text txtStand;
    /**
     * Anzeige des Kontostandes
     */
    private Text stand;
    private Text txtGesperrt;
    /**
     * Anzeige und Änderung des Gesperrt-Zustandes
     */
    private CheckBox gesperrt;
    private Text txtAdresse;
    /**
     * Anzeige und Änderung der Adresse des Kontoinhabers
     */
    private TextArea adresse;
    /**
     * Anzeige von Meldungen über Kontoaktionen
     */
    private Text meldung;
    /**
     * Anzeige von Fehlern bei Kontoaktionen
     */
    private Text fehlerMeldung;
    private String meldungText = "Willkommen";
    private String adresseText;
    private HBox aktionen;
    /**
     * Auswahl des Betrags für eine Kontoaktion
     */
    private TextField betrag;
    /**
     * löst eine Einzahlung aus
     */
    private Button einzahlen;
    /**
     * löst eine Abhebung aus
     */
    private Button abheben;

    /**
     * erstellt die Oberfläche
     */
    public KontoOberflaeche(Konto model, KontoController controller) {
        ueberschrift = new Text("Ein Konto verändern");
        ueberschrift.setFont(new Font("Sans Serif", 25));
        BorderPane.setAlignment(ueberschrift, Pos.CENTER);
        this.setTop(ueberschrift);

        anzeige = new GridPane();
        anzeige.setPadding(new Insets(20));
        anzeige.setVgap(10);
        anzeige.setAlignment(Pos.CENTER);

        txtNummer = new Text("Kontonummer:");
        txtNummer.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtNummer, 0, 0);
        nummer = new Text();
        nummer.setFont(new Font("Sans Serif", 15));
        GridPane.setHalignment(nummer, HPos.RIGHT);
        anzeige.add(nummer, 1, 0);
        nummer.textProperty().set(model.getKontonummerFormatiert());

        txtStand = new Text("Kontostand:");
        txtStand.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtStand, 0, 1);
        stand = new Text();
        stand.setFont(new Font("Sans Serif", 15));
        GridPane.setHalignment(stand, HPos.RIGHT);
        anzeige.add(stand, 1, 1);
        stand.textProperty().bind(Bindings.format("%.2f %s", model.kontostandProperty(), model.getAktuelleWaehrung()));
        stand.styleProperty().bind(Bindings.when(model.isKontostandPositivProperty())
                .then("-fx-fill: green;")
                .otherwise("-fx-fill: red;"));


        txtGesperrt = new Text("Gesperrt: ");
        txtGesperrt.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtGesperrt, 0, 2);
        gesperrt = new CheckBox();
        GridPane.setHalignment(gesperrt, HPos.RIGHT);
        anzeige.add(gesperrt, 1, 2);
        gesperrt.selectedProperty().bindBidirectional(model.gesperrtProperty());
        model.gesperrtProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                displayError("Das Konto wurde gesperrt.");
            } else {
                displayError("Das Konto wurde entsperrt.");
            }
        });

        txtAdresse = new Text("Adresse: ");
        txtAdresse.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtAdresse, 0, 3);
        adresse = new TextArea();
        adresse.setPrefColumnCount(25);
        adresse.setPrefRowCount(2);
        GridPane.setHalignment(adresse, HPos.RIGHT);
        anzeige.add(adresse, 1, 3);
        adresse.textProperty().bindBidirectional(model.getInhaber().adresseProperty());

        meldungText = meldungText + " " + model.getInhaber().getVorname() + " " + model.getInhaber().getNachname();
        meldung = new Text(meldungText);
        meldung.setFont(new Font("Sans Serif", 15));
        meldung.setFill(Color.RED);
        anzeige.add(meldung, 0, 4, 2, 1);

        this.setCenter(anzeige);

        fehlerMeldung = new Text();
        fehlerMeldung.setFont(new Font("Sans Serif", 15));
        fehlerMeldung.setFill(Color.RED);
        anzeige.add(fehlerMeldung, 0, 5, 2, 1);

        aktionen = new HBox();
        aktionen.setSpacing(10);
        aktionen.setAlignment(Pos.CENTER);
        aktionen.setPadding(new Insets(0, 0, 20, 0));

        betrag = new TextField("100.00");
        aktionen.getChildren().add(betrag);

        einzahlen = new Button("Einzahlen");
        aktionen.getChildren().add(einzahlen);
        einzahlen.setOnAction(c -> {
            try {
                controller.einzahlen(Double.parseDouble(betrag.getText()));
            } catch (NumberFormatException e) {
                displayError("Bitte geben Sie einen gültigen Betrag ein.");
            }

        });

        abheben = new Button("Abheben");
        aktionen.getChildren().add(abheben);
        abheben.setOnAction(c -> {
            try {
                controller.abheben(Double.parseDouble(betrag.getText()));
            } catch (NumberFormatException e) {
                displayError("Bitte geben Sie einen gültigen Betrag ein.");
            }
        });

        this.setBottom(aktionen);
    }

    /**
     * Displays an error message on the screen for a specified duration.
     *
     * @param message the error message to be displayed
     */
    public void displayError(String message) {
        fehlerMeldung.setText(message);

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5000),
                ae -> fehlerMeldung.setText("")
        ));

        timeline.play();
    }
}
