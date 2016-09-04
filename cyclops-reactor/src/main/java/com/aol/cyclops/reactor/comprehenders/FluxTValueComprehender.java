package com.aol.cyclops.reactor.comprehenders;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.reactor.transformer.FluxTValue;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.mixins.Printable;

import reactor.core.publisher.Flux;

public class FluxTValueComprehender implements Comprehender<FluxTValue>, Printable {

    @Override
    public Object resolveForCrossTypeFlatMap(Comprehender comp, FluxTValue apply) {

        return apply.isStreamPresent() ? comp.of(apply.get()) : comp.empty();
    }

    @Override
    public Object filter(FluxTValue t, Predicate p) {
        return t.filter(p);
    }

    @Override
    public Object map(FluxTValue t, Function fn) {
        return t.map(r -> fn.apply(r));
    }

    @Override
    public Object flatMap(FluxTValue t, Function fn) {
        return t.flatMapT(r -> fn.apply(r));
    }

    @Override
    public FluxTValue of(Object o) {
        return FluxTValue.of(Flux.just(o));
    }

    @Override
    public FluxTValue empty() {
        return FluxTValue.emptyOptional();
    }

    @Override
    public Class getTargetClass() {
        return FluxTValue.class;
    }

    @Override
    public FluxTValue fromIterator(Iterator o) {
        return FluxTValue.of(Flux.fromIterable(() -> o));
    }

}
