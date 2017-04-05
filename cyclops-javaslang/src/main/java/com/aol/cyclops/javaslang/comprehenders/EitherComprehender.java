package com.aol.cyclops.javaslang.comprehenders;

import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

import javaslang.control.Either;
import javaslang.control.Option;

public class EitherComprehender implements ValueComprehender<Either> {

    public Object filter(Either t, Predicate p) {
        return t.filter(p);
    }

    @Override
    public Object map(Either t, Function fn) {
        return t.map(fn);
    }

    @Override
    public Object flatMap(Either t, Function fn) {
        return t.flatMap(fn);
    }

    @Override
    public Either of(Object o) {
        return Either.right(o);
    }

    @Override
    public Either empty() {
        return Either.right(Option.none());
    }

    @Override
    public Class getTargetClass() {
        return Either.class;
    }

    public Object resolveForCrossTypeFlatMap(Comprehender comp, Either apply) {
        return apply.isRight() ? comp.of(apply.get()) : comp.empty();
    }
}
