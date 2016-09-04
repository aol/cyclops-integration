package com.aol.cyclops.javaslang.comprehenders;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.BaseStream;

import com.aol.cyclops.types.extensability.Comprehender;

import javaslang.collection.Vector;

public class VectorComprehender implements Comprehender<Vector> {

    @Override
    public Object map(Vector t, Function fn) {
        return t.map(s -> fn.apply(s));
    }

    @Override
    public Object executeflatMap(Vector t, Function fn) {
        return flatMap(t, input -> unwrapOtherMonadTypes(this, fn.apply(input)));
    }

    @Override
    public Object flatMap(Vector t, Function fn) {
        return t.flatMap(s -> fn.apply(s));
    }

    @Override
    public Vector of(Object o) {
        return Vector.of(o);
    }

    @Override
    public Vector empty() {
        return Vector.empty();
    }

    @Override
    public Class getTargetClass() {
        return Vector.class;
    }

    @Override
    public Object resolveForCrossTypeFlatMap(Comprehender comp, Vector apply) {
        return comp.fromIterator(apply.iterator());
    }

    static Vector unwrapOtherMonadTypes(Comprehender<Vector> comp, Object apply) {
        if (comp.instanceOfT(apply))
            return (Vector) apply;
        if (apply instanceof java.util.stream.Stream)
            return Vector.of(((java.util.stream.Stream) apply).iterator());
        if (apply instanceof Iterable)
            return Vector.of((Iterable) apply);

        if (apply instanceof Collection) {
            return Vector.ofAll((Collection) apply);
        }
        final Object finalApply = apply;
        if (apply instanceof BaseStream) {
            return Vector.ofAll(() -> ((BaseStream) finalApply).iterator());

        }
        return Comprehender.unwrapOtherMonadTypes(comp, apply);

    }

    @Override
    public Vector fromIterator(Iterator o) {
        return Vector.ofAll(() -> o);
    }

}
