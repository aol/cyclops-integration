package cyclops.monads;


import com.aol.cyclops.functionaljava.adapter.*;
import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import fj.data.Either;
import fj.data.Option;



public interface FJWitness {
    public static <L,T> Either<L,T> either(AnyM<either,? extends T> anyM){
        return anyM.unwrap();
    }
    public static <T> Option<T> option(AnyM<option,? extends T> anyM){
        return anyM.unwrap();
    }
    static interface ListWitness<W extends FJWitness.ListWitness<W>>  extends WitnessType<W> {

    }
    public static enum list implements ListWitness<list> {
        INSTANCE;

        @Override
        public FunctionalAdapter<list> adapter() {
            return new ListAdapter();
        }

    }
    static interface NonEmptyListWitness<W extends FJWitness.NonEmptyListWitness<W>>  extends WitnessType<W> {

    }
    public static enum nonEmptyList implements NonEmptyListWitness<nonEmptyList> {
        INSTANCE;

        @Override
        public FunctionalAdapter<nonEmptyList> adapter() {
            return new NonEmptyListAdapter();
        }

    }
    static interface StreamWitness<W extends StreamWitness<W>>  extends WitnessType<W> {

    }
    public static enum stream implements StreamWitness<stream> {
        INSTANCE;

        @Override
        public FunctionalAdapter<stream> adapter() {
            return new StreamAdapter();
        }

    }
    static interface IterableWWitness<W extends IterableWWitness<W>>  extends WitnessType<W> {

    }
    public static enum iterableW implements IterableWWitness<iterableW> {
        INSTANCE;

        @Override
        public FunctionalAdapter<iterableW> adapter() {
            return new IterableWAdapter();
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
