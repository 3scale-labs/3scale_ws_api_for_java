package net.threescale.api.cache;

import net.threescale.api.v2.HttpSender;
import org.jboss.cache.eviction.ExpirationAlgorithmConfig;


public class ECASTAlgorithmConfig extends ExpirationAlgorithmConfig {

    private final ApiCache api_cache;
    private final String host_url;
    private final String provider_key;
    private final HttpSender sender;

    public ECASTAlgorithmConfig(ApiCache cache, String host_url, String provider_id, HttpSender sender) {
        this.api_cache = cache;
        this.host_url = host_url;
        this.provider_key = provider_id;
        this.sender = sender;
    }

    @Override
    public String getEvictionAlgorithmClassName() {
        return ECASTAlgorithm.class.getName();
    }

    public ApiCache getApiCache() {
        return api_cache;
    }

    public String getProviderKey() {
        return provider_key;
    }

    public String getHost_url() {
        return host_url;
    }

    public HttpSender getSender() {
        return sender;
    }
}
