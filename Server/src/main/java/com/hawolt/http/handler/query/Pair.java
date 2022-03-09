package com.hawolt.http.handler.query;

/**
 * Created: 04/03/2022 18:31
 * Author: Twitter @hawolt
 **/

public class Pair<K, V> {

    public static <K, V> Pair<K, V> of(K k, V v) {
        return new Pair<>(k, v);
    }

    private final K k;
    private final V v;

    private Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public V getV() {
        return v;
    }
}
