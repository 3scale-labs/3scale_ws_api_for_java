package threescale.v3.api.impl;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import threescale.v3.api.*;

import nu.xom.Builder;

import java.io.IOException;

/**
 * User: geoffd
 * Date: 18/02/2013
 */
public class ClientDriver implements Client {

    private String provider_key = null;
    private String host = DEFAULT_HOST;
    private String redirect_url = "http://localhost:8080/oauth/oauth_redirect";

    private HtmlClient server = null;

    public ClientDriver() {
        this.server = new RemoteDriver();

    }

    public ClientDriver(String provider_key) {
        this.provider_key = provider_key;
        this.server = new RemoteDriver();
    }

    public ClientDriver(String provider_key, String host) {
        this.provider_key = provider_key;
        this.host = host;
        this.server = new RemoteDriver();
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

        final String s = getFullHostUrl() + "/transactions/authrep.xml?" + urlParams;
//        System.out.println("Actual: " + s);

        HtmlResponse response = server.get(s);
        if (response.getStatus() == 500) {
            throw new ServerError(response.getBody());
        }
        return convertXmlToAuthorizeResponse(response);
    }

    public ReportResponse report(ParameterMap... transactions) throws ServerError {
        if (transactions == null || transactions.length == 0)
            throw new IllegalArgumentException("No transactions provided");

        ParameterMap params = new ParameterMap();
        params.add("provider_key", provider_key);
        ParameterMap trans = new ParameterMap();
        params.add("transactions", trans);

        int index = 0;
        for (ParameterMap transaction : transactions) {
            trans.add("" + index, transaction);
            index++;
        }

        HtmlResponse response = server.post(getFullHostUrl() + "/transactions.xml", encodeAsString(params, null));
        if (response.getStatus() == 500) {
            throw new ServerError(response.getBody());
        }
        return new ReportResponse(response);
    }

    public AuthorizeResponse authorize(ParameterMap parameters) throws ServerError {
        parameters.add("provider_key", provider_key);
        String urlParams = encodeAsString(parameters, null);

        final String s = getFullHostUrl() + "/transactions/authorize.xml?" + urlParams;
        HtmlResponse response = server.get(s);
        if (response.getStatus() == 500) {
            throw new ServerError(response.getBody());
        }
        return convertXmlToAuthorizeResponse(response);
    }

    public String getHost() {
        return host;
    }

    public AuthorizeResponse oauth_authorize(ParameterMap params) throws ServerError {
        params.add("provider_key", provider_key);

        String urlParams = encodeAsString(params, null);

        final String s = getFullHostUrl() + "/transactions/oauth_authorize.xml?" + urlParams;
//        System.out.println("Actual: " + s);

        HtmlResponse response = server.get(s);
        if (response.getStatus() == 500) {
            throw new ServerError(response.getBody());
        }
        return convertXmlToAuthorizeResponse(response);
    }

    private String getFullHostUrl() {
        return "http://" + getHost();
    }


    public String encodeAsString(ParameterMap params, String prefix) {
        StringBuffer result = new StringBuffer();
        for (String key : params.getKeys()) {
            if (params.getType(key) == ParameterMap.STRING) {
                processString(params, key, result);
            } else if (params.getType(key) == ParameterMap.MAP) {
                processMap(params, key, result);
            } else if (params.getType(key) == ParameterMap.ARRAY) {
                processArray(params, key, result);
            }
        }
        return result.toString();
    }

    private void processString(ParameterMap params, String key, StringBuffer result) {
        appendAmpIfFirstParameter(result);
        result.append(key).append("=").append(params.getStringValue(key));
    }


    private void processMap(ParameterMap params, String mapKey, StringBuffer result) {
        ParameterMap map = params.getMapValue(mapKey);
        boolean first = true;
        int index = 0;
        for (String key : map.getKeys()) {
            if (map.getType(key) == ParameterMap.STRING) {
                appendAmpIfFirstParameter(result);
                result.append("[").append(mapKey).append("]").append("[").append(key).append("]=").append(map.getStringValue(key));
            } else if (map.getType(key) == ParameterMap.MAP) {
                processMap(map, key, result);
            } else if (map.getType(key) == ParameterMap.ARRAY) {
                processArray(map, key, result);
            }
            index++;
        }
    }

    private void processArray(ParameterMap params, String mapKey, StringBuffer result) {
        ParameterMap[] array = params.getArrayValue(mapKey);
        int index = 0;
        for (ParameterMap parameterMap : array) {
            result.append(mapKey).append("[").append(index).append("]");
            for (String key : parameterMap.getKeys()) {
                if (parameterMap.getType(key) == ParameterMap.STRING) {
                    result.append("[").append(key).append("]=").append(parameterMap.getStringValue(key));
                }
                if (parameterMap.getType(key) == ParameterMap.MAP) {

                }
            }
            index++;
        }
    }

    private boolean appendAmpIfFirstParameter(StringBuffer result) {
        if (result.length() != 0) {
            result.append("&");
            return true;
        }
        return false;
    }


    private AuthorizeResponse convertXmlToAuthorizeResponse(HtmlResponse res) throws ServerError {
        return new AuthorizeResponse(res.getStatus(), res.getBody());
    }


    public ClientDriver setServer(HtmlClient server) {
        this.server = server;
        return this;
    }
}
