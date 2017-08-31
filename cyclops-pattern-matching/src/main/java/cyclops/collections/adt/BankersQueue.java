package cyclops.collections.adt;

import cyclops.patterns.CaseClass2;
import cyclops.patterns.Sealed2;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;


public interface BankersQueue<T> extends Sealed2<BankersQueue.Cons<T>,BankersQueue.Nil> {


    int size() ;
    boolean isEmpty();
    BankersQueue<T> enqueue(T value);
    default Optional<T> get(int n){
        return Optional.empty();
    }

    default ReactiveSeq<T> stream(){
        return ReactiveSeq.fromIterable(lazyList().iterable());
    }

    public static <T> BankersQueue<T> cons(T value){
        return new Cons<>(1,LazyList.cons(value,()->LazyList.empty()),0,LazyList.empty());
    }
     LazyList<T> lazyList();

   @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cons<T> implements  BankersQueue<T> {
        private final int sizeFront;
        private final LazyList<T> front;
        private final int sizeBack;
        private final LazyList<T> back;



       private static <T> BankersQueue<T> check(Cons<T> check) {
            if(check.sizeBack<check.sizeFront)
                return check;
            return new Cons((check.sizeFront+check.sizeBack), check.front.prependAll(check.back) ,0, LazyList.empty());


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
        public Tuple2<T,BankersQueue<T>> dequeue() {

            return front.match(cons->cons.match((head,tail)->Tuple.tuple(head,check(new Cons<>(sizeFront-1,tail,sizeBack,back))))
                                 ,nil->{throw new RuntimeException("Unreachable!");});

        }
       public Optional<T> get(int n) {
           if (n < sizeFront)
               front.get(n);
           else if (n < sizeFront + sizeBack)
               back.get(sizeFront - (n - sizeFront) - 1);

           return Optional.empty();

       }
        @Override
        public LazyList<T> lazyList() {
            return front.appendAll(back.reverse());
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
        public LazyList<T> lazyList() {
            return LazyList.empty();
        }

        @Override
        public <R> R match(Function<? super Cons<T>, ? extends R> fn1, Function<? super Nil, ? extends R> fn2) {
            return fn2.apply(this);
        }
    }

}
