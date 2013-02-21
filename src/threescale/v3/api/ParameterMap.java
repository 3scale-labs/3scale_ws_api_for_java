package threescale.v3.api;

import java.util.HashMap;

/**
 * User: geoffd
 * Date: 18/02/2013
 */
public class ParameterMap {

    private HashMap<String, Object> data;

    public ParameterMap() {
        data = new HashMap<String, Object>();
    }

    public void add(String key, String value) {
        data.put(key, value);
    }

    public void add(String key, ParameterMap map) {
        data.put(key, map);
    }
}
