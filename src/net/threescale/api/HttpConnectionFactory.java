package net.threescale.api;

import java.io.*;
import java.net.*;

public interface HttpConnectionFactory {

	HttpURLConnection openConnection(String hostUrl) throws IOException;

}
