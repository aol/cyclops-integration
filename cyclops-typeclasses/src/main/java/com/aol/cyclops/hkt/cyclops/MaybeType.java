package com.aol.cyclops.hkt.cyclops;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.jdk.OptionalType;
import com.aol.cyclops.hkt.jdk.StreamType;
import com.aol.cyclops.types.MonadicValue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Simulates Higher Kinded Types for Maybe's
 * 
 * MaybeType is a Maybe and a Higher Kinded Type (MaybeType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Maybe
 */

public interface MaybeType<T> extends Higher<MaybeType.µ, T>, Maybe<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    
    /**
     * @return Get the empty Maybe (single instance)
     */
    @SuppressWarnings("unchecked")
    static <T> MaybeType<T> none() {
        return widen(Maybe.none());
    }
    /**
     * Construct an equivalent Maybe from the Supplied Optional
     * <pre>
     * {@code 
     *   MaybeType<Integer> some = MaybeType.fromOptional(Optional.of(10));
     *   //Maybe[10], Some[10]
     *  
     *   MaybeType<Integer> none = MaybeType.fromOptional(Optional.empty());
     *   //Maybe.empty, None[]
     * }
     * </pre>
     * 
     * @param opt Optional to construct Maybe from
     * @return Maybe created from Optional
     */
    public static <T> MaybeType<T> fromOptional(Higher<OptionalType.µ,T> optional){
        return widen(Maybe.fromOptional(OptionalType.narrow(optional)));
    }
    /**
     * Convert a Optional to a simulated HigherKindedType that captures Optional nature
     * and Optional element data type separately. Recover via @see OptionalType#narrow
     * 
     * If the supplied Optional implements OptionalType it is returned already, otherwise it
     * is wrapped into a Optional implementation that does implement OptionalType
     * 
     * @param Optional Optional to widen to a OptionalType
     * @return MaybeType encoding HKT info about Optionals (converts Optional to a Maybe)
     */
    public static <T> MaybeType<T> widen(final Optional<T> optional) {
        
        return new Box<>(Maybe.fromOptional(optional));
    }
    public static <C2,T> Higher<C2, Higher<MaybeType.µ,T>> widen2(Higher<C2, MaybeType<T>> nestedMaybe){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<MaybeType.µ,T> must be a StreamType
        return (Higher)nestedMaybe;
    }
    /**
     * Convert the HigherKindedType definition for a Optional into
     * 
     * @param Optional Type Constructor to convert back into narrowed type
     * @return Optional from Higher Kinded Type
     */
    public static <T> Optional<T> narrowOptional(final Higher<MaybeType.µ, T> Optional) {
        
         return ((Box<T>)Optional).narrow().toOptional();
        
    }

    /**
     * Construct an Maybe which contains the provided (non-null) value.
     * Alias for @see {@link Maybe#of(Object)}
     * 
     * <pre>
     * {@code 
     * 
     *    Maybe<Integer> some = Maybe.just(10);
     *    some.map(i->i*2);
     * }
     * </pre>
     * 
     * @param value Value to wrap inside a Maybe
     * @return Maybe containing the supplied value
     */
    static <T> MaybeType<T> just(final T value) {
        return of(value);
    }

    /**
     * Construct an Maybe which contains the provided (non-null) value
     * Equivalent to @see {@link Maybe#just(Object)}
     * <pre>
     * {@code 
     * 
     *    Maybe<Integer> some = Maybe.of(10);
     *    some.map(i->i*2);
     * }
     * </pre>
     * 
     * @param value Value to wrap inside a Maybe
     * @return Maybe containing the supplied value
     */
    static <T> MaybeType<T> of(final T value) {
       return widen(Maybe.of(value));
    }

    /**
     * <pre>
     * {@code 
     *    Maybe<Integer> maybe  = Maybe.ofNullable(null);
     *    //None
     *     
     *    Maybe<Integer> maybe = Maybe.ofNullable(10);
     *    //Maybe[10], Some[10]
     * 
     * }
     * </pre>
     * 
     * 
     * @param value
     * @return
     */
    static <T> MaybeType<T> ofNullable(final T value) {

        return widen(Maybe.ofNullable(value));
    }
    /**
     * Convert a Maybe to a simulated HigherKindedType that captures Maybe nature
     * and Maybe element data type separately. Recover via @see MaybeType#narrow
     * 
     * If the supplied Maybe implements MaybeType it is returned already, otherwise it
     * is wrapped into a Maybe implementation that does implement MaybeType
     * 
     * @param Maybe Maybe to widen to a MaybeType
     * @return MaybeType encoding HKT info about Maybes
     */
    public static <T> MaybeType<T> widen(final Maybe<T> maybe) {
        if (maybe instanceof MaybeType)
            return (MaybeType<T>) maybe;
        return new Box<>(
                         maybe);
    }

    /**
     * Convert the HigherKindedType definition for a Maybe into
     * 
     * @param Maybe Type Constructor to convert back into narrowed type
     * @return MaybeX from Higher Kinded Type
     */
    public static <T> Maybe<T> narrow(final Higher<MaybeType.µ, T> maybe) {
        if (maybe instanceof Maybe)
            return (Maybe) maybe;
        //this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) maybe;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of={"boxed"})
    static final class Box<T> implements MaybeType<T> {

        
        private final Maybe<T> boxed;

        /**
         * @return This back as a MaybeX
         */
        public Maybe<T> narrow() {
            return Maybe.fromIterable(boxed);
        }

       

        public T get() {
            return boxed.get();
        }

       
  
        public boolean isPresent() {
            return boxed.isPresent();
        }

        public Maybe<T> recover(Supplier<T> value) {
            return boxed.recover(value);
        }

        public Maybe<T> recover(T value) {
            return boxed.recover(value);
        }

        public <R> Maybe<R> map(Function<? super T, ? extends R> mapper) {
            return boxed.map(mapper);
        }

        public <R> Maybe<R> flatMap(Function<? super T, ? extends MonadicValue<? extends R>> mapper) {
            return boxed.flatMap(mapper);
        }

        public <R> R visit(Function<? super T, ? extends R> some, Supplier<? extends R> none) {
            return boxed.visit(some, none);
        }

       

        public Maybe<T> filter(Predicate<? super T> fn) {
            return boxed.filter(fn);
        }

      
    }


   

}
