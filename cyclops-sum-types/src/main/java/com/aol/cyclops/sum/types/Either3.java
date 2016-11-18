package com.aol.cyclops.sum.types;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import com.aol.cyclops.control.Eval;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.types.BiFunctor;
import com.aol.cyclops.types.Combiner;
import com.aol.cyclops.types.Functor;
import com.aol.cyclops.types.To;
import com.aol.cyclops.types.Value;
import com.aol.cyclops.types.applicative.ApplicativeFunctor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * A right biased Lazy Either3 type. map / flatMap operators are tail-call optimized
 * 
 * Can be one of 3 types
 * 
 * 
 * 
 * @author johnmcclean
 *
 * @param <LT> Left type
 * @param <M> Middle type
 * @param <RT> Right type
 */
public interface Either3<LT, M, RT>
        extends Functor<RT>, BiFunctor<M, RT>, To<Either3<LT, M, RT>>, Supplier<RT>, ApplicativeFunctor<RT> {

    public static <LT, B, RT> Either3<LT, B, RT> rightEval(final Eval<RT> right) {
        return new Right<>(
                           right);
    }

    public static <LT, B, RT> Either3<LT, B, RT> leftEval(final Eval<LT> left) {
        return new Left<>(
                          left);
    }

    public static <LT, B, RT> Either3<LT, B, RT> right(final RT right) {
        return new Right<>(
                           Eval.now(right));
    }

    public static <LT, B, RT> Either3<LT, B, RT> left(final LT left) {
        return new Left<>(
                          Eval.now(left));
    }

    public static <LT, B, RT> Either3<LT, B, RT> middle(final B middle) {
        return new Middle<>(
                            Eval.now(middle));
    }

    public static <LT, B, RT> Either3<LT, B, RT> middleEval(final Eval<B> middle) {
        return new Middle<>(
                            middle);
    }

    <R> R visit(final Function<? super LT, ? extends R> secondary, final Function<? super M, ? extends R> mid,
            final Function<? super RT, ? extends R> primary);

    Maybe<RT> filter(Predicate<? super RT> test);

    <LT1, M1, RT1> Either3<LT1, M1, RT1> flatMap(
            Function<? super RT, ? extends Either3<? extends LT1, ? extends M1, ? extends RT1>> mapper);

    /**
     * @return Swap the middle and the right types
     */
    Either3<LT, RT, M> swap2();

    /**
     * @return Swap the right and left types
     */
    Either3<RT, M, LT> swap1();

    /**
     * @return True if this either contains the right type
     */
    boolean isRight();

    /**
     * @return True if this either contains the left type
     */
    boolean isLeft();

    /**
     * @return True if this either contains the middle type
     */
    boolean isMiddle();

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.BiFunctor#bimap(java.util.function.Function,
     * java.util.function.Function)
     */
    @Override
    <R1, R2> Either3<LT, R1, R2> bimap(Function<? super M, ? extends R1> fn1, Function<? super RT, ? extends R2> fn2);

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Functor#map(java.util.function.Function)
     */
    @Override
    <R> Either3<LT, M, R> map(Function<? super RT, ? extends R> fn);

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Combiner#combine(com.aol.cyclops.types.Value,
     * java.util.function.BiFunction)
     */
    @Override
    default <T2, R> Either3<LT, M, R> combine(final Value<? extends T2> app,
            final BiFunction<? super RT, ? super T2, ? extends R> fn) {

        return (Either3<LT, M, R>) ApplicativeFunctor.super.combine(app, fn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Combiner#combine(java.util.function.BinaryOperator,
     * com.aol.cyclops.types.Combiner)
     */
    @Override
    default Either3<LT, M, RT> combine(final BinaryOperator<Combiner<RT>> combiner, final Combiner<RT> app) {

        return (Either3<LT, M, RT>) ApplicativeFunctor.super.combine(combiner, app);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(org.jooq.lambda.Seq,
     * java.util.function.BiFunction)
     */
    @Override
    default <U, R> Either3<LT, M, R> zip(final Seq<? extends U> other,
            final BiFunction<? super RT, ? super U, ? extends R> zipper) {

        return (Either3<LT, M, R>) ApplicativeFunctor.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.util.stream.Stream,
     * java.util.function.BiFunction)
     */
    @Override
    default <U, R> Either3<LT, M, R> zip(final Stream<? extends U> other,
            final BiFunction<? super RT, ? super U, ? extends R> zipper) {

        return (Either3<LT, M, R>) ApplicativeFunctor.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.util.stream.Stream)
     */
    @Override
    default <U> Either3<LT, M, Tuple2<RT, U>> zip(final Stream<? extends U> other) {

        return (Either3) ApplicativeFunctor.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(org.jooq.lambda.Seq)
     */
    @Override
    default <U> Either3<LT, M, Tuple2<RT, U>> zip(final Seq<? extends U> other) {

        return (Either3) ApplicativeFunctor.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.lang.Iterable)
     */
    @Override
    default <U> Either3<LT, M, Tuple2<RT, U>> zip(final Iterable<? extends U> other) {

        return (Either3) ApplicativeFunctor.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Unit#unit(java.lang.Object)
     */
    @Override
    <T> Either3<LT, M, T> unit(T unit);

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#zip(java.lang.
     * Iterable, java.util.function.BiFunction)
     */
    @Override
    default <T2, R> Either3<LT, M, R> zip(final Iterable<? extends T2> app,
            final BiFunction<? super RT, ? super T2, ? extends R> fn) {

        return (Either3<LT, M, R>) ApplicativeFunctor.super.zip(app, fn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#zip(java.util.
     * function.BiFunction, org.reactivestreams.Publisher)
     */
    @Override
    default <T2, R> Either3<LT, M, R> zip(final BiFunction<? super RT, ? super T2, ? extends R> fn,
            final Publisher<? extends T2> app) {

        return (Either3<LT, M, R>) ApplicativeFunctor.super.zip(fn, app);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.BiFunctor#bipeek(java.util.function.Consumer,
     * java.util.function.Consumer)
     */
    @Override
    default Either3<LT, M, RT> bipeek(final Consumer<? super M> c1, final Consumer<? super RT> c2) {

        return (Either3<LT, M, RT>) BiFunctor.super.bipeek(c1, c2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.BiFunctor#bicast(java.lang.Class,
     * java.lang.Class)
     */
    @Override
    default <U1, U2> Either3<LT, U1, U2> bicast(final Class<U1> type1, final Class<U2> type2) {

        return (Either3<LT, U1, U2>) BiFunctor.super.bicast(type1, type2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.BiFunctor#bitrampoline(java.util.function.Function,
     * java.util.function.Function)
     */
    @Override
    default <R1, R2> Either3<LT, R1, R2> bitrampoline(
            final Function<? super M, ? extends Trampoline<? extends R1>> mapper1,
            final Function<? super RT, ? extends Trampoline<? extends R2>> mapper2) {

        return (Either3<LT, R1, R2>) BiFunctor.super.bitrampoline(mapper1, mapper2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Functor#cast(java.lang.Class)
     */
    @Override
    default <U> Either3<LT, M, U> cast(final Class<? extends U> type) {

        return (Either3<LT, M, U>) ApplicativeFunctor.super.cast(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Functor#peek(java.util.function.Consumer)
     */
    @Override
    default Either3<LT, M, RT> peek(final Consumer<? super RT> c) {

        return (Either3<LT, M, RT>) ApplicativeFunctor.super.peek(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Functor#trampoline(java.util.function.Function)
     */
    @Override
    default <R> Either3<LT, M, R> trampoline(final Function<? super RT, ? extends Trampoline<? extends R>> mapper) {

        return (Either3<LT, M, R>) ApplicativeFunctor.super.trampoline(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Functor#patternMatch(java.util.function.Function,
     * java.util.function.Supplier)
     */
    @Override
    default <R> Either3<LT, M, R> patternMatch(final Function<CheckValue1<RT, R>, CheckValue1<RT, R>> case1,
            final Supplier<? extends R> otherwise) {

        return (Either3<LT, M, R>) ApplicativeFunctor.super.patternMatch(case1, otherwise);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final @EqualsAndHashCode(of = { "value" }) static class Lazy<ST, M, PT> implements Either3<ST, M, PT> {

        private final Eval<Either3<ST, M, PT>> lazy;

        public Either3<ST, M, PT> resolve() {
            return lazy.get()
                       .visit(Either3::left, Either3::middle, Either3::right);
        }

        private static <ST, M, PT> Lazy<ST, M, PT> lazy(final Eval<Either3<ST, M, PT>> lazy) {
            return new Lazy<>(
                              lazy);
        }

        @Override
        public <R> Either3<ST, M, R> map(final Function<? super PT, ? extends R> mapper) {

            return lazy(Eval.later(() -> resolve().map(mapper)));

        }

        @Override
        public <LT1, M1, RT1> Either3<LT1, M1, RT1> flatMap(
                final Function<? super PT, ? extends Either3<? extends LT1, ? extends M1, ? extends RT1>> mapper) {

            return lazy(Eval.later(() -> resolve().flatMap(mapper)));

        }

        @Override
        public Maybe<PT> filter(final Predicate<? super PT> test) {

            return Maybe.fromEval(Eval.later(() -> resolve().filter(test)))
                        .flatMap(Function.identity());

        }

        @Override
        public PT get() {
            return lazy.get()
                       .get();
        }

        @Override
        public ReactiveSeq<PT> stream() {

            return lazy.get()
                       .stream();
        }

        @Override
        public Iterator<PT> iterator() {

            return lazy.get()
                       .iterator();
        }

        @Override
        public <R> R visit(final Function<? super PT, ? extends R> present, final Supplier<? extends R> absent) {

            return lazy.get()
                       .visit(present, absent);
        }

        @Override
        public void subscribe(final Subscriber<? super PT> s) {

            lazy.get()
                .subscribe(s);
        }

        @Override
        public boolean test(final PT t) {
            return lazy.get()
                       .test(t);
        }

        @Override
        public <R> R visit(final Function<? super ST, ? extends R> secondary,
                final Function<? super M, ? extends R> mid, final Function<? super PT, ? extends R> primary) {

            return lazy.get()
                       .visit(secondary, mid, primary);
        }

        @Override
        public Either3<ST, PT, M> swap2() {
            return lazy(Eval.later(() -> resolve().swap2()));
        }

        @Override
        public Either3<PT, M, ST> swap1() {
            return lazy(Eval.later(() -> resolve().swap1()));
        }

        @Override
        public boolean isRight() {
            return lazy.get()
                       .isRight();
        }

        @Override
        public boolean isLeft() {
            return lazy.get()
                       .isLeft();
        }

        @Override
        public boolean isMiddle() {
            return lazy.get()
                       .isMiddle();
        }

        @Override
        public <R1, R2> Either3<ST, R1, R2> bimap(final Function<? super M, ? extends R1> fn1,
                final Function<? super PT, ? extends R2> fn2) {
            return lazy(Eval.later(() -> resolve().bimap(fn1, fn2)));
        }

        @Override
        public <T> Either3<ST, M, T> unit(final T unit) {

            return Either3.right(unit);
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of = { "value" })
    static class Right<ST, M, PT> implements Either3<ST, M, PT> {
        private final Eval<PT> value;

        @Override
        public <R> Either3<ST, M, R> map(final Function<? super PT, ? extends R> fn) {
            return new Right<ST, M, R>(
                                       value.map(fn));
        }

        @Override
        public Either3<ST, M, PT> peek(final Consumer<? super PT> action) {
            return map(i -> {
                action.accept(i);
                return i;
            });

        }

        @Override
        public Maybe<PT> filter(final Predicate<? super PT> test) {

            return Maybe.fromEval(Eval.later(() -> test.test(get()) ? Maybe.just(get()) : Maybe.<PT> none()))
                        .flatMap(Function.identity());

        }

        @Override
        public PT get() {
            return value.get();
        }

        @Override
        public <LT1, M1, RT1> Either3<LT1, M1, RT1> flatMap(
                final Function<? super PT, ? extends Either3<? extends LT1, ? extends M1, ? extends RT1>> mapper) {
            final Eval<Either3<LT1, M1, RT1>> e3 = (Eval<Either3<LT1, M1, RT1>>) value.map(mapper);
            return new Lazy<>(
                              e3);

        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public String toString() {
            return mkString();
        }

        @Override
        public String mkString() {
            return "Either3.right[" + value + "]";
        }

        @Override
        public <R> R visit(final Function<? super ST, ? extends R> secondary,
                final Function<? super M, ? extends R> mid, final Function<? super PT, ? extends R> primary) {
            return primary.apply(value.get());
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#ap(com.aol.
         * cyclops.types.Value, java.util.function.BiFunction)
         */
        @Override
        public <T2, R> Either3<ST, M, R> combine(final Value<? extends T2> app,
                final BiFunction<? super PT, ? super T2, ? extends R> fn) {
            return new Right<>(
                               value.combine(app, fn));

        }

        @Override
        public <R1, R2> Either3<ST, R1, R2> bimap(final Function<? super M, ? extends R1> fn1,
                final Function<? super PT, ? extends R2> fn2) {
            return (Either3<ST, R1, R2>) this.map(fn2);
        }

        @Override
        public ReactiveSeq<PT> stream() {
            return value.stream();
        }

        @Override
        public Iterator<PT> iterator() {
            return value.iterator();
        }

        @Override
        public <R> R visit(final Function<? super PT, ? extends R> present, final Supplier<? extends R> absent) {
            return value.visit(present, absent);
        }

        @Override
        public void subscribe(final Subscriber<? super PT> s) {
            value.subscribe(s);

        }

        @Override
        public boolean test(final PT t) {
            return value.test(t);
        }

        @Override
        public <T> Either3<ST, M, T> unit(final T unit) {
            return Either3.right(unit);
        }

        @Override
        public Either3<ST, PT, M> swap2() {

            return null;
        }

        @Override
        public Either3<PT, M, ST> swap1() {

            return new Left<>(
                              value);
        }

        @Override
        public boolean isMiddle() {

            return false;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of = { "value" })
    static class Left<ST, M, PT> implements Either3<ST, M, PT> {
        private final Eval<ST> value;

        @Override
        public <R> Either3<ST, M, R> map(final Function<? super PT, ? extends R> fn) {
            return (Either3<ST, M, R>) this;
        }

        @Override
        public Either3<ST, M, PT> peek(final Consumer<? super PT> action) {
            return this;

        }

        @Override
        public Maybe<PT> filter(final Predicate<? super PT> test) {

            return Maybe.none();

        }

        @Override
        public PT get() {
            throw new NoSuchElementException(
                                             "Attempt to access right value on a Left Either3");
        }

        @Override
        public <LT1, M1, RT1> Either3<LT1, M1, RT1> flatMap(
                final Function<? super PT, ? extends Either3<? extends LT1, ? extends M1, ? extends RT1>> mapper) {

            return (Either3) this;

        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public String toString() {
            return mkString();
        }

        @Override
        public String mkString() {
            return "Either3.left[" + value + "]";
        }

        @Override
        public <R> R visit(final Function<? super ST, ? extends R> secondary,
                final Function<? super M, ? extends R> mid, final Function<? super PT, ? extends R> primary) {
            return secondary.apply(value.get());
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#ap(com.aol.
         * cyclops.types.Value, java.util.function.BiFunction)
         */
        @Override
        public <T2, R> Either3<ST, M, R> combine(final Value<? extends T2> app,
                final BiFunction<? super PT, ? super T2, ? extends R> fn) {
            return (Either3<ST, M, R>) this;

        }

        @Override
        public <R1, R2> Either3<ST, R1, R2> bimap(final Function<? super M, ? extends R1> fn1,
                final Function<? super PT, ? extends R2> fn2) {
            return (Either3<ST, R1, R2>) this;
        }

        @Override
        public ReactiveSeq<PT> stream() {
            return ReactiveSeq.empty();
        }

        @Override
        public Iterator<PT> iterator() {
            return Arrays.<PT> asList()
                         .iterator();
        }

        @Override
        public <R> R visit(final Function<? super PT, ? extends R> present, final Supplier<? extends R> absent) {
            return absent.get();
        }

        @Override
        public void subscribe(final Subscriber<? super PT> s) {

        }

        @Override
        public boolean test(final PT t) {
            return false;
        }

        @Override
        public <T> Either3<ST, M, T> unit(final T unit) {
            return Either3.right(unit);
        }

        @Override
        public Either3<ST, PT, M> swap2() {

            return (Either3<ST, PT, M>) this;
        }

        @Override
        public Either3<PT, M, ST> swap1() {

            return new Right<>(
                               value);
        }

        @Override
        public boolean isMiddle() {

            return false;
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of = { "value" })
    static class Middle<ST, M, PT> implements Either3<ST, M, PT> {
        private final Eval<M> value;

        @Override
        public <R> Either3<ST, M, R> map(final Function<? super PT, ? extends R> fn) {
            return (Either3<ST, M, R>) this;
        }

        @Override
        public Either3<ST, M, PT> peek(final Consumer<? super PT> action) {
            return this;

        }

        @Override
        public Maybe<PT> filter(final Predicate<? super PT> test) {

            return Maybe.none();

        }

        @Override
        public PT get() {
            throw new NoSuchElementException(
                                             "Attempt to access right value on a Middle Either3");
        }

        @Override
        public <LT1, M1, RT1> Either3<LT1, M1, RT1> flatMap(
                final Function<? super PT, ? extends Either3<? extends LT1, ? extends M1, ? extends RT1>> mapper) {

            return (Either3) this;

        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public String toString() {
            return mkString();
        }

        @Override
        public String mkString() {
            return "Either3.middle[" + value + "]";
        }

        @Override
        public <R> R visit(final Function<? super ST, ? extends R> secondary,
                final Function<? super M, ? extends R> mid, final Function<? super PT, ? extends R> primary) {
            return mid.apply(value.get());
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#ap(com.aol.
         * cyclops.types.Value, java.util.function.BiFunction)
         */
        @Override
        public <T2, R> Either3<ST, M, R> combine(final Value<? extends T2> app,
                final BiFunction<? super PT, ? super T2, ? extends R> fn) {
            return (Either3<ST, M, R>) this;

        }

        @Override
        public <R1, R2> Either3<ST, R1, R2> bimap(final Function<? super M, ? extends R1> fn1,
                final Function<? super PT, ? extends R2> fn2) {
            return (Either3<ST, R1, R2>) this;
        }

        @Override
        public ReactiveSeq<PT> stream() {
            return ReactiveSeq.empty();
        }

        @Override
        public Iterator<PT> iterator() {
            return Arrays.<PT> asList()
                         .iterator();
        }

        @Override
        public <R> R visit(final Function<? super PT, ? extends R> present, final Supplier<? extends R> absent) {
            return absent.get();
        }

        @Override
        public void subscribe(final Subscriber<? super PT> s) {

        }

        @Override
        public boolean test(final PT t) {
            return false;
        }

        @Override
        public <T> Either3<ST, M, T> unit(final T unit) {
            return Either3.right(unit);
        }

        @Override
        public Either3<ST, PT, M> swap2() {
            return new Right<>(
                               value);

        }

        @Override
        public Either3<PT, M, ST> swap1() {
            return (Either3<PT, M, ST>) this;

        }

        @Override
        public boolean isMiddle() {

            return true;
        }

    }

}
