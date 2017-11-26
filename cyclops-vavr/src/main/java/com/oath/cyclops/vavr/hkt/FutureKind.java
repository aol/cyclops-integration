package com.oath.cyclops.vavr.hkt;


import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;


import cyclops.companion.vavr.Futures;
import cyclops.conversion.vavr.FromCyclops;

import com.oath.cyclops.hkt.Higher;

import cyclops.conversion.vavr.ToCyclops;
import cyclops.monads.VavrWitness.future;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.FutureT;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Vavr Future's
 *
 * FutureKind is a Future and a Higher Kinded Type (future,T)
 *
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Future
 */

public interface FutureKind<T> extends Higher<future, T>, Future<T> {

    public static <T> Higher<future,T> widenK(final Future<T> completableList) {

        return new FutureKind.Box<>(
                completableList);
    }
    default Active<future,T> allTypeclasses(){
        return Active.of(this, Futures.Instances.definitions());
    }
    default <W2,R> Nested<future,W2,R> mapM(Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        return Futures.mapM(this,fn,defs);
    }
    default <W extends WitnessType<W>> FutureT<W, T> liftM(W witness) {
        return FutureT.of(witness.adapter().unit(ToCyclops.future(this)));
    }
    default <R> FutureKind<R> fold(Function<? super Future<? super T>,? extends Future<R>> op){
        return widen(op.apply(this));
    }
    public static <T> FutureKind<T> failed(Throwable exception){
        return widen(Future.failed(exception));
    }
    /**
     * Construct a HKT encoded completed Future
     *
     * @param value To encode inside a HKT encoded Future
     * @return Completed HKT encoded Future
     */
    public static <T> FutureKind<T> successful(T value){
        return widen(Future.successful(value));
    }

    /**
     * Convert a Future to a simulated HigherKindedType that captures Future nature
     * and Future element data type separately. Recover via @see FutureKind#narrow
     *
     * If the supplied Future implements FutureKind it is returned already, otherwise it
     * is wrapped into a Future implementation that does implement FutureKind
     *
     * @param completableFuture Future to widen to a FutureKind
     * @return FutureKind encoding HKT info about Futures
     */
    public static <T> FutureKind<T> widen(final Future<T> completableFuture) {
        if (completableFuture instanceof FutureKind)
            return (FutureKind<T>) completableFuture;
        return new Box<>(
                         completableFuture);
    }
    public static <T> FutureKind<T> widen(final cyclops.async.Future<T> future) {

        return widen(FromCyclops.future(future));
    }
    public static <T> FutureKind<T> promise(){
        Promise<T> result =  Promise.make();
        return widen(result.future());
    }
    /**
     * Convert the raw Higher Kinded Type for FutureKind types into the FutureKind type definition class
     *
     * @param future HKT encoded list into a FutureKind
     * @return FutureKind
     */
    public static <T> FutureKind<T> narrowK(final Higher<future, T> future) {
       return (FutureKind<T>)future;
    }

    /**
     * Convert the HigherKindedType definition for a Future into
     *
     * @param completableFuture Type Constructor to convert back into narrowed type
     * @return Future from Higher Kinded Type
     */
    public static <T> Future<T> narrow(final Higher<future, T> completableFuture) {
        if (completableFuture instanceof Future) {
            return (Future)completableFuture;

        }
        // this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) completableFuture;
        final Future<T> stage = type.narrow();
        return stage;

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements FutureKind<T> {

        private final Future<T> boxed;

        /**
         * @return wrapped Future
         */
        public Future<T> narrow() {
            return boxed;
        }



        public boolean equals(Object o) {
            return boxed.equals(o);
        }

        public Future<T> await() {
            return boxed.await();
        }


        public int hashCode() {
            return boxed.hashCode();
        }


        public String toString() {
            return boxed.toString();
        }

        public ExecutorService executorService() {
            return boxed.executorService();
        }


        public Option<Try<T>> getValue() {
            return boxed.getValue();
        }

        public boolean isCompleted() {
            return boxed.isCompleted();
        }



        public Future<T> onComplete(Consumer<? super Try<T>> action) {
            return boxed.onComplete(action);
        }



        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return boxed.cancel(mayInterruptIfRunning);
        }



        @Override
        public T get() {
            return boxed.get();
        }



        @Override
        public boolean isEmpty() {
            return boxed.isEmpty();
        }



        @Override
        public boolean isSingleValued() {
            return boxed.isSingleValued();
        }



        @Override
        public String stringPrefix() {
           return boxed.stringPrefix();
        }



        @Override
        public io.vavr.collection.Iterator<T> iterator() {
           return boxed.iterator();
        }



        public <U> Future<U> map(Function<? super T, ? extends U> mapper) {
            return boxed.map(mapper);
        }



        public Future<T> peek(Consumer<? super T> action) {
            return boxed.peek(action);
        }


    }


}
