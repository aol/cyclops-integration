package com.aol.cyclops.reactor.collections.extensions.base;

import java.util.Collection;

import reactor.core.publisher.Flux;

public interface LazyFluentCollection<T,C extends Collection<T>> {
    
    C get();
    
    Flux<T> stream();
}