/*
 *  This file is part of LeagueLib.
 *  LeagueLib is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  LeagueLib is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with LeagueLib.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.achimala.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BidirectionalMap<K, V> implements Map<K, V> {
    protected HashMap<K, V> kvMap;
    protected HashMap<V, K> vkMap;

    public BidirectionalMap() {
        kvMap = new HashMap<K, V>();
        vkMap = new HashMap<V, K>();
    }

    public BidirectionalMap<V, K> inverse() {
        BidirectionalMap<V, K> inv = new BidirectionalMap<V, K>();
        inv.kvMap = new HashMap<V, K>(vkMap);
        inv.vkMap = new HashMap<K, V>(kvMap);
        return inv;
    }

    @Override
    public V put(K key, V value) {
        vkMap.put(value, key);
        return kvMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(K k : m.keySet())
            put(k, m.get(k));
    }

    @Override
    public V get(Object key) {
        return kvMap.get(key);
    }

    public K getKey(Object value) {
        return vkMap.get(value);
    }

    @Override
    public V remove(Object key) {
        vkMap.remove(get(key));
        return kvMap.remove(key);
    }

    @Override
    public void clear() {
        kvMap.clear();
        vkMap.clear();
    }

    @Override
    public int size() {
        return kvMap.size();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof BidirectionalMap)
            return ((BidirectionalMap)o).kvMap.equals(kvMap);
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return kvMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return kvMap.containsValue(value);
    }

    @Override
    public int hashCode() {
        return kvMap.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return kvMap.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return kvMap.keySet();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return kvMap.entrySet();
    }

    @Override
    public Collection<V> values() {
        return kvMap.values();
    }
}