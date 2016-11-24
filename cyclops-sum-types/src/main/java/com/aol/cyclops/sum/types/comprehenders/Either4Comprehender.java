package com.aol.cyclops.sum.types.comprehenders;


import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.sum.types.Either4;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

public class Either4Comprehender implements ValueComprehender<Either4> {

    @Override
    public Object filter(final Either4 t, final Predicate p) {
        return t.filter(x -> p.test(x));
    }

    @Override
    public Object map(final Either4 t, final Function fn) {
        return t.map(e -> fn.apply(e));
    }

    @Override
    public Object flatMap(final Either4 t, final Function fn) {
        return t.flatMap(e -> fn.apply(e));
    }

    @Override
    public Either4 of(final Object o) {
        return Either4.right(o);
    }

    @Override
    public Either4 empty() {
        return Either4.left1(null);
    }

    @Override
    public Class getTargetClass() {
        return Either4.class;
    }

    @Override
    public Object resolveForCrossTypeFlatMap(final Comprehender comp, final Either4 apply) {
        if (apply.isRight())
            return comp.of(apply.get());
        return comp.empty();
    }
}
