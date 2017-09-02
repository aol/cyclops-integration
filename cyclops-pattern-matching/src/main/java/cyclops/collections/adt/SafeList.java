package cyclops.collections.adt;


import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.functor.Transformable;
import cyclops.collections.immutable.LinkedListX;
import cyclops.control.Trampoline;
import cyclops.function.Monoid;
import cyclops.patterns.CaseClass2;
import cyclops.patterns.Sealed2;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

//safe list implementation that does not support exceptional states
public interface SafeList<T> extends Sealed2<SafeList.Cons<T>,SafeList.Nil>, Folds<T>, Filters<T>, Transformable<T> {

    default ReactiveSeq<T> stream(){
        return ReactiveSeq.fromIterable(iterable());
    }
    default LinkedListX<T> linkedListX(){
        return LinkedListX.fromIterable(iterable());
    }

    static <T> SafeList<T> of(T... value){
        SafeList<T> result = empty();
        for(int i=value.length;i>0;i--){
            result = result.prepend(value[i-1]);
        }
        return result;
    }
    static <T> SafeList<T> fromIterator(Iterator<T> it){
        List<T> values = new ArrayList<>();
        while(it.hasNext()){
          values.add(it.next());
        }
        SafeList<T> result = empty();
        for(int i=values.size();i>0;i--){
            result = result.prepend(values.get(i-1));
        }
        return result;
    }
    static <T> SafeList<T> fromStream(Stream<T> stream){
        Iterator<T> t = stream.iterator();
       return t.hasNext() ? cons(t.next(),fromIterator(t)) : empty();
    }
    static <T> SafeList<T> empty(){
        return Nil.Instance;
    }

    default Optional<T> get(final int pos){
        T result = null;
        SafeList<T> l = this;
        for(int i=0;i<pos;i++){
           l = l.match(c->c.tail,n->n);
           if(l instanceof Nil){ //short circuit
               return Optional.empty();
           }
        }
        return Optional.ofNullable(l.match(c->c.head,n->null));
    }
    default SafeList<T> prepend(T value){
        return cons(value,this);
    }
    default SafeList<T> prependAll(SafeList<T> value){
        return value.match(cons->
                        cons.foldRight(this,(a,b)->b.prepend(a))
                ,nil->this);
    }

    default SafeList<T> take(final int num) {
        if( num <= 0)
           return Nil.Instance;
        if(num<1000) {
            return this.match(cons -> cons(cons.head, cons.take(num - 1)), nil -> nil);
        }
        return fromStream(ReactiveSeq.fromIterable(this.iterable()).take(num));

    }
    default SafeList<T> drop(final int num) {
        SafeList<T> current = this;
        int pos = num;
        while (pos-- > 0 && !current.isEmpty()) {
            current = current.match(c->c.tail,nil->nil);
        }
        return current;
    }
    default SafeList<T> reverse() {
        SafeList<T> res = empty();
        for (T a : iterable()) {
            res = res.prepend(a);
        }
        return res;
    }
    default Iterable<T> iterable(){
        return ()->new Iterator<T>() {
            SafeList<T> current= SafeList.this;
            @Override
            public boolean hasNext() {
                return current.match(c->true,n->false);
            }

            @Override
            public T next() {
                return current.match(c->{
                    current = c.tail;
                    return c.head;
                },n->null);
            }
        };
    }
    default  T foldRight(Monoid<T> m){
        return foldRight(m.zero(),(T a,T b)->m.apply(a,b));
    }
    default  T foldLeft(Monoid<T> m){
        return foldLeft(m.zero(),(T a,T b)->m.apply(a,b));
    }
    <R> R foldRight(R zero, BiFunction<? super T, ? super R, ? extends R> f);

    default <R> R foldLeft(R zero, BiFunction< R, ? super T,  R> f){
        R acc= zero;
        for(T next : iterable()){
            acc= f.apply(acc,next);
        }
        return acc;
    }
    default SafeList<T> filter(Predicate<? super T> pred){
        return foldRight(empty(),(a,l)->{
            if(pred.test(a)){
                return l.prepend(a);
            }
            return l;
        });
    }
    default <R> SafeList<R> map(Function<? super T, ? extends R> fn) {
        return foldRight(empty(), (a, l) -> l.prepend(fn.apply(a)));
    }
    default <R> SafeList<R> flatMap(Function<? super T, ? extends SafeList<R>> fn) {
        return foldRight(empty(), (a, l) -> fn.apply(a).prependAll(l));
    }


    int size();

    boolean isEmpty();

    default Optional<NonEmptyList<T>> asNonEmptyList(){
        return match(c->Optional.of(NonEmptyList.cons(c.head,c.tail)), n->Optional.empty());
    }

    static <T> SafeList<T> cons(T head, SafeList<T> tail) {
        return Cons.cons(head,tail);
    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of={"head,tail"})
    public static class Cons<T> implements CaseClass2<T,SafeList<T>>, SafeList<T> {

        public final T head;
        public final SafeList<T> tail;

        public static <T> Cons<T> cons(T value, SafeList<T> tail){
            return new Cons<>(value,tail);
        }

        @Override
        public Tuple2<T, SafeList<T>> unapply() {
            return Tuple.tuple(head,tail);
        }
        public boolean isEmpty(){
            return false;
        }

        @Override
        public T foldRight(T identity, BinaryOperator<T> accumulator) {
            return foldRight(identity,accumulator);
        }

        public <R> R foldRight(R zero, BiFunction<? super T, ? super R, ? extends R> f) {
            class Step{
                public Trampoline<R> loop(SafeList<T> s, Function<? super R, ? extends Trampoline<R>> fn){

                    return s.match(c-> Trampoline.more(()->loop(c.tail, rem -> Trampoline.more(() -> fn.apply(f.apply(c.head, rem))))), n->fn.apply(zero));

                }
            }
            return new Step().loop(this,i-> Trampoline.done(i)).result();
        }


        public int size(){
            int result =1;
            SafeList<T> current[] = new SafeList[0];
            current[0]=tail;
            while(true){
               int toAdd =current[0].match(c->{
                    current[0]=c;
                    return 1;
                },n->0);
                result+=toAdd;
                if(toAdd==0)
                    break;
            }
            return result;
        }
        @Override
        public int hashCode() {
            return linkedListX().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof LazyList)
                return linkedListX().equals(((LazyList)obj).linkedListX());
            return false;
        }

        @Override
        public <R> R match(Function<? super Cons<T>, ? extends R> fn1, Function<? super Nil, ? extends R> fn2) {
            return fn1.apply(this);
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public class Nil<T> implements SafeList<T> {
        static Nil Instance = new Nil();
        @Override
        public <R> R foldRight(R zero, BiFunction<? super T, ? super R, ? extends R> f) {
            return zero;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public <R> R match(Function<? super Cons<T>, ? extends R> fn1, Function<? super Nil, ? extends R> fn2) {
            return fn2.apply(this);
        }
    }

}
