package net.threescale.api.v2;

/**
 * Reponse returned for Http Request.
 */
public class ApiHttpResponse {
    
    private final int responseCode;
    private final String responseText;
    private final String contentType;

    public ApiHttpResponse(int responseCode, String responseText, String contentType) {
        this.responseCode = responseCode;
        this.responseText = responseText;
        this.contentType = contentType;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public String getContentType() {
        return contentType;
    }
}
