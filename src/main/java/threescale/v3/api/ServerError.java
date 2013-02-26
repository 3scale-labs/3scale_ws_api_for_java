package threescale.v3.api;

/**
 * User: geoffd
 * Date: 18/02/2013
 */
public class ServerError extends Exception {
    public ServerError(String reason) {
        super(reason);
    }
}
