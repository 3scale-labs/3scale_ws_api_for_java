package net.threescale.api;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 26-Jul-2010
 * Time: 23:51:16
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
