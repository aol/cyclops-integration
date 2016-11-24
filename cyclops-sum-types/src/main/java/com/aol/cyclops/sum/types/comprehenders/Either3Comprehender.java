package com.aol.cyclops.sum.types.comprehenders;


import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.sum.types.Either3;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

public class Either3Comprehender implements ValueComprehender<Either3> {

    @Override
    public Object filter(final Either3 t, final Predicate p) {
        return t.filter(x -> p.test(x));
    }

    @Override
    public Object map(final Either3 t, final Function fn) {
        return t.map(e -> fn.apply(e));
    }

    @Override
    public Object flatMap(final Either3 t, final Function fn) {
        return t.flatMap(e -> fn.apply(e));
    }

    @Override
    public Either3 of(final Object o) {
        return Either3.right(o);
    }

    @Override
    public Either3 empty() {
        return Either3.left1(null);
    }

    @Override
    public Class getTargetClass() {
        return Either3.class;
    }

    @Override
    public Object resolveForCrossTypeFlatMap(final Comprehender comp, final Either3 apply) {
        if (apply.isRight())
            return comp.of(apply.get());
        return comp.empty();
    }
}
