package com.aol.cyclops.hkt.typeclasses;

import java.util.function.Predicate;

import com.aol.cyclops.hkt.alias.Higher;

public interface Filterable<CRE> {

    public <T> Higher<CRE,T> filter(Predicate<? super T> predicate,  Higher<CRE,T> ds);
}
