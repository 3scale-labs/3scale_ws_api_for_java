package net.threescale.api.cache;

import net.threescale.api.LogFactory;
import net.threescale.api.v2.ApiTransaction;
import net.threescale.api.v2.ApiUtil;
import org.jboss.cache.Fqn;
import org.jboss.cache.config.EvictionAlgorithmConfig;
import org.jboss.cache.eviction.EvictionEvent;
import org.jboss.cache.eviction.EvictionException;
import org.jboss.cache.eviction.ExpirationAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Cache eviction algorithm that writes evicted records to the server.
 */
public class ECASTAlgorithm extends ExpirationAlgorithm {

    private Logger log = LogFactory.getLogger(this);
    private List<ApiTransaction> transactionsToSend = new ArrayList<ApiTransaction>();

    public ECASTAlgorithm() {
    }

    public Class<? extends EvictionAlgorithmConfig> getConfigurationClass() {
        return ECASTAlgorithmConfig.class;
    }


    /**
     * Evict a node and add it to the server transmission list.
     * @param fqn
     * @return
     */
    @Override
    protected boolean evictCacheNode(Fqn fqn) {
        log.fine("evictCacheNode: " + fqn);
        transactionsToSend.addAll(getEvictionAlgorithmConfig().getApiCache().getTransactionFor(fqn.getLastElementAsString()));
        return super.evictCacheNode(fqn);    //To change body of overridden methods use File | Settings | File Templates.
    }


    /**
     * Call the super classes procees method then send all transactions in the transactionsToSend list to the server.
     * @param eventQueue
     * @throws EvictionException
     */
    @Override
    public void process(BlockingQueue<EvictionEvent> eventQueue) throws EvictionException {
        log.fine("process was called: size " + eventQueue.size());
        super.process(eventQueue);
        getEvictionAlgorithmConfig().getApiCache().incrementCurrentResponseExpirationTime();
        sendTransactions();
    }

    /**
     * Send transactions to the server.
     */
    private void sendTransactions() {
        ECASTAlgorithmConfig config = getEvictionAlgorithmConfig();

        if (transactionsToSend.size() > 0) {
            String data_to_send = ApiUtil.formatPostData(config.getProviderKey(), transactionsToSend);
            config.getSender().sendPostToServer(config.getHost_url(), data_to_send);
            transactionsToSend.clear();
        }
    }

    public ECASTAlgorithmConfig getEvictionAlgorithmConfig() {
        return (ECASTAlgorithmConfig) super.getEvictionAlgorithmConfig();
    }
}
