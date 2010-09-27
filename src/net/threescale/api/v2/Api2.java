package net.threescale.api.v2;

import net.threescale.api.ApiTransaction;

/**
 * Created by IntelliJ IDEA.
 * User: geoffd
 * Date: 05-Sep-2010
 * Time: 22:44:39
 */
public interface Api2 {
    
    ApiResponse authorize(String appKey) throws ApiException;

    void report(ApiTransaction[] transactions) throws ApiException;
}
