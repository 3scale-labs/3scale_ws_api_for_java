package threescale.v3.api.impl;

import threescale.v3.api.HtmlClient;
import threescale.v3.api.HtmlResponse;
import threescale.v3.api.ServerError;

/**
 * User: geoffd
 * Date: 21/02/2013
 */
public class RemoteDriver implements HtmlClient {

    public RemoteDriver(String hostUrl) {
    }

    public HtmlResponse get(String url) throws ServerError {
        return null;
    }

    public HtmlResponse post(String url, String data) throws ServerError {
        return null;
    }
}
