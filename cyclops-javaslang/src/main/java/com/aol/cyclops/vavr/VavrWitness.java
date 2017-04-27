package com.aol.cyclops.vavr;


import com.aol.cyclops.guava.adapter.FluentIterableAdapter;
import com.aol.cyclops.guava.adapter.OptionalAdapter;
import com.aol.cyclops.vavr.adapter.OptionAdapter;
import com.aol.cyclops.vavr.adapter.TraversableAdapter;
import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import cyclops.monads.Witness;
import cyclops.monads.WitnessType;

public interface VavrWitness {
    static interface TraversableWitness<W extends TraversableWitness<W>>  extends WitnessType<W>{

    }
    public static enum stream implements TraversableWitness<stream> {
        INSTANCE;

        @Override
        public FunctionalAdapter<stream> adapter() {
            return new TraversableAdapter();
        }

    }
    static interface OptionalWitness<W extends VavrWitness.OptionalWitness<W>>  extends WitnessType<W> {

    }
    public static enum option implements OptionalWitness<option> {
        INSTANCE;

        @Override
        public FunctionalAdapter<option> adapter() {
            return new OptionAdapter();
        }

    }
}
