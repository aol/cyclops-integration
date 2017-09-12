package cyclops.data;

import cyclops.control.Maybe;
import cyclops.patterns.Sealed2;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.function.BiFunction;
import java.util.function.Function;


public interface BankersQueue<T> extends Sealed2<BankersQueue.Cons<T>,BankersQueue.Nil> {


    default Tuple2<T,BankersQueue<T>> dequeue(T defaultValue){
        return match(c->c.dequeue(),n->Tuple.tuple(defaultValue,this));
    }
    public static <T> BankersQueue<T> empty(){
        return Nil.Instance;
    }
    int size() ;
    boolean isEmpty();
    BankersQueue<T> enqueue(T value);
    <R> BankersQueue<R> map(Function<? super T, ? extends R> map);
    <R> BankersQueue<R> flatMap(Function<? super T, ? extends BankersQueue<? extends R>> fn);

    default Maybe<T> get(int n){
        return Maybe.none();
    }

    default ReactiveSeq<T> stream(){
        return ReactiveSeq.fromIterable(lazySeq());
    }

    BankersQueue<T> replace(T currentElement, T newElement);
    public static <T> BankersQueue<T> cons(T value){
        return new Cons<>(1, LazySeq.cons(value,()-> LazySeq.empty()),0, LazySeq.empty());
    }
    public static <T> BankersQueue<T> ofAll(ImmutableList<T> list){
        return new Cons<>(list.size(), list,0, list.emptyUnit());
    }
     LazySeq<T> lazySeq();

    static <T> BankersQueue<T> of(T... values) {
        BankersQueue<T> result = empty();
        for(T next : values){
            result = result.enqueue(next);
        }
        return result;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cons<T> implements  BankersQueue<T> {
        private final int sizeFront;
        private final ImmutableList<T> front;
        private final int sizeBack;
        private final ImmutableList<T> back;

        private Cons(ImmutableList<T> front, ImmutableList<T> back){
            this.sizeFront = front.size();
            this.sizeBack=back.size();
            this.front = front;
            this.back = back;
        }


       private static <T> BankersQueue<T> check(Cons<T> check) {
            if(check.sizeBack<check.sizeFront)
                return check;
           return new Cons((check.sizeFront + check.sizeBack), check.front.prependAll(check.back), 0, LazySeq.empty());
        }

        @Override
        public <R> R match(Function<? super Cons<T>, ? extends R> fn1, Function<? super Nil, ? extends R> fn2) {
            return fn1.apply(this);
        }

        @Override
        public int size() {
            return sizeFront + sizeBack;
        }

        @Override
        public boolean isEmpty() {
            return size()>0;
        }

        @Override
        public BankersQueue<T> enqueue(T value) {
            return check(new Cons(sizeFront, front, sizeBack + 1, back.prepend(value)));
        }

        @Override
        public <R> BankersQueue<R> map(Function<? super T, ? extends R> map) {
            return check(new Cons(sizeFront,front.map(map),sizeBack,back.map(map)));
        }

        @Override
        public <R> BankersQueue<R> flatMap(Function<? super T, ? extends BankersQueue<? extends R>> fn) {
            return check(new Cons(sizeFront,front.flatMap(fn.andThen(q->q.lazySeq())),sizeBack,back.flatMap(fn.andThen(q->q.lazySeq()))));
        }

        public Tuple2<T,BankersQueue<T>> dequeue() {

            return front.match(cons->cons.match((head,tail)->Tuple.tuple(head,tail.match(c->check(new Cons<>(sizeFront-1,tail,sizeBack,back)),n->Nil.Instance)))
                                 ,nil->{throw new RuntimeException("Unreachable!");});

        }
        public BankersQueue<T> replace(T currentElement, T newElement) {
            ImmutableList<T> replaceF = front.replace(currentElement, newElement);
            ImmutableList<T> replaceB = back.replace(currentElement, newElement);
            return  front==replaceF && back==replaceB ? this : new Cons<>(replaceF, replaceB);
        }
       public Maybe<T> get(int n) {
           if (n < sizeFront)
               return front.get(sizeFront-n-1);
           else if (n < sizeFront + sizeBack) {
               int pos = n-sizeFront;
               return  back.get(sizeBack-pos-1);
           }
           return Maybe.none();

       }
        @Override
        public LazySeq<T> lazySeq() {
            return front.appendAll(back.reverse()).lazySeq();
        }

        public String toString(){
           return lazySeq().toString();
        }

    }
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public class Nil<T> implements BankersQueue<T> {
        static Nil Instance = new Nil();

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
        public BankersQueue<T> enqueue(T value) {
            return cons(value);
        }

        @Override
        public <R> BankersQueue<R> map(Function<? super T, ? extends R> map) {
            return Instance;
        }

        @Override
        public <R> BankersQueue<R> flatMap(Function<? super T, ? extends BankersQueue<? extends R>> fn) {
            return Instance;
        }

        @Override
        public BankersQueue<T> replace(T currentElement, T newElement) {
            return this;
        }


        @Override
        public LazySeq<T> lazySeq() {
            return LazySeq.empty();
        }

        @Override
        public <R> R match(Function<? super Cons<T>, ? extends R> fn1, Function<? super Nil, ? extends R> fn2) {
            return fn2.apply(this);
        }
    }

}
