package com.aol.cyclops.functionaljava;


import com.aol.cyclops.functionaljava.adapter.EitherAdapter;
import com.aol.cyclops.functionaljava.adapter.OptionAdapter;
import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import cyclops.monads.WitnessType;
import javaslang.collection.*;

public interface FJWitness {

    static interface OptionWitness<W extends FJWitness.OptionWitness<W>>  extends WitnessType<W> {

    }
    public static enum option implements OptionWitness<option> {
        INSTANCE;

        @Override
        public FunctionalAdapter<option> adapter() {
            return new OptionAdapter();
        }

    }
    static interface EitherWitness<W extends FJWitness.EitherWitness<W>>  extends WitnessType<W> {

    }
    public static enum either implements EitherWitness<either> {
        INSTANCE;

        @Override
        public FunctionalAdapter<either> adapter() {
            return new EitherAdapter();
        }

    }

}
