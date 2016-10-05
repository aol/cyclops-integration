package com.aol.cyclops.reactor.flux.pushable;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.jooq.lambda.tuple.Tuple2;
import org.reactivestreams.Publisher;

import com.aol.cyclops.data.async.Adapter;

public abstract class AbstractPushablePublisher<T, X extends Adapter<T>, R  extends Publisher<T>> extends Tuple2<X, R> {

    public AbstractPushablePublisher(X v1, R v2) {
        super(v1, v2);
    }

    public X getInput() {
        return v1;
    }

    public R getFlux() {
        return v2;
    }

    public <U> U visit(BiFunction<? super X, ? super R, ? extends U> visitor) {
        return visitor.apply(v1, v2);
    }

    public void peekFlux(Consumer<? super R> consumer) {
        consumer.accept(v2);
    }

    public void peekInput(Consumer<? super X> consumer) {
        consumer.accept(v1);
    }

    private static final long serialVersionUID = 1L;

}