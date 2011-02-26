package net.threescale.api;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface HttpConnectionFactory {

    HttpURLConnection openConnection(String hostUrl) throws IOException;

}
