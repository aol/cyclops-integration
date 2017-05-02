package com.aol.cyclops.guava.hkt;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import com.aol.cyclops2.hkt.Higher;
import cyclops.stream.ReactiveSeq;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


import com.aol.cyclops.guava.FromCyclopsReact;
import com.aol.cyclops.guava.ToCyclopsReact;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for ToCyclopsReact FluentIterable's
 * 
 * FluentIterableKind is a FluentIterable and a Higher Kinded Type (FluentIterableKind.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <E> Data type stored within the FluentIterable
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FluentIterableKind<E> implements Higher<FluentIterableKind.µ, E>, Publisher<E>, Iterable<E> {

    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Construct a HKT encoded completed FluentIterable
     * 
     * @param value To encode inside a HKT encoded FluentIterable
     * @return Completed HKT encoded FFluentIterable
     */
    public static <T> FluentIterableKind<T> just(T value) {

        return widen(FluentIterable.of(value));
    }

    public static <T> FluentIterableKind<T> just(T... values) {

        return widen(FluentIterable.from(values));
    }

    public static <T> FluentIterableKind<T> empty() {
        return widen(FluentIterable.of());
    }

    /**
     * Convert a FluentIterable to a simulated HigherKindedType that captures FluentIterable nature
     * and FluentIterable element data type separately. Recover via @see FluentIterableKind#narrow
     * 
     * If the supplied FluentIterable implements FluentIterableKind it is returned already, otherwise it
     * is wrapped into a FluentIterable implementation that does implement FluentIterableKind
     * 
     * @param fluentIterable FluentIterable to widen to a FluentIterableKind
     * @return FluentIterableKind encoding HKT info about FluentIterables
     */
    public static <T> FluentIterableKind<T> widen(final FluentIterable<T> fluentIterable) {

        return new FluentIterableKind<>(
                                        fluentIterable);
    }

    /**
     * Widen a FluentIterableKind nested inside another HKT encoded type
     * 
     * @param flux HTK encoded type containing  a FluentIterable to widen
     * @return HKT encoded type with a widened FluentIterable
     */
    public static <C2, T> Higher<C2, Higher<FluentIterableKind.µ, T>> widen2(Higher<C2, FluentIterableKind<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<StreamType.µ,T> must be a StreamType
        return (Higher) flux;
    }

    public static <T> FluentIterableKind<T> widen(final Publisher<T> completableFluentIterable) {

        
        return new FluentIterableKind<>(FromCyclopsReact.fromSimpleReact(ReactiveSeq.fromPublisher(completableFluentIterable)));
    }

    /**
     * Convert the raw Higher Kinded Type for FluentIterableKind types into the FluentIterableKind type definition class
     * 
     * @param future HKT encoded list into a FluentIterableKind
     * @return FluentIterableKind
     */
    public static <T> FluentIterableKind<T> narrowK(final Higher<FluentIterableKind.µ, T> future) {
        return (FluentIterableKind<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a FluentIterable into
     * 
     * @param fluentIterable Type Constructor to convert back into narrowed type
     * @return FluentIterable from Higher Kinded Type
     */
    public static <T> FluentIterable<T> narrow(final Higher<FluentIterableKind.µ, T> fluentIterable) {

        return ((FluentIterableKind<T>) fluentIterable).narrow();

    }

    private final FluentIterable<E> boxed;

    /**
     * @return wrapped FluentIterable
     */
    public FluentIterable<E> narrow() {
        return boxed;
    }

    @Override
    public void subscribe(Subscriber<? super E> s) {
        ToCyclopsReact.fluentIterable(boxed)
             .subscribe(s);

    }

    @Override
    public Iterator<E> iterator() {
        return boxed.iterator();
    }

  

    /**
     * @return
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return boxed.hashCode();
    }


    /**
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return boxed.equals(obj);
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#toString()
     */
    public String toString() {
        return "[FluentIterableKind "+boxed.toString()+"]";
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#size()
     */
    public final int size() {
        return boxed.size();
    }

    /**
     * @param target
     * @return
     * @see com.google.common.collect.FluentIterable#contains(java.lang.Object)
     */
    public final boolean contains(Object target) {
        return boxed.contains(target);
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#cycle()
     */
    public final FluentIterable<E> cycle() {
        return boxed.cycle();
    }

    /**
     * @param other
     * @return
     * @see com.google.common.collect.FluentIterable#append(java.lang.Iterable)
     */
    public final FluentIterable<E> append(Iterable<? extends E> other) {
        return boxed.append(other);
    }

    /**
     * @param elements
     * @return
     * @see com.google.common.collect.FluentIterable#append(java.lang.Object[])
     */
    public final FluentIterable<E> append(E... elements) {
        return boxed.append(elements);
    }

    /**
     * @param predicate
     * @return
     * @see com.google.common.collect.FluentIterable#filter(com.google.common.base.Predicate)
     */
    public final FluentIterable<E> filter(Predicate<? super E> predicate) {
        return boxed.filter(predicate);
    }

    /**
     * @param type
     * @return
     * @see com.google.common.collect.FluentIterable#filter(java.lang.Class)
     */
    public final <T> FluentIterable<T> filter(Class<T> type) {
        return boxed.filter(type);
    }

    /**
     * @param predicate
     * @return
     * @see com.google.common.collect.FluentIterable#anyMatch(com.google.common.base.Predicate)
     */
    public final boolean anyMatch(Predicate<? super E> predicate) {
        return boxed.anyMatch(predicate);
    }

    /**
     * @param predicate
     * @return
     * @see com.google.common.collect.FluentIterable#allMatch(com.google.common.base.Predicate)
     */
    public final boolean allMatch(Predicate<? super E> predicate) {
        return boxed.allMatch(predicate);
    }

    /**
     * @param predicate
     * @return
     * @see com.google.common.collect.FluentIterable#firstMatch(com.google.common.base.Predicate)
     */
    public final Optional<E> firstMatch(Predicate<? super E> predicate) {
        return boxed.firstMatch(predicate);
    }

    /**
     * @param function
     * @return
     * @see com.google.common.collect.FluentIterable#transform(com.google.common.base.Function)
     */
    public final <T> FluentIterable<T> transform(Function<? super E, T> function) {
        return boxed.transform(function);
    }

    /**
     * @param function
     * @return
     * @see com.google.common.collect.FluentIterable#transformAndConcat(com.google.common.base.Function)
     */
    public <T> FluentIterable<T> transformAndConcat(Function<? super E, ? extends Iterable<? extends T>> function) {
        return boxed.transformAndConcat(function);
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#first()
     */
    public final Optional<E> first() {
        return boxed.first();
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#last()
     */
    public final Optional<E> last() {
        return boxed.last();
    }

    /**
     * @param numberToSkip
     * @return
     * @see com.google.common.collect.FluentIterable#skip(int)
     */
    public final FluentIterable<E> skip(int numberToSkip) {
        return boxed.skip(numberToSkip);
    }

    /**
     * @param maxSize
     * @return
     * @see com.google.common.collect.FluentIterable#limit(int)
     */
    public final FluentIterable<E> limit(int maxSize) {
        return boxed.limit(maxSize);
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#isEmpty()
     */
    public final boolean isEmpty() {
        return boxed.isEmpty();
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#toList()
     */
    public final ImmutableList<E> toList() {
        return boxed.toList();
    }

    /**
     * @param comparator
     * @return
     * @see com.google.common.collect.FluentIterable#toSortedList(java.util.Comparator)
     */
    public final ImmutableList<E> toSortedList(Comparator<? super E> comparator) {
        return boxed.toSortedList(comparator);
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#toSet()
     */
    public final ImmutableSet<E> toSet() {
        return boxed.toSet();
    }

    /**
     * @param comparator
     * @return
     * @see com.google.common.collect.FluentIterable#toSortedSet(java.util.Comparator)
     */
    public final ImmutableSortedSet<E> toSortedSet(Comparator<? super E> comparator) {
        return boxed.toSortedSet(comparator);
    }

    /**
     * @return
     * @see com.google.common.collect.FluentIterable#toMultiset()
     */
    public final ImmutableMultiset<E> toMultiset() {
        return boxed.toMultiset();
    }

    /**
     * @param valueFunction
     * @return
     * @see com.google.common.collect.FluentIterable#toMap(com.google.common.base.Function)
     */
    public final <V> ImmutableMap<E, V> toMap(Function<? super E, V> valueFunction) {
        return boxed.toMap(valueFunction);
    }

    /**
     * @param keyFunction
     * @return
     * @see com.google.common.collect.FluentIterable#index(com.google.common.base.Function)
     */
    public final <K> ImmutableListMultimap<K, E> index(Function<? super E, K> keyFunction) {
        return boxed.index(keyFunction);
    }

    /**
     * @param keyFunction
     * @return
     * @see com.google.common.collect.FluentIterable#uniqueIndex(com.google.common.base.Function)
     */
    public final <K> ImmutableMap<K, E> uniqueIndex(Function<? super E, K> keyFunction) {
        return boxed.uniqueIndex(keyFunction);
    }

    /**
     * @param type
     * @return
     * @see com.google.common.collect.FluentIterable#toArray(java.lang.Class)
     */
    public final E[] toArray(Class<E> type) {
        return boxed.toArray(type);
    }

    /**
     * @param collection
     * @return
     * @see com.google.common.collect.FluentIterable#copyInto(java.util.Collection)
     */
    public final <C extends Collection<? super E>> C copyInto(C collection) {
        return boxed.copyInto(collection);
    }

    /**
     * @param joiner
     * @return
     * @see com.google.common.collect.FluentIterable#join(com.google.common.base.Joiner)
     */
    public final String join(Joiner joiner) {
        return boxed.join(joiner);
    }

    /**
     * @param position
     * @return
     * @see com.google.common.collect.FluentIterable#get(int)
     */
    public final E get(int position) {
        return boxed.get(position);
    }

    public ReactiveSeq<E> toReactiveSeq(){
        return ReactiveSeq.fromIterable(boxed);
    }
    

   
}
