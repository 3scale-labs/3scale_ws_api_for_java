package threescale.v3.api.impl;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.Client;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ReportResponse;

/**
 * User: geoffd
 * Date: 18/02/2013
 */
public class ClientDriver implements Client {

    public ClientDriver() {

    }

    public ClientDriver(String provider_key) {

    }

    public ClientDriver(String provider_key, String host) {

    }

    public AuthorizeResponse authrep(ParameterMap metrics) {
        return null;
    }

    public ReportResponse report(ParameterMap... transactions) {
        return null;
    }

    public AuthorizeResponse authorize(ParameterMap parameters) {
        return null;
    }

    public String getHost() {
        return null;
    }

    public AuthorizeResponse oauth_authorize(ParameterMap params) {
        return null;
    }
}
