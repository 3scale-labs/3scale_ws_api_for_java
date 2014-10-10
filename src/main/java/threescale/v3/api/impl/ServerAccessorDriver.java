package threescale.v3.api.impl;

import threescale.v3.api.HttpResponse;
import threescale.v3.api.ServerAccessor;
import threescale.v3.api.ServerError;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Properties;
/**
 * Performs GET's and POST's against the live 3Scale Server
 */
public class ServerAccessorDriver implements ServerAccessor {
	
	private Properties props;
	private String pluginHeaderValue;
	private String defaultVersion = "3.1";
    public ServerAccessorDriver() {

        
    	props = new Properties();
        try {
        	InputStream in = ServerAccessorDriver.class.getClassLoader().getResourceAsStream("props.properties");
        	if (in == null) {
        		System.out.println("props.properties not found");
        	}
        	else{
        		props.load(in);
        		defaultVersion = props.getProperty(MAVEN_PROJECT_VERSION);
        	}
        	
            
        } catch (Exception e) {
        	System.out.println(e);
        }   	
		pluginHeaderValue = X_3SCALE_USER_CLIENT_HEADER_JAVA_PLUGIN+defaultVersion;

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
            connection.setRequestProperty(X_3SCALE_USER_CLIENT_HEADER, pluginHeaderValue); 
            
            connection.connect();


            return new HttpResponse(connection.getResponseCode(), getBody(connection.getInputStream()));

        } catch (IOException ex) {
            try {
                return new HttpResponse(connection.getResponseCode(), getBody(connection.getErrorStream()));
            } catch (IOException e) {
                throw new ServerError(e.getMessage());
            }
        } catch (NullPointerException npe) {
        	throw new ServerError("NullPointerException thrown ServerAccessorDriver::get, urlParams were "+urlParams);
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
            connection.setRequestProperty(X_3SCALE_USER_CLIENT_HEADER, pluginHeaderValue); 


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
        }  catch (NullPointerException npe) {
        	throw new ServerError("NullPointerException thrown ServerAccessorDriver::post, urlParams were "+urlParams+", data was "+data);
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

	public String getPluginHeaderValue() {
		return pluginHeaderValue;
	}
}
