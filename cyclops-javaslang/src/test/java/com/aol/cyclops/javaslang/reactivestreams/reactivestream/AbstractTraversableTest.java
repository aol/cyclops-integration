/*     / \____  _    _  ____   ______  / \ ____  __    _ _____
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  / /  _  \   Javaslang
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/  \__/  /   Copyright 2014-now Daniel Dietrich
 * /___/\_/  \_/\____/\_/  \_/\__\/__/___\_/  \_//  \__/_____/    Licensed under the Apache License, Version 2.0
 */
package com.aol.cyclops.javaslang.reactivestreams.reactivestream;

import static java.lang.System.lineSeparator;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collector;

import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.Iterator;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Traversable;
import javaslang.collection.Vector;
import javaslang.control.Option;

import org.junit.Ignore;
import org.junit.Test;

public abstract class AbstractTraversableTest extends AbstractValueTest {

    protected boolean isTraversableAgain() {
        return true;
    }

    abstract protected <T> Collector<T, ArrayList<T>, ? extends Traversable<T>> collector();

    @Override
    abstract protected <T> Traversable<T> empty();

    @Override
    abstract protected <T> Traversable<T> of(T element);

    @SuppressWarnings("unchecked")
    @Override
    abstract protected <T> Traversable<T> of(T... elements);

    abstract protected <T> Traversable<T> ofAll(java.lang.Iterable<? extends T> elements);

    abstract protected Traversable<Boolean> ofAll(boolean[] array);

    abstract protected Traversable<Byte> ofAll(byte[] array);

    abstract protected Traversable<Character> ofAll(char[] array);

    abstract protected Traversable<Double> ofAll(double[] array);

    abstract protected Traversable<Float> ofAll(float[] array);

    abstract protected Traversable<Integer> ofAll(int[] array);

    abstract protected Traversable<Long> ofAll(long[] array);

    abstract protected Traversable<Short> ofAll(short[] array);

    // -- static empty()

    @Test
    public void shouldCreateNil() {
        final Traversable<?> actual = empty();
        assertThat(actual.length()).isEqualTo(0);
    }

    // -- static of()

    @Test
    public void shouldCreateSeqOfSeqUsingCons() {
        final List<List<Object>> actual = of(List.empty()).toList();
        assertThat(actual).isEqualTo(List.of(List.empty()));
    }

    // -- static of(T...)

    @Test
    public void shouldCreateInstanceOfElements() {
        final List<Integer> actual = of(1, 2).toList();
        assertThat(actual).isEqualTo(List.of(1, 2));
    }

    // -- static of(Iterable)

    @Test
    public void shouldCreateListOfIterable() {
        final java.util.List<Integer> arrayList = Arrays.asList(1, 2);
        final List<Integer> actual = ofAll(arrayList).toList();
        assertThat(actual).isEqualTo(List.of(1, 2));
    }

    // -- static of(<primitive array>)

    @Test
    public void shouldCreateListOfPrimitiveBooleanArray() {
        final Traversable<Boolean> actual = ofAll(new boolean[] { true, false });
        final Traversable<Boolean> expected = of(true, false);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldCreateListOfPrimitiveByteArray() {
        final Traversable<Byte> actual = ofAll(new byte[] { 1, 2, 3 });
        final Traversable<Byte> expected = of((byte) 1, (byte) 2, (byte) 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldCreateListOfPrimitiveCharArray() {
        final Traversable<Character> actual = ofAll(new char[] { 'a', 'b', 'c' });
        final Traversable<Character> expected = of('a', 'b', 'c');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldCreateListOfPrimitiveDoubleArray() {
        final Traversable<Double> actual = ofAll(new double[] { 1d, 2d, 3d });
        final Traversable<Double> expected = of(1d, 2d, 3d);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldCreateListOfPrimitiveFloatArray() {
        final Traversable<Float> actual = ofAll(new float[] { 1f, 2f, 3f });
        final Traversable<Float> expected = of(1f, 2f, 3f);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldCreateListOfPrimitiveIntArray() {
        final Traversable<Integer> actual = ofAll(new int[] { 1, 2, 3 });
        final Traversable<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldCreateListOfPrimitiveLongArray() {
        final Traversable<Long> actual = ofAll(new long[] { 1L, 2L, 3L });
        final Traversable<Long> expected = of(1L, 2L, 3L);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldCreateListOfPrimitiveShortArray() {
        final Traversable<Short> actual = ofAll(new short[] { (short) 1, (short) 2, (short) 3 });
        final Traversable<Short> expected = of((short) 1, (short) 2, (short) 3);
        assertThat(actual).isEqualTo(expected);
    }

    // -- average

    @Test
    public void shouldReturnNoneWhenComputingAverageOfNil() {
        assertThat(empty().average()).isEqualTo(Option.none());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenComputingAverageOfStrings() {
        of("1", "2", "3").average();
    }

    @Test
    public void shouldComputeAverageOfByte() {
        assertThat(of((byte) 1, (byte) 2).average().get()).isEqualTo(1.5);
    }

    @Test
    public void shouldComputeAverageOfDouble() {
        assertThat(of(.1, .2, .3).average().get()).isEqualTo(.2, within(10e-17));
    }

    @Test
    public void shouldComputeAverageOfFloat() {
        assertThat(of(.1f, .2f, .3f).average().get()).isEqualTo(.2, within(10e-9));
    }

    @Test
    public void shouldComputeAverageOfInt() {
        assertThat(of(1, 2, 3).average().get()).isEqualTo(2);
    }

    @Test
    public void shouldComputeAverageOfLong() {
        assertThat(of(1L, 2L, 3L).average().get()).isEqualTo(2);
    }

    @Test
    public void shouldComputeAverageOfShort() {
        assertThat(of((short) 1, (short) 2, (short) 3).average().get()).isEqualTo(2);
    }

    @Test
    public void shouldComputeAverageOfBigInteger() {
        assertThat(of(BigInteger.ZERO, BigInteger.ONE).average().get()).isEqualTo(.5);
    }

    @Test
    public void shouldComputeAverageOfBigDecimal() {
        assertThat(of(BigDecimal.ZERO, BigDecimal.ONE).average().get()).isEqualTo(.5);
    }

    // -- clear

    @Test

    public void shouldClearNil() {
        assertThat(empty().clear()).isEqualTo(empty());
    }

    @Test
    public void shouldClearNonNil() {
        assertThat(of(1, 2, 3).clear()).isEmpty();
    }

    // -- contains

    @Test
    public void shouldRecognizeNilContainsNoElement() {
        final boolean actual = empty().contains(null);
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilDoesNotContainElement() {
        final boolean actual = of(1, 2, 3).contains(0);
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilDoesContainElement() {
        final boolean actual = of(1, 2, 3).contains(2);
        assertThat(actual).isTrue();
    }

    // -- containsAll

    @Test
    public void shouldRecognizeNilNotContainsAllElements() {
        final boolean actual = empty().containsAll(of(1, 2, 3));
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilNotContainsAllOverlappingElements() {
        final boolean actual = of(1, 2, 3).containsAll(of(2, 3, 4));
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilContainsAllOnSelf() {
        final boolean actual = of(1, 2, 3).containsAll(of(1, 2, 3));
        assertThat(actual).isTrue();
    }

    // -- distinct

    @Test
    public void shouldComputeDistinctOfEmptyTraversable() {
        assertThat(empty().distinct()).isEqualTo(empty());
    }

    @Test
    public void shouldComputeDistinctOfNonEmptyTraversable() {
        assertThat(of(1, 1, 2, 2, 3, 3).distinct()).isEqualTo(of(1, 2, 3));
    }

    // -- distinct(Comparator)

    @Test
    public void shouldComputeDistinctByOfEmptyTraversableUsingComparator() {
        final Comparator<Integer> comparator = (i1, i2) -> i1 - i2;
        assertThat(this.<Integer> empty().distinctBy(comparator)).isEqualTo(empty());
    }

    @Test
    public void shouldComputeDistinctByOfNonEmptyTraversableUsingComparator() {
        final Comparator<String> comparator = (s1, s2) -> (s1.charAt(1)) - (s2.charAt(1));
        assertThat(of("1a", "2a", "3a", "3b", "4b", "5c").distinctBy(comparator)).isEqualTo(of("1a", "3b", "5c"));
    }

    // -- distinct(Function)

    @Test
    public void shouldComputeDistinctByOfEmptyTraversableUsingKeyExtractor() {
        assertThat(empty().distinctBy(Function.identity())).isEqualTo(empty());
    }

    @Test
    public void shouldComputeDistinctByOfNonEmptyTraversableUsingKeyExtractor() {
        assertThat(of("1a", "2a", "3a", "3b", "4b", "5c").distinctBy(c -> c.charAt(1)))
                .isEqualTo(of("1a", "3b", "5c"));
    }

    // -- drop

    @Test
    public void shouldDropNoneOnNil() {
        assertThat(empty().drop(1)).isEqualTo(empty());
    }

    @Test
    public void shouldDropNoneIfCountIsNegative() {
        final Traversable<Integer> t = of(1, 2, 3);
        assertThat(t.drop(-1)).isEqualTo(t);
    }

    @Test
    public void shouldDropAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).drop(2)).isEqualTo(of(3));
    }

    @Test
    public void shouldDropAllIfCountExceedsSize() {
        assertThat(of(1, 2, 3).drop(4)).isEqualTo(empty());
    }

    // -- dropRight

    @Test
    public void shouldDropRightNoneOnNil() {
        assertThat(empty().dropRight(1)).isEqualTo(empty());
    }

    @Test
    public void shouldDropRightNoneIfCountIsNegative() {
        final Traversable<Integer> t = of(1, 2, 3);
        assertThat(t.dropRight(-1)).isEqualTo(t);
    }

    @Test
    public void shouldDropRightAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).dropRight(2)).isEqualTo(of(1));
    }

    @Test
    public void shouldDropRightAllIfCountExceedsSize() {
        assertThat(of(1, 2, 3).dropRight(4)).isEqualTo(empty());
    }


    // -- dropUntil

    @Test
    public void shouldDropUntilNoneOnNil() {
        assertTrue(empty().dropUntil(ignored -> true).isEmpty());
    }

    @Test
    public void shouldDropUntilNoneIfPredicateIsTrue() {
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(of(1, 2, 3).dropUntil(ignored -> true)).isEqualTo(of(1, 2, 3));
        } else {
            Traversable<Integer> t = of(1, 2, 3);
            assertThat(t.dropUntil(ignored -> true)).isSameAs(t);
        }
    }

    @Test
    public void shouldDropUntilAllIfPredicateIsFalse() {
        assertTrue(of(1, 2, 3).dropUntil(ignored -> false).isEmpty());
    }

    @Test
    public void shouldDropUntilCorrect() {
        assertThat(of(1, 2, 3).dropUntil(i -> i >= 2)).isEqualTo(of(2, 3));
    }

    // -- dropWhile

    @Test
    public void shouldDropWhileNoneOnNil() {
        assertThat(empty().dropWhile(ignored -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldDropWhileNoneIfPredicateIsFalse() {
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(of(1, 2, 3).dropWhile(ignored -> false)).isEqualTo(of(1, 2, 3));
        } else {
            Traversable<Integer> t = of(1, 2, 3);
            assertThat(t.dropWhile(ignored -> false)).isSameAs(t);
        }
    }

    @Test
    public void shouldDropWhileAllIfPredicateIsTrue() {
        assertThat(of(1, 2, 3).dropWhile(ignored -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldDropWhileCorrect() {
        assertThat(of(1, 2, 3).dropWhile(i -> i < 2)).isEqualTo(of(2, 3));
    }

    // -- existsUnique

    @Test
    public void shouldBeAwareOfExistingUniqueElement() {
        assertThat(of(1, 2).existsUnique(i -> i == 1)).isTrue();
    }

    @Test
    public void shouldBeAwareOfNonExistingUniqueElement() {
        assertThat(this.<Integer> empty().existsUnique(i -> i == 1)).isFalse();
    }

    @Test
    public void shouldBeAwareOfExistingNonUniqueElement() {
        assertThat(of(1, 1, 2).existsUnique(i -> i == 1)).isFalse();
    }

    // -- findFirst

    @Test
    public void shouldFindFirstOfNil() {
        assertThat(empty().find(ignored -> true)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindFirstOfNonNil() {
        assertThat(of(1, 2, 3, 4).find(i -> i % 2 == 0)).isEqualTo(Option.of(2));
    }

    // -- findLast

    @Test
    public void shouldFindLastOfNil() {
        assertThat(empty().findLast(ignored -> true)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindLastOfNonNil() {
        assertThat(of(1, 2, 3, 4).findLast(i -> i % 2 == 0)).isEqualTo(Option.of(4));
    }

    // -- fold

    @Test
    public void shouldFoldMultipleElements() {
        assertThat(of(1, 2, 3).fold(0, (a, b) -> a + b)).isEqualTo(6);
    }

    // -- foldLeft

    @Test
    public void shouldFoldLeftNil() {
        assertThat(this.<String> empty().foldLeft("", (xs, x) -> xs + x)).isEqualTo("");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldLeftNullOperator() {
        this.<String> empty().foldLeft(null, null);
    }

    @Test
    public void shouldFoldLeftNonNil() {
        assertThat(of("a", "b", "c").foldLeft("", (xs, x) -> xs + x)).isEqualTo("abc");
    }

    // -- foldRight

    @Test
    public void shouldFoldRightNil() {
        assertThat(this.<String> empty().foldRight("", (x, xs) -> x + xs)).isEqualTo("");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldRightNullOperator() {
        this.<String> empty().foldRight(null, null);
    }

    @Test
    public void shouldFoldRightNonNil() {
        assertThat(of("a", "b", "c").foldRight("", (x, xs) -> x + xs)).isEqualTo("abc");
    }

    // -- hasDefiniteSize

    @Test
    public void shouldReturnSomethingOnHasDefiniteSize() {
        empty().hasDefiniteSize();
    }

    // -- head

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenHeadOnNil() {
        empty().head();
    }

    @Test
    public void shouldReturnHeadOfNonNil() {
        assertThat(of(1, 2, 3).head()).isEqualTo(1);
    }

    // -- headOption

    @Test
    public void shouldReturnNoneWhenCallingHeadOptionOnNil() {
        assertThat(empty().headOption().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnSomeHeadWhenCallingHeadOptionOnNonNil() {
        assertThat(of(1, 2, 3).headOption()).isEqualTo(Option.some(1));
    }

    // -- init

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenInitOfNil() {
        empty().init().get();
    }

    @Test
    public void shouldGetInitOfNonNil() {
        assertThat(of(1, 2, 3).init()).isEqualTo(of(1, 2));
    }

    // -- groupBy

    @Test
    public void shouldNilGroupBy() {
        assertThat((Iterable)empty().groupBy(Function.identity())).isEqualTo(HashMap.empty());
    }

    @Test
    public void shouldNonNilGroupByIdentity() {
        Map<?, ?> actual = of('a', 'b', 'c').groupBy(Function.identity());
        Map<?, ?> expected = HashMap.empty().put('a', of('a')).put('b', of('b')).put('c', of('c'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldNonNilGroupByEqual() {
        Map<?, ?> actual = (Map<?, ?>) of('a', 'b', 'c').groupBy(c -> 1);
        Map<?, ?> expected = HashMap.empty().put(1, of('a', 'b', 'c'));
        assertThat(actual).isEqualTo(expected);
    }


    // -- grouped

    @Test
    public void shouldGroupedNil() {
        assertThat(empty().grouped(1).isEmpty()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenGroupedWithSizeZero() {
        empty().grouped(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenGroupedWithNegativeSize() {
        empty().grouped(-1);
    }

    @Test
    public void shouldGroupedTraversableWithEqualSizedBlocks() {
        final List<Traversable<Integer>> actual = of(1, 2, 3, 4).grouped(2).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1, 2), Vector.of(3, 4));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldGroupedTraversableWithRemainder() {
        final List<Traversable<Integer>> actual = of(1, 2, 3, 4, 5).grouped(2).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1, 2), Vector.of(3, 4), Vector.of(5));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldGroupedWhenTraversableLengthIsSmallerThanBlockSize() {
        final List<Traversable<Integer>> actual = of(1, 2, 3, 4).grouped(5).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1, 2, 3, 4));
        assertThat(actual).isEqualTo(expected);
    }

    // -- initOption

    @Test
    public void shouldReturnNoneWhenCallingInitOptionOnNil() {
        assertThat(empty().initOption().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnSomeInitWhenCallingInitOptionOnNonNil() {
        assertThat(of(1, 2, 3).initOption()).isEqualTo(Option.some(of(1, 2)));
    }

    // -- isEmpty

    @Test
    public void shouldRecognizeNil() {
        assertThat(empty().isEmpty()).isTrue();
    }

    @Test
    public void shouldRecognizeNonNil() {
        assertThat(of(1).isEmpty()).isFalse();
    }

    // -- isTraversableAgain

    @Test
    public void shouldReturnSomethingOnIsTraversableAgain() {
        empty().isTraversableAgain();
    }

    // -- iterator

    @Test
    public void shouldNotHasNextWhenNilIterator() {
        assertThat(empty().iterator().hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowOnNextWhenNilIterator() {
        empty().iterator().next();
    }

    @Test
    public void shouldIterateFirstElementOfNonNil() {
        assertThat(of(1, 2, 3).iterator().next()).isEqualTo(1);
    }

    @Test
    public void shouldFullyIterateNonNil() {
        final Iterator<Integer> iterator = of(1, 2, 3).iterator();
        int actual;
        for (int i = 1; i <= 3; i++) {
            actual = iterator.next();
            assertThat(actual).isEqualTo(i);
        }
        assertThat(iterator.hasNext()).isFalse();
    }

    // -- mkString()

    @Test
    public void shouldMkStringNil() {
        assertThat(empty().mkString()).isEqualTo("");
    }

    @Test
    public void shouldMkStringNonNil() {
        assertThat(of('a', 'b', 'c').mkString()).isEqualTo("abc");
    }

    // -- mkString(delimiter)

    @Test
    public void shouldMkStringWithDelimiterNil() {
        assertThat(empty().mkString(",")).isEqualTo("");
    }

    @Test
    public void shouldMkStringWithDelimiterNonNil() {
        assertThat(of('a', 'b', 'c').mkString(",")).isEqualTo("a,b,c");
    }

    // -- mkString(delimiter, prefix, suffix)

    @Test
    public void shouldMkStringWithDelimiterAndPrefixAndSuffixNil() {
        assertThat(empty().mkString("[", ",", "]")).isEqualTo("[]");
    }

    @Test
    public void shouldMkStringWithDelimiterAndPrefixAndSuffixNonNil() {
        assertThat(of('a', 'b', 'c').mkString("[", ",", "]")).isEqualTo("[a,b,c]");
    }

    // -- last

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenLastOnNil() {
        empty().last();
    }

    @Test
    public void shouldReturnLastOfNonNil() {
        assertThat(of(1, 2, 3).last()).isEqualTo(3);
    }

    // -- lastOption

    @Test
    public void shouldReturnNoneWhenCallingLastOptionOnNil() {
        assertThat(empty().lastOption().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnSomeLastWhenCallingLastOptionOnNonNil() {
        assertThat(of(1, 2, 3).lastOption()).isEqualTo(Option.some(3));
    }

    // -- length

    @Test
    public void shouldComputeLengthOfNil() {
        assertThat(empty().length()).isEqualTo(0);
    }

    @Test
    public void shouldComputeLengthOfNonNil() {
        assertThat(of(1, 2, 3).length()).isEqualTo(3);
    }

    // -- partition

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenPartitionNilAndPredicateIsNull() {
        empty().partition(null);
    }

    @Test
    public void shouldPartitionNil() {
        assertThat(empty().partition(e -> true)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldPartitionIntsInOddAndEvenHavingOddAndEvenNumbers() {
        assertThat(of(1, 2, 3, 4).partition(i -> i % 2 != 0)).isEqualTo(Tuple.of(of(1, 3), of(2, 4)));
    }

    @Test
    public void shouldPartitionIntsInOddAndEvenHavingOnlyOddNumbers() {
        assertThat(of(1, 3).partition(i -> i % 2 != 0)).isEqualTo(Tuple.of(of(1, 3), empty()));
    }

    @Test
    public void shouldPartitionIntsInOddAndEvenHavingOnlyEvenNumbers() {
        assertThat(of(2, 4).partition(i -> i % 2 != 0)).isEqualTo(Tuple.of(empty(), of(2, 4)));
    }

    // -- max

    @Test
    public void shouldReturnNoneWhenComputingMaxOfNil() {
        assertThat(empty().max()).isEqualTo(Option.none());
    }

    @Test
    public void shouldComputeMaxOfStrings() {
        assertThat(of("1", "2", "3").max()).isEqualTo(Option.some("3"));
    }

    @Test
    public void shouldComputeMaxOfBoolean() {
        assertThat(of(true, false).max()).isEqualTo(Option.some(true));
    }

    @Test
    public void shouldComputeMaxOfByte() {
        assertThat(of((byte) 1, (byte) 2).max()).isEqualTo(Option.some((byte) 2));
    }

    @Test
    public void shouldComputeMaxOfChar() {
        assertThat(of('a', 'b', 'c').max()).isEqualTo(Option.some('c'));
    }

    @Test
    public void shouldComputeMaxOfDouble() {
        assertThat(of(.1, .2, .3).max()).isEqualTo(Option.some(.3));
    }

    @Test
    public void shouldComputeMaxOfFloat() {
        assertThat(of(.1f, .2f, .3f).max()).isEqualTo(Option.some(.3f));
    }

    @Test
    public void shouldComputeMaxOfInt() {
        assertThat(of(1, 2, 3).max()).isEqualTo(Option.some(3));
    }

    @Test
    public void shouldComputeMaxOfLong() {
        assertThat(of(1L, 2L, 3L).max()).isEqualTo(Option.some(3L));
    }

    @Test
    public void shouldComputeMaxOfShort() {
        assertThat(of((short) 1, (short) 2, (short) 3).max()).isEqualTo(Option.some((short) 3));
    }

    @Test
    public void shouldComputeMaxOfBigInteger() {
        assertThat(of(BigInteger.ZERO, BigInteger.ONE).max()).isEqualTo(Option.some(BigInteger.ONE));
    }

    @Test
    public void shouldComputeMaxOfBigDecimal() {
        assertThat(of(BigDecimal.ZERO, BigDecimal.ONE).max()).isEqualTo(Option.some(BigDecimal.ONE));
    }

    // -- maxBy(Comparator)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMaxByWithNullComparator() {
        of(1).maxBy((Comparator<Integer>) null);
    }

    @Test
    public void shouldThrowWhenMaxByOfNil() {
        assertThat(empty().maxBy((o1, o2) -> 0)).isEqualTo(Option.none());
    }

    @Test
    public void shouldCalculateMaxByOfInts() {
        assertThat(of(1, 2, 3).maxBy((i1, i2) -> i1 - i2)).isEqualTo(Option.some(3));
    }

    @Test
    public void shouldCalculateInverseMaxByOfInts() {
        assertThat(of(1, 2, 3).maxBy((i1, i2) -> i2 - i1)).isEqualTo(Option.some(1));
    }

    // -- maxBy(Function)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMaxByWithNullFunction() {
        of(1).maxBy((Function<Integer, Integer>) null);
    }

    @Test
    public void shouldThrowWhenMaxByFunctionOfNil() {
        assertThat(this.<Integer> empty().maxBy(i -> i)).isEqualTo(Option.none());
    }

    @Test
    public void shouldCalculateMaxByFunctionOfInts() {
        assertThat(of(1, 2, 3).maxBy(i -> i)).isEqualTo(Option.some(3));
    }

    @Test
    public void shouldCalculateInverseMaxByFunctionOfInts() {
        assertThat(of(1, 2, 3).maxBy(i -> -i)).isEqualTo(Option.some(1));
    }

    @Test
    public void shouldCallMaxFunctionOncePerElement() {
        final int[] cnt = { 0 };
        assertThat(of(1, 2, 3).maxBy(i -> {
            cnt[0]++;
            return i;
        })).isEqualTo(Option.some(3));
        assertThat(cnt[0]).isEqualTo(3);
    }

    // -- min

    @Test
    public void shouldReturnNoneWhenComputingMinOfNil() {
        assertThat(empty().min()).isEqualTo(Option.none());
    }

    @Test
    public void shouldComputeMinOfStrings() {
        assertThat(of("1", "2", "3").min()).isEqualTo(Option.some("1"));
    }

    @Test
    public void shouldComputeMinOfBoolean() {
        assertThat(of(true, false).min()).isEqualTo(Option.some(false));
    }

    @Test
    public void shouldComputeMinOfByte() {
        assertThat(of((byte) 1, (byte) 2).min()).isEqualTo(Option.some((byte) 1));
    }

    @Test
    public void shouldComputeMinOfChar() {
        assertThat(of('a', 'b', 'c').min()).isEqualTo(Option.some('a'));
    }

    @Test
    public void shouldComputeMinOfDouble() {
        assertThat(of(.1, .2, .3).min()).isEqualTo(Option.some(.1));
    }

    @Test
    public void shouldComputeMinOfFloat() {
        assertThat(of(.1f, .2f, .3f).min()).isEqualTo(Option.some(.1f));
    }

    @Test
    public void shouldComputeMinOfInt() {
        assertThat(of(1, 2, 3).min()).isEqualTo(Option.some(1));
    }

    @Test
    public void shouldComputeMinOfLong() {
        assertThat(of(1L, 2L, 3L).min()).isEqualTo(Option.some(1L));
    }

    @Test
    public void shouldComputeMinOfShort() {
        assertThat(of((short) 1, (short) 2, (short) 3).min()).isEqualTo(Option.some((short) 1));
    }

    @Test
    public void shouldComputeMinOfBigInteger() {
        assertThat(of(BigInteger.ZERO, BigInteger.ONE).min()).isEqualTo(Option.some(BigInteger.ZERO));
    }

    @Test
    public void shouldComputeMinOfBigDecimal() {
        assertThat(of(BigDecimal.ZERO, BigDecimal.ONE).min()).isEqualTo(Option.some(BigDecimal.ZERO));
    }

    // -- minBy(Comparator)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMinByWithNullComparator() {
        of(1).minBy((Comparator<Integer>) null);
    }

    @Test
    public void shouldThrowWhenMinByOfNil() {
        assertThat(empty().minBy((o1, o2) -> 0)).isEqualTo(Option.none());
    }

    @Test
    public void shouldCalculateMinByOfInts() {
        assertThat(of(1, 2, 3).minBy((i1, i2) -> i1 - i2)).isEqualTo(Option.some(1));
    }

    @Test
    public void shouldCalculateInverseMinByOfInts() {
        assertThat(of(1, 2, 3).minBy((i1, i2) -> i2 - i1)).isEqualTo(Option.some(3));
    }

    // -- minBy(Function)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMinByWithNullFunction() {
        of(1).minBy((Function<Integer, Integer>) null);
    }

    @Test
    public void shouldThrowWhenMinByFunctionOfNil() {
        assertThat(this.<Integer> empty().minBy(i -> i)).isEqualTo(Option.none());
    }

    @Test
    public void shouldCalculateMinByFunctionOfInts() {
        assertThat(of(1, 2, 3).minBy(i -> i)).isEqualTo(Option.some(1));
    }

    @Test
    public void shouldCalculateInverseMinByFunctionOfInts() {
        assertThat(of(1, 2, 3).minBy(i -> -i)).isEqualTo(Option.some(3));
    }

    @Test
    public void shouldCallMinFunctionOncePerElement() {
        final int[] cnt = { 0 };
        assertThat(of(1, 2, 3).minBy(i -> {
            cnt[0]++;
            return i;
        })).isEqualTo(Option.some(1));
        assertThat(cnt[0]).isEqualTo(3);
    }

    // -- product

    @Test
    public void shouldComputeProductOfNil() {
        assertThat(empty().product()).isEqualTo(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenComputingProductOfStrings() {
        of("1", "2", "3").product();
    }

    @Test
    public void shouldComputeProductOfByte() {
        assertThat(of((byte) 1, (byte) 2).product()).isEqualTo(2);
    }

    @Test
    public void shouldComputeProductOfDouble() {
        assertThat(of(.1, .2, .3).product().doubleValue()).isEqualTo(.006, within(10e-18));
    }

    @Test
    public void shouldComputeProductOfFloat() {
        assertThat(of(.1f, .2f, .3f).product().doubleValue()).isEqualTo(.006, within(10e-10));
    }

    @Test
    public void shouldComputeProductOfInt() {
        assertThat(of(1, 2, 3).product()).isEqualTo(6);
    }

    @Test
    public void shouldComputeProductOfLong() {
        assertThat(of(1L, 2L, 3L).product()).isEqualTo(6L);
    }

    @Test
    public void shouldComputeProductOfShort() {
        assertThat(of((short) 1, (short) 2, (short) 3).product()).isEqualTo(6);
    }

    @Test
    public void shouldComputeProductOfBigInteger() {
        assertThat(of(BigInteger.ZERO, BigInteger.ONE).product()).isEqualTo(0L);
    }

    @Test
    public void shouldComputeProductOfBigDecimal() {
        assertThat(of(BigDecimal.ZERO, BigDecimal.ONE).product()).isEqualTo(0.0);
    }

    // -- reduce

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenReduceNil() {
        this.<String> empty().reduce((a, b) -> a + b);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceNullOperator() {
        this.<String> empty().reduce(null);
    }

    @Test
    public void shouldReduceNonNil() {
        assertThat(of(1, 2, 3).reduce((a, b) -> a + b)).isEqualTo(6);
    }

    // -- reduceLeft

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenReduceLeftNil() {
        this.<String> empty().reduceLeft((a, b) -> a + b);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceLeftNullOperator() {
        this.<String> empty().reduceLeft(null);
    }

    @Test
    public void shouldReduceLeftNonNil() {
        assertThat(of("a", "b", "c").reduceLeft((xs, x) -> xs + x)).isEqualTo("abc");
    }

    // -- reduceRight

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenReduceRightNil() {
        this.<String> empty().reduceRight((a, b) -> a + b);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceRightNullOperator() {
        this.<String> empty().reduceRight(null);
    }

    @Test
    public void shouldReduceRightNonNil() {
        assertThat(of("a", "b", "c").reduceRight((x, xs) -> x + xs)).isEqualTo("abc");
    }

    // -- replace(curr, new)

    @Test
    public void shouldReplaceElementOfNilUsingCurrNew() {
        assertThat(this.<Integer> empty().replace(1, 2)).isEqualTo(empty());
    }

    @Test
    public void shouldReplaceFirstOccurrenceOfNonNilUsingCurrNewWhenMultipleOccurrencesExist() {
        final Traversable<Integer> testee = of(0, 1, 2, 1);
        final Traversable<Integer> actual = testee.replace(1, 3);
        final Traversable<Integer> expected = of(0, 3, 2, 1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReplaceElementOfNonNilUsingCurrNewWhenOneOccurrenceExists() {
        assertThat(of(0, 1, 2).replace(1, 3)).isEqualTo(of(0, 3, 2));
    }

    // -- replaceAll(curr, new)

    @Test
    public void shouldReplaceAllElementsOfNilUsingCurrNew() {
        assertThat(this.<Integer> empty().replaceAll(1, 2)).isEqualTo(empty());
    }

    @Test
    public void shouldReplaceAllElementsOfNonNilUsingCurrNew() {
        assertThat(of(0, 1, 2, 1).replaceAll(1, 3)).isEqualTo(of(0, 3, 2, 3));
    }

    // -- retainAll

    @Test
    public void shouldRetainAllElementsFromNil() {
        assertThat(empty().retainAll(of(1, 2, 3))).isEqualTo(empty());
    }

    @Test
    public void shouldRetainAllExistingElementsFromNonNil() {
        assertThat(of(1, 2, 3, 1, 2, 3).retainAll(of(1, 2))).isEqualTo(of(1, 2, 1, 2));
    }

    @Test
    public void shouldNotRetainAllNonExistingElementsFromNonNil() {
        assertThat(of(1, 2, 3).retainAll(of(4, 5))).isEqualTo(empty());
    }

    // -- scan, scanLeft, scanRight

    @Test
    public void shouldScanEmpty() {
        final Traversable<Integer> testee = empty();
        final Traversable<Integer> actual = testee.scan(0, (s1, s2) -> s1 + s2);
        assertThat(actual).isEqualTo(this.of(0));
    }

    @Test
    public void shouldScanLeftEmpty() {
        final Traversable<Integer> testee = empty();
        final Traversable<Integer> actual = testee.scanLeft(0, (s1, s2) -> s1 + s2);
        assertThat(actual).isEqualTo(of(0));
    }

    @Test
    public void shouldScanRightEmpty() {
        final Traversable<Integer> testee = empty();
        final Traversable<Integer> actual = testee.scanRight(0, (s1, s2) -> s1 + s2);
        assertThat(actual).isEqualTo(of(0));
    }

    @Test
    public void shouldScanNonEmpty() {
        final Traversable<Integer> testee = of(1, 2, 3);
        final Traversable<Integer> actual = testee.scan(0, (acc, s) -> acc + s);
        assertThat(actual).isEqualTo(of(0, 1, 3, 6));
    }

    @Test
    public void shouldScanLeftNonEmpty() {
        final Traversable<Integer> testee = of(1, 2, 3);
        final Traversable<String> actual = testee.scanLeft("x", (acc, i) -> acc + i);
        assertThat(actual).isEqualTo(of("x", "x1", "x12", "x123"));
    }

    @Test
    public void shouldScanRightNonEmpty() {
        final Traversable<Integer> testee = of(1, 2, 3);
        final Traversable<String> actual = testee.scanRight("x", (i, acc) -> acc + i);
        
        assertThat(actual).isEqualTo(of("x", "x3","x32","x321"));
    }

    @Test
    public void shouldScanWithNonComparable() {
        final Traversable<NonComparable> testee = of(new NonComparable("a"));
        final List<NonComparable> actual = List.ofAll(testee.scan(new NonComparable("x"), (u1, u2) -> new NonComparable(u1.value + u2.value)));
        final List<NonComparable> expected = List.of("x", "xa").map(NonComparable::new);
        assertThat(actual).containsAll(expected);
        assertThat(actual.length()).isEqualTo(expected.length());
    }

    @Test
    public void shouldScanLeftWithNonComparable() {
        final Traversable<NonComparable> testee = of(new NonComparable("a"));
        final List<NonComparable> actual = List.ofAll(testee.scanLeft(new NonComparable("x"), (u1, u2) -> new NonComparable(u1.value + u2.value)));
        final List<NonComparable> expected = List.of("x", "xa").map(NonComparable::new);
        assertThat(actual).containsAll(expected);
        assertThat(actual.length()).isEqualTo(expected.length());
    }

    @Test
    public void shouldScanRightWithNonComparable() {
        final Traversable<NonComparable> testee = of(new NonComparable("a"));
        final List<NonComparable> actual = List.ofAll(testee.scanRight(new NonComparable("x"), (u1, u2) -> new NonComparable(u1.value + u2.value)));
        final List<NonComparable> expected = List.of("ax", "x").map(NonComparable::new);
        assertThat(actual).containsAll(expected);
        assertThat(actual.length()).isEqualTo(expected.length());
    }

    // -- sliding(size)

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNilByZeroSize() {
        empty().sliding(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNilByNegativeSize() {
        empty().sliding(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNonNilByZeroSize() {
        of(1).sliding(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNonNilByNegativeSize() {
        of(1).sliding(-1);
    }

    @Test
    public void shouldSlideNilBySize() {
        assertThat(empty().sliding(1)).isEmpty();
    }

    @Test
    public void shouldSlideNonNilBySize1() {
        final List<Traversable<Integer>> actual = of(1, 2, 3).sliding(1).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1), Vector.of(2), Vector.of(3));
        assertThat(actual).isEqualTo(expected);
    }

    @Test // #201
    public void shouldSlideNonNilBySize2() {
        final List<Traversable<Integer>> actual = of(1, 2, 3, 4, 5).sliding(2).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1, 2), Vector.of(2, 3), Vector.of(3, 4), Vector.of(4, 5));
        assertThat(actual).isEqualTo(expected);
    }

    // -- sliding(size, step)

    @Test
    public void shouldSlideNilBySizeAndStep() {
        assertThat(empty().sliding(1, 1).isEmpty()).isTrue();
    }

    @Test
    public void shouldSlide5ElementsBySize2AndStep3() {
        final List<Traversable<Integer>> actual = of(1, 2, 3, 4, 5).sliding(2, 3).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1, 2), Vector.of(4, 5));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldSlide5ElementsBySize2AndStep4() {
        final List<Traversable<Integer>> actual = of(1, 2, 3, 4, 5).sliding(2, 4).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1, 2), Vector.of(5));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldSlide5ElementsBySize2AndStep5() {
        final List<Traversable<Integer>> actual = of(1, 2, 3, 4, 5).sliding(2, 5).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1, 2));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldSlide4ElementsBySize5AndStep3() {
        final List<Traversable<Integer>> actual = of(1, 2, 3, 4).sliding(5, 3).toList().map(Vector::ofAll);
        final List<Traversable<Integer>> expected = List.of(Vector.of(1, 2, 3, 4));
        assertThat(actual).isEqualTo(expected);
    }

    // -- span

    @Test
    public void shouldSpanNil() {
        assertThat(this.<Integer> empty().span(i -> i < 2)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldSpanNonNil() {
        assertThat(of(0, 1, 2, 3).span(i -> i < 2)).isEqualTo(Tuple.of(of(0, 1), of(2, 3)));
    }

    // -- spliterator

    @Test
    public void shouldSplitNil() {
        final java.util.List<Integer> actual = new java.util.ArrayList<>();
        this.<Integer> empty().spliterator().forEachRemaining(actual::add);
        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldSplitNonNil() {
        final java.util.List<Integer> actual = new java.util.ArrayList<>();
        of(1, 2, 3).spliterator().forEachRemaining(actual::add);
        assertThat(actual).isEqualTo(Arrays.asList(1, 2, 3));
    }

    @Test @Ignore
    public void shouldHaveImmutableSpliterator() {
        assertThat(of(1, 2, 3).spliterator().characteristics() & Spliterator.IMMUTABLE).isNotZero();
    }

    @Test
    public void shouldHaveOrderedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().characteristics() & Spliterator.ORDERED).isNotZero();
    }

    

    @Test
    public void shouldReturnSizeWhenSpliterator() {
        assertThat(of(1, 2, 3).spliterator().getExactSizeIfKnown()).isEqualTo(-1l);
    }

    // -- stderr

    @Test
    public void shouldWriteToStderr() {
        of(1, 2, 3).stderr();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandleStderrIOException() {
        final PrintStream originalErr = System.err;
        try (PrintStream failingPrintStream = failingPrintStream()) {
            System.setErr(failingPrintStream);
            of(0).stderr();
        } finally {
            System.setErr(originalErr);
        }
    }

    // -- stdout

    @Test
    public void shouldWriteToStdout() {
        of(1, 2, 3).stdout();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandleStdoutIOException() {
        final PrintStream originalOut = System.out;
        try (PrintStream failingPrintStream = failingPrintStream()) {
            System.setOut(failingPrintStream);
            of(0).stdout();
        } finally {
            System.setOut(originalOut);
        }
    }

    // -- PrintStream

    @Test
    public void shouldWriteToPrintStream() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        final PrintStream out = new PrintStream(baos);
        of(1, 2, 3).out(out);
        assertThat(baos.toString()).isEqualTo(of(1, 2, 3).mkString("", lineSeparator(), lineSeparator()));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandlePrintStreamIOException() {
        try (PrintStream failingPrintStream = failingPrintStream()) {
            of(0).out(failingPrintStream);
        }
    }

    // -- PrintWriter

    @Test
    public void shouldWriteToPrintWriter() {
        final StringWriter sw = new StringWriter();
        final PrintWriter out = new PrintWriter(sw);
        of(1, 2, 3).out(out);
        assertThat(sw.toString()).isEqualTo(of(1, 2, 3).mkString("", lineSeparator(), lineSeparator()));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandlePrintWriterIOException() {
        try (PrintWriter failingPrintWriter = failingPrintWriter()) {
            of(0).out(failingPrintWriter);
        }
    }

    // -- sum

    @Test
    public void shouldComputeSumOfNil() {
        assertThat(empty().sum()).isEqualTo(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenComputingSumOfStrings() {
        of("1", "2", "3").sum();
    }

    @Test
    public void shouldComputeSumOfByte() {
        assertThat(of((byte) 1, (byte) 2).sum()).isEqualTo(3);
    }

    @Test
    public void shouldComputeSumOfDouble() {
        assertThat(of(.1, .2, .3).sum().doubleValue()).isEqualTo(.6, within(10e-16));
    }

    @Test
    public void shouldComputeSumOfFloat() {
        assertThat(of(.1f, .2f, .3f).sum().doubleValue()).isEqualTo(.6, within(10e-8));
    }

    @Test
    public void shouldComputeSumOfInt() {
        assertThat(of(1, 2, 3).sum()).isEqualTo(6);
    }

    @Test
    public void shouldComputeSumOfLong() {
        assertThat(of(1L, 2L, 3L).sum()).isEqualTo(6L);
    }

    @Test
    public void shouldComputeSumOfShort() {
        assertThat(of((short) 1, (short) 2, (short) 3).sum()).isEqualTo(6);
    }

    @Test
    public void shouldComputeSumOfBigInteger() {
        assertThat(of(BigInteger.ZERO, BigInteger.ONE).sum()).isEqualTo(1L);
    }

    @Test
    public void shouldComputeSumOfBigDecimal() {
        assertThat(of(BigDecimal.ZERO, BigDecimal.ONE).sum()).isEqualTo(1.0);
    }

    // -- take

    @Test
    public void shouldTakeNoneOnNil() {
        assertThat(empty().take(1)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeNoneIfCountIsNegative() {
        assertThat(of(1, 2, 3).take(-1)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).take(2)).isEqualTo(of(1, 2));
    }

    @Test
    public void shouldTakeAllIfCountExceedsSize() {
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(of(1, 2, 3).take(4)).isEqualTo(of(1, 2, 3));
        } else {
            final Traversable<Integer> t = of(1, 2, 3);
            assertThat(t.take(4)).isSameAs(t);
        }
    }

    // -- takeRight

    @Test
    public void shouldTakeRightNoneOnNil() {
        assertThat(empty().takeRight(1)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeRightNoneIfCountIsNegative() {
        assertThat(of(1, 2, 3).takeRight(-1)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeRightAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).takeRight(2)).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldTakeRightAllIfCountExceedsSize() {
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(of(1, 2, 3).takeRight(4)).isEqualTo(of(1, 2, 3));
        } else {
            final Traversable<Integer> t = of(1, 2, 3);
            assertThat(t.takeRight(4)).isSameAs(t);
        }
    }

    // -- takeUntil

    @Test
    public void shouldTakeUntilNoneOnNil() {
        assertThat(empty().takeUntil(x -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeUntilAllOnFalseCondition() {
        final Traversable<Integer> t = of(1, 2, 3);
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(of(1, 2, 3).takeUntil(x -> false)).isEqualTo(of(1, 2, 3));
        } else {
            assertThat(t.takeUntil(x -> false)).isSameAs(t);
        }
    }

    @Test
    public void shouldTakeUntilAllOnTrueCondition() {
        assertThat(of(1, 2, 3).takeUntil(x -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeUntilAsExpected() {
        assertThat(of(2, 4, 5, 6).takeUntil(x -> x % 2 != 0)).isEqualTo(of(2, 4));
    }

    // -- takeWhile

    @Test
    public void shouldTakeWhileNoneOnNil() {
        assertThat(empty().takeWhile(x -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeWhileAllOnFalseCondition() {
        assertThat(of(1, 2, 3).takeWhile(x -> false)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeWhileAllOnTrueCondition() {
        final Traversable<Integer> t = of(1, 2, 3);
        if (useIsEqualToInsteadOfIsSameAs()) {
            assertThat(of(1, 2, 3).takeWhile(x -> true)).isEqualTo(of(1, 2, 3));
        } else {
            assertThat(t.takeWhile(x -> true)).isSameAs(t);
        }
    }

    @Test
    public void shouldTakeWhileAsExpected() {
        assertThat(of(2, 4, 5, 6).takeWhile(x -> x % 2 == 0)).isEqualTo(of(2, 4));
    }

    // -- tail

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenTailOnNil() {
        empty().tail();
    }

    @Test
    public void shouldReturnTailOfNonNil() {
        assertThat(of(1, 2, 3).tail()).isEqualTo(of(2, 3));
    }

    // -- tailOption

    @Test
    public void shouldReturnNoneWhenCallingTailOptionOnNil() {
        assertThat(empty().tailOption().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnSomeTailWhenCallingTailOptionOnNonNil() {
        assertThat(of(1, 2, 3).tailOption()).isEqualTo(Option.some(of(2, 3)));
    }

    // -- toJavaArray(Class)

    @Test
    public void shouldConvertNilToJavaArray() {
        final Integer[] actual = List.<Integer> empty().toJavaArray(Integer.class);
        final Integer[] expected = new Integer[] {};
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldConvertNonNilToJavaArray() {
        final Integer[] array = of(1, 2).toJavaArray(Integer.class);
        final Integer[] expected = new Integer[] { 1, 2 };
        assertThat(array).isEqualTo(expected);
    }

    // -- toJavaList

    @Test
    public void shouldConvertNilToArrayList() {
        assertThat(this.<Integer> empty().toJavaList()).isEqualTo(new ArrayList<Integer>());
    }

    @Test
    public void shouldConvertNonNilToArrayList() {
        assertThat(of(1, 2, 3).toJavaList()).isEqualTo(Arrays.asList(1, 2, 3));
    }

    // -- toJavaMap(Function)

    @Test
    public void shouldConvertNilToHashMap() {
        assertThat(this.<Integer> empty().toJavaMap(x -> Tuple.of(x, x))).isEqualTo(new java.util.HashMap<>());
    }

    @Test
    public void shouldConvertNonNilToHashMap() {
        final java.util.Map<Integer, Integer> expected = new java.util.HashMap<>();
        expected.put(1, 1);
        expected.put(2, 2);
        assertThat(of(1, 2).toJavaMap(x -> Tuple.of(x, x))).isEqualTo(expected);
    }

    // -- toJavaSet

    @Test
    public void shouldConvertNilToHashSet() {
        assertThat(this.<Integer> empty().toJavaSet()).isEqualTo(new java.util.HashSet<>());
    }

    @Test
    public void shouldConvertNonNilToHashSet() {
        final java.util.Set<Integer> expected = new java.util.HashSet<>();
        expected.add(2);
        expected.add(1);
        expected.add(3);
        assertThat(of(1, 2, 2, 3).toJavaSet()).isEqualTo(expected);
    }

    // ++++++ OBJECT ++++++

    // -- equals

    @Test
    public void shouldEqualSameTraversableInstance() {
        final Traversable<?> traversable = empty();
        assertThat(traversable).isEqualTo(traversable);
    }

    @Test
    public void shouldNilNotEqualsNull() {
        assertThat(empty()).isNotNull();
    }

    @Test
    public void shouldNonNilNotEqualsNull() {
        assertThat(of(1)).isNotNull();
    }

    @Test
    public void shouldEmptyNotEqualsDifferentType() {
        assertThat(empty()).isNotEqualTo("");
    }

    @Test
    public void shouldNonEmptyNotEqualsDifferentType() {
        assertThat(of(1)).isNotEqualTo("");
    }

    @Test
    public void shouldRecognizeEqualityOfNils() {
        assertThat(empty()).isEqualTo(empty());
    }

    @Test
    public void shouldRecognizeEqualityOfNonNils() {
        assertThat(of(1, 2, 3).equals(of(1, 2, 3))).isTrue();
    }

    @Test
    public void shouldRecognizeNonEqualityOfTraversablesOfSameSize() {
        assertThat(of(1, 2, 3).equals(of(1, 2, 4))).isFalse();
    }

    @Test
    public void shouldRecognizeNonEqualityOfTraversablesOfDifferentSize() {
        assertThat(of(1, 2, 3).equals(of(1, 2))).isFalse();
    }

    // -- hashCode

    @Test
    public void shouldCalculateHashCodeOfNil() {
        assertThat(empty().hashCode() == empty().hashCode()).isTrue();
    }

    @Test
    public void shouldCalculateHashCodeOfNonNil() {
        assertThat(of(1, 2).hashCode() == of(1, 2).hashCode()).isTrue();
    }

    @Test
    public void shouldCalculateDifferentHashCodesForDifferentTraversables() {
        assertThat(of(1, 2).hashCode() != of(2, 3).hashCode()).isTrue();
    }

    // -- Serializable interface
    /**
    @Test
    public void shouldSerializeDeserializeNil() {
        final Object actual = deserialize(serialize(empty()));
        final Object expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPreserveSingletonInstanceOnDeserialization() {
        final boolean actual = deserialize(serialize(empty())) == empty();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldSerializeDeserializeNonNil() {
        final Object actual = deserialize(serialize(of(1, 2, 3)));
        final Object expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }
**/
    // -- static collector()

    @Test
    public void shouldStreamAndCollectNil() {
        testCollector(() -> {
            final Traversable<?> actual = java.util.stream.Stream.empty().collect(this.<Object> collector());
            assertThat(actual).isEmpty();
        });
    }

    @Test
    public void shouldStreamAndCollectNonNil() {
        testCollector(() -> {
            final Traversable<?> actual = java.util.stream.Stream.of(1, 2, 3).collect(this.<Object> collector());
            assertThat(actual).isEqualTo(of(1, 2, 3));
        });
    }

    @Test
    public void shouldParallelStreamAndCollectNil() {
        testCollector(() -> {
            final Traversable<?> actual = java.util.stream.Stream.empty().parallel().collect(this.<Object> collector());
            assertThat(actual).isEmpty();
        });
    }

    @Test
    public void shouldParallelStreamAndCollectNonNil() {
        testCollector(() -> {
            final Traversable<?> actual = java.util.stream.Stream.of(1, 2, 3).parallel().collect(this.<Object> collector());
            assertThat(actual).isEqualTo(of(1, 2, 3));
        });
    }

    private void testCollector(Runnable test) {
        if (isTraversableAgain()) {
            test.run();
        } else {
            try {
                collector();
                fail("Collections which are not traversable again should not define a Collector.");
            } catch (UnsupportedOperationException x) {
                // ok
            } catch (Throwable x) {
                fail("Unexpected exception", x);
            }
        }
    }

    // helpers

    static PrintStream failingPrintStream() {
        return new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException();
            }
        });
    }

    static PrintWriter failingPrintWriter() {
        return new PrintWriter(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException();
            }
        });
    }

    /**
     * Wraps a String in order to ensure that it is not Comparable.
     */
    static final class NonComparable {

        final String value;

        NonComparable(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof NonComparable) {
                final NonComparable that = (NonComparable) obj;
                return Objects.equals(this.value, that.value);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
