package com.aol.cyclops.guava.hkt;


import java.util.Set;

import com.aol.cyclops.control.Eval;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.guava.FromCyclopsReact;
import com.aol.cyclops.guava.FromJDK;
import com.aol.cyclops.guava.Guava;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Optional's
 * 
 * OptionalType is a Optional and a Higher Kinded Type (OptionalType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Optional
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OptionalType<T> implements Higher<OptionalType.µ, T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    
    /**
     * @return Get the empty Optional (single instance)
     */
    @SuppressWarnings("unchecked")
    public static <T> OptionalType<T> absent() {
        return widen(Optional.absent());
    }
    public static <T> OptionalType<T> of(T element) {
        return widen(Optional.of(element));
    }
    /**
     *  Construct a OptionalType  that contains a single value extracted from the supplied Iterable
     * <pre>
     * {@code 
     *   ReactiveSeq<Integer> stream =  ReactiveSeq.of(1,2,3);
        
         OptionalType<Integer> maybe = OptionalType.fromIterable(stream);
        
        //Optional[1]
     * 
     * }
     * </pre> 
     * @param iterable Iterable  to extract value from
     * @return Optional populated with first value from Iterable (Optional.empty if Publisher empty)
     */
    public static <T> OptionalType<T> fromIterable(final Iterable<T> iterable) {
       
        return widen(FromCyclopsReact.option(Eval.fromIterable(iterable)));
    }

    /**
     * Construct an equivalent Optional from the Supplied Optional
     * <pre>
     * {@code 
     *   OptionalType<Integer> some = OptionalType.fromOptional(Optional.of(10));
     *   //Optional[10], Some[10]
     *  
     *   OptionalType<Integer> none = OptionalType.fromOptional(Optional.empty());
     *   //Optional.empty, None[]
     * }
     * </pre>
     * 
     * @param opt Optional to construct Optional from
     * @return Optional created from Optional
     */
    public static <T> OptionalType<T> fromOptional(Higher<com.aol.cyclops.hkt.jdk.OptionalType.µ,T> optional){
        return widen(FromCyclopsReact.option(MaybeType.fromOptional(optional)));
    }
    /**
     * Convert a Optional to a simulated HigherKindedType that captures Optional nature
     * and Optional element data type separately. Recover via @see OptionalType#narrow
     * 
     * If the supplied Optional implements OptionalType it is returned already, otherwise it
     * is wrapped into a Optional implementation that does implement OptionalType
     * 
     * @param Optional Optional to widen to a OptionalType
     * @return OptionalType encoding HKT info about Optionals (converts Optional to a Optional)
     */
    public static <T> OptionalType<T> widen(final java.util.Optional<T> optional) {
        
        return new OptionalType<>(FromJDK.option(optional));
    }
    public static <T> OptionalType<T> widen(final Maybe<T> option) {
        
        return new OptionalType<T>(FromCyclopsReact.option(option));
    }
    /**
     * Convert the raw Higher Kinded Type for OptionalType types into the OptionalType type definition class
     * 
     * @param future HKT encoded list into a OptionalType
     * @return OptionalType
     */
    public static <T> OptionalType<T> narrowK(final Higher<OptionalType.µ, T> future) {
       return (OptionalType<T>)future;
    }
    public static <C2,T> Higher<C2, Higher<OptionalType.µ,T>> widen2(Higher<C2, OptionalType<T>> nestedOptional){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<OptionalType.µ,T> must be a StreamType
        return (Higher)nestedOptional;
    }
    /**
     * Convert the HigherKindedType definition for a Optional into
     * 
     * @param Optional Type Constructor to convert back into narrowed type
     * @return Optional from Higher Kinded Type
     */
    public static <T> java.util.Optional<T> narrowOptional(final Higher<OptionalType.µ, T> optional) {
        
         return Guava.asMaybe(narrow(optional)).toOptional();
        
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
    public static <T> OptionalType<T> just(final T value) {
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
    public static <T> OptionalType<T> ofNullable(final T value) {

        return widen(Optional.fromNullable(value));
    }
    /**
     * Convert a Optional to a simulated HigherKindedType that captures Optional nature
     * and Optional element data type separately. Recover via @see OptionalType#narrow
     * 
     * If the supplied Optional implements OptionalType it is returned already, otherwise it
     * is wrapped into a Optional implementation that does implement OptionalType
     * 
     * @param Optional Optional to widen to a OptionalType
     * @return OptionalType encoding HKT info about Optionals
     */
    public static <T> OptionalType<T> widen(final Optional<T> maybe) {
   
        return new OptionalType<>(
                         maybe);
    }

    /**
     * Convert the HigherKindedType definition for a Optional into
     * 
     * @param Optional Type Constructor to convert back into narrowed type
     * @return OptionalX from Higher Kinded Type
     */
    public static <T> Optional<T> narrow(final Higher<OptionalType.µ, T> maybe) {
        if (maybe instanceof Optional)
            return (Optional) maybe;
        //this code should be unreachable due to HKT type checker
        final OptionalType<T> type = (OptionalType<T>) maybe;
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
