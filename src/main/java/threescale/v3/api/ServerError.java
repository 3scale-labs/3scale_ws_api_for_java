package threescale.v3.api;

/**
 * Encapulates error information for a server operation.
 */
public class ServerError extends Exception {

    public ServerError(String reason) {
        super(reason);
    }
}
