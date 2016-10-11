package com.aol.cyclops.reactor.collections.extensions.base;

import java.util.Collection;

import reactor.core.publisher.Flux;

/**
 * Interface for a wrapper around a Fluent, Lazy Collection
 * 
 * @author johnmcclean
 *
 * @param <T>  the type of elements held in the wrapped collection
 * @param <C> The wrapped Collection type
 */
public interface LazyFluentCollection<T,C extends Collection<T>> {
    
    /**
     * @return Wrapped Collection
     */
    C get();
    
    /**
     * @return Collection data inside a stream - a Reactor Flux
     */
    Flux<T> flux();
}