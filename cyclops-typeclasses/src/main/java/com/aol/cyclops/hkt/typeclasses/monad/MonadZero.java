package com.aol.cyclops.hkt.typeclasses.monad;

import java.util.function.Predicate;

import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.typeclasses.Filterable;

/**
 * A filterable monad
 * 
 * The zero() operator is used to replace supplied HKT with it's zero / empty equivalent when filtered out
 * 
 * @author johnmcclean
 *
 * @param <CRE> CORE Type
 */
public interface MonadZero<CRE> extends Monad<CRE>, Filterable<CRE> {
    
    /**
     * 
     * @return Identity value or zero value
     */
    public <T> Higher<CRE, T> zero();
    
    /* (non-Javadoc)
     * @see com.aol.cyclops.hkt.typeclasses.Filterable#filter(java.util.function.Predicate, com.aol.cyclops.hkt.alias.Higher)
     */
    default <T> Higher<CRE,T> filter(Predicate<? super T> predicate,  Higher<CRE,T> ds){
        
        return flatMap((T in)->predicate.test(in) ? ds : this.<T>zero(),ds);
    }
    
}
