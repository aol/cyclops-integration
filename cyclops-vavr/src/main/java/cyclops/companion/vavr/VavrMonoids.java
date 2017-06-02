package cyclops.companion.vavr;

import cyclops.function.Monoid;
import io.vavr.collection.*;
import io.vavr.collection.HashSet;
import io.vavr.collection.LinkedHashSet;
import io.vavr.collection.List;
import io.vavr.collection.Queue;
import io.vavr.collection.Set;
import io.vavr.collection.TreeSet;
import io.vavr.collection.Vector;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.*;


/**
 *
 * A static class with a large number of Monoids  or Combiners.
 *
 * A semigroup is an Object that can be used to combine objects of the same type.
 *
 *  @author johnmcclean
 */
public interface VavrMonoids {

    /**
     * To manage javac type inference first assign the Monoid
     * <pre>
     * {@code
     *
     *    Monoid<List<Integer>> listX = VavrMonoid.linearSeqConcat();
     *    Monoid<Stream<Integer>> streamX = VavrMonoid.linearSeqConcat();
     *
     *
     *
     * }
     * </pre>
     * @return A Monoid that can combine any vavr Set type
     */
    static <T, C extends LinearSeq<T>> Monoid<C> linearSeqConcat(C identity) {

        return Monoid.of(identity,VavrSemigroups.linearSeqConcat());
    }
    static <T, C extends IndexedSeq<T>> Monoid<C> indexedSeqConcat(C identity) {

        return Monoid.of(identity,VavrSemigroups.indexedSeqConcat());
    }

    static <T, C extends Set<T>> Monoid<C> setConcat(C identity) {
        return Monoid.of(identity,VavrSemigroups.setConcat());
    }


    static <T> Monoid<List<T>> listConcat() {
        return VavrMonoids.linearSeqConcat(List.empty());
    }
    static <T> Monoid<Vector<T>> vectorConcat() {
        return VavrMonoids.indexedSeqConcat(Vector.empty());
    }
    static   Monoid<CharSeq> charSeqConcat() {
        return VavrMonoids.indexedSeqConcat(CharSeq.empty());
    }
    static <T> Monoid<Array<T>> arrayConcat() {
        return VavrMonoids.indexedSeqConcat(Array.empty());
    }
    static <T> Monoid<Stream<T>> streamConcat() {
        return VavrMonoids.linearSeqConcat(Stream.empty());
    }
    static <T> Monoid<Queue<T>> queueConcat() {
        return VavrMonoids.linearSeqConcat(Queue.empty());
    }
    static <T> Monoid<LinkedHashSet<T>> linkedHashSetConcat() {
        return VavrMonoids.setConcat(LinkedHashSet.empty());
    }
    static <T> Monoid<HashSet<T>> hashSetConcat() {
        return VavrMonoids.setConcat(HashSet.empty());
    }
    static <T extends Comparable<T>> Monoid<TreeSet<T>> treeSetConcat() {
        return VavrMonoids.<T,TreeSet<T>>setConcat(TreeSet.empty(Comparator.naturalOrder()));
    }
    static <T> Monoid<TreeSet<T>> treeSetConcat(Comparator<? super T> c) {
        return VavrMonoids.<T,TreeSet<T>>setConcat(TreeSet.empty(c));
    }




    /**
     * @return Combination of two Collection, first non-empty is returned
     */
    static <T,C extends Seq<T>> Monoid<C> firstNonEmptySeq(C identity) {
        return Monoid.of(identity,VavrSemigroups.firstNonEmptySeq());
    }
    static <T,C extends Set<T>> Monoid<C> firstNonEmptySet(C identity) {
        return Monoid.of(identity,VavrSemigroups.firstNonEmptySet());
    }

    static <T> Monoid<List<T>> firstNonEmptyList() {
        return VavrMonoids.firstNonEmptySeq(List.empty());
    }
    static <T> Monoid<Vector<T>> firstNonEmptyVector() {
        return VavrMonoids.firstNonEmptySeq(Vector.empty());
    }
    static   Monoid<CharSeq> firstNonEmptyCharSeq() {
        return VavrMonoids.firstNonEmptySeq(CharSeq.empty());
    }
    static <T> Monoid<Array<T>> firstNonEmptyArray() {
        return VavrMonoids.firstNonEmptySeq(Array.empty());
    }
    static <T> Monoid<Stream<T>> firstNonEmptyStream() {
        return VavrMonoids.firstNonEmptySeq(Stream.empty());
    }
    static <T> Monoid<Queue<T>> firstNonEmptyQueue() {
        return VavrMonoids.firstNonEmptySeq(Queue.empty());
    }
    static <T> Monoid<LinkedHashSet<T>> firstNonEmptylinkedHashSet() {
        return VavrMonoids.firstNonEmptySet(LinkedHashSet.<T>empty());
    }
    static <T> Monoid<HashSet<T>> firstNonEmptyHashSet() {
        return VavrMonoids.firstNonEmptySet(HashSet.empty());
    }
    static <T extends Comparable<T>> Monoid<TreeSet<T>> firstNonEmptyTreeSet() {
        return VavrMonoids.<T,TreeSet<T>>firstNonEmptySet(TreeSet.empty());
    }

    /**
     * @return Combination of two Collection, last non-empty is returned
     */
    static <T,C extends Seq<T>> Monoid<C> lastNonEmptySeq(C identity) {
        return Monoid.of(identity,VavrSemigroups.lastNonEmptySeq());
    }
    /**
     * @return Combination of two Collection, last non-empty is returned
     */
    static <T,C extends Set<T>> Monoid<C> lastNonEmptySet(C identity) {
        return Monoid.of(identity,VavrSemigroups.lastNonEmptySet());
    }
    static <T> Monoid<List<T>> lastNonEmptyList() {
        return VavrMonoids.lastNonEmptySeq(List.empty());
    }
    static <T> Monoid<Vector<T>> lastNonEmptyVector() {
        return VavrMonoids.lastNonEmptySeq(Vector.empty());
    }
    static   Monoid<CharSeq> lastNonEmptyCharSeq() {
        return VavrMonoids.lastNonEmptySeq(CharSeq.empty());
    }
    static <T> Monoid<Array<T>> lastNonEmptyArray() {
        return VavrMonoids.lastNonEmptySeq(Array.empty());
    }
    static <T> Monoid<Stream<T>> lastNonEmptyStream() {
        return VavrMonoids.lastNonEmptySeq(Stream.empty());
    }
    static <T> Monoid<Queue<T>> lastNonEmptyQueue() {
        return VavrMonoids.lastNonEmptySeq(Queue.empty());
    }
    static <T> Monoid<LinkedHashSet<T>> lastNonEmptylinkedHashSet() {
        return VavrMonoids.lastNonEmptySet(LinkedHashSet.<T>empty());
    }
    static <T> Monoid<HashSet<T>> lastNonEmptyHashSet() {
        return VavrMonoids.lastNonEmptySet(HashSet.empty());
    }
    static <T extends Comparable<T>> Monoid<TreeSet<T>> lastNonEmptyTreeSet() {
        return VavrMonoids.<T,TreeSet<T>>lastNonEmptySet(TreeSet.empty());
    }
    /**
     * @return Combine two Future's by taking the first result
     */
    static <T> Monoid<Future<T>> firstCompleteFuture() {
        return Monoid.of(Promise.<T>make().future(),VavrSemigroups.firstCompleteFuture());
    }


    /**
     * @return Combine two Future's by taking the first successful
     */
    static <T> Monoid<Future<T>> firstSuccessfulFuture() {
        return Monoid.of(Promise.<T>make().future(),VavrSemigroups.firstSuccessfulFuture());
    }
    /**
     * @return Combine two Either's by taking the first primary
     */
    static <ST,PT> Monoid<Either<ST,PT>> firstRightEither() {
        return  Monoid.of(Either.left(null),VavrSemigroups.firstRightEither());
    }
    /**
     * @return Combine two Either's by taking the first secondary
     */
    static <ST,PT> Monoid<Either<ST,PT>> firstLeftEither() {
        return  Monoid.of(Either.right(null),VavrSemigroups.firstLeftEither());
    }
    /**
     * @return Combine two Either's by taking the last primary
     */
    static <ST,PT> Monoid<Either<ST,PT>> lastRightEither() {
        return  Monoid.of(Either.left(null),VavrSemigroups.lastRightEither());
    }
    /**
     * @return Combine two Either's by taking the last secondary
     */
    static <ST,PT> Monoid<Either<ST,PT>> lastLeftEither() {
        return  Monoid.of(Either.right(null),VavrSemigroups.lastLeftEither());
    }
    /**
     * @return Combine two Try's by taking the first primary
     */
    static <T,X extends Throwable> Monoid<Try<T>> firstTrySuccess() {
        return  Monoid.of(Try.failure(new NoSuchElementException()),VavrSemigroups.firstTrySuccess());
    }
    /**
     * @return Combine two Try's by taking the first secondary
     */
    static <T,X extends Throwable> Monoid<Try<T>> firstTryFailure() {
        return  Monoid.of(Try.success(null),VavrSemigroups.firstTryFailure());
    }
    /**
     * @return Combine two Tryr's by taking the last primary
     */
    static<T,X extends Throwable> Monoid<Try<T>> lastTrySuccess() {
        return  Monoid.of(Try.failure(new NoSuchElementException()),VavrSemigroups.lastTrySuccess());
    }
    /**
     * @return Combine two Try's by taking the last secondary
     */
    static <T,X extends Throwable> Monoid<Try<T>>lastTryFailure() {
        return  Monoid.of(Try.success(null),VavrSemigroups.lastTryFailure());
    }


    /**
     * @return Combine two Options by taking the first present
     */
    static <T> Monoid<Option<T>> firstPresentOption() {
        return Monoid.of(Option.none(),VavrSemigroups.firstPresentOption());
    }


    /**
     * @return Combine two Options by taking the last present
     */
    static <T> Monoid<Option<T>> lastPresentOption() {
        return Monoid.of(Option.none(),VavrSemigroups.lastPresentOption());
    }


}