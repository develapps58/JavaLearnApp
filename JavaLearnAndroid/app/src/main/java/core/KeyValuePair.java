package core;

/**
 * Created by Дмитрий on 09.12.2016.
 */

public class KeyValuePair<K, V> {
    K key;
    V value;

    public KeyValuePair () { }
    public KeyValuePair (K k, V v) {
        key = k;
        value = v;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
