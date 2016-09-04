package com.aol.cyclops.reactor.comprehenders;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.aol.cyclops.internal.comprehensions.comprehenders.MaterializedList;
import com.aol.cyclops.reactor.transformer.FluxTSeq;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.mixins.Printable;

import reactor.core.publisher.Flux;

public class FluxTSeqComprehender implements Comprehender<FluxTSeq>, Printable {

    @Override
    public Object resolveForCrossTypeFlatMap(Comprehender comp, FluxTSeq apply) {
        List list = (List) apply.stream()
                                .collect(Collectors.toCollection(MaterializedList::new));
        return list.size() > 0 ? comp.of(list) : comp.empty();
    }

    @Override
    public Object filter(FluxTSeq t, Predicate p) {
        return t.filter(p);
    }

    @Override
    public Object map(FluxTSeq t, Function fn) {
        return t.map(r -> fn.apply(r));
    }

    @Override
    public Object flatMap(FluxTSeq t, Function fn) {
        return t.flatMapT(r -> fn.apply(r));
    }

    @Override
    public FluxTSeq of(Object o) {
        return FluxTSeq.of(Flux.just(o));
    }

    @Override
    public FluxTSeq empty() {
        return FluxTSeq.emptyStream();
    }

    @Override
    public Class getTargetClass() {
        return FluxTSeq.class;
    }

    @Override
    public FluxTSeq fromIterator(Iterator o) {
        return FluxTSeq.of(Flux.fromIterable(() -> o));
    }

}
