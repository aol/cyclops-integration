package com.aol.cyclops.reactor.operators;



import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.data.collections.extensions.standard.ListXImpl;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;

import reactor.core.publisher.Flux;

public class GroupedWhile<T, C extends Collection<? super T>> {
    private static final Object UNSET = new Object();
    private final Flux<T> stream;
    private final Supplier<C> factory;

    public GroupedWhile(Flux<T> stream) {
        this.stream = stream;
        factory = () -> (C) new ListXImpl();
    }

    public GroupedWhile(Flux<T> stream, Supplier<C> factory) {
        this.stream = stream;
        this.factory = factory;
    }

    public Flux<C> batchWhile(Predicate<? super T> predicate) {
        SeqSubscriber<T> sub = SeqSubscriber.subscriber();
        Iterator<T> it = stream.subscribeWith(sub).iterator();
       
        return Flux.fromIterable(()->new Iterator<C>() {
            T value = (T) UNSET;

            @Override
            public boolean hasNext() {
                return value != UNSET || it.hasNext();
            }

            @Override
            public C next() {

                C list = factory.get();
                if (value != UNSET)
                    list.add(value);
                T value;

                label: while (it.hasNext()) {
                    value = it.next();
                    list.add(value);

                    if (!predicate.test(value)) {
                        value = (T) UNSET;
                        break label;
                    }
                    value = (T) UNSET;

                }
                return list;
            }

        })
                          .filter(l -> l.size() > 0);
    }

}
