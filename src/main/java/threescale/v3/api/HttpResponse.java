package threescale.v3.api;

/**
 * Returns the result of an Http GET / POST
 */
public class HttpResponse {

    private int status;
    private String body;

    /**
     * construct an HtmlResponse from the Http Status and Body Content
     *
     * @param status Http Status
     * @param body   Http Body
     */
    public HttpResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    /**
     * Return the content
     *
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * Get the Status.
     *
     * @return status.
     */
    public int getStatus() {
        return status;
    }
}
