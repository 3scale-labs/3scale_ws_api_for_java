package threescale.v3.api.impl;

import threescale.v3.api.HttpResponse;
import threescale.v3.api.ServerAccessor;
import threescale.v3.api.ServerError;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Performs GET's and POST's against the live 3Scale Server
 */
public class ServerAccessorDriver implements ServerAccessor {

    public ServerAccessorDriver() {
    }

    /**
     * @param urlParams url + parameter string
     * @return Http Response
     * @throws ServerError
     * @see ServerAccessor
     */
    public HttpResponse get(final String urlParams) throws ServerError {
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


            return new HttpResponse(connection.getResponseCode(), getBody(connection.getInputStream()));

        } catch (IOException ex) {
            try {
                return new HttpResponse(connection.getResponseCode(), getBody(connection.getErrorStream()));
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

    /**
     * @param urlParams url to access
     * @param data      The data to be sent
     * @return Response from the server
     * @throws ServerError
     * @see ServerAccessor
     */
    public HttpResponse post(final String urlParams,final String data) throws ServerError {
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
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            connection.connect();
            wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(data);
            wr.flush();

            return new HttpResponse(connection.getResponseCode(), getBody(connection.getInputStream()));
        } catch (IOException ex) {
            try {
                return new HttpResponse(connection.getResponseCode(),
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
