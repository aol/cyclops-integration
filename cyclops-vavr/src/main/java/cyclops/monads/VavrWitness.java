package cyclops.monads;



import com.oath.anym.extensability.MonadAdapter;
import com.oath.cyclops.vavr.adapter.*;


import io.vavr.collection.*;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public interface VavrWitness {

    public static <T> List<T> list(AnyM<list,? extends T> anyM){
        return anyM.unwrap();
    }
    public static <T> Vector<T> vector(AnyM<vector,? extends T> anyM){
        return anyM.unwrap();
    }
    public static <T> Queue<T> queue(AnyM<queue,? extends T> anyM){
        return anyM.unwrap();
    }
    public static <T> Option<T> option(AnyM<option,? extends T> anyM){
        return anyM.unwrap();
    }
    public static <T> Try<T> tryType(AnyM<tryType,? extends T> anyM){
        return anyM.unwrap();
    }
    public static <T> Future<T> future(AnyM<future,? extends T> anyM){
        return anyM.unwrap();
    }
    public static <L,T> Either<L,T> either(AnyM<either,? extends T> anyM){
        return anyM.unwrap();
    }

    static interface TraversableWitness<W extends TraversableWitness<W>>  extends WitnessType<W>{

    }
    public static enum stream implements TraversableWitness<stream> {
        INSTANCE;

        @Override
        public MonadAdapter<stream> adapter() {
            return new TraversableAdapter<stream>(INSTANCE){

                @Override
                public Traversable traversableFromIterable(Iterable value) {
                    return Stream.ofAll(value);
                }

                @Override
                public Traversable singletonTraversable(Object value) {
                    return Stream.of(value);
                }

                @Override
                public Traversable emptyTraversable() {
                    return Stream.empty();
                }
            };
        }

    }
    public static enum list implements TraversableWitness<list> {
        INSTANCE;

        @Override
        public MonadAdapter<list> adapter() {
            return new TraversableAdapter<list>(INSTANCE){

                @Override
                public Traversable traversableFromIterable(Iterable value) {
                    return List.ofAll(value);
                }

                @Override
                public Traversable singletonTraversable(Object value) {
                    return List.of(value);
                }

                @Override
                public Traversable emptyTraversable() {
                    return List.empty();
                }
            };
        }

    }
    public static enum vector implements TraversableWitness<vector> {
        INSTANCE;

        @Override
        public MonadAdapter<vector> adapter() {
            return new TraversableAdapter<vector>(INSTANCE){

                @Override
                public Traversable traversableFromIterable(Iterable value) {
                    return Vector.ofAll(value);
                }

                @Override
                public Traversable singletonTraversable(Object value) {
                    return Vector.of(value);
                }

                @Override
                public Traversable emptyTraversable() {
                    return Vector.empty();
                }
            };
        }

    }
    public static enum array implements TraversableWitness<array> {
        INSTANCE;

        @Override
        public MonadAdapter<array> adapter() {
            return new TraversableAdapter<array>(INSTANCE){

                @Override
                public Traversable traversableFromIterable(Iterable value) {
                    return Array.ofAll(value);
                }

                @Override
                public Traversable singletonTraversable(Object value) {
                    return Array.of(value);
                }

                @Override
                public Traversable emptyTraversable() {
                    return Array.empty();
                }
            };
        }

    }
    public static enum charSeq implements TraversableWitness<charSeq> {
        INSTANCE;

        @Override
        public MonadAdapter<charSeq> adapter() {
            return new TraversableAdapter<charSeq>(INSTANCE){

                @Override
                public Traversable traversableFromIterable(Iterable value) {
                    return CharSeq.ofAll(value);
                }

                @Override
                public Traversable singletonTraversable(Object value) {
                    return CharSeq.of((Character)value);
                }

                @Override
                public Traversable emptyTraversable() {
                    return CharSeq.empty();
                }
            };
        }

    }
    public static enum queue implements TraversableWitness<queue> {
        INSTANCE;

        @Override
        public MonadAdapter<queue> adapter() {
            return new TraversableAdapter<queue>(INSTANCE){

                @Override
                public Traversable traversableFromIterable(Iterable value) {
                    return Queue.ofAll(value);
                }

                @Override
                public Traversable singletonTraversable(Object value) {
                    return Queue.of(value);
                }

                @Override
                public Traversable emptyTraversable() {
                    return Queue.empty();
                }
            };
        }

    }

    static interface OptionWitness<W extends VavrWitness.OptionWitness<W>>  extends WitnessType<W> {

    }
    public static enum option implements OptionWitness<option> {
        INSTANCE;

        @Override
        public MonadAdapter<option> adapter() {
            return new OptionAdapter();
        }

    }
    static interface FutureWitness<W extends FutureWitness<W>>  extends WitnessType<W> {

    }
    public static enum future implements FutureWitness<future> {
        INSTANCE;

        @Override
        public MonadAdapter<future> adapter() {
            return new FutureAdapter();
        }

    }
    static interface EitherWitness<W extends VavrWitness.EitherWitness<W>>  extends WitnessType<W> {

    }
    public static enum either implements EitherWitness<either> {
        INSTANCE;

        @Override
        public MonadAdapter<either> adapter() {
            return new EitherAdapter();
        }

    }
    static interface TryWitness<W extends VavrWitness.TryWitness<W>>  extends WitnessType<W> {

    }
    public static enum tryType implements TryWitness<tryType> {
        INSTANCE;

        @Override
        public MonadAdapter<tryType> adapter() {
            return new TryAdapter();
        }

    }
    public static enum lazy implements WitnessType<lazy> {
        INSTANCE;

        @Override
        public MonadAdapter<lazy> adapter() {
            return null;
        }

    }
    public static enum hashSet implements TraversableWitness<hashSet> {
        INSTANCE;

        @Override
        public MonadAdapter<hashSet> adapter() {
            return new TraversableAdapter(INSTANCE){

                @Override
                public Traversable traversableFromIterable(Iterable value) {
                    return HashSet.ofAll(value);
                }

                @Override
                public Traversable singletonTraversable(Object value) {
                    return HashSet.of(value);
                }

                @Override
                public Traversable emptyTraversable() {
                    return HashSet.empty();
                }
            };
        }

    }}
