package net.threescale.api.v2;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 05-Sep-2010
 * Time: 22:54:49
 */
public class Api2Impl implements Api2 {
    public Api2Impl(String hostUrl, String app_id, String provider_key) {
    }

    public ApiResponse authorize(String appKey) {
        return new ApiResponse();
    }

    public void report(net.threescale.api.ApiTransaction[] transactions) throws ApiException {

    }
}
