package com.aol.cyclops.hkt.typeclasses.foldable;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.hkt.alias.Higher;

public interface Foldable<CRE> {

    public <T> T foldRight(Monoid<T> monoid, Higher<CRE,T> ds);
    
    public <T> T foldLeft(Monoid<T> monoid, Higher<CRE,T> ds);
}
