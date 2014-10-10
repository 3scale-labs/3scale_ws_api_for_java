package threescale.v3.api;

/**
 * Interface to HTML operation between this client and the 3Scale Server
 */
public interface ServerAccessor {
	String X_3SCALE_USER_CLIENT_HEADER = "X-3scale-User-Agent";
	String MAVEN_PROJECT_VERSION = "MAVEN_PROJECT_VERSION";
	String X_3SCALE_USER_CLIENT_HEADER_JAVA_PLUGIN = "plugin-java-v";
	
    /**
     * Perform and HTML GET with the provided URL
     *
     * @param url The URL and parameters to be sent
     * @return Status and content as a HtmlResponse
     * @throws ServerError If there are problems connection tp the server. 
     */
    public HttpResponse get(String url) throws ServerError;

    /**
     * Perform and HTML POST with the provided URL and form data
     *
     * @param url  The URl to contact
     * @param data The data to be sent
     * @return Status and content as a HtmlResponse
     * @throws ServerError If there are problems connection tp the server.
     */
    public HttpResponse post(String url, String data) throws ServerError;
}
