package com.aol.cyclops.reactor.types;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Represents a type that can be converted to a Reactor Flux or Mono
 * 
 * @author johnmcclean
 *
 * @param <T> type of elements contained
 */
public interface ReactorConvertable<T> {

    /**
     * Convert this type to a Reactor Flux
     * 
     * @return Reactor Flux
     */
    Flux<T> flux();

    /**
     * Convert this type to a Reactor Mono
     * 
     * @return Reactor Mono
     */
    default Mono<T> mono() {
        return Mono.from(flux());
    }

}
