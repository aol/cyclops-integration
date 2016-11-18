package com.aol.cyclops.hkt.cyclops;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.control.Eval;
import com.aol.cyclops.control.Ior;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Xor;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.alias.Higher2;
import com.aol.cyclops.types.MonadicValue2;
import com.aol.cyclops.types.Value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Xor's
 * 
 * XorType is a Xor and a Higher Kinded Type (XorType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Xor
 */

public interface XorType<ST, PT> extends Higher2<XorType.µ, ST, PT>, Xor<ST, PT> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    @SuppressWarnings("unchecked")
    static <ST, PT> XorType<ST, PT> primary(final PT primary) {
        return widen(Xor.<ST, PT> primary(primary));
    }
    static <ST, PT> XorType<ST, PT> secondary(final ST secondary) {
        return widen(Xor.<ST, PT> secondary(secondary));
    }

    public static <C2, ST, PT> Higher<C2, Higher2<XorType.µ, ST, PT>> widen2(
            final Higher<C2, XorType<ST, PT>> nestedXor) {

        return (Higher) nestedXor;
    }

    /**
     * Convert the raw Higher Kinded Type for XorType types into the XorType type definition class
     * 
     * @param xor HKT encoded list into a XorType
     * @return Xor
     */
    public static <ST, PT> XorType<ST, PT> narrowK(final Higher2<XorType.µ, ST, PT> xor) {
        return (XorType<ST, PT>) xor;
    }

    /**
     * Convert a Xor to a simulated HigherKindedType that captures Xor nature
     * and Xor element data type separately. Recover via @see XorType#narrow
     * 
     * If the supplied Xor implements XorType it is returned already, otherwise it
     * is wrapped into a Xor implementation that does implement XorType
     * 
     * @param Xor Xor to widen to a XorType
     * @return XorType encoding HKT info about Xors
     */
    public static <ST, PT> XorType<ST, PT> widen(final Xor<ST, PT> xor) {
        if (xor instanceof XorType)
            return (XorType<ST, PT>) xor;
        return new Box<>(
                         xor);
    }

    /**
     * Convert the HigherKindedType definition for a Xor into
     * 
     * @param Xor Type Constructor to convert back into narrowed type
     * @return XorX from Higher Kinded Type
     */
    public static <ST, PT> Xor<ST, PT> narrow(final Higher2<XorType.µ, ST, PT> xor) {
        if (xor instanceof Xor)
            return (Xor) xor;
        // this code should be unreachable due to HKT type checker
        final Box<ST, PT> type = (Box<ST, PT>) xor;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<ST, PT> implements XorType<ST, PT> {
        private final Xor<ST, PT> boxed;

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.control.Xor#secondaryMap(java.util.function.Function)
         */
        @Override
        public <R> Xor<R, PT> secondaryMap(final Function<? super ST, ? extends R> fn) {
            return boxed.secondaryMap(fn);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.control.Xor#visit(java.util.function.Function,
         * java.util.function.Function)
         */
        @Override
        public <R> R visit(final Function<? super ST, ? extends R> secondary,
                final Function<? super PT, ? extends R> primary) {
            return boxed.visit(secondary, primary);
        }

        /**
         * @return This back as a XorX
         */
        public Xor<ST, PT> narrow() {
            return boxed;
        }

        @Override
        public PT get() {
            return boxed.get();
        }

        @Override
        public boolean isPresent() {
            return boxed.isPresent();
        }

        @Override
        public String toString() {
            return "[XorType " + boxed.toString() + "]";
        }

        @Override
        public boolean equals(final Object obj) {

            return boxed.equals(obj);

        }

        @Override
        public int hashCode() {
            return boxed.hashCode();
        }

        @Override
        public Xor<ST, PT> filter(final Predicate<? super PT> test) {
            return boxed.filter(test);
        }

        @Override
        public Xor<ST, PT> secondaryToPrimayMap(final Function<? super ST, ? extends PT> fn) {
            return boxed.secondaryToPrimayMap(fn);
        }

        @Override
        public <R> Xor<ST, R> map(final Function<? super PT, ? extends R> fn) {
            return boxed.map(fn);
        }

        @Override
        public Xor<ST, PT> secondaryPeek(final Consumer<? super ST> action) {
            return boxed.secondaryPeek(action);
        }

        @Override
        public Xor<ST, PT> peek(final Consumer<? super PT> action) {
            return boxed.peek(action);
        }

        @Override
        public Xor<PT, ST> swap() {
            return boxed.swap();
        }

        @Override
        public Ior<ST, PT> toIor() {
            return boxed.toIor();
        }

        @Override
        public <R> Eval<R> matches(final Function<CheckValue1<ST, R>, CheckValue1<ST, R>> fn1,
                final Function<CheckValue1<PT, R>, CheckValue1<PT, R>> fn2, final Supplier<? extends R> otherwise) {
            return boxed.matches(fn1, fn2, otherwise);
        }

        @Override
        public Value<ST> secondaryValue() {
            return boxed.secondaryValue();
        }

        @Override
        public ST secondaryGet() {
            return boxed.secondaryGet();
        }

        @Override
        public Optional<ST> secondaryToOptional() {
            return boxed.secondaryToOptional();
        }

        @Override
        public ReactiveSeq<ST> secondaryToStream() {
            return boxed.secondaryToStream();
        }

        @Override
        public <LT1, RT1> Xor<LT1, RT1> flatMap(
                final Function<? super PT, ? extends MonadicValue2<? extends LT1, ? extends RT1>> mapper) {
            return boxed.flatMap(mapper);
        }

        @Override
        public <LT1, RT1> Xor<LT1, RT1> secondaryFlatMap(final Function<? super ST, ? extends Xor<LT1, RT1>> mapper) {
            return boxed.secondaryFlatMap(mapper);
        }

        @Override
        public Xor<ST, PT> secondaryToPrimayFlatMap(final Function<? super ST, ? extends Xor<ST, PT>> fn) {
            return boxed.secondaryToPrimayFlatMap(fn);
        }

        @Override
        public void peek(final Consumer<? super ST> stAction, final Consumer<? super PT> ptAction) {
            boxed.peek(stAction, ptAction);
        }

        @Override
        public boolean isPrimary() {
            return boxed.isPrimary();
        }

        @Override
        public boolean isSecondary() {
            return boxed.isSecondary();
        }

        @Override
        public <T2, R> Xor<ST, R> combine(final Value<? extends T2> app,
                final BiFunction<? super PT, ? super T2, ? extends R> fn) {
            return boxed.combine(app, fn);
        }

    }

}
