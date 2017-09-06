package cyclops.collections.adt;


import com.aol.cyclops2.types.foldable.Evaluation;
import cyclops.collections.immutable.VectorX;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntMap<T> {


    private final IntPatriciaTrie.Node<T> intMap;
    private final int size;


    public static <T> IntMap<T> of(T... values){
        IntPatriciaTrie.Node<T> tree = IntPatriciaTrie.empty();
        for(int i=0;i<values.length;i++){
            tree = tree.put(i,i,values[i]);
        }
        return new IntMap<T>(tree,values.length);
    }
    public IntMap<T> plus(T value){
        return new IntMap<>(intMap.put(size,size,value),size+1);
    }

    public Optional<T> get(int index){
        return intMap.get(index,index);
    }
    public T getOrElse(int index,T value){
        return intMap.getOrElse(index,index,value);
    }

    int calcSize(){
        return intMap.size();
    }
    public int size(){
        return size;
    }

    public VectorX<T> vectorX(){
        return stream().to().vectorX(Evaluation.LAZY);
    }
    public ReactiveSeq<T> stream(){
        return intMap.stream();
    }

}

