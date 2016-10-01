package com.aol.cyclops.control;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.reactor.operators.GroupBySize;
import com.aol.cyclops.reactor.operators.GroupedWhile;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;

import reactor.core.publisher.Flux;

public class FluxUtils {
    public final static <T, C extends Collection<? super T>> Flux<C> grouped(final Flux<T> stream, final int groupSize,
            final Supplier<C> factory) {
        return new GroupBySize<T, C>(
                                             stream, factory).grouped(groupSize);

    }
    
    public static <T> Iterator<T> iterator(Flux<T> stream){
        SeqSubscriber<T> sub = SeqSubscriber.subscriber();
        Iterator<T> it = stream.subscribeWith(sub).iterator();
        return it;
    }
    
    public static <T> Flux<T> combine(final Flux<T> stream, final BiPredicate<? super T, ? super T> predicate, final BinaryOperator<T> op) {
        final Iterator<T> it = iterator(stream);
        final Object UNSET = new Object();
        return Flux.fromIterable(()->new Iterator<ReactiveSeq<T>>() {
            T current = (T) UNSET;

            @Override
            public boolean hasNext() {
                return it.hasNext() || current != UNSET;
            }

            @Override
            public ReactiveSeq<T> next() {
                while (it.hasNext()) {
                    final T next = it.next();

                    if (current == UNSET) {
                        current = next;

                    } else if (predicate.test(current, next)) {
                        current = op.apply(current, next);

                    } else {
                        final T result = current;
                        current = (T) UNSET;
                        return ReactiveSeq.of(result, next);
                    }
                }
                if (it.hasNext())
                    return ReactiveSeq.empty();
                final T result = current;
                current = (T) UNSET;
                return ReactiveSeq.of(result);
            }

        }).flatMap(Function.identity());
    }
    /**
     * Repeat in a Flux while specified predicate holds
     * <pre>
     * {@code 
     *  int count =0;
     *  
        assertThat(FluxUtils.cycleWhile(Flux.just(1,2,2)
                                            ,next -> count++<6 )
                                            .collect(Collectors.toList()),equalTo(Arrays.asList(1,2,2,1,2,2)));
     * }
     * </pre>
     * @param predicate
     *            repeat while true
     * @return Repeating Stream
     */
    public final static <T> Flux<T> cycleWhile(final Flux<T> stream, final Predicate<? super T> predicate) {
       
        return stream.repeat().takeWhile(predicate);
    }

    /**
     * Repeat in a Stream until specified predicate holds
     * 
     * <pre>
     * {@code 
     *  count =0;
        assertThat(FluxUtils.cycleUntil(Flux.just(1,2,2,3)
                                            ,next -> count++>10 )
                                            .collect(Collectors.toList()),equalTo(Arrays.asList(1, 2, 2, 3, 1, 2, 2, 3, 1, 2, 2)));
    
     * }
     * </pre>
     * @param predicate
     *            repeat while true
     * @return Repeating Stream
     */
    public final static <T> Flux<T> cycleUntil(final Flux<T> stream, final Predicate<? super T> predicate) {
        return stream.repeat().takeUntil(predicate);
    }
    
    /**
     * Keep only those elements in a stream that are of a given type.
     * 
     * 
     * assertThat(Arrays.asList(1, 2, 3), 
     *      equalTo( FluxUtils.ofType(Flux.just(1, "a", 2, "b", 3,Integer.class));
     * 
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Flux<U> ofType(final Flux<T> stream, final Class<? extends U> type) {
        return stream.filter(type::isInstance)
                     .map(t -> (U) t);
    }
    
    public final static <T> Flux<ListX<T>> groupedWhile(final Flux<T> stream, final Predicate<? super T> predicate) {
        return new GroupedWhile<T, ListX<T>>(
                                                   stream).batchWhile(predicate);
    }

    public final static <T, C extends Collection<? super T>> Flux<C> groupedWhile(final Flux<T> stream, final Predicate<? super T> predicate,
            final Supplier<C> factory) {
        return new GroupedWhile<T, C>(
                                            stream, factory).batchWhile(predicate);
    }

    public final static <T> Flux<ListX<T>> groupedUntil(final Flux<T> stream, final Predicate<? super T> predicate) {
        return groupedWhile(stream, predicate.negate());
    }
    /**
     * Create a Stream that finitely cycles the provided Streamable, provided number of times
     * 
     * <pre>
     * {@code 
     * assertThat(StreamUtils.cycle(3,Streamable.of(1,2,2))
                                .collect(Collectors.toList()),
                                    equalTo(Arrays.asList(1,2,2,1,2,2,1,2,2)));
     * }
     * </pre>
     * @param s Streamable to cycle
     * @return New cycling stream
     */
    public static <U> Stream<U> cycle(final int times, final Flux<U> s) {
        return Stream.iterate(s.stream(), s1 -> s.stream())
                     .limit(times)
                     .flatMap(Function.identity());
    }
}

