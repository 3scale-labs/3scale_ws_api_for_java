package net.threescale.api.cache;

import org.jboss.cache.Fqn;
import org.jboss.cache.eviction.EvictionQueue;
import org.jboss.cache.eviction.NodeEntry;


import java.util.Iterator;
import java.util.LinkedList;


public class LinkedListEvictionQueue implements EvictionQueue{

    private LinkedList<NodeEntry> list = new LinkedList<NodeEntry>();

    public NodeEntry getFirstNodeEntry() {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.getFirst();
        }
    }

    public NodeEntry getNodeEntry(Fqn fqn) {
        for (NodeEntry nodeEntry : list) {
            if (nodeEntry.getFqn() == fqn) {
                return nodeEntry;
            }
        }
        return null;
    }

    public NodeEntry getNodeEntry(String s) {
        for (NodeEntry nodeEntry : list) {
            if (nodeEntry.getFqn().toString().equals(s)) {
                return nodeEntry;
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean containsNodeEntry(NodeEntry nodeEntry) {
        return list.contains(nodeEntry);
    }

    public void removeNodeEntry(NodeEntry nodeEntry) {
        list.remove(nodeEntry);
    }

    public void addNodeEntry(NodeEntry nodeEntry) {
        list.add(nodeEntry);
    }

    public int getNumberOfNodes() {
        return list.size();
    }

    public int getNumberOfElements() {
        return list.size();
    }

    public void modifyElementCount(int i) {
        throw new RuntimeException("modifyElementCount is not implemented");
    }

    public void clear() {
        list.clear();
    }

    public Iterator<NodeEntry> iterator() {
        return list.iterator();
    }
}
