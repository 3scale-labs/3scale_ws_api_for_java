package net.threescale.api.v2;

import net.threescale.api.LogFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;


/**
 * Concrete implementation of HttpSender
 */
public class HttpSenderImpl implements HttpSender {

    private Logger log = LogFactory.getLogger(this);
    private final HttpConnectionFactory factory;

    /**
     * Normal Constructor using live implementation.
     */
    public HttpSenderImpl() {
        this.factory = new HttpConnectionFactoryImpl();
    }

    /**
     * Constructor that overrides factory.  Used for testing.
     *
     * @param factory
     */
    public HttpSenderImpl(HttpConnectionFactory factory) {
        this.factory = factory;
    }

    /**
     * Send a POST message.
     *
     * @param hostUrl  Url and parameters to send to the server.
     * @param postData Data to be POSTed.
     * @return Transaction data returned from the server.
     */
    public ApiHttpResponse sendPostToServer(String hostUrl, String postData) {
        HttpURLConnection con = null;
        try {
            log.info("Connecting to: " + hostUrl + "/transactions.xml");

            con = factory.openConnection(hostUrl + "/transactions.xml");
            log.info("Connected");

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            OutputStreamWriter out = new OutputStreamWriter(con
                    .getOutputStream());
            out.write(postData);
            out.close();
            log.info("Written Post data : " + postData);

            if (con.getResponseCode() == 202 || con.getResponseCode() == 403) {
                ApiHttpResponse response = new ApiHttpResponse(con.getResponseCode(), extractContent(con));
                log.info("Received response: " + response.getResponseCode() + " with message: " + response.getResponseText());
                return response;
            } else {
                ApiHttpResponse response = handleErrors(con);
                log.info("Error response: " + response.getResponseCode() + " with message: " + response.getResponseText());
                return response;
            }
        }
        catch (Exception ex) {
            return handleErrors(con);
        }
    }

    /**
     * Send a Get message to the server
     *
     * @param hostUrlWithParameters
     * @return Response from Server for successful action
     */
    public ApiHttpResponse sendGetToServer(String hostUrlWithParameters) {
        HttpURLConnection con = null;
        try {
            log.info("Connecting to: " + hostUrlWithParameters);

            con = factory.openConnection(hostUrlWithParameters);
            log.info("Connected");

            con.setRequestMethod("GET");

            if (con.getResponseCode() == 200) {
                ApiHttpResponse response = new ApiHttpResponse(con.getResponseCode(), extractContent(con));
                log.info("Received response: " + response.getResponseCode() + " with message: " + response.getResponseText());
                return response;
            } else  if (con.getResponseCode() == 403 || con.getResponseCode() == 404 || con.getResponseCode() == 409) {
                ApiHttpResponse response = new ApiHttpResponse(con.getResponseCode(), getErrorMessage(con));
                log.info("Received response: " + response.getResponseCode() + " with message: " + response.getResponseText());
                return response;
            } else {
                ApiHttpResponse response = handleErrors(con);
                log.info("Error response: " + response.getResponseCode() + " with message: " + response.getResponseText());
                return response;
            }
        }
        catch (Exception ex) {
            return handleErrors(con);
        }
    }

    /**
     * Reads error message from response and returns it as a string.
     *
     * @param con current connection
     * @return String with error message contents.
     * @throws IOException
     */
    private String getErrorMessage(HttpURLConnection con) throws IOException {
        assert (con != null);

        StringBuffer errStream = new StringBuffer();
        InputStream errorStream = con.getErrorStream();
        assert (errorStream != null);

        BufferedReader in = new BufferedReader(new InputStreamReader(errorStream));
        String errres;
        while ((errres = in.readLine()) != null) {
            errStream.append(errres);
        }
        in.close();
        return (errStream.toString());
    }

    /**
     * Extract data from message.
     *
     * @param con
     * @return
     * @throws IOException
     */
    private String extractContent(HttpURLConnection con) throws IOException {
        InputStream inputStream = con.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer response = new StringBuffer();

        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }


    private ApiHttpResponse handleErrors(HttpURLConnection con) {
        if (con != null) {
            try {
                return new ApiHttpResponse(con.getResponseCode(), con.getResponseMessage());
            } catch (IOException e) {
                return new ApiHttpResponse(500, IOERROR_RESPONSE);
            }
        } else {
            return new ApiHttpResponse(500, ERROR_CONNECTING_RESPONSE);
        }
    }

    private final String IOERROR_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?> " +
                    "<error code=\"ioerror\">IO Error connecting to the server</error>";

    private final String ERROR_CONNECTING_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?> " +
                    "<error code=\"server_error\">Could not connect to the server</error>";

    /**
     * Private class to get new live connection to the server.
     */
    private class HttpConnectionFactoryImpl implements HttpConnectionFactory {

        public HttpURLConnection openConnection(String hostUrl) throws IOException {
            URL url = new URL(hostUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            return con;
        }

    }

}
