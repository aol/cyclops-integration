package cyclops.collections.adt;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImmutableHashMap<K,V> {
    HAMT.Node<K,V> map;

    public static <K,V> ImmutableHashMap<K,V> empty(){
        return new ImmutableHashMap<>(HAMT.empty());
    }
    public int size(){
        return map.size();
    }

    public ImmutableHashMap<K,V> put(K key, V value){
        return new ImmutableHashMap<K,V>(map.plus(0,key.hashCode(),key,value));
    }
}
