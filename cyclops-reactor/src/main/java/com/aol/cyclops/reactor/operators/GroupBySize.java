package com.aol.cyclops.reactor.operators;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.aol.cyclops.control.StreamUtils;
import com.aol.cyclops.data.collections.extensions.standard.ListXImpl;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;

import reactor.core.publisher.Flux;

public class GroupBySize<T, C extends Collection<? super T>> {

    private final Flux<T> stream;
    private final Supplier<C> factory;

    public GroupBySize(Flux<T> stream) {
        this.stream = stream;
        factory = () -> (C) new ListXImpl<>();
    }

    public GroupBySize(Flux<T> stream2, Supplier<C> factory2) {
        this.stream = stream2;
        this.factory = factory2;
    }

    public Flux<C> grouped(int groupSize) {
        if (groupSize < 1)
            throw new IllegalArgumentException(
                                               "Batch size must be 1 or more");
        SeqSubscriber<T> sub = SeqSubscriber.subscriber();
        Iterator<T> it = stream.subscribeWith(sub)
                               .iterator();

        return Flux.fromIterable(() -> new Iterator<C>() {

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public C next() {
                C list = factory.get();
                for (int i = 0; i < groupSize; i++) {
                    if (it.hasNext())
                        list.add(it.next());

                }
                return list;
            }

        });
    }

}