package cyclops.collections.adt;


import com.aol.cyclops2.types.foldable.Evaluation;
import cyclops.collections.immutable.VectorX;
import cyclops.stream.ReactiveSeq;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor
public class Vector<T> {
    private final BAMT.NestedArray<T> root;
    private final BAMT.ActiveTail<T> tail;
    private final int size;

    public static <T> Vector<T> empty(){
        return new Vector<>(new BAMT.Zero<>(),BAMT.ActiveTail.emptyTail(),0);
    }
    public static <T> Vector<T> fromIterable(Iterable<T> it){
        Vector<T> res = empty();
        for(T next : it){
            res = res.plus(next);
        }
        return res;
    }

    public VectorX<T> vectorX(){
        return stream().to().vectorX(Evaluation.LAZY);
    }
    public ReactiveSeq<T> stream(){
        return ReactiveSeq.concat(root.stream(),tail.stream());
    }

    public Vector<T> filter(Predicate<? super T> pred){
        return fromIterable(stream().filter(pred));
    }

    public <R> Vector<R> map(Function<? super T, ? extends R> fn){
        return fromIterable(stream().map(fn));
    }

    public <R> Vector<R> flatMap(Function<? super T, ? extends Vector<? extends R>> fn){
        return fromIterable(stream().flatMap(fn.andThen(Vector::stream)));
    }

    public Vector<T> set(int pos, T value){
        if(pos<0||pos>=size){
            return this;
        }
        int tailStart = size-tail.size();
        if(pos>=tailStart){
            return new Vector<T>(root,tail.set(pos-tailStart,value),size);
        }
        return new Vector<>(root.match(z->z,p->p.set(pos,value)),tail,size);
    }

    public int size(){
        return size;
    }
    public Vector<T> plus(T t){
        if(tail.size()<32) {
            return new Vector<T>(root,tail.append(t),size+1);
        }else{
            return new Vector<T>(root.append(tail),BAMT.ActiveTail.tail(t),size+1);
        }
    }

    public Optional<T> get(int pos){
        if(pos<0||pos>=size){
            return Optional.empty();
        }
        int tailStart = size-tail.size();
        if(pos>=tailStart){
            return tail.get(pos-tailStart);
        }
        return ((BAMT.PopulatedArray<T>)root).get(pos);

    }
}
