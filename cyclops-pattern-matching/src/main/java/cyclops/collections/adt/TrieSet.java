package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrieSet<T> {
    private final HashedPatriciaTrie.Node<T,T> map;


    public static <T> TrieSet<T> of(T... values){
        HashedPatriciaTrie.Node<T, T> tree = HashedPatriciaTrie.empty();
        for(T value : values){
            tree = tree.put(value.hashCode(),value,value);
        }
        return new TrieSet<>(tree);
    }


    public boolean contains(T value){
        return map.get(value.hashCode(),value).isPresent();
    }
    public TrieSet<T> plus(T value){
        return new TrieSet<>(map.put(value.hashCode(),value,value));
    }
    public TrieSet<T> minus(T value){
        return new TrieSet<>(map.minus(value.hashCode(),value));
    }
}
