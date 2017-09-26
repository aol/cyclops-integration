package cyclops.data;


import com.aol.cyclops2.types.foldable.Evaluation;
import cyclops.collections.immutable.VectorX;
import cyclops.control.Maybe;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntMap<T> implements ImmutableList<T>{


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

    public Maybe<T> get(int index){
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

    class IntMapSome extends IntMap<T> implements ImmutableList.Some<T>{

        public IntMapSome(IntMap<T> vec) {
            super(vec.intMap, vec.size);
        }

        @Override
        public ImmutableList<T> tail() {

            return drop(1);
        }

        @Override
        public T head() {
            return getOrElse(0,null);
        }

        @Override
        public Some<T> reverse() {
            ImmutableList<T> vec = IntMap.this.reverse();
            Vector<T> rev = (Vector<T>)vec;
            return rev.new VectorSome(rev);
        }

        @Override
        public Tuple2<T, ImmutableList<T>> unapply() {
            return Tuple.tuple(head(),tail());
        }
    }

}

