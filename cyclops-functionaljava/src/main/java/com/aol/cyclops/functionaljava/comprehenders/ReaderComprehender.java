package com.aol.cyclops.functionaljava.comprehenders;

import java.util.function.Function;

import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

import fj.data.Reader;

public class ReaderComprehender implements ValueComprehender<Reader> {

    @Override
    public Object resolveForCrossTypeFlatMap(Comprehender comp, Reader apply) {
        return comp.of(apply.getFunction());
    }

    @Override
    public Object map(Reader t, Function fn) {
        return t.map(r -> fn.apply(r));
    }

    @Override
    public Object flatMap(Reader t, Function fn) {
        return t.bind(r -> fn.apply(r));
    }

    @Override
    public Reader of(Object o) {
        return Reader.constant(o);
    }

    @Override
    public Reader empty() {
        return Reader.constant(null);
    }

    @Override
    public Class getTargetClass() {
        return Reader.class;
    }

}
