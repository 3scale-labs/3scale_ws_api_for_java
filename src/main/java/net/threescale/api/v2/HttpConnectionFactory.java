package net.threescale.api.v2;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Interface to obtain an HttpURLConnection.
 */
public interface HttpConnectionFactory {

    /**
     * Open an http connection to the specified host.
     * @param hostUrl Fully qualified URL to connect to e.g. http://su1.3scale.net
     * @return An open HttpURLConnection.
     * @throws IOException Thrown if the connection cannot be opend.
     */
    HttpURLConnection openConnection(String hostUrl) throws IOException;

}
