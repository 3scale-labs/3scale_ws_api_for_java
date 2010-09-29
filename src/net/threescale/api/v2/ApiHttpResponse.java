package net.threescale.api.v2;

/**
 * Response returned for Http Request.
 */
public class ApiHttpResponse {
    
    private final int responseCode;
    private final String responseText;

    /**
     * Constructor
     * @param responseCode HTTP Response code
     * @param responseText HTTP Message contents
     */
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
