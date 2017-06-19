package cyclops.monads.transformers.rx2;


import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.MonadicValue;
import com.aol.cyclops2.types.Value;
import com.aol.cyclops2.types.Zippable;
import com.aol.cyclops2.types.anyM.transformers.ValueTransformer;
import com.aol.cyclops2.types.foldable.To;
import com.aol.cyclops2.types.functor.Transformable;
import cyclops.async.Future;
import cyclops.companion.rx2.Functions;
import cyclops.companion.rx2.Maybes;
import cyclops.companion.rx2.Singles;
import cyclops.control.Trampoline;
import cyclops.function.Fn3;
import cyclops.function.Fn4;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.stream.ReactiveSeq;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.reactivestreams.Publisher;


import java.util.Iterator;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Monad Transformer for Single's nested within another monadic type

 *
 * SingleT allows the deeply wrapped Single to be manipulating within it's nested /contained context
 *
 * @author johnmcclean
 *
 * @param <T> Type of data stored inside the nested Single(s)
 */
public final class SingleT<W extends WitnessType<W>,T> extends ValueTransformer<W,T>
        implements To<SingleT<W,T>>,
        Transformable<T>,
        Filters<T> {

    private final AnyM<W,Single<T>> run;

    @Override
    public Iterator<T> iterator() {
        return stream().iterator();
    }

    @Override
    public ReactiveSeq<T> stream() {
        return run.stream().map(Single::blockingGet);
    }



    /**
     * @return The wrapped AnyM
     */
    @Override
    public AnyM<W,Single<T>> unwrap() {
        return run;
    }

    public <R> R unwrapTo(Function<? super AnyM<W,Single<T>>, ? extends R> fn) {
        return unwrap().to(fn);
    }

    private SingleT(final AnyM<W,Single<T>> run) {
        this.run = run;
    }


    @Override @Deprecated (/*DO NOT USE INTERNAL USE ONLY*/)
    protected <R> SingleT<W,R> unitAnyM(AnyM<W,? super MonadicValue<R>> traversable) {

        return of((AnyM) traversable);
    }

    @Override
    public AnyM<W,? extends MonadicValue<T>> transformerStream() {

        return run.map(m-> Future.fromPublisher(m.toFlowable()));
    }

    @Override
    public SingleT<W,T> filter(final Predicate<? super T> test) {
        return of(run.map(f->f.map(in->Tuple.tuple(in,test.test(in))))
                .filter( f->f.blockingGet().v2 )
                .map( f->f.map(in->in.v1)));
    }

    /**
     * Peek at the current value of the Single
     * <pre>
     * {@code
     *    SingleWT.of(AnyM.fromStream(Arrays.asSingleW(10))
     *             .peek(System.out::println);
     *
     *     //prints 10
     * }
     * </pre>
     *
     * @param peek  Consumer to accept current value of Single
     * @return SingleWT with peek call
     */
    @Override
    public SingleT<W,T> peek(final Consumer<? super T> peek) {
        return map(e->{
            peek.accept(e);
            return e;
        });

    }

    /**
     * Map the wrapped Single
     *
     * <pre>
     * {@code
     *  SingleWT.of(AnyM.fromStream(Arrays.asSingleW(10))
     *             .map(t->t=t+1);
     *
     *
     *  //SingleWT<AnyMSeq<Stream<Single[11]>>>
     * }
     * </pre>
     *
     * @param f Mapping function for the wrapped Single
     * @return SingleWT that applies the map function to the wrapped Single
     */
    @Override
    public <B> SingleT<W,B> map(final Function<? super T, ? extends B> f) {
        return new SingleT<W,B>(
                run.map(o -> o.map(Functions.rxFunction(f))));
    }


    /**
     * Flat Map the wrapped Single

     * @param f FlatMap function
     * @return SingleT that applies the flatMap function to the wrapped Single
     */

    public <B> SingleT<W,B> flatMapT(final Function<? super T, SingleT<W,B>> f) {
        SingleT<W, B> r = of(run.map(future -> future.flatMap(a -> {
            Single<B> m = f.apply(a).run.stream()
                    .toList()
                    .get(0);
            return m;
        })));
        return r;
    }

    private static <W extends WitnessType<W>,B> AnyM<W,Single<B>> narrow(final AnyM<W,Single<? extends B>> run) {
        return (AnyM) run;
    }

    @Override
    public <B> SingleT<W,B> flatMap(final Function<? super T, ? extends MonadicValue<? extends B>> f) {

        final AnyM<W,Single<? extends B>> mapped = run.map(o -> o.flatMap(in->{
            MonadicValue<? extends B> r = f.apply(in);
            Single<B> r2 = Singles.fromValue((MonadicValue<B>) r);
            return r2;
        }));
        return of(narrow(mapped));

    }

    /**
     * Lift a function into one that accepts and returns an SingleWT
     * This allows multiple monad types to add functionality to existing function and methods
     *
     * e.g. to add list handling  / iteration (via Single) and iteration (via Stream) to an existing function
     * <pre>
     * {@code
    Function<Integer,Integer> add2 = i -> i+2;
    Function<SingleWT<Integer>, SingleWT<Integer>> optTAdd2 = SingleWT.lift(add2);

    Stream<Integer> withNulls = Stream.of(1,2,3);
    AnyMSeq<Integer> reactiveStream = AnyM.fromStream(withNulls);
    AnyMSeq<Single<Integer>> streamOpt = reactiveStream.map(Single::completedSingle);
    List<Integer> results = optTAdd2.apply(SingleWT.of(streamOpt))
    .unwrap()
    .<Stream<Single<Integer>>>unwrap()
    .map(Single::join)
    .collect(Collectors.toList());


    //Single.completedSingle(List[3,4]);
     *
     *
     * }</pre>
     *
     *
     * @param fn Function to enhance with functionality from Single and another monad type
     * @return Function that accepts and returns an SingleWT
     */
    public static <W extends WitnessType<W>,U, R> Function<SingleT<W,U>, SingleT<W,R>> lift(final Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Lift a BiFunction into one that accepts and returns  SingleWTs
     * This allows multiple monad types to add functionality to existing function and methods
     *
     * e.g. to add list handling / iteration (via Single), iteration (via Stream)  and asynchronous execution (Single)
     * to an existing function
     *
     * <pre>
     * {@code
    BiFunction<Integer,Integer,Integer> add = (a,b) -> a+b;
    BiFunction<SingleWT<Integer>,SingleWT<Integer>,SingleWT<Integer>> optTAdd2 = SingleWT.lift2(add);

    Stream<Integer> withNulls = Stream.of(1,2,3);
    AnyMSeq<Integer> reactiveStream = AnyM.ofMonad(withNulls);
    AnyMSeq<Single<Integer>> streamOpt = reactiveStream.map(Single::completedSingle);

    Single<Single<Integer>> two = Single.completedSingle(Single.completedSingle(2));
    AnyMSeq<Single<Integer>> Single=  AnyM.fromSingleW(two);
    List<Integer> results = optTAdd2.apply(SingleWT.of(streamOpt),SingleWT.of(Single))
    .unwrap()
    .<Stream<Single<Integer>>>unwrap()
    .map(Single::join)
    .collect(Collectors.toList());

    //Single.completedSingle(List[3,4,5]);
    }
     </pre>
     * @param fn BiFunction to enhance with functionality from Single and another monad type
     * @return Function that accepts and returns an SingleWT
     */
    public static <W extends WitnessType<W>, U1,  U2, R> BiFunction<SingleT<W,U1>, SingleT<W,U2>, SingleT<W,R>> lift2(
            final BiFunction<? super U1, ? super U2, ? extends R> fn) {
        return (optTu1, optTu2) -> optTu1.flatMapT(input1 -> optTu2.map(input2 -> fn.apply(input1, input2)));
    }

    /**
     * Construct an SingleWT from an AnyM that contains a monad type that contains type other than Single
     * The values in the underlying monad will be mapped to Single<A>
     *
     * @param anyM AnyM that doesn't contain a monad wrapping an Single
     * @return SingleWT
     */
    public static <W extends WitnessType<W>,A> SingleT<W,A> fromAnyM(final AnyM<W,A> anyM) {
        return of(anyM.map(Single::just));
    }

    /**
     * Construct an SingleWT from an AnyM that wraps a monad containing  SingleWs
     *
     * @param monads AnyM that contains a monad wrapping an Single
     * @return SingleWT
     */
    public static <W extends WitnessType<W>,A> SingleT<W,A> of(final AnyM<W,Single<A>> monads) {
        return new SingleT<>(
                monads);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("SingleT[%s]", run.unwrap().toString());
    }




    public <R> SingleT<W,R> unitIterator(final Iterator<R> it) {
        return of(run.unitIterator(it)
                .map(i -> Single.just(i)));
    }

    @Override
    public <R> SingleT<W,R> unit(final R value) {
        return of(run.unit(Single.just(value)));
    }

    @Override
    public <R> SingleT<W,R> empty() {
        return of(run.unit(Single.<R>just(null)));
    }




    @Override
    public int hashCode() {
        return run.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SingleT) {
            return run.equals(((SingleT) o).run);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#combine(com.aol.cyclops2.types.Value, java.util.function.BiFunction)
     */
    @Override
    public <T2, R> SingleT<W,R> combine(Value<? extends T2> app,
                                        BiFunction<? super T, ? super T2, ? extends R> fn) {
        return (SingleT<W,R>)super.combine(app, fn);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#combine(java.util.function.BinaryOperator, com.aol.cyclops2.types.Combiner)
     */
    @Override
    public SingleT<W, T> zip(BinaryOperator<Zippable<T>> combiner, Zippable<T> app) {

        return (SingleT<W, T>)super.zip(combiner, app);
    }



    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#iterate(java.util.function.UnaryOperator)
     */
    @Override
    public AnyM<W, ? extends ReactiveSeq<T>> iterate(UnaryOperator<T> fn) {

        return super.iterate(fn);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#generate()
     */
    @Override
    public AnyM<W, ? extends ReactiveSeq<T>> generate() {

        return super.generate();
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#zip(java.lang.Iterable, java.util.function.BiFunction)
     */
    @Override
    public <T2, R> SingleT<W, R> zip(Iterable<? extends T2> iterable,
                                     BiFunction<? super T, ? super T2, ? extends R> fn) {

        return (SingleT<W, R>)super.zip(iterable, fn);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#zip(java.util.function.BiFunction, org.reactivestreams.Publisher)
     */
    @Override
    public <T2, R> SingleT<W, R> zipP(Publisher<? extends T2> publisher, BiFunction<? super T, ? super T2, ? extends R> fn) {

        return (SingleT<W, R>)super.zipP(publisher,fn);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#zip(java.util.reactiveStream.Stream)
     */
    @Override
    public <U> SingleT<W, Tuple2<T, U>> zipS(Stream<? extends U> other) {

        return (SingleT)super.zipS(other);
    }


    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#zip(java.lang.Iterable)
     */
    @Override
    public <U> SingleT<W, Tuple2<T, U>> zip(Iterable<? extends U> other) {

        return (SingleT)super.zip(other);
    }


    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#forEach4(java.util.function.Function, java.util.function.BiFunction, com.aol.cyclops2.util.function.TriFunction, com.aol.cyclops2.util.function.QuadFunction)
     */
    @Override
    public <T2, R1, R2, R3, R> SingleT<W, R> forEach4(Function<? super T, ? extends MonadicValue<R1>> value1,
                                                      BiFunction<? super T, ? super R1, ? extends MonadicValue<R2>> value2,
                                                      Fn3<? super T, ? super R1, ? super R2, ? extends MonadicValue<R3>> value3,
                                                      Fn4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return (SingleT<W, R>)super.forEach4(value1, value2, value3, yieldingFunction);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#forEach4(java.util.function.Function, java.util.function.BiFunction, com.aol.cyclops2.util.function.TriFunction, com.aol.cyclops2.util.function.QuadFunction, com.aol.cyclops2.util.function.QuadFunction)
     */
    @Override
    public <T2, R1, R2, R3, R> SingleT<W, R> forEach4(Function<? super T, ? extends MonadicValue<R1>> value1,
                                                      BiFunction<? super T, ? super R1, ? extends MonadicValue<R2>> value2,
                                                      Fn3<? super T, ? super R1, ? super R2, ? extends MonadicValue<R3>> value3,
                                                      Fn4<? super T, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                      Fn4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return (SingleT<W, R>)super.forEach4(value1, value2, value3, filterFunction, yieldingFunction);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#forEach3(java.util.function.Function, java.util.function.BiFunction, com.aol.cyclops2.util.function.TriFunction)
     */
    @Override
    public <T2, R1, R2, R> SingleT<W, R> forEach3(Function<? super T, ? extends MonadicValue<R1>> value1,
                                                  BiFunction<? super T, ? super R1, ? extends MonadicValue<R2>> value2,
                                                  Fn3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return (SingleT<W, R>)super.forEach3(value1, value2, yieldingFunction);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#forEach3(java.util.function.Function, java.util.function.BiFunction, com.aol.cyclops2.util.function.TriFunction, com.aol.cyclops2.util.function.TriFunction)
     */
    @Override
    public <T2, R1, R2, R> SingleT<W, R> forEach3(Function<? super T, ? extends MonadicValue<R1>> value1,
                                                  BiFunction<? super T, ? super R1, ? extends MonadicValue<R2>> value2,
                                                  Fn3<? super T, ? super R1, ? super R2, Boolean> filterFunction,
                                                  Fn3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return (SingleT<W, R>)super.forEach3(value1, value2, filterFunction, yieldingFunction);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#forEach2(java.util.function.Function, java.util.function.BiFunction)
     */
    @Override
    public <R1, R> SingleT<W, R> forEach2(Function<? super T, ? extends MonadicValue<R1>> value1,
                                          BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return (SingleT<W, R>)super.forEach2(value1, yieldingFunction);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#forEach2(java.util.function.Function, java.util.function.BiFunction, java.util.function.BiFunction)
     */
    @Override
    public <R1, R> SingleT<W, R> forEach2(Function<? super T, ? extends MonadicValue<R1>> value1,
                                          BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                          BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return (SingleT<W, R>)super.forEach2(value1, filterFunction, yieldingFunction);
    }



    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#flatMapI(java.util.function.Function)
     */
    @Override
    public <R> SingleT<W, R> flatMapIterable(Function<? super T, ? extends Iterable<? extends R>> mapper) {

        return (SingleT<W, R>)super.flatMapIterable(mapper);
    }

    /* (non-Javadoc)
     * @see cyclops2.monads.transformers.values.ValueTransformer#flatMapP(java.util.function.Function)
     */
    @Override
    public <R> SingleT<W, R> flatMapPublisher(Function<? super T, ? extends Publisher<? extends R>> mapper) {

        return (SingleT<W, R>)super.flatMapPublisher(mapper);
    }
    public <T2, R1, R2, R3, R> SingleT<W,R> forEach4M(Function<? super T, ? extends SingleT<W,R1>> value1,
                                                      BiFunction<? super T, ? super R1, ? extends SingleT<W,R2>> value2,
                                                      Fn3<? super T, ? super R1, ? super R2, ? extends SingleT<W,R3>> value3,
                                                      Fn4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {
        return this.flatMapT(in->value1.apply(in)
                .flatMapT(in2-> value2.apply(in,in2)
                        .flatMapT(in3->value3.apply(in,in2,in3)
                                .map(in4->yieldingFunction.apply(in,in2,in3,in4)))));

    }
    public <T2, R1, R2, R3, R> SingleT<W,R> forEach4M(Function<? super T, ? extends SingleT<W,R1>> value1,
                                                      BiFunction<? super T, ? super R1, ? extends SingleT<W,R2>> value2,
                                                      Fn3<? super T, ? super R1, ? super R2, ? extends SingleT<W,R3>> value3,
                                                      Fn4<? super T, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                                                      Fn4<? super T, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {
        return this.flatMapT(in->value1.apply(in)
                .flatMapT(in2-> value2.apply(in,in2)
                        .flatMapT(in3->value3.apply(in,in2,in3)
                                .filter(in4->filterFunction.apply(in,in2,in3,in4))
                                .map(in4->yieldingFunction.apply(in,in2,in3,in4)))));

    }

    public <T2, R1, R2, R> SingleT<W,R> forEach3M(Function<? super T, ? extends SingleT<W,R1>> value1,
                                                  BiFunction<? super T, ? super R1, ? extends SingleT<W,R2>> value2,
                                                  Fn3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return this.flatMapT(in->value1.apply(in).flatMapT(in2-> value2.apply(in,in2)
                .map(in3->yieldingFunction.apply(in,in2,in3))));

    }

    public <T2, R1, R2, R> SingleT<W,R> forEach3M(Function<? super T, ? extends SingleT<W,R1>> value1,
                                                  BiFunction<? super T, ? super R1, ? extends SingleT<W,R2>> value2,
                                                  Fn3<? super T, ? super R1, ? super R2, Boolean> filterFunction,
                                                  Fn3<? super T, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return this.flatMapT(in->value1.apply(in).flatMapT(in2-> value2.apply(in,in2).filter(in3->filterFunction.apply(in,in2,in3))
                .map(in3->yieldingFunction.apply(in,in2,in3))));

    }
    public <R1, R> SingleT<W,R> forEach2M(Function<? super T, ? extends SingleT<W,R1>> value1,
                                          BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {


        return this.flatMapT(in->value1.apply(in)
                .map(in2->yieldingFunction.apply(in,in2)));
    }

    public <R1, R> SingleT<W,R> forEach2M(Function<? super T, ? extends SingleT<W,R1>> value1,
                                          BiFunction<? super T, ? super R1, Boolean> filterFunction,
                                          BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {


        return this.flatMapT(in->value1.apply(in)
                .filter(in2->filterFunction.apply(in,in2))
                .map(in2->yieldingFunction.apply(in,in2)));
    }

    public String mkString(){
        return toString();
    }

    @Override
    public <U> SingleT<W,U> cast(Class<? extends U> type) {
        return (SingleT<W,U>)super.cast(type);
    }

    @Override
    public <U> SingleT<W,U> ofType(Class<? extends U> type) {
        return (SingleT<W,U>)Filters.super.ofType(type);
    }

    @Override
    public SingleT<W,T> filterNot(Predicate<? super T> predicate) {
        return (SingleT<W,T>)Filters.super.filterNot(predicate);
    }

    @Override
    public SingleT<W,T> notNull() {
        return (SingleT<W,T>)Filters.super.notNull();
    }

    @Override
    public <R> SingleT<W,R> zipWith(Iterable<Function<? super T, ? extends R>> fn) {
        return (SingleT<W,R>)super.zipWith(fn);
    }

    @Override
    public <R> SingleT<W,R> zipWithS(Stream<Function<? super T, ? extends R>> fn) {
        return (SingleT<W,R>)super.zipWithS(fn);
    }

    @Override
    public <R> SingleT<W,R> zipWithP(Publisher<Function<? super T, ? extends R>> fn) {
        return (SingleT<W,R>)super.zipWithP(fn);
    }

    @Override
    public <R> SingleT<W,R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
        return (SingleT<W,R>)super.trampoline(mapper);
    }

    @Override
    public <U, R> SingleT<W,R> zipS(Stream<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return (SingleT<W,R>)super.zipS(other,zipper);
    }

    @Override
    public <U> SingleT<W,Tuple2<T, U>> zipP(Publisher<? extends U> other) {
        return (SingleT)super.zipP(other);
    }

    @Override
    public <S, U> SingleT<W,Tuple3<T, S, U>> zip3(Iterable<? extends S> second, Iterable<? extends U> third) {
        return (SingleT)super.zip3(second,third);
    }

    @Override
    public <S, U, R> SingleT<W,R> zip3(Iterable<? extends S> second, Iterable<? extends U> third, Fn3<? super T, ? super S, ? super U, ? extends R> fn3) {
        return (SingleT<W,R>)super.zip3(second,third, fn3);
    }

    @Override
    public <T2, T3, T4> SingleT<W,Tuple4<T, T2, T3, T4>> zip4(Iterable<? extends T2> second, Iterable<? extends T3> third, Iterable<? extends T4> fourth) {
        return (SingleT)super.zip4(second,third,fourth);
    }

    @Override
    public <T2, T3, T4, R> SingleT<W,R> zip4(Iterable<? extends T2> second, Iterable<? extends T3> third, Iterable<? extends T4> fourth, Fn4<? super T, ? super T2, ? super T3, ? super T4, ? extends R> fn) {
        return (SingleT<W,R>)super.zip4(second,third,fourth,fn);
    }
}