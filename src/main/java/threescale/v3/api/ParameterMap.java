package threescale.v3.api;

import java.util.HashMap;
import java.util.Set;

/**
 * Hold a set of parameter and metrics for an AuthRep, Authorize, OAuth Authorize or Report.
 * <p/>
 * Each item consists of a name/value pair, where the value can be a String, An Array of ParameterMaps or another Parameter Map.
 * <p/>
 * E.g.  For an AuthRep:
 * <code>
 * ParameterMap params = new ParameterMap();
 * params.add("app_id", "app_1234");
 * <p/>
 * ParameterMap usage = new ParameterMap();
 * usage.add("hits", "3");
 * <p/>
 * params.add("usage", usage);
 * <p/>
 * AuthorizeResponse response = serviceApi.authrep(params);
 * </code>
 * <p/>
 * An example for a report might be:
 * <code>
 * ParameterMap params = new ParameterMap();
 * params.add("app_id", "foo");
 * params.add("timestamp", fmt.print(new DateTime(2010, 4, 27, 15, 0)));
 * <p/>
 * ParameterMap usage = new ParameterMap();
 * usage.add("hits", "1");
 * params.add("usage", usage);
 * <p/>
 * ReportResponse response = serviceApi.report(params);
 * </code>
 */
public class ParameterMap {

    private HashMap<String, Object> data;

    /**
     * Construct and empty ParameterMap
     */
    public ParameterMap() {
        data = new HashMap<String, Object>();
    }

    /**
     * Add a string value
     *
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        data.put(key, value);
    }

    /**
     * Add another ParameterMap
     *
     * @param key
     * @param map
     */
    public void add(String key, ParameterMap map) {
        data.put(key, map);
    }

    /**
     * Add an array of parameter maps
     *
     * @param key
     * @param array
     */
    public void add(String key, ParameterMap[] array) {
        data.put(key, array);
    }

    /**
     * Return the keys in a ParameterMap
     *
     * @return
     */
    public Set<String> getKeys() {
        return data.keySet();
    }

    /**
     * Get the type of data item associated with the key
     *
     * @param key
     * @return STRING, MAP, ARRAY
     */
    public ParameterMapType getType(String key) {
        Class<?> clazz = data.get(key).getClass();
        if (clazz == String.class) {
            return ParameterMapType.STRING;
        }
        if (clazz == ParameterMap[].class) {
            return ParameterMapType.ARRAY;
        }
        if (clazz == ParameterMap.class) {
            return ParameterMapType.MAP;
        }
        throw new RuntimeException("Unknown object in parameters");
    }

    /**
     * Get the String associated with a key
     *
     * @param key
     * @return
     */
    public String getStringValue(String key) {
        return (String) data.get(key);
    }

    /**
     * Get the map associated with a key
     *
     * @param key
     * @return
     */
    public ParameterMap getMapValue(String key) {
        return (ParameterMap) data.get(key);
    }

    /**
     * Get the array associated with a key.
     *
     * @param key
     * @return
     */
    public ParameterMap[] getArrayValue(String key) {
        return (ParameterMap[]) data.get(key);
    }

    /**
     * Return the number of elements in the map.
     *
     * @return
     */
    public int size() {
        return data.size();
    }
}
