package threescale.v3.api.impl;

import threescale.v3.api.*;

import java.util.Set;

/**
 * User: geoffd
 * Date: 18/02/2013
 */
public class ClientDriver implements Client {

    private String provider_key = null;
    private String host = DEFAULT_HOST;

    private HtmlClient server = new RemoteDriver(getHost());

    public ClientDriver() {
        this.server = new RemoteDriver(getHost());

    }

    public ClientDriver(String provider_key) {
        this.provider_key = provider_key;
        this.server = new RemoteDriver(getHost());
    }

    public ClientDriver(String provider_key, String host) {
        this.provider_key = provider_key;
        this.host = host;
        this.server = new RemoteDriver(getHost());
    }

    public AuthorizeResponse authrep(ParameterMap metrics) throws ServerError {
        metrics.add("provider_key", provider_key);

        ParameterMap usage = metrics.getMapValue("usage");

        if (usage == null || usage.getStringValue("hits") == null) {
            if (usage == null) {
                usage = new ParameterMap();
                metrics.add("usage", usage);
            }
            usage.add("hits", "1");
        }
        String urlParams = encodeAsString(metrics, null);

        final String s = "http://" + getHost() + "/transactions/authrep.xml?" + urlParams;
//        System.out.println("Actual: " + s);

        HtmlResponse response = server.get(s);
        return convertXmlToAuthorizeResponse(response);
    }

    public ReportResponse report(ParameterMap... transactions) {
        transactions[0].add("provider_key", provider_key);
        return null;
    }

    public AuthorizeResponse authorize(ParameterMap parameters) throws ServerError {
        parameters.add("provider_key", provider_key);
        return null;
    }

    public String getHost() {
        return host;
    }

    public AuthorizeResponse oauth_authorize(ParameterMap params) throws ServerError {
        params.add("provider_key", provider_key);
        return null;
    }


    public String encodeAsString(ParameterMap params, String prefix) {
        boolean first = true;
        StringBuffer result = new StringBuffer();
        for (String key : params.getKeys()) {
            if (params.getType(key) == ParameterMap.STRING) {
                if (first) {
                    first = false;
                } else {
                    if (prefix == null) {
                        result.append("&");
                    }
                }
                if (prefix == null) {
                    result.append(key).append("=").append(params.getStringValue(key));
                } else {
                    result.append("&[").append(prefix).append("]");
                    result.append("[").append(key).append("]").append("=").append(params.getStringValue(key));
                }
            } else if (params.getType(key) == ParameterMap.MAP) {
                result.append(encodeAsString(params.getMapValue(key), key));
            }
        }

        return result.toString();
    }


    private AuthorizeResponse convertXmlToAuthorizeResponse(HtmlResponse res) {
        return new AuthorizeResponse();
    }

    public ClientDriver setServer(HtmlClient server) {
        this.server = server;
        return this;
    }
}
