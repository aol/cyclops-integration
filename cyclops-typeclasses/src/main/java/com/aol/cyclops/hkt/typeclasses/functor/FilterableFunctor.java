package com.aol.cyclops.hkt.typeclasses.functor;

import com.aol.cyclops.hkt.typeclasses.Filterable;

public interface FilterableFunctor<CRE> extends Functor<CRE>, Filterable<CRE> {

}
