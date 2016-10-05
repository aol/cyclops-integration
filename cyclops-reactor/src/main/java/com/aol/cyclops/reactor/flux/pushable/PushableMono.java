package com.aol.cyclops.reactor.flux.pushable;

import com.aol.cyclops.data.async.Queue;

import reactor.core.publisher.Mono;

public class PushableMono<T> extends AbstractPushablePublisher<T, Queue<T>,Mono<T>> {

    public PushableMono(Queue<T> v1, Mono<T> v2) {
        super(v1, v2);

    }

    private static final long serialVersionUID = 1L;

}

