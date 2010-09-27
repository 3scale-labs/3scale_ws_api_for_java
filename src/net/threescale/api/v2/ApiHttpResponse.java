package net.threescale.api.v2;

/**
 * Reponse returned for Http Request.
 */
public class ApiHttpResponse {
    
    private final int responseCode;
    private final String responseText;

    public ApiHttpResponse(int responseCode, String responseText) {
        this.responseCode = responseCode;
        this.responseText = responseText;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseText() {
        return responseText;
    }

}
