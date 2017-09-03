package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntMap<T> {

    private final IntPatriciaTrie.Node<T> vector;
    private final int size;

    public static <T> IntMap<T> of(T... values){
        IntPatriciaTrie.Node<T> tree = IntPatriciaTrie.empty();
        for(int i=0;i<values.length;i++){
            tree = tree.put(i,i,values[i]);
        }
        return new IntMap<T>(tree,values.length);
    }
    public IntMap<T> plus(T value){
        return new IntMap<>(vector.put(size,size,value),size+1);
    }

    public Optional<T> get(int index){
        return vector.get(index,index);
    }
    public T getOrElse(int index,T value){
        return vector.getOrElse(index,index,value);
    }

    int calcSize(){
        return vector.size();
    }
    public int size(){
        return size;
    }
}

