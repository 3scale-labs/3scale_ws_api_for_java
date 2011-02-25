package net.threescale.api.cache;

import net.threescale.api.LogFactory;
import org.jboss.cache.config.EvictionAlgorithmConfig;
import org.jboss.cache.eviction.*;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;


public class ECASTAlgorithm extends BaseEvictionAlgorithm {

    private Logger log = LogFactory.getLogger(this);
    
    private EvictionQueue evictionQueue;

    public ECASTAlgorithm() {
        log.info("ECASTAlgorithm was created");
    }

    @Override
    protected EvictionQueue setupEvictionQueue() throws EvictionException {
        evictionQueue = new LinkedListEvictionQueue();
        return evictionQueue;
    }

    @Override
    protected boolean shouldEvictNode(NodeEntry nodeEntry) {
        return nodeEntry.getCreationTimeStamp() > getEvictionAlgorithmConfig().getApiCache().getCurrentResponseExpirationTime();
    }

    public Class<? extends EvictionAlgorithmConfig> getConfigurationClass() {
        return ECASTAlgorithmConfig.class;
    }

    @Override
    public void process(BlockingQueue<EvictionEvent> eventQueue) throws EvictionException {
        log.info("process was called");
        super.process(eventQueue);
        getEvictionAlgorithmConfig().getApiCache().incrementCurrentResponseExpirationTime();
    }

    public ECASTAlgorithmConfig getEvictionAlgorithmConfig() {
        log.info("getEvictionAlgorithmConfig was called");
        return (ECASTAlgorithmConfig) super.getEvictionAlgorithmConfig();
    }
}
