package com.aol.cyclops.javaslang.hkt;


import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.javaslang.FromCyclopsReact;

import javaslang.collection.Iterator;
import javaslang.concurrent.Future;
import javaslang.concurrent.Promise;
import javaslang.control.Option;
import javaslang.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Javaslang Future's
 * 
 * FutureType is a Future and a Higher Kinded Type (FutureType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Future
 */

public interface FutureType<T> extends Higher<FutureType.µ, T>, Future<T> {

    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    
    /**
     * Construct a HKT encoded completed Future
     * 
     * @param value To encode inside a HKT encoded Future
     * @return Completed HKT encoded Future
     */
    public static <T> FutureType<T> successful(T value){
        return widen(Future.successful(value));
    }

    /**
     * Convert a Future to a simulated HigherKindedType that captures Future nature
     * and Future element data type separately. Recover via @see FutureType#narrow
     * 
     * If the supplied Future implements FutureType it is returned already, otherwise it
     * is wrapped into a Future implementation that does implement FutureType
     * 
     * @param Future Future to widen to a FutureType
     * @return FutureType encoding HKT info about Futures
     */
    public static <T> FutureType<T> widen(final Future<T> completableFuture) {
        if (completableFuture instanceof FutureType)
            return (FutureType<T>) completableFuture;
        return new Box<>(
                         completableFuture);
    }
    public static <T> FutureType<T> widen(final FutureW<T> future) {
        return widen(FromCyclopsReact.future(future));
    }
    public static <T> FutureType<T> promise(){
        Promise<T> result =  Promise.make();
        return widen(result.future());
    }
    /**
     * Convert the raw Higher Kinded Type for FutureType types into the FutureType type definition class
     * 
     * @param future HKT encoded list into a FutureType
     * @return FutureType
     */
    public static <T> FutureType<T> narrowK(final Higher<FutureType.µ, T> future) {
       return (FutureType<T>)future;
    }

    /**
     * Convert the HigherKindedType definition for a Future into
     * 
     * @param Future Type Constructor to convert back into narrowed type
     * @return Future from Higher Kinded Type
     */
    public static <T> Future<T> narrow(final Higher<FutureType.µ, T> completableFuture) {
        if (completableFuture instanceof Future) {
            return (Future)completableFuture;
           
        }
        // this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) completableFuture;
        final Future<T> stage = type.narrow();
        return stage;

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements FutureType<T> {

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

        public void await() {
            boxed.await();
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
        public Iterator<T> iterator() {
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
