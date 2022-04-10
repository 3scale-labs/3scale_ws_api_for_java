package threescale.v3.api.impl;

import threescale.v3.api.*;

/**
 * Concrete implementation of the ServiceApi.
 *
 * @see ServiceApi
 */
public class ServiceApiDriver implements ServiceApi {
	
	/**
	 * Creates a Service Api with default settings.  This will communicate with the 3scale
	 * platform SaaS default server.
	 */
	public static ServiceApi createApi() {
		return ServiceApiDriver.createApi(ServiceApi.DEFAULT_HOST, 443, true);
	}
	
	/**
	 * Creates a Service Api for the given host.  Use this method when connecting to an on-premise
	 * instance of the 3scale platform.
	 */
	public static ServiceApi createApi(String host, int port, boolean useHttps) {
		ServiceApiDriver driver = new ServiceApiDriver();
		driver.host = host + ":" + port;
		driver.useHttps = useHttps;
		return driver;
	}

    private String provider_key = null;
    private String host = DEFAULT_HOST;
    private boolean useHttps = false;
//    private String redirect_url = "http://localhost:8080/oauth/oauth_redirect";

    private ServerAccessor server = null;

    public ServiceApiDriver() {
        this.server = new ServerAccessorDriver();
    }

    /**
     * @deprecated Instead of using a provider_key, use service tokens.  See static constructor methods: createApi().
     */
    @Deprecated
    public ServiceApiDriver(String provider_key) {
        this.provider_key = provider_key;
        this.server = new ServerAccessorDriver();
    }
    
    /**
     * @deprecated Instead of using a provider_key, use service tokens.  See static constructor methods: createApi().
     */
    @Deprecated
    public ServiceApiDriver(String provider_key, boolean useHttps) {
        this.provider_key = provider_key;
        this.useHttps = useHttps;
        this.server = new ServerAccessorDriver();
    }

    /**
     * @deprecated Instead of using a provider_key, use service tokens.  See static constructor methods: createApi().
     */
    @Deprecated
    public ServiceApiDriver(String provider_key, String host) {
        this.provider_key = provider_key;
        this.host = host;
        this.server = new ServerAccessorDriver();
    }
    
    /**
     * @deprecated Instead of using a provider_key, use service tokens.  See static constructor methods: createApi().
     */
    @Deprecated
    public ServiceApiDriver(String provider_key, String host, boolean useHttps) {
        this.provider_key = provider_key;
        this.host = host;
        this.useHttps = useHttps;
        this.server = new ServerAccessorDriver();
    }
    
    /* (non-Javadoc)
     * @see threescale.v3.api.ServiceApi#authrep(threescale.v3.api.ParameterMap)
     */
    public AuthorizeResponse authrep(ParameterMap metrics) throws ServerError {
    	if (this.provider_key != null) {
            metrics.add("provider_key", provider_key);
    	}

        ParameterMap usage = metrics.getMapValue("usage");

        if (usage == null || usage.size()==0) {
            if (usage == null) {
                usage = new ParameterMap();
                metrics.add("usage", usage);
            }
            usage.add("hits", "1");
        }
        String urlParams = encodeAsString(metrics);

        final String s = getFullHostUrl() + "/transactions/authrep.xml?" + urlParams;
//        System.out.println("Actual: " + s);

        HttpResponse response = server.get(s);
        if (response.getStatus() == 500) {
            throw new ServerError(response.getBody());
        }
        return convertXmlToAuthorizeResponse(response);
    }
    
    /* (non-Javadoc)
     * @see threescale.v3.api.ServiceApi#authrep(java.lang.String, java.lang.String, threescale.v3.api.ParameterMap)
     */
    public AuthorizeResponse authrep(String serviceToken, String serviceId, ParameterMap metrics) throws ServerError {
    	if (serviceToken != null) {
    		metrics.add("service_token", serviceToken);
    	}
    	if (serviceId != null) {
    		metrics.add("service_id", serviceId);
    	}
    	return authrep(metrics);
    }
    
    /* (non-Javadoc)
     * @see threescale.v3.api.ServiceApi#report(java.lang.String, java.lang.String, threescale.v3.api.ParameterMap[])
     */
    public ReportResponse report(String serviceToken, String serviceId, ParameterMap... transactions)
    		throws ServerError {
        if (transactions == null || transactions.length == 0){
            throw new IllegalArgumentException("No transactions provided");
        }

        ParameterMap params = new ParameterMap();
        if (this.provider_key != null) {
            params.add("provider_key", provider_key);
        }
        if (serviceToken != null) {
            params.add("service_token", serviceToken);
        }
        if (serviceId != null) {
            params.add("service_id", serviceId);
        }
        params.add("transactions", transactions);

        HttpResponse response = server.post(getFullHostUrl() + "/transactions.xml", encodeAsString(params));
        if (response.getStatus() == 500) {
            throw new ServerError(response.getBody());
        }
        return new ReportResponse(response);
    }
    
    @Override
    public ReportResponse report(String serviceId, ParameterMap... transactions) throws ServerError {
    	return this.report(null, serviceId, transactions);
    }

    /* (non-Javadoc)
     * @see threescale.v3.api.ServiceApi#authorize(threescale.v3.api.ParameterMap)
     */
    public AuthorizeResponse authorize(ParameterMap parameters) throws ServerError {
    	if (this.provider_key != null) {
            parameters.add("provider_key", provider_key);
    	}
        String urlParams = encodeAsString(parameters);

        final String s = getFullHostUrl() + "/transactions/authorize.xml?" + urlParams;
        HttpResponse response = server.get(s);
        if (response.getStatus() == 500) {
            throw new ServerError(response.getBody());
        }
        return convertXmlToAuthorizeResponse(response);
    }
    
    @Override
    public AuthorizeResponse authorize(String serviceToken, String serviceId, ParameterMap parameters)
    		throws ServerError {
    	if (serviceToken != null) {
    		parameters.add("service_token", serviceToken);
    	}
    	if (serviceId != null) {
    		parameters.add("service_id", serviceId);
    	}
    	return authorize(parameters);
    }

    public String getHost() {
        return host;
    }

    /* (non-Javadoc)
     * @see threescale.v3.api.ServiceApi#oauth_authorize(threescale.v3.api.ParameterMap)
     */
    public AuthorizeResponse oauth_authorize(ParameterMap params) throws ServerError {
    	if (this.provider_key != null) {
            params.add("provider_key", provider_key);
    	}
        final String urlParams = encodeAsString(params);

        final String s = getFullHostUrl() + "/transactions/oauth_authorize.xml?" + urlParams;
//        System.out.println("Actual: " + s);

        HttpResponse response = server.get(s);
        if (response.getStatus() == 500) {
            throw new ServerError(response.getBody());
        }
        return convertXmlToAuthorizeResponse(response);
    }
    
    /* (non-Javadoc)
     * @see threescale.v3.api.ServiceApi#oauth_authorize(java.lang.String, java.lang.String, threescale.v3.api.ParameterMap)
     */
    public AuthorizeResponse oauth_authorize(String serviceToken, String serviceId, ParameterMap params)
    		throws ServerError {
    	if (serviceToken != null) {
    		params.add("service_token", serviceToken);
    	}
    	if (serviceId != null) {
    		params.add("service_id", serviceId);
    	}
    	return oauth_authorize(params);
    }

    private String getFullHostUrl() {
        
        return useHttps ? "https://" + getHost() : "http://" + getHost();
    }


    public String encodeAsString(ParameterMap params) {
        ParameterEncoder encoder = new ParameterEncoder();
        return encoder.encode(params);
    }


    private AuthorizeResponse convertXmlToAuthorizeResponse(HttpResponse res) throws ServerError {
        return new AuthorizeResponse(res.getStatus(), res.getBody());
    }


    public ServiceApiDriver setServer(ServerAccessor server) {
        this.server = server;
        return this;
    }
}
