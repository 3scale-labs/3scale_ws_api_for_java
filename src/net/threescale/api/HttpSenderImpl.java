package net.threescale.api;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import org.w3c.dom.html.*;

/**
 * Sends requests to a server using Http.
 */
public class HttpSenderImpl implements HttpSender {

    private Logger log = LogFactory.getLogger(this);
    private HttpConnectionFactory factory;

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
     * @throws ApiException Error information.
     */
    public ApiHttpResponse sendPostToServer(String hostUrl, String postData)
            throws ApiException {
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
            out.write(postData.toString());
            out.close();
            log.info("Written Post data");
            String content = extractContent(con);
            if (con.getResponseCode() == 200 && content.length() > 0) {
                return new ApiHttpResponse(con.getResponseCode(), content, con.getContentType());
            } else if (con.getResponseCode() == 201)
                return new ApiHttpResponse(201, "", "");
            else {
                throw new ApiException(con.getResponseCode(), content);
            }
        }
        catch (IOException ex) {
            handleErrors(con);
        }
        return null;
    }

    /**
     * Send a DELETE message to the server.
     *
     * @param hostUrl Url and parameters to send to the server.
     * @return Http Response code.
     * @throws ApiException Error Information.
     */
    public int sendDeleteToServer(String hostUrl) throws ApiException {
        HttpURLConnection con = null;

        try {
            URL url = new URL(hostUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setRequestMethod("DELETE");

            con.getInputStream(); // Ensure we try to read something
            return con.getResponseCode();
        }
        catch (Exception ex) {
            handleErrors(con);
        }
        return -1;
    }

    /**
     * Send a Get message to the server
     *
     * @param hostUrl
     * @return Response from Server for successful action
     * @throws ApiException Error information if request fails
     */
    public String sendGetToServer(String hostUrl) throws ApiException {
        HttpURLConnection con = null;
        try {
            log.info("Connecting to: " + hostUrl);

            con = factory.openConnection(hostUrl);
            log.info("Connected");

            con.setRequestMethod("GET");

            if (con.getResponseCode() == 200) {
                return extractContent(con);
            }
            else {
                throw new ApiException(con.getResponseCode(), getErrorMessage(con));
            }
        }
        catch (Exception ex) {
            handleErrors(con);
        }
        return "";
    }

    private void handleErrors(HttpURLConnection con) throws ApiException {
        if (con != null) {
            try {
                throw new ApiException(con.getResponseCode(), getErrorMessage(con));
            } catch (IOException e) {
                throw new ApiException(500, "provider.other",  e.getMessage());
            }
        } else {
            throw new ApiException(500, "provider.other", "Error connecting to server");
        }
    }


    private String extractContent(HttpURLConnection con) throws IOException {
        assert (con != null);
        InputStream inputStream = con.getInputStream();
        assert (inputStream != null);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        StringBuffer response = new StringBuffer();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

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

    private class HttpConnectionFactoryImpl implements HttpConnectionFactory {

        public HttpURLConnection openConnection(String hostUrl) throws MalformedURLException, IOException {
            URL url = new URL(hostUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            return con;
        }

    }
}
