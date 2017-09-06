package cyclops.collections.adt;


import com.aol.cyclops2.types.foldable.Evaluation;
import cyclops.collections.immutable.VectorX;
import cyclops.stream.ReactiveSeq;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor
public class ImmutableVector<T> {
    private final BAMT.NestedArray<T> root;
    private final BAMT.ActiveTail<T> tail;
    private final int size;

    public static <T> ImmutableVector<T> empty(){
        return new ImmutableVector<>(new BAMT.Zero<>(),BAMT.ActiveTail.emptyTail(),0);
    }
    public static <T> ImmutableVector<T> fromIterable(Iterable<T> it){
        ImmutableVector<T> res = empty();
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

    public ImmutableVector<T> filter(Predicate<? super T> pred){
        return fromIterable(stream().filter(pred));
    }

    public <R> ImmutableVector<R> map(Function<? super T, ? extends R> fn){
        return fromIterable(stream().map(fn));
    }

    public <R> ImmutableVector<R> flatMap(Function<? super T, ? extends ImmutableVector<? extends R>> fn){
        return fromIterable(stream().flatMap(fn.andThen(ImmutableVector::stream)));
    }

    public ImmutableVector<T> set(int pos, T value){
        if(pos<0||pos>=size){
            return this;
        }
        int tailStart = size-tail.size();
        if(pos>=tailStart){
            return new ImmutableVector<T>(root,tail.set(pos-tailStart,value),size);
        }
        return new ImmutableVector<>(root.match(z->z, p->p.set(pos,value)),tail,size);
    }

    public int size(){
        return size;
    }
    public ImmutableVector<T> plus(T t){
        if(tail.size()<32) {
            return new ImmutableVector<T>(root,tail.append(t),size+1);
        }else{
            return new ImmutableVector<T>(root.append(tail),BAMT.ActiveTail.tail(t),size+1);
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
