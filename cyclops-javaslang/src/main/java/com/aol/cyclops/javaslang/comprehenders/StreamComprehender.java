package com.aol.cyclops.javaslang.comprehenders;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.BaseStream;

import com.aol.cyclops.types.extensability.Comprehender;

import javaslang.collection.List;
import javaslang.collection.Stream;
import javaslang.collection.Vector;

public class StreamComprehender implements Comprehender<Stream> {

    @Override
    public Object map(Stream t, Function fn) {
        return t.map(s -> fn.apply(s));
    }

    @Override
    public Object executeflatMap(Stream t, Function fn) {
        return flatMap(t, input -> unwrapOtherMonadTypes(this, fn.apply(input)));
    }

    @Override
    public Object flatMap(Stream t, Function fn) {
        return t.flatMap(s -> fn.apply(s));
    }

    @Override
    public Stream of(Object o) {
        return Stream.of(o);
    }

    @Override
    public Stream empty() {
        return Stream.empty();
    }

    @Override
    public Class getTargetClass() {
        return Stream.class;
    }

    @Override
    public Object resolveForCrossTypeFlatMap(Comprehender comp, Stream apply) {
        return comp.fromIterator(apply.iterator());
    }

    static Stream unwrapOtherMonadTypes(Comprehender<Stream> comp, final Object apply) {
        if (comp.instanceOfT(apply))
            return (Stream) apply;
        if (apply instanceof java.util.stream.Stream)
            return Stream.ofAll(() -> ((java.util.stream.Stream) apply).iterator());
        if (apply instanceof Iterable)
            return Stream.ofAll(((Iterable) apply));

        if (apply instanceof Collection) {
            return Stream.ofAll((Collection) apply);
        }
        if (apply instanceof BaseStream) {
            return Stream.ofAll(() -> ((BaseStream) apply).iterator());

        }

        return Comprehender.unwrapOtherMonadTypes(comp, apply);

    }

    @Override
    public Stream fromIterator(Iterator o) {
        return Stream.ofAll(() -> o);
    }
}
