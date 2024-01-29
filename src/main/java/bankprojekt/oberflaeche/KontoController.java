package bankprojekt.oberflaeche;

import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Kunde;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * A controller class for managing a bank account.
 * This class extends the Application class and provides methods for depositing
 * and withdrawing money from the account.
 */
public class KontoController extends Application {
    /**
     * The view variable represents the user interface for a single bank account.
     */
    private KontoOberflaeche view;
    /**
     * The private variable 'model' represents a Girokonto.
     * A Girokonto is a type of bank account that allows overdrafts and supports
     * sending and receiving transfers.
     * In this context, the Girokonto is used as a model for the bank account in the application.
     */
    private Girokonto model;

    @Override
    public void start(Stage stage) {

        model = new Girokonto(Kunde.MUSTERMANN, 1234567, 500);
        view = new KontoOberflaeche(model, this);
        Scene scene = new Scene(view, 500, 300);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method is used to deposit money into the bank account.
     * It retrieves the amount entered by the user from the view,
     * checks if the account is locked, and then calls the 'einzahlen'
     * method of the model to deposit the money.
     * If any error occurs during the process, it displays an error message
     * using the 'displayError' method of the view.
     */
    public void einzahlen(double betrag) {
        if (model.isGesperrt()){
            this.view.displayError("Fehler beim Einzahlen: Konto ist gesperrt");
            return;
        }
        try {
            this.model.einzahlen(betrag);
        } catch (IllegalArgumentException e) {
            this.view.displayError("Fehler beim Einzahlen: " + e.getMessage());
        }
    }

    /**
     * This method is used to withdraw money into the bank account.
     * It retrieves the amount entered by the user from the view,
     * checks if the account is locked, and then calls the 'abheben'
     * method of the model to withdraw the money.
     * If any error occurs during the process, it displays an error message
     * using the 'displayError' method of the view.
     */
    public void abheben(double betrag) {
        try {
            boolean isSuccessful = this.model.abheben(betrag);
            if (!isSuccessful) {
                this.view.displayError("Fehler beim Abheben: Kontostand zu niedrig");
            }
        } catch (IllegalArgumentException e) {
            this.view.displayError("Fehler beim Abheben: " + e.getMessage());
        } catch (GesperrtException e) {
            this.view.displayError(e.getMessage());
        }
    }
}
