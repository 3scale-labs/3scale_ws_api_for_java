package net.threescale.api.v2;

import net.threescale.api.*;
import net.threescale.api.ApiException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 27-Sep-2010
 * Time: 17:07:48
 */
public class HttpSenderImpl implements HttpSender {

    private Logger log = LogFactory.getLogger(this);
    private final HttpConnectionFactory factory;

    public HttpSenderImpl() {
        this.factory = new HttpConnectionFactoryImpl();
    }

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
            log.info("Connecting to: " + hostUrl);

            con = factory.openConnection(hostUrl);
            log.info("Connected");

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            OutputStreamWriter out = new OutputStreamWriter(con
                    .getOutputStream());
            out.write(postData);
            out.close();
            log.info("Written Post data");

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

            if (con.getResponseCode() == 200 || con.getResponseCode() == 403) {
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

    private final String  IOERROR_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?> " +
        "<error code=\"ioerror\">IO Error connecting to the server</error>";

    private final String  ERROR_CONNECTING_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"utf-8\" ?> " +
        "<error code=\"server_error\">Could not connect to the server</error>";

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
