package cyclops.collections.adt;


import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.foldable.Evaluation;
import com.aol.cyclops2.types.foldable.Folds;
import com.aol.cyclops2.types.foldable.To;
import com.aol.cyclops2.types.functor.Transformable;
import cyclops.collections.adt.DataWitness.immutableList;
import cyclops.collections.immutable.LinkedListX;
import cyclops.collections.mutable.ListX;
import cyclops.control.Trampoline;
import cyclops.control.Xor;
import cyclops.function.Monoid;
import cyclops.patterns.CaseClass2;
import cyclops.patterns.Sealed2;
import cyclops.stream.Generator;
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
import java.util.function.*;
import java.util.stream.Stream;

//safe list implementation that does not support exceptional states
public interface ImmutableList<T> extends Sealed2<ImmutableList.Cons<T>,ImmutableList.Nil>,
                                            Folds<T>,
                                            Filters<T>,
                                            Transformable<T>,
                                            Higher<immutableList,T>,
                                            To<ImmutableList<T>> {

    default ReactiveSeq<T> stream(){
        return ReactiveSeq.fromIterable(iterable());
    }
    default LinkedListX<T> linkedListX(){
        return LinkedListX.fromIterable(iterable());
    }


      static  <T,R> ImmutableList<R> tailRec(T initial, Function<? super T, ? extends ImmutableList<? extends Xor<T, R>>> fn) {
          ImmutableList<Xor<T, R>> next = ImmutableList.of(Xor.secondary(initial));

        boolean newValue[] = {true};
        for(;;){

            next = next.flatMap(e -> e.visit(s -> {
                        newValue[0]=true;
                        return fromStream(fn.apply(s).stream());
                        },
                    p -> {
                        newValue[0]=false;
                        return ImmutableList.of(e);
                    }));
            if(!newValue[0])
                break;

        }
        ListX<R> x = Xor.sequencePrimary(next.stream().to().listX(Evaluation.LAZY)).get();
        return ImmutableList.fromIterator(x.iterator());
    }
    static <T> ImmutableList<T> fill(T t,int max){
        return ImmutableList.fromStream(ReactiveSeq.fill(t).take(max));
    }
    static <U, T> ImmutableList<T> unfold(final U seed, final Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return fromStream(ReactiveSeq.unfold(seed,unfolder));
    }

    static <T> ImmutableList<T> iterate(final T seed, Predicate<? super T> pred, final UnaryOperator<T> f) {
        return fromStream(ReactiveSeq.iterate(seed,pred,f));

    }

    static <T, U> Tuple2<ImmutableList<T>, ImmutableList<U>> unzip(final LazyList<Tuple2<T, U>> sequence) {
        return ReactiveSeq.unzip(sequence.stream()).map((a,b)->Tuple.tuple(fromStream(a),fromStream(b)));
    }
    static <T> ImmutableList<T> generate(Supplier<T> s,int max){
        return fromStream(ReactiveSeq.generate(s));
    }
    static <T> ImmutableList<T> generate(Generator<T> s){
        return fromStream(ReactiveSeq.generate(s));
    }
    static ImmutableList<Integer> range(final int start, final int end) {
        return ImmutableList.fromStream(ReactiveSeq.range(start,end));

    }
    static ImmutableList<Integer> range(final int start, final  int step,final int end) {
        return ImmutableList.fromStream(ReactiveSeq.range(start,step,end));

    }
    static ImmutableList<Long> rangeLong(final long start, final  long step,final long end) {
        return ImmutableList.fromStream(ReactiveSeq.rangeLong(start,step,end));
    }


    static ImmutableList<Long> rangeLong(final long start, final long end) {
        return ImmutableList.fromStream(ReactiveSeq.rangeLong(start,end));

    }

    static <T> ImmutableList<T> of(T... value){
        ImmutableList<T> result = empty();
        for(int i=value.length;i>0;i--){
            result = result.prepend(value[i-1]);
        }
        return result;
    }
    static <T> ImmutableList<T> fromIterator(Iterator<T> it){
        List<T> values = new ArrayList<>();
        while(it.hasNext()){
          values.add(it.next());
        }
        ImmutableList<T> result = empty();
        for(int i=values.size();i>0;i--){
            result = result.prepend(values.get(i-1));
        }
        return result;
    }
    static <T> ImmutableList<T> fromStream(Stream<T> stream){
        Iterator<T> t = stream.iterator();
       return t.hasNext() ? cons(t.next(),fromIterator(t)) : empty();
    }
    static <T> ImmutableList<T> empty(){
        return Nil.Instance;
    }

    default Optional<T> get(final int pos){
        T result = null;
        ImmutableList<T> l = this;
        for(int i=0;i<pos;i++){
           l = l.match(c->c.tail,n->n);
           if(l instanceof Nil){ //short circuit
               return Optional.empty();
           }
        }
        return Optional.ofNullable(l.match(c->c.head,n->null));
    }
    default ImmutableList<T> prepend(T value){
        return cons(value,this);
    }
    default ImmutableList<T> prependAll(ImmutableList<T> value){
        return value.match(cons->
                        cons.foldRight(this,(a,b)->b.prepend(a))
                ,nil->this);
    }

    default ImmutableList<T> take(final int num) {
        if( num <= 0)
           return Nil.Instance;
        if(num<1000) {
            return this.match(cons -> cons(cons.head, cons.take(num - 1)), nil -> nil);
        }
        return fromStream(ReactiveSeq.fromIterable(this.iterable()).take(num));

    }
    default ImmutableList<T> drop(final int num) {
        ImmutableList<T> current = this;
        int pos = num;
        while (pos-- > 0 && !current.isEmpty()) {
            current = current.match(c->c.tail,nil->nil);
        }
        return current;
    }
    default ImmutableList<T> reverse() {
        ImmutableList<T> res = empty();
        for (T a : iterable()) {
            res = res.prepend(a);
        }
        return res;
    }
    default Iterable<T> iterable(){
        return ()->new Iterator<T>() {
            ImmutableList<T> current= ImmutableList.this;
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
    default ImmutableList<T> filter(Predicate<? super T> pred){
        return foldRight(empty(),(a,l)->{
            if(pred.test(a)){
                return l.prepend(a);
            }
            return l;
        });
    }
    default <R> ImmutableList<R> map(Function<? super T, ? extends R> fn) {
        return foldRight(empty(), (a, l) -> l.prepend(fn.apply(a)));
    }
    default <R> ImmutableList<R> flatMap(Function<? super T, ? extends ImmutableList<? extends R>> fn) {
         return foldRight(empty(), (a, l) -> {
             ImmutableList<R> b = narrow(fn.apply(a));
             return b.prependAll(l);
         });
    }

    static <T> ImmutableList<T> narrow(ImmutableList<? extends T> list){
        return (ImmutableList<T>)list;
    }

    int size();

    boolean isEmpty();

    default Optional<NonEmptyList<T>> asNonEmptyList(){
        return match(c->Optional.of(NonEmptyList.cons(c.head,c.tail)), n->Optional.empty());
    }

    static <T> ImmutableList<T> cons(T head, ImmutableList<T> tail) {
        return Cons.cons(head,tail);
    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of={"head,tail"})
    public static class Cons<T> implements CaseClass2<T,ImmutableList<T>>, ImmutableList<T> {

        public final T head;
        public final ImmutableList<T> tail;

        public static <T> Cons<T> cons(T value, ImmutableList<T> tail){
            return new Cons<>(value,tail);
        }

        @Override
        public Tuple2<T, ImmutableList<T>> unapply() {
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
                public Trampoline<R> loop(ImmutableList<T> s, Function<? super R, ? extends Trampoline<R>> fn){

                    return s.match(c-> Trampoline.more(()->loop(c.tail, rem -> Trampoline.more(() -> fn.apply(f.apply(c.head, rem))))), n->fn.apply(zero));

                }
            }
            return new Step().loop(this,i-> Trampoline.done(i)).result();
        }


        public int size(){
            int result =1;
            ImmutableList<T> current[] = new ImmutableList[0];
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
    public class Nil<T> implements ImmutableList<T> {
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
