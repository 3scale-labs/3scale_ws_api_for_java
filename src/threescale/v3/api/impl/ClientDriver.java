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

    private String provider_key = null;
    private String host = DEFAULT_HOST;

    public ClientDriver() {
    }

    public ClientDriver(String provider_key) {
        this.provider_key = provider_key;
    }

    public ClientDriver(String provider_key, String host) {
        this.provider_key = provider_key;
        this.host = host;
    }

    public AuthorizeResponse authrep(ParameterMap metrics) {
        metrics.add("provider_key", provider_key);
        return null;
    }

    public ReportResponse report(ParameterMap... transactions) {
        transactions[0].add("provider_key", provider_key);
        return null;
    }

    public AuthorizeResponse authorize(ParameterMap parameters) {
        parameters.add("provider_key", provider_key);
        return null;
    }

    public String getHost() {
        return host;
    }

    public AuthorizeResponse oauth_authorize(ParameterMap params) {
        params.add("provider_key", provider_key);
        return null;
    }
}
