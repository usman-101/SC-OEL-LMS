package library.ui;

/**
 * Implemented by panels that can reload their data from the services after a change.
 *
 * @author Usman
 */
public interface Refreshable {

    /**
     * Reloads the panel's data from the services and updates its components.
     */
    void refresh();
}
