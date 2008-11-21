package net.threescale.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Sends requests to a server using Http.
 */
public class HttpSenderImpl implements HttpSender {

	/**
	 * Send a POST message.
	 * @param hostUrl	Url and parameters to send to the server.
	 * @param postData	Data to be POSTed.
	 * @return	Transaction data returned from the server.
	 * @throws ApiException Error information.
	 */
	public ApiStartResponse sendPostToServer(String hostUrl, String postData)
			throws ApiException {
		HttpURLConnection con = null;

		try {
			URL url = new URL(hostUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			OutputStreamWriter out = new OutputStreamWriter(con
					.getOutputStream());
			out.write(postData.toString());
			out.close();

			return new ApiStartResponse(extractContent(con), con
					.getResponseCode());
		} catch (Exception ex) {
			if (con != null) {
				try {
					throw new ApiException(con.getResponseCode(),
							getErrorMessage(con));
				} catch (IOException e) {
					throw new ApiException(999, e.getMessage());
				}
			} else {
				throw new ApiException(999, "Error connecting to server");
			}
		}
	}

	/**
	 * Send a DELETE message to the server.
	 * @param hostUrl Url and parameters to send to the server.
	 * @return Http Response code.
	 * @throws ApiException Error Information.
	 */
	public int sendDeleteToServer(String hostUrl) throws ApiException {
		HttpURLConnection con = null;

		try {
			URL url = new URL(hostUrl);
			con = (HttpURLConnection)url.openConnection();
			con.setDoInput(true);
			con.setRequestMethod("DELETE");

			con.getInputStream(); // Ensure we try to read something
			return con.getResponseCode();
		} 
		catch (Exception ex) {
			if (con != null) {
					try {
						throw new ApiException(con.getResponseCode(), getErrorMessage(con));
					} catch (IOException e) {
						throw new ApiException(999, e.getMessage());
					}
			}
			else {
				throw new ApiException(999, "Error connecting to server");
			}
		}
	}

	
	
	private String extractContent(HttpURLConnection con) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(con
				.getInputStream()));
		String line = null;
		StringBuffer response = new StringBuffer();

		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		return response.toString();
	}

	private String getErrorMessage(HttpURLConnection con) throws IOException {
		StringBuffer errStream = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(con
				.getErrorStream()));
		String errres;
		while ((errres = in.readLine()) != null) {
			errStream.append(errres);
		}
		return (errStream.toString());
	}

}
