package com.aol.cyclops.vavr.hkt;

import com.aol.cyclops2.hkt.Higher;
import cyclops.collections.vavr.VavrSetX;
import cyclops.companion.vavr.Sets;
import cyclops.monads.VavrWitness.set;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.SetT;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Function;

/**
 * Simulates Higher Kinded Types for Vavr Set's
 * 
 * SetKind is a Set and a Higher Kinded Type (SetKind.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Set
 */


public interface SetKind<T> extends Higher<set, T>, Publisher<T>, Set<T> {

    default Active<set,T> allTypeclasses(){
        return Active.of(this, Sets.Instances.definitions());
    }

    default <W2,R> Nested<set,W2,R> mapM(Function<? super T, ? extends Higher<W2, R>> fn, InstanceDefinitions<W2> defs){
        return Sets.mapM(this,fn,defs);
    }
    default <R> SetKind<R> fold(Function<? super Set<? super T>, ? extends Set<R>> op){
        return widen(op.apply(this));
    }

    /**
     * Construct a HKT encoded completed Set
     * 
     * @param value To encode inside a HKT encoded Set
     * @return Completed HKT encoded FSet
     */
    public static <T> SetKind<T> just(T value) {

        return widen(HashSet.of(value));
    }

    public static <T> SetKind<T> just(T... values) {

        return widen(HashSet.of(values));
    }

    public static <T> SetKind<T> empty() {
        return widen(HashSet.empty());
    }

    /**
     * Convert a Set to a simulated HigherKindedType that captures Set nature
     * and Set element data type separately. Recover via @see SetKind#narrow
     * 
     * If the supplied Set implements SetKind it is returned already, otherwise it
     * is wrapped into a Set implementation that does implement SetKind
     * 
     * @param completableSet Set to widen to a SetKind
     * @return SetKind encoding HKT info about Sets
     */
    public static <T> SetKind<T> widen(final Set<T> completableSet) {

        return new Box<>(
                                completableSet);
    }

    /**
     * Widen a SetKind nested inside another HKT encoded type
     * 
     * @param flux HTK encoded type containing  a Set to widen
     * @return HKT encoded type with a widened Set
     */
    public static <C2, T> Higher<C2, Higher<set, T>> widen2(Higher<C2, SetKind<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<set,T> must be a SetKind
        return (Higher) flux;
    }

    public static <T> SetKind<T> widen(final Publisher<T> completableSet) {

        return new Box<>(
                                HashSet.ofAll((Iterable<T>)ReactiveSeq.fromPublisher(completableSet)));
    }

    /**
     * Convert the raw Higher Kinded Type for SetKind types into the SetKind type definition class
     * 
     * @param future HKT encoded set into a SetKind
     * @return SetKind
     */
    public static <T> SetKind<T> narrowK(final Higher<set, T> future) {
        return (SetKind<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a Set into
     * 
     * @param set Type Constructor to convert back into narrowed type
     * @return Set from Higher Kinded Type
     */
    public static <T> Set<T> narrow(final Higher<set, T> set) {

        return ((SetKind<T>) set).narrow();

    }
    public Set<T> narrow();
    public ReactiveSeq<T> toReactiveSeq();

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements SetKind<T> {

        @Delegate
        private final Set<T> boxed;
        public ReactiveSeq<T> toReactiveSeq(){
            return ReactiveSeq.fromIterable(boxed);
        }

        /**
         * @return wrapped Set
         */
        public Set<T> narrow() {
            return boxed;
        }

        @Override
        public void subscribe(Subscriber<? super T> s) {
            ReactiveSeq.fromIterable(boxed)
                    .subscribe(s);

        }


        /**
         * @param o
         * @return
         * @see io.vavr.Value#equals(Object)
         */
        public boolean equals(Object o) {
            return boxed.equals(o);
        }



        /**
         * @return
         * @see io.vavr.Value#hashCode()
         */
        public int hashCode() {
            return boxed.hashCode();
        }


        /**
         * @return
         * @see io.vavr.Value#toString()
         */
        public String toString() {
            return boxed.toString();
        }



    }
}
