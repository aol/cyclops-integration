package cyclops.collections.adt;

import lombok.AllArgsConstructor;

import java.util.Optional;

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
    public Optional<V> get(K key){
        return map.get(0,key.hashCode(),key);
    }
    public ImmutableHashMap<K,V> minus(K key){
        return new ImmutableHashMap<K,V>(map.minus(0,key.hashCode(),key));
    }
}
