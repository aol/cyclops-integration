package com.oath.cyclops.vavr.hkt;

import com.oath.cyclops.hkt.Higher;
import cyclops.companion.vavr.HashSets;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.hashSet;
import cyclops.reactive.ReactiveSeq;
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
 * HashSetKind is a Set and a Higher Kinded Type (HashSetKind.µ,T)
 *
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Set
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashSetKind<T> implements Higher<hashSet, T>, Publisher<T>, Set<T> {

    public static <T> Higher<VavrWitness.hashSet,T> widenK(final HashSet<T> completableList) {

        return new HashSetKind<>(
                completableList);
    }
    public Active<hashSet,T> allTypeclasses(){
        return Active.of(this, HashSets.Instances.definitions());
    }

    public <W2,R> Nested<VavrWitness.hashSet,W2,R> mapM(Function<? super T, ? extends Higher<W2, R>> fn, InstanceDefinitions<W2> defs){
        return HashSets.mapM(boxed,fn,defs);
    }
    public <R> HashSetKind<R> fold(Function<? super Set<? super T>, ? extends HashSet<R>> op){
        return widen(op.apply(this));
    }

    /**
     * Construct a HKT encoded completed Set
     *
     * @param value To encode inside a HKT encoded Set
     * @return Completed HKT encoded FSet
     */
    public static <T> HashSetKind<T> just(T value) {

        return widen(HashSet.of(value));
    }

    public static <T> HashSetKind<T> just(T... values) {

        return widen(HashSet.of(values));
    }

    public static <T> HashSetKind<T> empty() {
        return widen(HashSet.empty());
    }

    /**
     * Convert a Set to a simulated HigherKindedType that captures Set nature
     * and Set element data type separately. Recover via @see HashSetKind#narrow
     *
     * If the supplied Set implements HashSetKind it is returned already, otherwise it
     * is wrapped into a Set implementation that does implement HashSetKind
     *
     * @param completableSet Set to widen to a HashSetKind
     * @return HashSetKind encoding HKT info about HashSets
     */
    public static <T> HashSetKind<T> widen(final HashSet<T> completableSet) {

        return new HashSetKind<>(
                                completableSet);
    }

    /**
     * Widen a HashSetKind nested inside another HKT encoded type
     *
     * @param flux HTK encoded type containing  a Set to widen
     * @return HKT encoded type with a widened Set
     */
    public static <C2, T> Higher<C2, Higher<VavrWitness.hashSet, T>> widen2(Higher<C2, HashSetKind<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<hashSet,T> must be a HashSetKind
        return (Higher) flux;
    }

    public static <T> HashSetKind<T> widen(final Publisher<T> completableSet) {

        return new HashSetKind<>(
                                HashSet.ofAll((Iterable<T>)ReactiveSeq.fromPublisher(completableSet)));
    }

    /**
     * Convert the raw Higher Kinded Type for HashSetKind types into the HashSetKind type definition class
     *
     * @param future HKT encoded hashSet into a HashSetKind
     * @return HashSetKind
     */
    public static <T> HashSetKind<T> narrowK(final Higher<hashSet, T> future) {
        return (HashSetKind<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a Set into
     *
     * @param set Type Constructor to convert back into narrowed type
     * @return Set from Higher Kinded Type
     */
    public static <T> HashSet<T> narrow(final Higher<VavrWitness.hashSet, T> set) {

        return ((HashSetKind<T>) set).narrow();

    }



        @Delegate
        private final HashSet<T> boxed;
        public ReactiveSeq<T> toReactiveSeq(){
            return ReactiveSeq.fromIterable(boxed);
        }



        /**
         * @return wrapped Set
         */
        public HashSet<T> narrow() {
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
