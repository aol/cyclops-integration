package com.aol.cyclops.rx.comprehenders;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.aol.cyclops.internal.comprehensions.comprehenders.MaterializedList;
import com.aol.cyclops.rx.transformer.ObservableTSeq;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.mixins.Printable;

import rx.Observable;

public class ObservableTSeqComprehender implements Comprehender<ObservableTSeq>, Printable {

    @Override
    public Object resolveForCrossTypeFlatMap(Comprehender comp, ObservableTSeq apply) {
        List list = (List) apply.stream()
                                .collect(Collectors.toCollection(MaterializedList::new));
        return list.size() > 0 ? comp.of(list) : comp.empty();
    }

    @Override
    public Object filter(ObservableTSeq t, Predicate p) {
        return t.filter(p);
    }

    @Override
    public Object map(ObservableTSeq t, Function fn) {
        return t.map(r -> fn.apply(r));
    }

    @Override
    public Object flatMap(ObservableTSeq t, Function fn) {
        return t.flatMapT(r -> fn.apply(r));
    }

    @Override
    public ObservableTSeq of(Object o) {
        return ObservableTSeq.of(Observable.just(o));
    }

    @Override
    public ObservableTSeq empty() {
        return ObservableTSeq.emptyStream();
    }

    @Override
    public Class getTargetClass() {
        return ObservableTSeq.class;
    }

    @Override
    public ObservableTSeq fromIterator(Iterator o) {
        return ObservableTSeq.of(Observable.from(() -> o));
    }

}
