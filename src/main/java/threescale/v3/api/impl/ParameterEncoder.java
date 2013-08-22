package threescale.v3.api.impl;

import threescale.v3.api.ParameterMap;

/**
 * Encodes a ParameterMap as a string suitable for sending as part of an HTML request.
 */
public class ParameterEncoder {

    /**
     * Takes the parameter map and returns an encoded string.
     *
     * @param params Parameter map to encode
     * @return Encoded string
     */
    public String encode(ParameterMap params) {
        StringBuffer result = new StringBuffer();

        int index = 0;
        for (String mapKey : params.getKeys()) {
            if (index != 0) result.append("&");
            switch (params.getType(mapKey)) {
                case STRING:
                    result.append(emitNormalValue(mapKey, params.getStringValue(mapKey)));
                    break;
                case MAP:
                    result.append(emitNormalMap(mapKey, params.getMapValue(mapKey)));
                    break;
                case ARRAY:
                    result.append(emitNormalArray(mapKey, params.getArrayValue(mapKey)));
                    break;
            }
            index++;
        }

        return substitueCharacters(result.toString());
    }

    private String emitNormalArray(String mapKey, ParameterMap[] mapValue) {
        StringBuffer b = new StringBuffer();
        int index = 0;

        for (ParameterMap arrayMap : mapValue) {
            if (index != 0) b.append("&");
            b.append(emitArray(mapKey, arrayMap, index));
            index++;
        }
        return b.toString();
    }

    private String emitArray(String mapKey, ParameterMap arrayMap, int arrayIndex) {
        StringBuffer b = new StringBuffer();
        int index = 0;

        for (String key : arrayMap.getKeys()) {
            switch (arrayMap.getType(key)) {
                case STRING:
                    if (index != 0) b.append("&");
                    b.append(mapKey).append("[").append(arrayIndex).append("]");
                    b.append("[").append(key).append("]=").append(arrayMap.getStringValue(key));
                    index++;
                    break;
                case MAP:
                    ParameterMap map = arrayMap.getMapValue(key);
                    for (String itemKey : map.getKeys()) {
                        if (index != 0) b.append("&");
                        b.append(emitArrayValue(mapKey, key, itemKey, map.getStringValue(itemKey), arrayIndex));
                        index++;
                    }
            }
        }
        return b.toString();
    }

    private String emitArrayValue(String mapKey, String key, String itemKey, String stringValue, int index) {
        StringBuffer b = new StringBuffer();
        b.append(mapKey).append("[").append(index).append("]");
        b.append("[").append(key).append("]");
        b.append("[").append(itemKey).append("]=").append(stringValue);
        return b.toString();
    }

    private String emitNormalMap(String mapKey, ParameterMap mapValue) {
        StringBuffer b = new StringBuffer();
        int index = 0;
        for (String key : mapValue.getKeys()) {
            if (index != 0) b.append("&");
            switch (mapValue.getType(key)) {
                case STRING:
                    b.append(emitMapValue(mapKey, key, mapValue.getStringValue(key)));
                    break;
            }
            index++;
        }
        return b.toString();
    }

    private String emitMapValue(String mapKey, String key, String stringValue) {
        StringBuffer b = new StringBuffer();
        b.append("[").append(mapKey).append("][").append(key).append("]=").append(stringValue);
        return b.toString();
    }

    private String emitNormalValue(String key, String value) {
        StringBuffer b = new StringBuffer();
        b.append(key).append("=").append(value);
        return b.toString();
    }

    private String substitueCharacters(String input) {
        return input.replace(" ", "%20").replace("[", "%5B").replace("]", "%5D").replace("#", "%23").replace(":", "%3A");
    }
}
