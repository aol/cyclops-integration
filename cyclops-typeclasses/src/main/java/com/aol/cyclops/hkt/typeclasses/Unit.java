package com.aol.cyclops.hkt.typeclasses;

import com.aol.cyclops.hkt.alias.Higher;
@FunctionalInterface
public interface Unit<CRE> {
    public <T> Higher<CRE,T> unit(T value);
}
