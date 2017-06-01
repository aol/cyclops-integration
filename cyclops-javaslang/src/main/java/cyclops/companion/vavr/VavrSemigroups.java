package cyclops.companion.vavr;

import com.aol.cyclops2.types.Zippable;
import cyclops.function.Semigroup;
import javaslang.collection.*;
import javaslang.concurrent.Future;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.control.Try;

import java.util.function.BiFunction;


/**
 *
 * A static class with a large number of Semigroups  or Combiners.
 *
 * A semigroup is an Object that can be used to combine objects of the same type.
 *
 *  @author johnmcclean
 */
public interface VavrSemigroups {

    /**
     * To manage javac type inference first assign the semigroup
     * <pre>
     * {@code
     *
     *    Semigroup<List<Integer>> listX = VavrSemigroups.linearSeqConcat();
     *    Semigroup<Stream<Integer>> streamX = Semigroups.linearSeqConcat();
     *
     *
     *
     * }
     * </pre>
     * @return A Semigroup that can combine any vavr Set type
     */
    static <T, C extends LinearSeq<T>> Semigroup<C> linearSeqConcat() {
        return (a, b) -> (C) a.appendAll(b);
    }
    static <T, C extends IndexedSeq<T>> Semigroup<C> indexedSeqConcat() {
        return (a, b) -> (C) a.appendAll(b);
    }

    static <T, C extends Set<T>> Semigroup<C> setConcat() {
        return (a, b) -> (C) a.addAll(b);
    }





    static <T> Semigroup<List<T>> listConcat() {
        return VavrSemigroups.linearSeqConcat();
    }
    static <T> Semigroup<Vector<T>> vectorConcat() {
        return VavrSemigroups.indexedSeqConcat();
    }
    static   Semigroup<CharSeq> charSeqConcat() {
        return VavrSemigroups.indexedSeqConcat();
    }
    static <T> Semigroup<Array<T>> arrayConcat() {
        return VavrSemigroups.indexedSeqConcat();
    }
    static <T> Semigroup<Stream<T>> streamConcat() {
        return VavrSemigroups.linearSeqConcat();
    }
    static <T> Semigroup<Queue<T>> queueConcat() {
        return VavrSemigroups.linearSeqConcat();
    }
    static <T> Semigroup<LinkedHashSet<T>> linkedHashSetConcat() {
        return VavrSemigroups.setConcat();
    }
    static <T> Semigroup<HashSet<T>> hashSetConcat() {
        return VavrSemigroups.setConcat();
    }
    static <T> Semigroup<TreeSet<T>> treeSetConcat() {
        return VavrSemigroups.setConcat();
    }


    /**
     * <pre>
     * {@code
     *  BinaryOperator<Zippable<Integer>> sumInts = Semigroups.combineZippables(Semigroups.intSum);
    sumInts.apply(ListX.of(1,2,3), ListX.of(4,5,6));

    //List[5,7,9];
     *
     * }
     * </pre>
     *
     * @param semigroup Semigroup toNested combine the values inside the zippables
     * @return Combination of two Zippables
     */
    static <T,A extends Zippable<T>> Semigroup<A> combineZippables(BiFunction<T,T,T> semigroup) {
        return (a, b) -> (A) a.zip(b, semigroup);
    }
    /**
     *
     * <pre>
     * {@code
     *
     *  BinaryOperator<Combiner<Integer>> sumMaybes = Semigroups.combineScalarFunctors(Semigroups.intSum);
     *  Maybe.just(1)
     *       .combine(sumMaybes, Maybe.just(5))
     *
     *  //Maybe[6]
     * }
     * </pre>
     *
     *
     * @param semigroup Semigroup toNested combine the values inside the Scalar Functors (Maybe, Xor, Ior, Try, Eva, FeatureToggle etc)
     * @return Combination of two Scalar Functors
     */
    static <T,A extends Zippable<T>> Semigroup<A> combineScalarFunctors(BiFunction<T,T,T> semigroup) {
        return (a, b) -> (A) a.zip(b, semigroup);
    }



    /**
     * @return Combination of two Collection, first non-empty is returned
     */
    static <T,C extends Seq<T>> Semigroup<C> firstNonEmpty() {
        return (a, b) -> a.isEmpty() ? b: a;
    }
    /**
     * @return Combination of two Collection, last non-empty is returned
     */
    static <T,C extends Seq<T>> Semigroup<C> lastNonEmpty() {
        return (a, b) -> b.isEmpty() ? a: b;
    }

    /**
     * @return Combine two Future's by taking the first result
     */
    static <T> Semigroup<Future<T>> firstCompleteFuture() {
        return (a, b) -> Futures.anyOf(a,b);
    }


    /**
     * @return Combine two Future's by taking the first successful
     */
    static <T> Semigroup<Future<T>> firstSuccessfulFuture() {
        return (a, b) -> Futures.firstSuccess(a,b);
    }
    /**
     * @return Combine two Either's by taking the first primary
     */
    static <ST,PT> Semigroup<Either<ST,PT>> firstRightEither() {
        return  (a, b) -> a.isRight() ? a : b;
    }
    /**
     * @return Combine two Either's by taking the first secondary
     */
    static <ST,PT> Semigroup<Either<ST,PT>> firstLeftEither() {
        return  (a, b) -> a.isLeft() ? a : b;
    }
    /**
     * @return Combine two Either's by taking the last primary
     */
    static <ST,PT> Semigroup<Either<ST,PT>> lastRightEither() {
        return  (a, b) -> b.isRight() ? b : a;
    }
    /**
     * @return Combine two Either's by taking the last secondary
     */
    static <ST,PT> Semigroup<Either<ST,PT>> lastLeftEither() {
        return  (a, b) -> b.isLeft() ? b : a;
    }
    /**
     * @return Combine two Try's by taking the first primary
     */
    static <T,X extends Throwable> Semigroup<Try<T>> firstTrySuccess() {
        return  (a, b) -> a.isSuccess() ? a : b;
    }
    /**
     * @return Combine two Try's by taking the first secondary
     */
    static <T,X extends Throwable> Semigroup<Try<T>> firstTryFailure() {
        return  (a, b) -> a.isFailure() ? a : b;
    }
    /**
     * @return Combine two Tryr's by taking the last primary
     */
    static<T,X extends Throwable> Semigroup<Try<T>> lastTrySuccess() {
        return  (a, b) -> b.isSuccess() ? b : a;
    }
    /**
     * @return Combine two Try's by taking the last secondary
     */
    static <T,X extends Throwable> Semigroup<Try<T>>lastTryFailure() {
        return  (a, b) -> b.isFailure() ? b : a;
    }



    /**
     * @return Combine two optionals by taking the first present
     */
    static <T> Semigroup<Option<T>> firstPresentOption() {
        return (a, b) -> a.isDefined() ? a : b;
    }



    /**
     * @return Combine two Options by taking the last present
     */
    static <T> Semigroup<Option<T>> lastPresentOption() {
        return (a, b) -> b.isDefined() ? b : a;
    }


}