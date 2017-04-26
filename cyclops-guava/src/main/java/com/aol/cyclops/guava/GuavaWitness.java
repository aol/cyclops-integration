package com.aol.cyclops.guava;


import com.aol.cyclops.guava.adapter.FluentIterableAdapter;
import com.aol.cyclops.guava.adapter.OptionalAdapter;

import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import cyclops.monads.WitnessType;

public interface GuavaWitness {
    static interface FluentIterableWitness<W extends FluentIterableWitness<W>>  extends WitnessType<W> {

    }
    public static enum fluentIterable implements FluentIterableWitness<fluentIterable> {
        INSTANCE;

        @Override
        public FunctionalAdapter<fluentIterable> adapter() {
            return new FluentIterableAdapter();
        }

    }
    static interface OptionalWitness<W extends GuavaWitness.OptionalWitness<W>>  extends WitnessType<W> {

    }
    public static enum optional implements OptionalWitness<optional> {
        INSTANCE;

        @Override
        public FunctionalAdapter<optional> adapter() {
            return new OptionalAdapter();
        }

    }
}
