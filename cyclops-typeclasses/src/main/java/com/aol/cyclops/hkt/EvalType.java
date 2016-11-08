package com.aol.cyclops.hkt;


import com.aol.cyclops.control.*;
import java.util.function.Supplier;

import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Simulates Higher Kinded Types for Eval's
 * 
 * EvalType is a Eval and a Higher Kinded Type (EvalType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Eval
 */

public interface EvalType<T> extends Higher<EvalType.µ, T>, Eval<T> {
    
    
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    
    /**
     * Create an EvalType instance from a reactive-streams publisher
     * 
     * <pre>
     * {@code
     *    EvalType<Integer> e = EvalType.fromPublisher(Mono.just(10));
     *    //EvalType[10]
     * }
     * </pre>
     * 
     * 
     * @param pub Publisher to create the Eval from
     * @return Eval created from Publisher
     */
    public static <T> EvalType<T> fromPublisher(final Publisher<T> pub) {
        return widen(Eval.fromPublisher(pub));
    }

    /**
     * Create an EvalType instance from an Iterable
     * 
     * <pre>
     * {@code
     *    EvalType<Integer> e = EvalType.fromIterable(Arrays.asList(10));
     *    //EvalType[10]
     * }
     * </pre>
     * @param iterable to create the Eval from
     * @return EvalType created from Publisher
     */
    public static <T> EvalType<T> fromIterable(final Iterable<T> iterable) {
        return widen(Eval.fromIterable(iterable));
    }

    /**
     * Create an Eval with the value specified
     * 
     * <pre>
     * {@code
     *   EvalType<Integer> e = EvalType.now(10);
     *   //Eval[10]
     * }</pre>
     * 
     * @param value of EvalType
     * @return EvalType with specified value
     */
    public static <T> EvalType<T> now(final T value) {
        return widen(Eval.now(value));

    }

    /**
     * Lazily create an Eval from the specified Supplier. Supplier#get will only be called once. Return values of Eval operations will also
     * be cached (later indicates lazy and caching - characteristics can be changed using flatMap).
     * 
     * <pre>
     * {@code
     *   EvalType<Integer> e = EvalType.later(()->10)
     *                                 .map(i->i*2);
     *   //EvalType[20] - lazy so will not be executed until the value is accessed
     * }</pre>
     * 
     * 
     * @param value Supplier to (lazily) populate this Eval
     * @return EvalType with specified value
     */
    public static <T> EvalType<T> later(final Supplier<T> value) {
        return widen(Eval.later(value));
    }

    /**
     * Lazily create an Eval from the specified Supplier. Supplier#get will only be every time get is called on the resulting Eval.
     * 
     * <pre>
     * {@code
     *   EvalType<Integer> e = EvalType.always(()->10)
     *                                 .map(i->i*2);
     *   //EvalType[20] - lazy so will not be executed until the value is accessed
     * }</pre>
     * 
     * 
     * @param value  Supplier to (lazily) populate this Eval
     * @return Eval with specified value
     */
    public static <T> EvalType<T> always(final Supplier<T> value) {
        return widen(Eval.always(value));
    }

    /**
     * Convert a Eval to a simulated HigherKindedType that captures Eval nature
     * and Eval element data type separately. Recover via @see EvalType#narrow
     * 
     * If the supplied Eval implements EvalType it is returned already, otherwise it
     * is wrapped into a Eval implementation that does implement EvalType
     * 
     * @param Eval Eval to widen to a EvalType
     * @return EvalType encoding HKT info about Evals
     */
    public static <T> EvalType<T> widen(final Eval<T> eval) {
        if (eval instanceof EvalType)
            return (EvalType<T>) eval;
        return new Box<>(
                         eval);
    }

    /**
     * Convert the HigherKindedType definition for a Eval into
     * 
     * @param Eval Type Constructor to convert back into narrowed type
     * @return EvalX from Higher Kinded Type
     */
    public static <T> Eval<T> narrow(final Higher<EvalType.µ, T> eval) {
        if (eval instanceof Eval)
            return (Eval) eval;
        //this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) eval;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements EvalType<T> {

        @Delegate
        private final Eval<T> boxed;

        /**
         * @return This back as a EvalX
         */
        public Eval<T> narrow() {
            return Eval.fromIterable(boxed);
        }

        
       

    }

}
