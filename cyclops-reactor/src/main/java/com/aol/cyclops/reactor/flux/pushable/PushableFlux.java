package com.aol.cyclops.reactor.flux.pushable;

import com.aol.cyclops.data.async.Queue;

import reactor.core.publisher.Flux;

public class PushableFlux<T> extends AbstractPushablePublisher<T, Queue<T>, Flux<T>> {

    public PushableFlux(Queue<T> v1, Flux<T> v2) {
        super(v1, v2);

    }

    private static final long serialVersionUID = 1L;

}

