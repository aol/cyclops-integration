package com.aol.cyclops.reactor.transformers;

import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.reactor.transformer.MonoT;
import com.aol.cyclops.types.Traversable;

import reactor.core.publisher.Mono;

public class MonoTSeqTraversableTest extends AbstractTraversableTest {

    @Override
    public <T> Traversable<T> of(T... elements) {
        ListX<Mono<T>> list = ListX.<T>of(elements).map(Mono::just);
        return MonoT.fromIterable(list);
    }

    @Override
    public <T> Traversable<T> empty() {
        return MonoT.emptyList();
    }

}
