package cyclops.collections.adt;


import com.aol.cyclops2.internal.stream.spliterators.IteratePredicateSpliterator;
import com.aol.cyclops2.internal.stream.spliterators.IterateSpliterator;
import com.aol.cyclops2.internal.stream.spliterators.UnfoldSpliterator;
import com.aol.cyclops2.internal.stream.spliterators.ints.ReversingRangeIntSpliterator;
import com.aol.cyclops2.internal.stream.spliterators.longs.ReversingRangeLongSpliterator;
import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.foldable.Evaluation;
import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.functor.Transformable;
import cyclops.collections.immutable.LinkedListX;
import cyclops.collections.mutable.ListX;
import cyclops.companion.Streams;
import cyclops.control.Maybe;
import cyclops.control.Trampoline;
import cyclops.control.Xor;
import cyclops.patterns.CaseClass2;
import cyclops.patterns.Sealed2;
import cyclops.stream.Generator;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Enumeration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

//safe LazyList (Stream) that does not support exceptional states
public interface LazyList<T> extends Sealed2<LazyList.Cons<T>,LazyList.Nil>, Folds<T>, Filters<T>,Transformable<T> {

    default ReactiveSeq<T> stream(){
        return ReactiveSeq.fromIterable(iterable());
    }
    default LinkedListX<T> linkedListX(){
        return LinkedListX.fromIterable(iterable());
    }

    static  <T,R> LazyList<R> tailRec(T initial, Function<? super T, ? extends LazyList<? extends Xor<T, R>>> fn) {
        LazyList<Xor<T, R>> next = LazyList.of(Xor.secondary(initial));

        boolean newValue[] = {true};
        for(;;){

            next = next.flatMap(e -> e.visit(s -> {
                        newValue[0]=true;
                        return fromStream(fn.apply(s).stream()); },
                    p -> {
                        newValue[0]=false;
                        return LazyList.of(e);
                    }));
            if(!newValue[0])
                break;

        }
        ListX<R> x = Xor.sequencePrimary(next.stream().to().listX(Evaluation.LAZY)).get();
        return LazyList.fromIterator(x.iterator());
    }
    static <U, T> LazyList<T> unfold(final U seed, final Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return fromStream(ReactiveSeq.unfold(seed,unfolder));
    }
    static <T> LazyList<T> iterate(final T seed, final UnaryOperator<T> f) {
        return fromStream(ReactiveSeq.iterate(seed,f));

    }
    static <T> LazyList<T> iterate(final T seed, Predicate<? super T> pred, final UnaryOperator<T> f) {
        return fromStream(ReactiveSeq.iterate(seed,pred,f));

    }
    static <T> LazyList<T> deferred(Supplier<? extends Iterable<? extends T>> lazy){
        return fromStream(ReactiveSeq.of(1).flatMapI(i->lazy.get()));
    }
    static <T, U> Tuple2<LazyList<T>, LazyList<U>> unzip(final LazyList<Tuple2<T, U>> sequence) {
       return ReactiveSeq.unzip(sequence.stream()).map((a,b)->Tuple.tuple(fromStream(a),fromStream(b)));
    }
    static <T> LazyList<T> generate(Supplier<T> s){
        return fromStream(ReactiveSeq.generate(s));
    }
    static <T> LazyList<T> generate(Generator<T> s){
        return fromStream(ReactiveSeq.generate(s));
    }
     static LazyList<Integer> range(final int start, final int end) {
        return LazyList.fromStream(ReactiveSeq.range(start,end));

    }
    static LazyList<Integer> range(final int start, final  int step,final int end) {
       return LazyList.fromStream(ReactiveSeq.range(start,step,end));

    }
    static LazyList<Long> rangeLong(final long start, final  long step,final long end) {
        return LazyList.fromStream(ReactiveSeq.rangeLong(start,step,end));
    }


    static LazyList<Long> rangeLong(final long start, final long end) {
        return LazyList.fromStream(ReactiveSeq.rangeLong(start,end));

    }
    /**
     *
     * Stream over the values of an enum
     * <pre>
     *     {@code
     *     LazyList.enums(Days.class)
    .printOut();
     *     }
     *
     *     Monday
     *     Tuesday
     *     Wednesday
     *     Thursday
     *     Friday
     *     Saturday
     *     Sunday
     * </pre>
     *
     * @param c Enum to process
     * @param <E> Enum type
     * @return Stream over enum
     */
    static <E extends Enum<E>> LazyList<E> enums(Class<E> c){
        return LazyList.fromStream(ReactiveSeq.enums(c));
    }

    /**
     *
     * Stream over the values of an enum
     * <pre>
     *     {@code
     *     LazyList.enums(Days.class,Days.Wednesday)
    .printOut();
     *     }
     *
     *     Wednesday
     *     Thursday
     *     Friday
     *     Saturday
     *     Sunday
     * </pre>
     * @param c Enum to process
     * @param start Start value
     * @param <E> Enum type
     * @return Stream over enum
     */
    static <E extends Enum<E>> LazyList<E> enums(Class<E> c,E start){
        return LazyList.fromStream(ReactiveSeq.enums(c,start));
    }
    /**
     *
     * Stream over the values of an enum
     * <pre>
     *     {@code
     *     LazyList.enums(Days.class,Days.Wednesday,Days.Friday)
    .printOut();
     *     }
     *
     *     Wednesday
     *     Thursday
     *     Friday
     * </pre>
     * @param c Enum to process
     * @param start Start value
     * @param end End value
     * @param <E> Enum type
     * @return Stream over enum
     */
    static <E extends Enum<E>> LazyList<E> enumsFromTo(Class<E> c,E start,E end){
       return LazyList.fromStream(ReactiveSeq.enumsFromTo(c,start,end));
    }
    /**
     *
     * Stream over the values of an enum
     * <pre>
     *     {@code
     *     LazyList.enums(Days.class,Days.Monday,Days.Wednesday,Days.Friday)
    .printOut();
     *     }
     *     Monday
     *     Wednesday
     *     Friday
     * </pre>
     * @param c Enum to process
     * @param start Start value
     * @param step Values for which the Distance from start in terms of the enum ordinal determines the stepping function
     * @param end End value
     * @param <E> Enum type
     * @return Stream over enum
     */
    static <E extends Enum<E>> LazyList<E> enums(Class<E> c,E start,E step,E end){
       return LazyList.fromStream(ReactiveSeq.enums(c,start,step,end));

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
    default Tuple2<LazyList<T>,LazyList<T>> duplicate(){
        return Tuple.tuple(this,this);
    }
    default <R1, R2> Tuple2<LazyList<R1>, LazyList<R2>> unzip(Function<? super T, Tuple2<? extends R1, ? extends R2>> fn) {
        Tuple2<LazyList<R1>, LazyList<Tuple2<? extends R1, ? extends R2>>> x = map(fn).duplicate().map1(s -> s.map(Tuple2::v1));
        return x.map2(s -> s.map(Tuple2::v2));
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
    default Tuple2<LazyList<T>, LazyList<T>> splitAt(int n) {
        return Tuple.tuple(take(n), drop(n));
    }

    default Zipper<T> focusAt(int pos, T alt){
        Tuple2<LazyList<T>, LazyList<T>> t2 = splitAt(pos);
        T value = t2.v2.match(c -> c.head, n -> alt);
        LazyList<T> right= t2.v2.match(c->c.tail.get(),n->null);
        return Zipper.of(t2.v1,value, right);
    }
    default Maybe<Zipper<T>> focusAt(int pos){
        Tuple2<LazyList<T>, LazyList<T>> t2 = splitAt(pos);
        Maybe<T> value = t2.v2.match(c -> Maybe.just(c.head), n -> Maybe.none());
        return value.map(l-> {
            LazyList<T> right = t2.v2.match(c -> c.tail.get(), n -> null);
            return Zipper.of(t2.v1, l, right);
        });
    }

    default T getOrElse(int pos, T alt){
        T result = null;
        LazyList<T> l = this;
        for(int i=0;i<pos;i++){
            l = l.match(c->c.tail.get(),n->n);
            if(l instanceof Nil){ //short circuit
                return alt;
            }
        }
        return l.match(c->c.head,n->null);
    }
    default LazyList<T> prepend(T value){
        return cons(value,()->this);
    }
    default LazyList<T> prependAll(LazyList<T> value){
        return value.match(cons->
                        cons.foldRight(this,(a,b)->b.prepend(a))
                ,nil->this);
    }
    default LazyList<T> append(T append) {
        return appendAll(LazyList.of(append));

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
        public Cons<T> reverse() {
            return (Cons<T>)LazyList.super.reverse();
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
