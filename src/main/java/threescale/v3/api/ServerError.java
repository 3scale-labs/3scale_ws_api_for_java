package threescale.v3.api;

/**
 * Encapulates error information for a server operation.
 */
public class ServerError extends Exception {

    private static final long serialVersionUID = -5900004126517852322L;

    public ServerError(String reason) {
        super(reason);
    }
}
