package com.aol.cyclops.functionaljava;


import com.aol.cyclops.functionaljava.adapter.EitherAdapter;
import com.aol.cyclops.functionaljava.adapter.ListAdapter;
import com.aol.cyclops.functionaljava.adapter.OptionAdapter;
import com.aol.cyclops.functionaljava.adapter.ValidationAdapter;
import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import cyclops.monads.WitnessType;
import javaslang.collection.*;

public interface FJWitness {
    static interface ListWitness<W extends FJWitness.ListWitness<W>>  extends WitnessType<W> {

    }
    public static enum list implements ListWitness<list> {
        INSTANCE;

        @Override
        public FunctionalAdapter<list> adapter() {
            return new ListAdapter();
        }

    }
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

    static interface ValidationWitness<W extends FJWitness.ValidationWitness<W>>  extends WitnessType<W> {

    }
    public static enum validation implements ValidationWitness<validation> {
        INSTANCE;

        @Override
        public FunctionalAdapter<validation> adapter() {
            return new ValidationAdapter();
        }

    }

}
