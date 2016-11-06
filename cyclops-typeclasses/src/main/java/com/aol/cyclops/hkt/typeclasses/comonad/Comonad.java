package com.aol.cyclops.hkt.typeclasses.comonad;

import java.util.function.Function;

import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;

public interface Comonad<CRE> extends Unit<CRE>, Functor<CRE> {

    default <T> Higher<CRE,Higher<CRE,T>> nest(Higher<CRE,T> ds){
        return map(i->unit(i),ds);
    }
    
    default <T,R> Higher<CRE,R> coflatMap(final Function<? super Higher<CRE,T>, R> mapper,Higher<CRE,T> ds) {
        return mapper.andThen(r -> unit(r))
                     .apply(ds);
    }
    
    public <T> T extract(Higher<CRE,T> ds);
}
