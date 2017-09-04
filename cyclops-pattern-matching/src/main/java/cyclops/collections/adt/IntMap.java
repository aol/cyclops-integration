package cyclops.collections.adt;


import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntMap<T> {

    private static final int STARTING_OFFSET = Integer.MAX_VALUE/2;
    private final IntPatriciaTrie.Node<T> intMap;
    private final int size;
    private final int offset;

    public static <T> IntMap<T> of(T... values){
        IntPatriciaTrie.Node<T> tree = IntPatriciaTrie.empty();
        for(int i=0;i<values.length;i++){
            tree = tree.put(i+STARTING_OFFSET,i+STARTING_OFFSET,values[i]);
        }
        return new IntMap<T>(tree,values.length,+STARTING_OFFSET);
    }
    public IntMap<T> plus(T value){
        return new IntMap<>(intMap.put(size+offset,size+offset,value),size+1,offset);
    }
    public IntMap<T> prepend(T value){
        return new IntMap<>(intMap.put(offset-1,offset-1,value),size+1,offset-1);
    }

    public Optional<T> get(int index){
        return intMap.get(index+offset,index+offset);
    }
    public T getOrElse(int index,T value){
        return intMap.getOrElse(index+offset,index+offset,value);
    }

    int calcSize(){
        return intMap.size();
    }
    public int size(){
        return size;
    }

    public ReactiveSeq<T> stream(){
        return intMap.stream();
    }

}

