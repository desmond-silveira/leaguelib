package com.gvaneyck.rtmp.encoding;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * A map of objects with utility methods
 * 
 * @author Gabriel Van Eyck
 */
public class ObjectMap extends HashMap<String, Object> {
    private static final long serialVersionUID = -7957187649383807122L;

    private static Pattern linePattern = Pattern.compile("^", Pattern.MULTILINE);

    /**
     * Convenience for going through object hierarchy
     * 
     * @param key The key of the ObjectMap
     * @return The ObjectMap
     */
    public ObjectMap getMap(String key) {
        return (ObjectMap)get(key);
    }

    /**
     * Convenience for retrieving Strings
     * 
     * @param key The key of the String
     * @return The String
     */
    public String getString(String key) {
    	if (get(key) == null)
    		return "null";
        return get(key).toString();
    }

    /**
     * Convenience for retrieving integers
     * 
     * @param key The key of the integer
     * @return The integer
     */
    public Integer getInt(String key) {
        Object val = get(key);
        if (val == null)
            return null;
        else if (val instanceof Integer)
            return (Integer)val;
        else if (val instanceof Long)
        	return ((Long)val).intValue();
    	else
            return ((Double)val).intValue();
    }

    /**
     * Convenience for retrieving longs
     * 
     * @param key The key of the long
     * @return The long
     */
    public Long getLong(String key) {
        Object val = get(key);
        if (val == null)
            return null;
        else if (val instanceof Integer)
            return ((Integer)val).longValue();
        else if (val instanceof Long)
            return ((Long)val).longValue();
        else
            return ((Double)val).longValue();
    }
    
    /**
     * Convenience for retrieving doubles
     * 
     * @param key The key of the double
     * @return The double
     */
    public Double getDouble(String key) {
        Object val = get(key);
        if (val == null)
            return null;
        else if (val instanceof Double)
            return (Double)val;
        else
            return ((Integer)val).doubleValue();
    }

    /**
     * Convenience for retrieving booleans
     * 
     * @param key The key of the boolean
     * @return The boolean
     */
    public Boolean getBool(String key) {
        return (Boolean)get(key);
    }

    /**
     * Convenience for retrieving object arrays
     * 
     * @param key The key of the object array
     * @return The object array
     */
    public Object[] getArray(String key) {
        return (Object[])get(key);
    }

    /**
     * Convenience for retrieving Date objects
     * 
     * @param key The key of the Date object
     * @return The Date object
     */
    public Date getDate(String key) {
        return (Date)get(key);
    }
    
    /**
     * Makes a pretty (indented) human readable form of this object
     * 
     * @return A pretty string
     */
    public String toPrettyString() {
        String[] keys = keySet().toArray(new String[0]);
        if (keys.length == 0)
            return "{ }\n";

        StringBuilder buff = new StringBuilder();

        buff.append("{\n");
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];

            if (get(key) instanceof Object[])
            	buff.append(key + '=' + indent(arrayToString((Object[])get(key))));
            else if (get(key) == null) {
                buff.append("    ");
                buff.append(key);
                buff.append("=null");
            }
            else if (get(key) instanceof Double) {
                buff.append("    ");
                buff.append(key);
                buff.append('=');
                buff.append(((Double)get(key)).longValue());
            }
            else if (get(key) instanceof ObjectMap)
            	buff.append(indent(key + '=' + ((ObjectMap)get(key)).toPrettyString()));
            else
                buff.append(indent(key + '=' + get(key).toString()));

            if (i < keys.length - 1)
                buff.append(",\n");
        }
        buff.append("\n}\n");

        return buff.toString();
    }

    /**
     * Turns an array into a pretty string
     * 
     * @param array The array to transform
     * @return A pretty string
     */
    private String arrayToString(Object[] array) {
        if (array.length == 0)
            return "[ ]\n";

        StringBuilder buff = new StringBuilder();

        buff.append("[\n");
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null)
                buff.append("    null");
            else if (array[i] instanceof TypedObject)
            	buff.append(indent(((TypedObject)array[i]).toPrettyString()));
            else
                buff.append(indent(array[i].toString()));

            if (i < array.length - 1)
                buff.append(",\n");
        }
        buff.append("\n]\n");

        return buff.toString();
    }

    /**
     * Indents some text
     * 
     * @param data The text to indent
     * @return Indented text
     */
    private String indent(String data) {
        return linePattern.matcher(data.trim()).replaceAll("    ");
    }
}
