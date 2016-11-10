package com.aol.cyclops.reactor.hkt;


import org.reactivestreams.Publisher;

import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Simulates Higher Kinded Types for Reactor Mono's
 * 
 * MonoType is a Mono and a Higher Kinded Type (MonoType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Mono
 */

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public final class MonoType<T> implements Higher<MonoType.µ, T> {

    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    
    /**
     * Construct a HKT encoded completed Mono
     * 
     * @param value To encode inside a HKT encoded Mono
     * @return Completed HKT encoded FMono
     */
    public static <T> MonoType<T> just(T value){
        
        return widen(Mono.just(value));
    }
    public static <T> MonoType<T> empty(){
        return widen(Mono.empty());
    }

    /**
     * Convert a Mono to a simulated HigherKindedType that captures Mono nature
     * and Mono element data type separately. Recover via @see MonoType#narrow
     * 
     * If the supplied Mono implements MonoType it is returned already, otherwise it
     * is wrapped into a Mono implementation that does implement MonoType
     * 
     * @param Mono Mono to widen to a MonoType
     * @return MonoType encoding HKT info about Monos
     */
    public static <T> MonoType<T> widen(final Mono<T> completableMono) {
        
        return new MonoType<>(
                         completableMono);
    }
    
    public static <T> MonoType<T> widen(final Publisher<T> completableMono) {
        
        return new MonoType<>(Mono.from(
                         completableMono));
    }
        
    
    /**
     * Convert the raw Higher Kinded Type for MonoType types into the MonoType type definition class
     * 
     * @param future HKT encoded list into a MonoType
     * @return MonoType
     */
    public static <T> MonoType<T> narrowK(final Higher<MonoType.µ, T> future) {
       return (MonoType<T>)future;
    }

    /**
     * Convert the HigherKindedType definition for a Mono into
     * 
     * @param Mono Type Constructor to convert back into narrowed type
     * @return Mono from Higher Kinded Type
     */
    public static <T> Mono<T> narrow(final Higher<MonoType.µ, T> completableMono) {
      
            return ((MonoType<T>)completableMono).narrow();
           
       

    }

    

        private final Mono<T> boxed;

        /**
         * @return wrapped Mono
         */
        public Mono<T> narrow() {
            return boxed;
        }

        
        public FutureW<T> toFuture(){
            return FutureW.of(boxed.toFuture());
        }

    

    
}
