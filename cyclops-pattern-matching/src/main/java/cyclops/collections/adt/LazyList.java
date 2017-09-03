package cyclops.collections.adt;


import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.functor.Transformable;
import cyclops.collections.immutable.LinkedListX;
import cyclops.control.Trampoline;
import cyclops.patterns.CaseClass2;
import cyclops.patterns.Sealed2;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

//safe LazyList (Stream) that does not support exceptional states
public interface LazyList<T> extends Sealed2<LazyList.Cons<T>,LazyList.Nil>, Folds<T>, Filters<T>,Transformable<T> {

    default ReactiveSeq<T> stream(){
        return ReactiveSeq.fromIterable(iterable());
    }
    default LinkedListX<T> linkedListX(){
        return LinkedListX.fromIterable(iterable());
    }


    static <T> LazyList<T> fromIterator(Iterator<T> it){
        return it.hasNext() ? cons(it.next(), () -> fromIterator(it)) : empty();
    }
    static <T> LazyList<T> fromStream(Stream<T> stream){
        Iterator<T> t = stream.iterator();
        return t.hasNext() ? cons(t.next(),()->fromIterator(t)) : empty();
    }
    static <T> LazyList<T> of(T... value){
        LazyList<T> result = empty();
        for(int i=value.length;i>0;i--){
            result = result.prepend(value[i-1]);
        }
        return result;
    }
    static <T> LazyList<T> empty(){
        return Nil.Instance;
    }

    default Tuple2<LazyList<T>, LazyList<T>> span(Predicate<? super T> pred) {
        return Tuple.tuple(takeWhile(pred), dropWhile(pred));
    }
    default Tuple2<LazyList<T>,LazyList<T>> splitBy(Predicate<? super T> test) {
        return span(test.negate());
    }
    default LazyList<LazyList<T>> split(Predicate<? super T> test) {
        LazyList<T> next = dropWhile(test);
        Tuple2<LazyList<T>, LazyList<T>> split = next.splitBy(test);
        return next.match(c->cons(split.v1,()->split.v2.split(test)),n->n);
    }
    default LazyList<T> take(final int n) {
        if( n <= 0)
            return LazyList.Nil.Instance;
        if(n<1000) {
            return this.match(cons -> cons(cons.head, ()->cons.take(n - 1)), nil -> nil);
        }
        return fromStream(ReactiveSeq.fromIterable(this.iterable()).take(n));

    }
    default LazyList<T> takeWhile(Predicate<? super T> p) {
        return match(c->{
            if(p.test(c.head)){
                return cons(c.head,()->c.tail.get().takeWhile(p));
            }else{
                return empty();
            }
        },n->this);
    }
    default LazyList<T> dropWhile(Predicate<? super T> p) {
        LazyList<T> current = this;
        while(true && !current.isEmpty()){
            current =  current.match(c->{
                if(!p.test(c.head)){
                    return LazyList.empty();
                }
                return c.tail.get();
            },empty->empty);
        }
        return current;
    }
    default LazyList<T> drop(final int num) {
        LazyList<T> current = this;
        int pos = num;
        while (pos-- > 0 && !current.isEmpty()) {
            current = current.match(c->c.tail.get(),nil->nil);
        }
        return current;
    }
    default LazyList<T> reverse() {
        LazyList<T> res = empty();
        for (T a : iterable()) {
            res = res.prepend(a);
        }
        return res;
    }

    default LazyList<T> replace(T currentElement, T newElement) {
        LazyList<T> preceding = empty();
        LazyList<T> tail = this;
        while(!tail.isEmpty()){
            LazyList<T> ref=  preceding;
            LazyList<T> tailRef = tail;
            Tuple3<LazyList<T>, LazyList<T>, Boolean> t3 = tail.match(c -> {
                if (Objects.equals(c.head, currentElement))
                    return Tuple.tuple(ref, tailRef, true);
                return Tuple.tuple(ref.prepend(c.head), c.tail.get(), false);
            }, nil -> Tuple.tuple(ref, tailRef, true));

            preceding = t3.v1;
            tail = t3.v2;
            if(t3.v3)
                break;

        }

        LazyList<T> start = preceding;
        return tail.match(cons->cons.tail.get().prepend(newElement).prependAll(start),nil->this);

    }


    default Optional<T> get(int pos){
        T result = null;
        LazyList<T> l = this;
        for(int i=0;i<pos;i++){
           l = l.match(c->c.tail.get(),n->n);
           if(l instanceof Nil){ //short circuit
               return Optional.empty();
           }
        }
        return Optional.ofNullable(l.match(c->c.head,n->null));
    }
    default LazyList<T> prepend(T value){
        return cons(value,()->this);
    }
    default LazyList<T> prependAll(LazyList<T> value){
        return value.match(cons->
                        cons.foldRight(this,(a,b)->b.prepend(a))
                ,nil->this);
    }
    default LazyList<T> appendAll(LazyList<T> append) {
        return this.match(cons->{
            return append.match(c2->{
                return cons(cons.head,()->cons.tail.get().appendAll(append));
            },n2->this);
        },nil->append);

    }
    default <R> R foldLeft(R zero, BiFunction<R, ? super T,  R> f){
        R acc= zero;
        for(T next : iterable()){
            acc= f.apply(acc,next);
        }
        return acc;
    }

    default String mkString(){
        return stream().join(",","[","]");
    }

    default Iterable<T> iterable(){
        return ()->new Iterator<T>() {
            LazyList<T> current= LazyList.this;
            @Override
            public boolean hasNext() {
                return current.match(c->true,n->false);
            }

            @Override
            public T next() {
                return current.match(c->{
                    current = c.tail.get();
                    return c.head;
                },n->null);
            }
        };
    }
    <R> R foldRight(R zero, BiFunction<? super T, ? super R, ? extends R> f);

    default LazyList<T> filter(Predicate<? super T> pred){
        return foldRight(empty(),(a,l)->{
            if(pred.test(a)){
                return l.prepend(a);
            }
            return l;
        });
    }
    default <R> LazyList<R> map(Function<? super T, ? extends R> fn) {
        return foldRight(empty(), (a, l) -> l.prepend(fn.apply(a)));
    }

    default <R> LazyList<R> flatMap(Function<? super T, ? extends LazyList<? extends R>> fn) {
        return this.match(cons->{
            LazyList<R> l1 = (LazyList<R>)fn.apply(cons.head);
            return l1.appendAll(cons.tail.get().flatMap(a -> fn.apply(a)));
        },nil->empty());
    }
    int size();

    boolean isEmpty();

    static <T> LazyList<T> cons(T head, Supplier<LazyList<T>> tail) {
        return Cons.cons(head,tail);
    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cons<T> implements CaseClass2<T,LazyList<T>>, LazyList<T> {

        public final T head;
        public final Supplier<LazyList<T>> tail;

        public static <T> Cons<T> cons(T value, Supplier<LazyList<T>> tail){
            return new Cons<>(value,tail);
        }

        @Override
        public Tuple2<T, LazyList<T>> unapply() {
            return Tuple.tuple(head,tail.get());
        }
        public boolean isEmpty(){
            return false;
        }

        public <R> R foldRight(R zero,BiFunction<? super T, ? super R, ? extends R> f) {
            class Step{
                public Trampoline<R> loop(LazyList<T> s, Function<? super R, ? extends Trampoline<R>> fn){

                    return s.match(c-> Trampoline.more(()->loop(c.tail.get(), rem -> Trampoline.more(() -> fn.apply(f.apply(c.head, rem))))), n->fn.apply(zero));

                }
            }
            return new Step().loop(this,i-> Trampoline.done(i)).result();
        }


        public int size(){
            int result =1;
            LazyList<T> current[] = new LazyList[1];
            current[0]=tail.get();
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
        public String toString(){
            return mkString();
        }
    }

    public class Nil<T> implements LazyList<T> {
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
        public String toString(){
            return mkString();
        }


    }

}
