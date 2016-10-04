package com.aol.cyclops.reactor.comprehenders;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.reactor.MonoUtils;
import com.aol.cyclops.reactor.transformer.MonoTSeq;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.mixins.Printable;

import reactor.core.publisher.Mono;

public class MonoTSeqComprehender implements Comprehender<MonoTSeq>, Printable {

    @Override
    public Object resolveForCrossTypeFlatMap(Comprehender comp, MonoTSeq apply) {

        return apply.isSeqPresent() ? comp.of(apply.stream()
                                                   .toListX())
                : comp.empty();
    }

    @Override
    public Object filter(MonoTSeq t, Predicate p) {
        return t.filter(p);
    }

    @Override
    public Object map(MonoTSeq t, Function fn) {
        return t.map(r -> fn.apply(r));
    }

    @Override
    public Object flatMap(MonoTSeq t, Function fn) {
        return t.flatMapT(r -> fn.apply(r));
    }

    @Override
    public MonoTSeq of(Object o) {
        return MonoTSeq.of(Mono.just(o));
    }

    @Override
    public MonoTSeq empty() {
        return MonoTSeq.emptyList();
    }

    @Override
    public Class getTargetClass() {
        return MonoTSeq.class;
    }

    @Override
    public MonoTSeq fromIterator(Iterator o) {
        
        return MonoTSeq.of(MonoUtils.fromIterable(() -> o));
    }

}
