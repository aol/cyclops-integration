package com.aol.cyclops.hkt.jdk;

import java.util.Optional;

import com.aol.cyclops.hkt.alias.Higher;

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
public final class OptionalType<T> implements Higher<OptionalType.µ, T> {
    private final Optional<T> boxed;  
   
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    /**
     * @return An HKT encoded empty Optional
     */
    public static <T> OptionalType<T> empty() {
        return widen(Optional.empty());
    }
    /**
     * @param value Value to embed in an Optional
     * @return An HKT encoded Optional
     */
    public static <T> OptionalType<T> of(T value) {
        return widen(Optional.of(value));
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
    public static <T> OptionalType<T> widen(final Optional<T> Optional) {
        
        return new OptionalType<T>(Optional);
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
    /**
     * Convert the HigherKindedType definition for a Optional into
     * 
     * @param Optional Type Constructor to convert back into narrowed type
     * @return Optional from Higher Kinded Type
     */
    public static <T> Optional<T> narrow(final Higher<OptionalType.µ, T> Optional) {
        //has to be an OptionalType as only OptionalType can implement Higher<OptionalType.µ, T>
         return ((OptionalType<T>)Optional).boxed;
        
    }
   

   
   
}
