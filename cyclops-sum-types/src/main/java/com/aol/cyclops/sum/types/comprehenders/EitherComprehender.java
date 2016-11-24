package com.aol.cyclops.sum.types.comprehenders;


import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.sum.types.Either;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

public class EitherComprehender implements ValueComprehender<Either> {

    @Override
    public Object filter(final Either t, final Predicate p) {
        return t.filter(x -> p.test(x));
    }

    @Override
    public Object map(final Either t, final Function fn) {
        return t.map(e -> fn.apply(e));
    }

    @Override
    public Object flatMap(final Either t, final Function fn) {
        return t.flatMap(e -> fn.apply(e));
    }

    @Override
    public Either of(final Object o) {
        return Either.right(o);
    }

    @Override
    public Either empty() {
        return Either.left(null);
    }

    @Override
    public Class getTargetClass() {
        return Either.class;
    }

    @Override
    public Object resolveForCrossTypeFlatMap(final Comprehender comp, final Either apply) {
        if (apply.isRight())
            return comp.of(apply.get());
        return comp.empty();
    }
}
