package com.rslakra.appsuite.protocol.http;

import com.rslakra.appsuite.core.BeanUtils;

import java.io.Serializable;

/**
 * Added to handle the key/value pair.
 *
 * @param <K>
 * @param <V>
 * @author Rohtash Lakra
 * @since 04/19/2017 11:12:00 AM
 */
public class Pair<K, V> implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final K key;
    private final V value;

    /**
     * Extracts the key and value from the specified
     * <code>keyValueString</code> based on the equals to (=) sign, if its
     * not null or empty otherwise null.
     *
     * @param keyValueString
     * @return
     */
    public static Pair<String, String> newPair(String keyValueString) {
        Pair<String, String> pair = null;
        if (BeanUtils.isNotEmpty(keyValueString)) {
            int equalIndex = keyValueString.indexOf("=");
            if (equalIndex != -1) {
                String key = keyValueString.substring(0, equalIndex).trim();
                int lastIndex = keyValueString.indexOf(";");
                String value = null;
                if (lastIndex != -1) {
                    value = keyValueString.substring(equalIndex + 1, lastIndex).trim();
                } else {
                    value = keyValueString.substring(equalIndex + 1).trim();
                }
                pair = new Pair<String, String>(key, value);
            }
        }

        return pair;
    }

    /**
     * @param key
     * @param value
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key.
     *
     * @return
     */
    public final K getKey() {
        return key;
    }

    /**
     * Return the value.
     *
     * @return
     */
    public final V getValue() {
        return value;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object object) {
        if (!(object instanceof Pair)) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) object;
        return (getKey().equals(pair.getKey()) && getValue().equals(pair.getValue()));
    }

    /**
     * Returns the hash code of this object.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
    }

    /**
     * Returns the string representation of this object.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return (getKey() + "=" + getValue());
    }
}
