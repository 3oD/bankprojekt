package bankprojekt.verarbeitung;

import java.util.List;

/**
 * The Beobachter interface represents an observer that can receive update notifications.
 *
 * This interface extends the List interface, allowing instances of Beobachter to maintain a list
 * of other Beobachter objects that it observes.
 *
 * @param <S> The type of data that the Beobachter can receive as update.
 */
public interface Beobachter<S> extends List<Beobachter> {
    void aktualisiere(S daten);
}
