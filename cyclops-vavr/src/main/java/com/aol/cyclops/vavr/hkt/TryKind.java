package com.aol.cyclops.vavr.hkt;


import com.aol.cyclops2.hkt.Higher;
import cyclops.companion.vavr.Futures;
import cyclops.companion.vavr.Trys;
import cyclops.conversion.vavr.FromCyclopsReact;
import cyclops.conversion.vavr.ToCyclopsReact;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.future;
import cyclops.monads.VavrWitness.tryType;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.FutureT;
import cyclops.monads.transformers.XorT;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Simulates Higher Kinded Types for Vavr Future's
 * 
 * FutureKind is a Future and a Higher Kinded Type (future,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Future
 */

public interface TryKind<T> extends Higher<tryType, T>, Try<T> {

    default Active<tryType,T> allTypeclasses(){
        return Active.of(this, Trys.Instances.definitions());
    }
    default <W2,R> Nested<tryType,W2,R> mapM(Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        return Trys.mapM(this,fn,defs);
    }
    default <W extends WitnessType<W>> XorT<W,Throwable, T> liftM(W witness) {
        return XorT.of(witness.adapter().unit(ToCyclopsReact.toTry(this).asXor()));
    }
    
    public static <T> TryKind<T> failed(Throwable exception){
        return widen(Try.failure(exception));
    }
    /**
     * Construct a HKT encoded completed Future
     * 
     * @param value To encode inside a HKT encoded Future
     * @return Completed HKT encoded Future
     */
    public static <T> TryKind<T> successful(T value){
        return widen(Try.success(value));
    }

    /**
     * Convert a Future to a simulated HigherKindedType that captures Future nature
     * and Future element data type separately. Recover via @see FutureKind#narrow
     * 
     * If the supplied Future implements FutureKind it is returned already, otherwise it
     * is wrapped into a Future implementation that does implement FutureKind
     * 
     * @param newTry Future to widen to a FutureKind
     * @return FutureKind encoding HKT info about Futures
     */
    public static <T> TryKind<T> widen(final Try<T> newTry) {
        if (newTry instanceof TryKind)
            return (TryKind<T>) newTry;
        return new Box<>(
                         newTry);
    }
    public static <T,X extends Throwable> TryKind<T> widen(final cyclops.control.Try<T,X> newTry) {

        return new Box<>(FromCyclopsReact.toTry(
                newTry));
    }

    /**
     * Convert the raw Higher Kinded Type for FutureKind types into the FutureKind type definition class
     * 
     * @param future HKT encoded list into a FutureKind
     * @return FutureKind
     */
    public static <T> TryKind<T> narrowK(final Higher<tryType, T> future) {
       return (TryKind<T>)future;
    }

    /**
     * Convert the HigherKindedType definition for a Future into
     * 
     * @param newTry Type Constructor to convert back into narrowed type
     * @return Future from Higher Kinded Type
     */
    public static <T> Try<T> narrow(final Higher<tryType, T> newTry) {
        if (newTry instanceof Try) {
            return (Try)newTry;
           
        }
        // this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) newTry;
        final Try<T> stage = type.narrow();
        return stage;

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements TryKind<T> {

        private final Try<T> boxed;

        public Try<T> narrow() {
            return boxed;
        }
        @Override
        public T get() {
            return boxed.get();
        }

        @Override
        public Throwable getCause() {
            return boxed.getCause();
        }

        @Override
        public boolean isEmpty() {
            return boxed.isEmpty();
        }

        @Override
        public String stringPrefix() {
            return boxed.stringPrefix();
        }

        @Override
        public boolean isFailure() {
            return boxed.isFailure();
        }

        @Override
        public boolean isSuccess() {
            return boxed.isSuccess();
        }
    }

    
}
