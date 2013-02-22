package threescale.v3.api;

/**
 * User: geoffd
 * Date: 20/02/2013
 */
public class HtmlResponse {

    private int status;
    private String body;

    public HtmlResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }
}
