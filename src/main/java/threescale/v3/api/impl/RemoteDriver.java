package threescale.v3.api.impl;

import threescale.v3.api.HtmlClient;
import threescale.v3.api.HtmlResponse;
import threescale.v3.api.ServerError;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * User: geoffd
 * Date: 21/02/2013
 */
public class RemoteDriver implements HtmlClient {

    public RemoteDriver() {
    }

    public HtmlResponse get(String urlParams) throws ServerError {
        HttpURLConnection connection = null;
        URL url;

        try {
            url = new URL(urlParams);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            connection.connect();


            return new HtmlResponse(connection.getResponseCode(), getBody(connection.getInputStream()));

        } catch (IOException ex) {
            try {
                return new HtmlResponse(connection.getResponseCode(), getBody(connection.getErrorStream()));
            } catch (IOException e) {
                throw new ServerError(e.getMessage());
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String getBody(InputStream content) throws IOException {
        BufferedReader rd;
        StringBuilder sb;
        String line;
        rd = new BufferedReader(new InputStreamReader(content));
        sb = new StringBuilder();

        while ((line = rd.readLine()) != null) {
            sb.append(line + '\n');
        }
        return sb.toString();
    }

    public HtmlResponse post(String urlParams, String data) throws ServerError {
        HttpURLConnection connection = null;
        OutputStreamWriter wr;
        URL url;

        try {
            url = new URL(urlParams);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");


            connection.connect();
            wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(URLEncoder.encode(data, "UTF-8"));
            wr.flush();

            return new HtmlResponse(connection.getResponseCode(), getBody(connection.getInputStream()));
        } catch (IOException ex) {
            try {
                return new HtmlResponse(connection.getResponseCode(),
                        (connection.getErrorStream() == null) ? getBody(connection.getInputStream()) : getBody(connection.getErrorStream()));
            } catch (IOException e) {
                throw new ServerError(e.getMessage());
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
