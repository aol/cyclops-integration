package com.aol.cyclops.guava.hkt;


import java.util.Set;

import com.google.common.collect.FluentIterable;
import cyclops.companion.guava.FluentIterables;
import cyclops.companion.guava.Optionals;
import cyclops.conversion.guava.FromCyclopsReact;
import cyclops.conversion.guava.FromJDK;
import cyclops.conversion.guava.ToCyclopsReact;

import com.oath.cyclops.hkt.Higher;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.monads.GuavaWitness;
import cyclops.monads.GuavaWitness.optional;
import cyclops.monads.Witness;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.OptionalT;
import cyclops.monads.transformers.StreamT;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Optional's
 *
 * OptionalKind is a Optional and a Higher Kinded Type (optional,T)
 *
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Optional
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalKind<T> implements Higher<optional, T> {
    public <R> OptionalKind<R> fold(java.util.function.Function<? super Optional<?  super T>,? extends Optional<R>> op){
        return widen(op.apply(boxed));
    }
    public Active<optional,T> allTypeclasses(){
        return Active.of(this, Optionals.Instances.definitions());
    }

    public static <T> Higher<optional,T> widenK(final Optional<T> completableList) {

        return new OptionalKind<>(
                completableList);
    }
    public <W2,R> Nested<optional,W2,R> mapM(java.util.function.Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        return Optionals.mapM(boxed,fn,defs);
    }

    public <W extends WitnessType<W>> OptionalT<W, T> liftM(W witness) {
        return Optionals.liftM(boxed,witness);
    }
    /**
     * @return Get the empty Optional (single instance)
     */
    @SuppressWarnings("unchecked")
    public static <T> OptionalKind<T> absent() {
        return widen(Optional.absent());
    }
    public static <T> OptionalKind<T> of(T element) {
        return widen(Optional.of(element));
    }
    /**
     *  Construct a OptionalKind  that contains a single value extracted from the supplied Iterable
     * <pre>
     * {@code
     *   ReactiveSeq<Integer> stream =  ReactiveSeq.of(1,2,3);

         OptionalKind<Integer> maybe = OptionalKind.fromIterable(stream);

        //Optional[1]
     *
     * }
     * </pre>
     * @param iterable Iterable  to extract value from
     * @return Optional populated with first value from Iterable (Optional.empty if Publisher empty)
     */
    public static <T> OptionalKind<T> fromIterable(final Iterable<T> iterable) {

        return widen(FromCyclopsReact.optional(Eval.fromIterable(iterable)));
    }

    /**
     * Construct an equivalent Optional from the Supplied Optional
     * <pre>
     * {@code
     *   OptionalKind<Integer> some = OptionalKind.fromOptional(Optional.of(10));
     *   //Optional[10], Some[10]
     *
     *   OptionalKind<Integer> none = OptionalKind.fromOptional(Optional.empty());
     *   //Optional.empty, None[]
     * }
     * </pre>
     *
     * @param optional Optional to construct Optional from
     * @return Optional created from Optional
     */
    public static <T> OptionalKind<T> fromOptional(Higher<Witness.optional,T> optional){

        return widen(FromCyclopsReact.optional(Maybe.fromOptional(optional)));
    }
    /**
     * Convert a Optional to a simulated HigherKindedType that captures Optional nature
     * and Optional element data type separately. Recover via @see OptionalKind#narrow
     *
     * If the supplied Optional implements OptionalKind it is returned already, otherwise it
     * is wrapped into a Optional implementation that does implement OptionalKind
     *
     * @param optional Optional to widen to a OptionalKind
     * @return OptionalKind encoding HKT info about Optionals (converts Optional to a Optional)
     */
    public static <T> OptionalKind<T> widen(final java.util.Optional<T> optional) {

        return new OptionalKind<>(FromJDK.optional(optional));
    }
    public static <T> OptionalKind<T> widen(final Maybe<T> option) {

        return new OptionalKind<T>(FromCyclopsReact.optional(option));
    }
    /**
     * Convert the raw Higher Kinded Type for OptionalKind types into the OptionalKind type definition class
     *
     * @param future HKT encoded list into a OptionalKind
     * @return OptionalKind
     */
    public static <T> OptionalKind<T> narrowK(final Higher<optional, T> future) {
       return (OptionalKind<T>)future;
    }
    public static <C2,T> Higher<C2, Higher<optional,T>> widen2(Higher<C2, OptionalKind<T>> nestedOptional){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<optional,T> must be a StreamType
        return (Higher)nestedOptional;
    }
    /**
     * Convert the HigherKindedType definition for a Optional into
     *
     * @param optional Type Constructor to convert back into narrowed type
     * @return Optional from Higher Kinded Type
     */
    public static <T> java.util.Optional<T> narrowOptional(final Higher<optional, T> optional) {

         return ToCyclopsReact.maybe(narrow(optional)).toOptional();

    }

    /**
     * Construct an Optional which contains the provided (non-null) value.
     * Alias for @see {@link Optional#of(Object)}
     *
     * <pre>
     * {@code
     *
     *    Optional<Integer> some = Optional.just(10);
     *    some.map(i->i*2);
     * }
     * </pre>
     *
     * @param value Value to wrap inside a Optional
     * @return Optional containing the supplied value
     */
    public static <T> OptionalKind<T> just(final T value) {
        return ofNullable(value);
    }



    /**
     * <pre>
     * {@code
     *    Optional<Integer> maybe  = Optional.ofNullable(null);
     *    //None
     *
     *    Optional<Integer> maybe = Optional.ofNullable(10);
     *    //Optional[10], Some[10]
     *
     * }
     * </pre>
     *
     *
     * @param value
     * @return
     */
    public static <T> OptionalKind<T> ofNullable(final T value) {

        return widen(Optional.fromNullable(value));
    }
    /**
     * Convert a Optional to a simulated HigherKindedType that captures Optional nature
     * and Optional element data type separately. Recover via @see OptionalKind#narrow
     *
     * If the supplied Optional implements OptionalKind it is returned already, otherwise it
     * is wrapped into a Optional implementation that does implement OptionalKind
     *
     * @param maybe Optional to widen to a OptionalKind
     * @return OptionalKind encoding HKT info about Optionals
     */
    public static <T> OptionalKind<T> widen(final Optional<T> maybe) {

        return new OptionalKind<>(
                         maybe);
    }

    /**
     * Convert the HigherKindedType definition for a Optional into
     *
     * @param maybe Type Constructor to convert back into narrowed type
     * @return OptionalX from Higher Kinded Type
     */
    public static <T> Optional<T> narrow(final Higher<optional, T> maybe) {
        if (maybe instanceof Optional)
            return (Optional) maybe;
        //this code should be unreachable due to HKT type checker
        final OptionalKind<T> type = (OptionalKind<T>) maybe;
        return type.boxed;
    }
    private final Optional<T> boxed;


    public Optional<T> narrow(){
        return boxed;
    }
    /**
     * @return
     * @see com.google.common.base.Optional#isPresent()
     */
    public boolean isPresent() {
        return boxed.isPresent();
    }
    /**
     * @return
     * @see com.google.common.base.Optional#get()
     */
    public T get() {
        return boxed.get();
    }
    /**
     * @param defaultValue
     * @return
     * @see com.google.common.base.Optional#or(java.lang.Object)
     */
    public T or(T defaultValue) {
        return boxed.or(defaultValue);
    }
    /**
     * @param secondChoice
     * @return
     * @see com.google.common.base.Optional#or(com.google.common.base.Optional)
     */
    public Optional<T> or(Optional<? extends T> secondChoice) {
        return boxed.or(secondChoice);
    }
    /**
     * @param supplier
     * @return
     * @see com.google.common.base.Optional#or(com.google.common.base.Supplier)
     */
    public T or(Supplier<? extends T> supplier) {
        return boxed.or(supplier);
    }
    /**
     * @return
     * @see com.google.common.base.Optional#orNull()
     */
    public T orNull() {
        return boxed.orNull();
    }
    /**
     * @return
     * @see com.google.common.base.Optional#asSet()
     */
    public Set<T> asSet() {
        return boxed.asSet();
    }
    /**
     * @param function
     * @return
     * @see com.google.common.base.Optional#transform(com.google.common.base.Function)
     */
    public <V> Optional<V> transform(Function<? super T, V> function) {
        return boxed.transform(function);
    }
    /**
     * @return
     * @see com.google.common.base.Optional#toString()
     */
    public String toString() {
        return boxed.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((boxed == null) ? 0 : boxed.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        Optional other = (Optional) obj;
        if (boxed == null) {
            if (other != null)
                return false;
        } else if (!boxed.equals(other))
            return false;
        return true;
    }





}
