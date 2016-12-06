package com.aol.cyclops.sum.types.comprehenders;


import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.sum.types.Either4;
import com.aol.cyclops.sum.types.Either5;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

public class Either5Comprehender implements ValueComprehender<Either5> {

    @Override
    public Object filter(final Either5 t, final Predicate p) {
        return t.filter(x -> p.test(x));
    }

    @Override
    public Object map(final Either5 t, final Function fn) {
        return t.map(e -> fn.apply(e));
    }

    @Override
    public Object flatMap(final Either5 t, final Function fn) {
        return t.flatMap(e -> fn.apply(e));
    }

    @Override
    public Either5 of(final Object o) {
        return Either5.right(o);
    }

    @Override
    public Either5 empty() {
        return Either5.left1(null);
    }

    @Override
    public Class getTargetClass() {
        return Either5.class;
    }

    @Override
    public Object resolveForCrossTypeFlatMap(final Comprehender comp, final Either5 apply) {
        if (apply.isRight())
            return comp.of(apply.get());
        return comp.empty();
    }
}
