package net.threescale.api.v2;

import net.threescale.api.*;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 05-Sep-2010
 * Time: 22:54:49
 */
public class Api2Impl implements Api2 {
    public Api2Impl(String hostUrl) {
    }

    public ApiResponse authorize(String appId, String providerKey, String appKey) {
        return new ApiResponse();
    }

    public void report(String appId, String providerKey, net.threescale.api.ApiTransaction[] transactions) throws ApiException {

    }
}
