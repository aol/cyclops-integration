package com.aol.cyclops.sum.types;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.reactivestreams.Publisher;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Semigroups;
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.Eval;
import com.aol.cyclops.control.FluentFunctions;
import com.aol.cyclops.control.Ior;
import com.aol.cyclops.control.Matchable;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.StreamUtils;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.control.Xor;
import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.types.Combiner;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.MonadicValue2;
import com.aol.cyclops.types.Value;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.types.stream.reactive.ValueSubscriber;
import com.aol.cyclops.util.function.Curry;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * A totally Lazy Either implementation with tail call optimization for map and flatMap operators.
 * 
 * 'Right' (or primary type) biased disjunct union. Often called Either, but in a generics heavy Java world Either is half the length of Either.
 * 
 *  No 'projections' are provided, swap() and secondaryXXXX alternative methods can be used instead.
 *  
 *  Either is used to represent values that can be one of two states (for example a validation result, either everything is ok - or we have an error).
 *  It can be used to avoid a common design anti-pattern where an Object has two fields one of which is always null (or worse, both are defined as Optionals).
 *  
 *  <pre>
 *  {@code 
 *     
 *     public class Member{
 *           Either<SeniorTeam,JuniorTeam> team;      
 *     }
 *     
 *     Rather than
 *     
 *     public class Member{
 *           @Setter
 *           SeniorTeam seniorTeam = null;
 *           @Setter
 *           JuniorTeam juniorTeam = null;      
 *     }
 *  }
 *  </pre>
 *  
 *  Either's have two states
 *  Right : Most methods operate naturally on the primary type, if it is present. If it is not, nothing happens.
 *  Left : Most methods do nothing to the secondary type if it is present. 
 *              To operate on the Left type first call swap() or use secondary analogs of the main operators.
 *  
 *  Instantiating an Either - Right
 *  <pre>
 *  {@code 
 *      Either.primary("hello").map(v->v+" world") 
 *    //Either.primary["hello world"]
 *  }
 *  </pre>
 *  
 *  Instantiating an Either - Left
 *  <pre>
 *  {@code 
 *      Either.secondary("hello").map(v->v+" world") 
 *    //Either.seconary["hello"]
 *  }
 *  </pre>
 *  
 *  Either can operate (via map/flatMap) as a Functor / Monad and via combine as an ApplicativeFunctor
 *  
 *   Values can be accumulated via 
 *  <pre>
 *  {@code 
 *  Either.accumulateLeft(ListX.of(Either.secondary("failed1"),
                                                    Either.secondary("failed2"),
                                                    Either.primary("success")),
                                                    Semigroups.stringConcat)
 *  
 *  //failed1failed2
 *  
 *   Either<String,String> fail1 = Either.secondary("failed1");
     fail1.swap().combine((a,b)->a+b)
                 .combine(Either.secondary("failed2").swap())
                 .combine(Either.<String,String>primary("success").swap())
 *  
 *  //failed1failed2
 *  }
 *  </pre>
 * 
 * 
 * For Inclusive Ors @see Ior
 * 
 * @author johnmcclean
 *
 * @param <ST> Left type
 * @param <PT> Right type
 */
public interface Either<ST, PT> extends Xor<ST,PT> {

    /**
     * Construct a Right Either from the supplied publisher
     * <pre>
     * {@code 
     *   ReactiveSeq<Integer> stream =  ReactiveSeq.of(1,2,3);
        
         Either<Throwable,Integer> future = Either.fromPublisher(stream);
        
         //Either[1]
     * 
     * }
     * </pre>
     * @param pub Publisher to construct an Either from
     * @return Either constructed from the supplied Publisher
     */
    public static <T> Either<Throwable, T> fromPublisher(final Publisher<T> pub) {
        final ValueSubscriber<T> sub = ValueSubscriber.subscriber();
        pub.subscribe(sub);
        return null;
    }

    /**
     * Construct a Right Either from the supplied Iterable
     * <pre>
     * {@code 
     *   List<Integer> list =  Arrays.asList(1,2,3);
        
         Either<Throwable,Integer> future = Either.fromPublisher(stream);
        
         //Either[1]
     * 
     * }
     * </pre> 
     * @param iterable Iterable to construct an Either from
     * @return Either constructed from the supplied Iterable
     */
    public static <ST, T> Either<ST, T> fromIterable(final Iterable<T> iterable) {

        final Iterator<T> it = iterable.iterator();
        return Either.right(it.hasNext() ? it.next() : null);
    }

    /**
     * Create an instance of the secondary type. Most methods are biased to the primary type,
     * so you will need to use swap() or secondaryXXXX to manipulate the wrapped value
     * 
     * <pre>
     * {@code 
     *   Either.<Integer,Integer>secondary(10).map(i->i+1);
     *   //Either.secondary[10]
     *    
     *    Either.<Integer,Integer>secondary(10).swap().map(i->i+1);
     *    //Either.primary[11]
     * }
     * </pre>
     * 
     * 
     * @param value to wrap
     * @return Left instance of Either
     */
    public static <ST, PT> Either<ST, PT> secondary(final ST value) {
        return new Left<>(Eval.now( value));
    }

    /**
     * Create an instance of the primary type. Most methods are biased to the primary type,
     * which means, for example, that the map method operates on the primary type but does nothing on secondary Eithers
     * 
     * <pre>
     * {@code 
     *   Either.<Integer,Integer>primary(10).map(i->i+1);
     *   //Either.primary[11]
     *    
     *   
     * }
     * </pre>
     * 
     * 
     * @param value To construct an Either from
     * @return Right type instanceof Either
     */
    public static <ST, PT> Either<ST, PT> right(final PT value) {
        return new Right<ST,PT>(Eval.now(
                           value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue#anyM()
     */
    @Override
    default AnyMValue<PT> anyM() {
        return AnyM.ofValue(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Applicative#combine(java.util.function.
     * BinaryOperator, com.aol.cyclops.types.Applicative)
     */
    @Override
    default Either<ST, PT> combine(BinaryOperator<Combiner<PT>> combiner, Combiner<PT> app) {

        return (Either<ST, PT>) Xor.super.combine(combiner, app);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.MonadicValue2#flatMapIterable(java.util.function.
     * Function)
     */
    @Override
    default <R> Either<ST, R> flatMapIterable(Function<? super PT, ? extends Iterable<? extends R>> mapper) {
        return (Either<ST, R>) Xor.super.flatMapIterable(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.MonadicValue2#flatMapPublisher(java.util.function.
     * Function)
     */
    @Override
    default <R> Either<ST, R> flatMapPublisher(Function<? super PT, ? extends Publisher<? extends R>> mapper) {
        return (Either<ST, R>) Xor.super.flatMapPublisher(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.MonadicValue#coflatMap(java.util.function.Function)
     */
    @Override
    default <R> Either<ST, R> coflatMap(final Function<? super MonadicValue<PT>, R> mapper) {
        return (Either<ST, R>) Xor.super.coflatMap(mapper);
    }

    // cojoin
    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue#nest()
     */
    @Override
    default Either<ST, MonadicValue<PT>> nest() {
        return this.map(t -> unit(t));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue2#combine(com.aol.cyclops.Monoid,
     * com.aol.cyclops.types.MonadicValue2)
     */
    @Override
    default Either<ST, PT> combineEager(final Monoid<PT> monoid, final MonadicValue2<? extends ST, ? extends PT> v2) {
        return (Either<ST, PT>) Xor.super.combineEager(monoid, v2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue2#unit(java.lang.Object)
     */
    @Override
    default <T> Either<ST, T> unit(final T unit) {
        return Either.right(unit);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Convertable#toOptional()
     */
    @Override
    default Optional<PT> toOptional() {
        return isRight() ? Optional.of(get()) : Optional.empty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Filterable#filter(java.util.function.Predicate)
     */
    @Override
    Either<ST, PT> filter(Predicate<? super PT> test);

    /**
     * If this Either contains the Left type, map it's value so that it contains the Right type 
     * 
     * 
     * @param fn Function to map secondary type to primary
     * @return Either with secondary type mapped to primary
     */
    Either<ST, PT> secondaryToPrimayMap(Function<? super ST, ? extends PT> fn);

    /**
     * Always map the Left type of this Either if it is present using the provided transformation function
     * 
     * @param fn Transformation function for Left types
     * @return Either with Left type transformed
     */
    <R> Either<R, PT> secondaryMap(Function<? super ST, ? extends R> fn);

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue2#map(java.util.function.Function)
     */
    @Override
    <R> Either<ST, R> map(Function<? super PT, ? extends R> fn);

    /**
     * Peek at the Left type value if present
     * 
     * @param action Consumer to peek at the Left type value
     * @return Either with the same values as before
     */
    Either<ST, PT> secondaryPeek(Consumer<? super ST> action);

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Functor#peek(java.util.function.Consumer)
     */
    @Override
    Either<ST, PT> peek(Consumer<? super PT> action);

    /**
     * Swap types so operations directly affect the current (pre-swap) Left type
     *<pre>
     *  {@code 
     *    
     *    Either.secondary("hello")
     *       .map(v->v+" world") 
     *    //Either.seconary["hello"]
     *    
     *    Either.secondary("hello")
     *       .swap()
     *       .map(v->v+" world") 
     *       .swap()
     *    //Either.seconary["hello world"]
     *  }
     *  </pre>
     * 
     * 
     * @return Swap the primary and secondary types, allowing operations directly on what was the Left type
     */
    Either<PT, ST> swap();

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Value#toIor()
     */
    @Override
    Ior<ST, PT> toIor();

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Convertable#isPresent()
     */
    @Override
    default boolean isPresent() {
        return isRight();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Value#toEither()
     */
    @Override
    default Xor<ST, PT> toXor() {
        return visit(Xor::secondary, Xor::primary);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Value#toEither(java.lang.Object)
     */
    @Override
    default <ST2> Xor<ST2, PT> toXor(final ST2 secondary) {
        return visit(s -> Xor.secondary(secondary), p -> Xor.primary(p));
    }

     


    default boolean isSecondary(){
        return isLeft();
    }
    default boolean isPrimary(){
        return isRight();
    }
   

   
    /**
     * Visitor pattern for this Ior.
     * Execute the secondary function if this Either contains an element of the secondary type
     * Execute the primary function if this Either contains an element of the primary type
     * 
     * 
     * <pre>
     * {@code 
     *  Either.primary(10)
     *     .visit(secondary->"no", primary->"yes")
     *  //Either["yes"]
        
        Either.secondary(90)
           .visit(secondary->"no", primary->"yes")
        //Either["no"]
         
    
     * 
     * }
     * </pre>
     * 
     * @param secondary Function to execute if this is a Left Either
     * @param primary Function to execute if this is a Right Ior
     * @param both Function to execute if this Ior contains both types
     * @return Result of executing the appropriate function
     */
    <R> R visit(Function<? super ST, ? extends R> secondary, Function<? super PT, ? extends R> primary);

    @Deprecated // use bimap instead
    default <R1, R2> Either<R1, R2> mapBoth(final Function<? super ST, ? extends R1> secondary,
            final Function<? super PT, ? extends R2> primary) {
        if (isLeft())
            return (Either<R1, R2>) swap().map(secondary)
                                          .swap();
        return (Either<R1, R2>) map(primary);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.BiFunctor#bimap(java.util.function.Function,
     * java.util.function.Function)
     */
    @Override
    default <R1, R2> Either<R1, R2> bimap(Function<? super ST, ? extends R1> secondary,
            Function<? super PT, ? extends R2> primary) {
        if (isLeft())
            return (Either<R1, R2>) swap().map(secondary)
                                          .swap();
        return (Either<R1, R2>) map(primary);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.BiFunctor#bipeek(java.util.function.Consumer,
     * java.util.function.Consumer)
     */
    @Override
    default Either<ST, PT> bipeek(Consumer<? super ST> c1, Consumer<? super PT> c2) {

        return (Either<ST, PT>) Xor.super.bipeek(c1, c2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.BiFunctor#bicast(java.lang.Class,
     * java.lang.Class)
     */
    @Override
    default <U1, U2> Either<U1, U2> bicast(Class<U1> type1, Class<U2> type2) {

        return (Either<U1, U2>) Xor.super.bicast(type1, type2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.BiFunctor#bitrampoline(java.util.function.Function,
     * java.util.function.Function)
     */
    @Override
    default <R1, R2> Either<R1, R2> bitrampoline(Function<? super ST, ? extends Trampoline<? extends R1>> mapper1,
            Function<? super PT, ? extends Trampoline<? extends R2>> mapper2) {

        return (Either<R1, R2>) Xor.super.bitrampoline(mapper1, mapper2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Functor#patternMatch(java.util.function.Function,
     * java.util.function.Supplier)
     */
    @Override
    default <R> Either<ST, R> patternMatch(final Function<CheckValue1<PT, R>, CheckValue1<PT, R>> case1,
            final Supplier<? extends R> otherwise) {

        return (Either<ST, R>) Xor.super.patternMatch(case1, otherwise);
    }

    /**
     * Pattern match on the value/s inside this Either.
     * 
     * <pre>
     * {@code 
     * 
     * import static com.aol.cyclops.control.Matchable.otherwise;
       import static com.aol.cyclops.control.Matchable.then;
       import static com.aol.cyclops.control.Matchable.when;
       import static com.aol.cyclops.util.function.Predicates.instanceOf;
     * 
     * Either.primary(10)
     *    .matches(c->c.is(when("10"),then("hello")),
                   c->c.is(when(instanceOf(Integer.class)), then("error")),
                   otherwise("miss"))
           .get()
       //"error" Note the second case, 'primary' case is the one that matches
     * 
     * 
     * }
     * </pre>
     * 
     * 
     * @param fn1 Pattern matching function executed if this Either has the secondary type
     * @param fn2 Pattern matching function executed if this Either has the primary type
     * @param otherwise Supplier used to provide a value if the selecting pattern matching function fails to find a match
     * @return Lazy result of the pattern matching
     */
    <R> Eval<R> matches(Function<CheckValue1<ST, R>, CheckValue1<ST, R>> fn1,
            Function<CheckValue1<PT, R>, CheckValue1<PT, R>> fn2, Supplier<? extends R> otherwise);

    /*
     * (non-Javadoc)
     * 
     * @see java.util.function.Supplier#get()
     */
    @Override
    PT get();

    /**
     * @return A Value containing the secondary Value if present
     */
    Value<ST> secondaryValue();

    /**
     * @return The Left Value if present, otherwise null
     */
    ST secondaryGet();

    /**
     * @return The Left value wrapped in an Optional if present, otherwise an empty Optional
     */
    Optional<ST> secondaryToOptional();

    /**
     * @return A Stream containing the secondary value if present, otherwise an empty Stream
     */
    ReactiveSeq<ST> secondaryToStream();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.MonadicValue2#flatMap(java.util.function.Function)
     */
    @Override
    <LT1, RT1> Either<LT1, RT1> flatMap(
            Function<? super PT, ? extends MonadicValue2<? extends LT1, ? extends RT1>> mapper);

    /**
     * Perform a flatMap operation on the Left type
     * 
     * @param mapper Flattening transformation function
     * @return Either containing the value inside the result of the transformation function as the Left value, if the Left type was present
     */
    <LT1, RT1> Either<LT1, RT1> secondaryFlatMap(Function<? super ST, ? extends Xor<LT1, RT1>> mapper);

    /**
     * A flatMap operation that keeps the Left and Right types the same
     * 
     * @param fn Transformation function
     * @return Either
     */
    Either<ST, PT> secondaryToPrimayFlatMap(Function<? super ST, ? extends Xor<ST, PT>> fn);

    @Deprecated // use bipeek
    void peek(Consumer<? super ST> stAction, Consumer<? super PT> ptAction);

    /**
     * @return True if this is a primary Either
     */
    public boolean isRight();

    /**
     * @return True if this is a secondary Either
     */
    public boolean isLeft();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.applicative.ApplicativeFunctor#ap(com.aol.cyclops.
     * types.Value, java.util.function.BiFunction)
     */
    @Override
    <T2, R> Either<ST, R> combine(Value<? extends T2> app, BiFunction<? super PT, ? super T2, ? extends R> fn);

    /**
     * @return An Either with the secondary type converted to a persistent list, for use with accumulating app function  {@link Either#combine(Either,BiFunction)}
     */
    default Either<PStackX<ST>, PT> list() {
        return secondaryMap(PStackX::of);
    }

    /**
     * Accumulate secondarys into a PStackX (extended Persistent List) and Right with the supplied combiner function
     * Right accumulation only occurs if all phases are primary
     * 
     * @param app Value to combine with
     * @param fn Combiner function for primary values
     * @return Combined Either
     */
    default <T2, R> Either<PStackX<ST>, R> combineToList(final Either<ST, ? extends T2> app,
            final BiFunction<? super PT, ? super T2, ? extends R> fn) {
        return list().combine(app.list(), Semigroups.collectionXConcat(), fn);
    }

    /**
     * Accumulate secondary values with the provided BinaryOperator / Semigroup {@link Semigroups}
     * Right accumulation only occurs if all phases are primary
     * 
     * <pre>
     * {@code 
     *  Either<String,String> fail1 =  Either.secondary("failed1");
        Either<PStackX<String>,String> result = fail1.list().combine(Either.secondary("failed2").list(), Semigroups.collectionConcat(),(a,b)->a+b);
        
        //Left of [PStackX.of("failed1","failed2")))]
     * }
     * </pre>
     * 
     * @param app Value to combine with
     * @param semigroup to combine secondary types
     * @param fn To combine primary types
     * @return Combined Either
     */

    default <T2, R> Either<ST, R> combine(final Either<? extends ST, ? extends T2> app,
            final BinaryOperator<ST> semigroup, final BiFunction<? super PT, ? super T2, ? extends R> fn) {
        return this.visit(secondary -> app.visit(s2 -> Either.secondary(semigroup.apply(s2, secondary)),
                                                 p2 -> Either.secondary(secondary)),
                          primary -> app.visit(s2 -> Either.secondary(s2),
                                               p2 -> Either.right(fn.apply(primary, p2))));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#zip(java.lang.
     * Iterable, java.util.function.BiFunction)
     */
    @Override
    default <T2, R> Either<ST, R> zip(final Iterable<? extends T2> app,
            final BiFunction<? super PT, ? super T2, ? extends R> fn) {
        return map(v -> Tuple.tuple(v, Curry.curry2(fn)
                                            .apply(v))).flatMap(tuple -> Either.fromIterable(app)
                                                                               .visit(i -> Either.right(tuple.v2.apply(i)),
                                                                                      () -> Either.secondary(null)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#zip(java.util.
     * function.BiFunction, org.reactivestreams.Publisher)
     */
    @Override
    default <T2, R> Either<ST, R> zip(final BiFunction<? super PT, ? super T2, ? extends R> fn,
            final Publisher<? extends T2> app) {
        return map(v -> Tuple.tuple(v, Curry.curry2(fn)
                                            .apply(v))).flatMap(tuple -> Either.fromPublisher(app)
                                                                               .visit(i -> Either.right(tuple.v2.apply(i)),
                                                                                      () -> Either.secondary(null)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(org.jooq.lambda.Seq,
     * java.util.function.BiFunction)
     */
    @Override
    default <U, R> Either<ST, R> zip(final Seq<? extends U> other,
            final BiFunction<? super PT, ? super U, ? extends R> zipper) {

        return (Either<ST, R>) Xor.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.util.stream.Stream,
     * java.util.function.BiFunction)
     */
    @Override
    default <U, R> Either<ST, R> zip(final Stream<? extends U> other,
            final BiFunction<? super PT, ? super U, ? extends R> zipper) {

        return (Either<ST, R>) Xor.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.util.stream.Stream)
     */
    @Override
    default <U> Either<ST, Tuple2<PT, U>> zip(final Stream<? extends U> other) {

        return (Either) Xor.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(org.jooq.lambda.Seq)
     */
    @Override
    default <U> Either<ST, Tuple2<PT, U>> zip(final Seq<? extends U> other) {

        return (Either) Xor.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.lang.Iterable)
     */
    @Override
    default <U> Either<ST, Tuple2<PT, U>> zip(final Iterable<? extends U> other) {

        return (Either) Xor.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.lambda.monads.Filterable#ofType(java.lang.Class)
     */
    @Override
    default <U> Either<ST, U> ofType(final Class<? extends U> type) {

        return (Either<ST, U>) Xor.super.ofType(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.lambda.monads.Filterable#filterNot(java.util.function.
     * Predicate)
     */
    @Override
    default Either<ST, PT> filterNot(final Predicate<? super PT> fn) {

        return (Either<ST, PT>) Xor.super.filterNot(fn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.lambda.monads.Filterable#notNull()
     */
    @Override
    default Either<ST, PT> notNull() {

        return (Either<ST, PT>) Xor.super.notNull();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.lambda.monads.Functor#cast(java.lang.Class)
     */
    @Override
    default <U> Either<ST, U> cast(final Class<? extends U> type) {

        return (Either<ST, U>) Xor.super.cast(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.lambda.monads.Functor#trampoline(java.util.function.
     * Function)
     */
    @Override
    default <R> Either<ST, R> trampoline(final Function<? super PT, ? extends Trampoline<? extends R>> mapper) {

        return (Either<ST, R>) Xor.super.trampoline(mapper);
    }

    static <ST, PT> Either<ST, PT> narrow(final Either<? extends ST, ? extends PT> broad) {
        return (Either<ST, PT>) broad;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Lazy<ST, PT> implements Either<ST, PT> {

        private final Eval<Either<ST, PT>> lazy;

        private static <ST, PT> Lazy<ST, PT> lazy(Eval<Either<ST, PT>> lazy) {
            return new Lazy<>(
                              lazy);
        }

        public <L,R> Either<L,R> either(Xor<L,R> eager){
            return eager.visit(Either::secondary, Either::right);
        }
        
        @Override
        public <R> Either<ST, R> map(final Function<? super PT, ? extends R> mapper) {
          
            return lazy(Eval.later( () -> this.<ST,R>either(toXor().map(mapper))));
         
        }
        
        private <PT> Either<ST, PT> toEither(MonadicValue2<? extends ST, ? extends PT> value) {
            return value.visit(p -> Either.right(p), () -> Either.secondary(null));
        }

        @Override
        public <ST, RT1> Either<ST, RT1> flatMap(
                final Function<? super PT, ? extends MonadicValue2<? extends ST, ? extends RT1>> mapper) {

           
            return lazy(Eval.later( () -> this.<ST,RT1>either(toXor().flatMap(mapper))));
         
        }

        @Override
        public Either<ST, PT> filter(final Predicate<? super PT> test) {
            return flatMap(t -> test.test(t) ? this : Either.secondary(null));
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#secondaryValue()
         */
        @Override
        public Value<ST> secondaryValue() {
            return lazy.get()
                       .secondaryValue();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#secondaryToPrimayMap(java.util.
         * function.Function)
         */
        @Override
        public Either<ST, PT> secondaryToPrimayMap(Function<? super ST, ? extends PT> fn) {
            return lazy(Eval.later(() ->  either(toXor()
                                             .secondaryToPrimayMap(fn))));

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#secondaryMap(java.util.function.
         * Function)
         */
        @Override
        public <R> Either<R, PT> secondaryMap(Function<? super ST, ? extends R> fn) {
            return lazy(Eval.later(() -> this.<R,PT>either(toXor().secondaryMap(fn))));
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#secondaryPeek(java.util.function.
         * Consumer)
         */
        @Override
        public Either<ST, PT> secondaryPeek(Consumer<? super ST> action) {
            return lazy(Eval.later(() -> this.<ST,PT>either(toXor().secondaryPeek(action))));
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#peek(java.util.function.Consumer)
         */
        @Override
        public Either<ST, PT> peek(Consumer<? super PT> action) {
            return lazy(Eval.later(() -> this.<ST,PT>either(toXor().peek(action))));
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#swap()
         */
        @Override
        public Either<PT, ST> swap() {
            return lazy(Eval.later(() ->  either(toXor()
                                             .swap())));
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#toIor()
         */
        @Override
        public Ior<ST, PT> toIor() {
            return lazy.get()
                       .toIor();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#visit(java.util.function.Function,
         * java.util.function.Function)
         */
        @Override
        public <R> R visit(Function<? super ST, ? extends R> secondary, Function<? super PT, ? extends R> primary) {
            return lazy.get()
                       .visit(secondary, primary);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#get()
         */
        @Override
        public PT get() {

            return lazy.get()
                       .get();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#secondaryGet()
         */
        @Override
        public ST secondaryGet() {
            return lazy.get()
                       .secondaryGet();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#secondaryToOptional()
         */
        @Override
        public Optional<ST> secondaryToOptional() {
            return lazy.get()
                       .secondaryToOptional();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#secondaryToStream()
         */
        @Override
        public ReactiveSeq<ST> secondaryToStream() {
            return ReactiveSeq.generate(() -> lazy.get()
                                                  .secondaryToStream())
                              .flatMap(Function.identity());
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#secondaryFlatMap(java.util.function.
         * Function)
         */
        @Override
        public <LT1, RT1> Either<LT1, RT1> secondaryFlatMap(Function<? super ST, ? extends Xor<LT1, RT1>> mapper) {
            return lazy(Eval.later(() -> this.<LT1,RT1>either(toXor()
                                             .secondaryFlatMap(mapper))));
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#secondaryToPrimayFlatMap(java.util.
         * function.Function)
         */
        @Override
        public Either<ST, PT> secondaryToPrimayFlatMap(Function<? super ST, ? extends Xor<ST, PT>> fn) {
            return lazy(Eval.later(() -> this.<ST,PT>either(toXor().secondaryToPrimayFlatMap(fn))));
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#peek(java.util.function.Consumer,
         * java.util.function.Consumer)
         */
        @Override
        public void peek(Consumer<? super ST> stAction, Consumer<? super PT> ptAction) {
            lazy.get()
                .peek(stAction, ptAction);

        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#isRight()
         */
        @Override
        public boolean isRight() {
            return lazy.get()
                       .isRight();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.sum.types.Either#isLeft()
         */
        @Override
        public boolean isLeft() {
            return lazy.get()
                       .isLeft();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#combine(com.aol.cyclops.types.Value,
         * java.util.function.BiFunction)
         */
        @Override
        public <T2, R> Either<ST, R> combine(Value<? extends T2> app,
                BiFunction<? super PT, ? super T2, ? extends R> fn) {
            return lazy(Eval.later(() -> lazy.get()
                                             .combine(app, fn)));
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.aol.cyclops.sum.types.Either#matches(java.util.function.Function,
         * java.util.function.Function, java.util.function.Supplier)
         */
        @Override
        public <R> Eval<R> matches(Function<CheckValue1<ST, R>, CheckValue1<ST, R>> fn1,
                Function<CheckValue1<PT, R>, CheckValue1<PT, R>> fn2, Supplier<? extends R> otherwise) {
            return Eval.later(() -> lazy.get()
                                        .matches(fn1, fn2, otherwise))
                       .flatMap(Function.identity());
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of = { "value" })
    static class Right<ST, PT> implements Either<ST, PT> {
        private final Eval<PT> value;

        @Override
        public Either<ST, PT> secondaryToPrimayMap(final Function<? super ST, ? extends PT> fn) {
            return this;
        }

        @Override
        public <R> Either<R, PT> secondaryMap(final Function<? super ST, ? extends R> fn) {
            return (Either<R, PT>) this;
        }

        @Override
        public <R> Either<ST, R> map(final Function<? super PT, ? extends R> fn) {
            return new Right<ST, R>(
                                    value.map(fn));
        }

        @Override
        public Either<ST, PT> secondaryPeek(final Consumer<? super ST> action) {
            return this;
        }

        @Override
        public Either<ST, PT> peek(final Consumer<? super PT> action) {
            return map(i -> {
                action.accept(i);
                return i;
            });

        }

        @Override
        public Either<ST, PT> filter(final Predicate<? super PT> test) {

            return flatMap(i -> test.test(i) ? this : new Left<ST,PT>(
                                                               Eval.now(null)));

        }

        @Override
        public Either<PT, ST> swap() {
            return new Left<PT, ST>(
                                    value);
        }

        @Override
        public PT get() {
            return value.get();
        }

        @Override
        public ST secondaryGet() {
            return null;
        }

        @Override
        public Optional<ST> secondaryToOptional() {
            return Optional.empty();
        }

        @Override
        public ReactiveSeq<ST> secondaryToStream() {
            return ReactiveSeq.empty();
        }

        @Override
        public <LT1, RT1> Either<LT1, RT1> flatMap(
                final Function<? super PT, ? extends MonadicValue2<? extends LT1, ? extends RT1>> mapper) {

            return new Lazy<ST, PT>(
                                    Eval.now(this)).flatMap(mapper);

        }

        @Override
        public <LT1, RT1> Either<LT1, RT1> secondaryFlatMap(
                final Function<? super ST, ? extends Xor<LT1, RT1>> mapper) {
            return (Either<LT1, RT1>) this;
        }

        @Override
        public Either<ST, PT> secondaryToPrimayFlatMap(final Function<? super ST, ? extends Xor<ST, PT>> fn) {
            return this;
        }

        @Override
        public void peek(final Consumer<? super ST> stAction, final Consumer<? super PT> ptAction) {
            ptAction.accept(value.get());
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
        public Value<ST> secondaryValue() {
            return Value.of(() -> null);
        }

        @Override
        public String toString() {
            return mkString();
        }

        @Override
        public String mkString() {
            return "Either.right[" + value + "]";
        }

        @Override
        public Ior<ST, PT> toIor() {
            return Ior.primary(value.get());
        }

        @Override
        public <R> R visit(final Function<? super ST, ? extends R> secondary,
                final Function<? super PT, ? extends R> primary) {
            return primary.apply(value.get());
        }

        @Override
        public <R> Eval<R> matches(
                final Function<com.aol.cyclops.control.Matchable.CheckValue1<ST, R>, com.aol.cyclops.control.Matchable.CheckValue1<ST, R>> secondary,
                final Function<com.aol.cyclops.control.Matchable.CheckValue1<PT, R>, com.aol.cyclops.control.Matchable.CheckValue1<PT, R>> primary,
                final Supplier<? extends R> otherwise) {
            return Eval.later(() -> {
                final Matchable.MTuple1<PT> mt1 = () -> Tuple.tuple(value.get());
                return mt1.matches(primary, otherwise);
            })
                       .flatMap(Function.identity());

        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#ap(com.aol.
         * cyclops.types.Value, java.util.function.BiFunction)
         */
        @Override
        public <T2, R> Either<ST, R> combine(final Value<? extends T2> app,
                final BiFunction<? super PT, ? super T2, ? extends R> fn) {
            return app.toXor()
                      .visit(s -> Either.secondary(null), f -> Either.right(fn.apply(get(), app.get())));
        }

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(of = { "value" })
    static class Left<ST, PT> implements Either<ST, PT> {
        private final Eval<ST> value;

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public <R> Eval<R> matches(
                final Function<com.aol.cyclops.control.Matchable.CheckValue1<ST, R>, com.aol.cyclops.control.Matchable.CheckValue1<ST, R>> secondary,
                final Function<com.aol.cyclops.control.Matchable.CheckValue1<PT, R>, com.aol.cyclops.control.Matchable.CheckValue1<PT, R>> primary,
                final Supplier<? extends R> otherwise) {
            return Eval.later(() -> {
                final Matchable.MTuple1<ST> mt1 = () -> Tuple.tuple(value.get());
                return mt1.matches(secondary, otherwise);
            })
                       .flatMap(Function.identity());
        }

        @Override
        public Either<ST, PT> secondaryToPrimayMap(final Function<? super ST, ? extends PT> fn) {
            return new Right<ST, PT>(
                                     value.map(fn));
        }

        @Override
        public <R> Either<R, PT> secondaryMap(final Function<? super ST, ? extends R> fn) {
            return new Left<R, PT>(
                                   value.map(fn));
        }

        @Override
        public <R> Either<ST, R> map(final Function<? super PT, ? extends R> fn) {
            return (Either<ST, R>) this;
        }

        @Override
        public Either<ST, PT> secondaryPeek(final Consumer<? super ST> action) {
            return secondaryMap((Function) FluentFunctions.expression(action));
        }

        @Override
        public Either<ST, PT> peek(final Consumer<? super PT> action) {
            return this;
        }

        @Override
        public Either<ST, PT> filter(final Predicate<? super PT> test) {
            return this;
        }

        @Override
        public Either<PT, ST> swap() {
            return new Right<PT, ST>(
                                     value);
        }

        @Override
        public PT get() {
            throw new NoSuchElementException();
        }

        @Override
        public ST secondaryGet() {
            return value.get();
        }

        @Override
        public Optional<ST> secondaryToOptional() {
            return Optional.ofNullable(value.get());
        }

        @Override
        public ReactiveSeq<ST> secondaryToStream() {
            return ReactiveSeq.fromStream(StreamUtils.optionalToStream(secondaryToOptional()));
        }

        @Override
        public <LT1, RT1> Either<LT1, RT1> flatMap(
                final Function<? super PT, ? extends MonadicValue2<? extends LT1, ? extends RT1>> mapper) {
            return (Either<LT1, RT1>) this;
        }

        @Override
        public <LT1, RT1> Either<LT1, RT1> secondaryFlatMap(
                final Function<? super ST, ? extends Xor<LT1, RT1>> mapper) {
           return new Lazy<ST, PT>(
                    Eval.now(this)).secondaryFlatMap(mapper);
        }

        @Override
        public Either<ST, PT> secondaryToPrimayFlatMap(final Function<? super ST, ? extends Xor<ST, PT>> fn) {
            return new Lazy<ST, PT>(
                    Eval.now(this)).secondaryToPrimayFlatMap(fn);
        }

        @Override
        public void peek(final Consumer<? super ST> stAction, final Consumer<? super PT> ptAction) {
            stAction.accept(value.get());

        }

        @Override
        public <R> R visit(final Function<? super ST, ? extends R> secondary,
                final Function<? super PT, ? extends R> primary) {
            return secondary.apply(value.get());
        }

        @Override
        public Maybe<PT> toMaybe() {
            return Maybe.none();
        }

        @Override
        public Optional<PT> toOptional() {
            return Optional.empty();
        }

        @Override
        public Value<ST> secondaryValue() {
            return value;
        }

        @Override
        public String toString() {
            return mkString();
        }

        @Override
        public String mkString() {
            return "Either.left[" + value + "]";
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.value.Value#unapply()
         */
        @Override
        public ListX<ST> unapply() {
            return ListX.of(value.get());
        }

        @Override
        public Ior<ST, PT> toIor() {
            return Ior.secondary(value.get());
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#ap(com.aol.
         * cyclops.types.Value, java.util.function.BiFunction)
         */
        @Override
        public <T2, R> Either<ST, R> combine(final Value<? extends T2> app,
                final BiFunction<? super PT, ? super T2, ? extends R> fn) {
            return (Either<ST, R>) this;
        }

    }

}