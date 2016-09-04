package com.aol.cyclops.javaslang.comprehenders;

import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

import javaslang.control.Try;

public class TryComprehender implements ValueComprehender<Try> {

    public Object filter(Try t, Predicate p) {
        return t.filter(x -> p.test(x));
    }

    @Override
    public Object map(Try t, Function fn) {
        return t.map(i -> fn.apply(i));
    }

    @Override
    public Object flatMap(Try t, Function fn) {
        return t.flatMap(i -> fn.apply(i));
    }

    @Override
    public Try of(Object o) {
        return Try.of(() -> o);
    }

    @Override
    public Try empty() {
        return Try.run(() -> {
        });
    }

    @Override
    public Class getTargetClass() {
        return Try.class;
    }

    public Object resolveForCrossTypeFlatMap(Comprehender comp, Try apply) {
        return comp.of(apply.get());
    }

}
