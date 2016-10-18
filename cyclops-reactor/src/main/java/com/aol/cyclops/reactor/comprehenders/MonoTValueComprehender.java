package com.aol.cyclops.reactor.comprehenders;

import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.reactor.transformer.MonoT;
import com.aol.cyclops.reactor.transformer.MonoTValue;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;
import com.aol.cyclops.types.mixins.Printable;

import reactor.core.publisher.Mono;

public class MonoTValueComprehender implements ValueComprehender<MonoTValue>, Printable {

    @Override
    public Object resolveForCrossTypeFlatMap(Comprehender comp, MonoTValue apply) {

        return apply.isValuePresent() ? comp.of(apply.get()) : comp.empty();
    }

    @Override
    public Object filter(MonoTValue t, Predicate p) {
        return t.filter(p);
    }

    @Override
    public Object map(MonoTValue t, Function fn) {
        return t.map(r -> fn.apply(r));
    }

    @Override
    public Object flatMap(MonoTValue t, Function fn) {

        return t.flatMapT(r -> fn.apply(r));
    }

    @Override
    public MonoTValue of(Object o) {
        return MonoTValue.of(Mono.just(o));
    }

    @Override
    public MonoTValue empty() {
        return MonoT.emptyOptional();
    }

    @Override
    public Class getTargetClass() {
        return MonoTValue.class;
    }

}
