package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashMap<K,V> {
    HAMT.Node<K,V> rootNode;

    public static <K,V> HashMap<K,V> empty(){
        return new HashMap<>(HAMT.empty());
    }

    public HashMap<K,V> plus(K key, V value){
        return new HashMap<>(rootNode.put(key.hashCode(),key,value));
    }
    public Optional<V> get(K key){
        return rootNode.get(key.hashCode(),key);
    }
    public HashMap<K,V> minus(K key){
        return new HashMap<>(rootNode.minus(key.hashCode(),key));
    }
    public int size(){
        return rootNode.size();
    }
}
