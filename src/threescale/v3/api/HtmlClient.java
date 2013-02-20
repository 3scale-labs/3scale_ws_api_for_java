package threescale.v3.api;

/**
 * User: geoffd
 * Date: 20/02/2013
 */
public interface HtmlClient {
    public HtmlResponse get(String url);

    public HtmlResponse post(String url, String data);
}
