package com.aol.cyclops.hkt;

import java.util.Optional;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

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
        
        return new Box<>(optional);
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
    static final class Box<T> implements MaybeType<T> {

        @Delegate
        private final Maybe<T> boxed;

        /**
         * @return This back as a MaybeX
         */
        public Maybe<T> narrow() {
            return Maybe.fromIterable(boxed);
        }

        
       

    }

}
