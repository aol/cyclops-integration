package com.aol.cyclops.reactor;


import com.aol.cyclops.reactor.adapter.FluxAdapter;
import com.aol.cyclops.reactor.adapter.MonoAdapter;
import com.aol.cyclops2.internal.comprehensions.comprehenders.StreamAdapter;
import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import cyclops.monads.Witness;
import cyclops.monads.WitnessType;

public interface ReactorWitness {
    static interface FluxWitness<W extends ReactorWitness.FluxWitness<W>>  extends WitnessType<W> {

    }
    public static enum flux implements FluxWitness<ReactorWitness.flux> {
        INSTANCE;

        @Override
        public FunctionalAdapter<flux> adapter() {
            return new FluxAdapter();
        }

    }
    static interface MonoWitness<W extends ReactorWitness.MonoWitness<W>>  extends WitnessType<W> {

    }
    public static enum mono implements MonoWitness<mono> {
        INSTANCE;

        @Override
        public FunctionalAdapter<mono> adapter() {
            return new MonoAdapter();
        }

    }
}
