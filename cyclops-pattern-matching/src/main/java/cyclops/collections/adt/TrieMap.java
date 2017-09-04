package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrieMap<K,V> {
    HashedPatriciaTrie.Node<K,V> rootNode;

    public static <K,V> TrieMap<K,V> empty(){
        return new TrieMap<>(HashedPatriciaTrie.empty());
    }

    public TrieMap<K,V> plus(K key, V value){
        return new TrieMap<>(rootNode.put(key.hashCode(),key,value));
    }
    public Optional<V> get(K key){
        return rootNode.get(key.hashCode(),key);
    }
    public V getOrElse(K key,V alt){
        return rootNode.getOrElse(key.hashCode(),key,alt);
    }
    public TrieMap<K,V> minus(K key){
        return new TrieMap<>(rootNode.minus(key.hashCode(),key));
    }
    public int size(){
        return rootNode.size();
    }
}
