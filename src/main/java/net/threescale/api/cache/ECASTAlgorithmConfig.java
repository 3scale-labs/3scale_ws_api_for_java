package net.threescale.api.cache;

import org.jboss.cache.config.EvictionAlgorithmConfig;
import org.jboss.cache.eviction.EvictionAlgorithmConfigBase;
import org.jboss.cache.eviction.ExpirationAlgorithmConfig;


public class ECASTAlgorithmConfig extends ExpirationAlgorithmConfig {

    private ApiCache api_cache;

    public ECASTAlgorithmConfig(ApiCache cache) {
        this.api_cache = cache;
    }

    @Override
    public String getEvictionAlgorithmClassName() {
        return ECASTAlgorithm.class.getName(); 
    }

    public ApiCache getApiCache() {
        return api_cache;
    }
}
