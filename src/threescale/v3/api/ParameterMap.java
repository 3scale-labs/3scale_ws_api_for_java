package threescale.v3.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * User: geoffd
 * Date: 18/02/2013
 */
public class ParameterMap {

    public static final String STRING = "string";
    public static final String MAP = "map";

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

    public Set<String> getKeys() {
        return data.keySet();
    }

    public String getType(String key) {
        Class clazz = data.get(key).getClass();
        if (clazz == String.class) {
            return STRING;
        }
        if (clazz == ParameterMap.class) {
            return MAP;
        }
        throw new RuntimeException("Unknown object in parameters");
    }

    public String getStringValue(String key) {
        return (String) data.get(key);
    }

    public ParameterMap getMapValue(String key) {
        return (ParameterMap) data.get(key);
    }
}
