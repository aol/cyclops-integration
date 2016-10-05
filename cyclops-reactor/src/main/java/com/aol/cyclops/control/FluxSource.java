package com.aol.cyclops.control;


import java.util.Objects;
import java.util.stream.Stream;

import com.aol.cyclops.data.async.Adapter;
import com.aol.cyclops.data.async.Queue;
import com.aol.cyclops.data.async.QueueFactories;
import com.aol.cyclops.data.async.QueueFactory;
import com.aol.cyclops.reactor.flux.pushable.MultipleFluxSource;
import com.aol.cyclops.reactor.flux.pushable.PushableFlux;
import com.aol.cyclops.types.futurestream.LazyFutureStream;
import com.aol.cyclops.util.stream.pushable.PushableLazyFutureStream;
import com.aol.cyclops.util.stream.pushable.PushableReactiveSeq;
import com.aol.cyclops.util.stream.pushable.PushableStream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * Create Java 8 Streams that data can be pushed into
 * 
 * @author johnmcclean
 *
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FluxSource {

    private final int backPressureAfter;
    private final boolean backPressureOn;

    /**
     * @return a builder that will use Topics to allow multiple Streams from the same data
     */
    public static <T> MultipleFluxSource<T> ofMultiple() {
        return new MultipleFluxSource<T>(
                                           FluxSource.ofUnbounded()
                                                       .createQueue());
    }

    /**
     * @return a builder that will use Topics to allow multiple Streams from the same data
     */
    public static <T> MultipleFluxSource<T> ofMultiple(int backPressureAfter) {
        return new MultipleFluxSource<T>(
                                           FluxSource.of(backPressureAfter)
                                                       .createQueue());
    }

    /**
     * @return a builder that will use Topics to allow multiple Streams from the same data
     */
    public static <T> MultipleFluxSource<T> ofMultiple(QueueFactory<?> q) {
        Objects.requireNonNull(q);
        return new MultipleFluxSource<T>(
                                           FluxSource.of(q)
                                                       .createQueue());
    }

    public static FluxSource of(QueueFactory<?> q) {
        Objects.requireNonNull(q);
        return new FluxSource() {
            @SuppressWarnings("unchecked")
            @Override
            <T> Queue<T> createQueue() {
                return (Queue<T>) q.build();

            }
        };
    }

    public static FluxSource ofUnbounded() {
        return new FluxSource();
    }

    public static FluxSource of(int backPressureAfter) {
        if (backPressureAfter < 1)
            throw new IllegalArgumentException(
                                               "Can't apply back pressure after less than 1 event");
        return new FluxSource(
                                backPressureAfter, true);
    }

    <T> Queue<T> createQueue() {

        Queue q;
        if (!backPressureOn)
            q = QueueFactories.unboundedNonBlockingQueue()
                              .build();
        else
            q = QueueFactories.boundedQueue(backPressureAfter)
                              .build();
        return q;
    }

    private FluxSource() {

        this.backPressureAfter = Runtime.getRuntime()
                                        .availableProcessors();
        this.backPressureOn = false;
    }

    /**
     * Create a pushable LazyFutureStream using the supplied ReactPool
     * 
     * @param s ReactPool to use to create the Stream
     * @return a Tuple2 with a Queue&lt;T&gt; and LazyFutureStream&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public <T> PushableLazyFutureStream<T> futureStream(LazyReact s) {

        Queue<T> q = createQueue();
        return new PushableLazyFutureStream<T>(
                                               q, s.fromStream(q.stream()));

    }

    /**
     * Create a LazyFutureStream. his will call LazyFutureStream#futureStream(Stream) which creates
     * a sequential LazyFutureStream
     * 
     * @param adapter Adapter to create a LazyFutureStream from
     * @return A LazyFutureStream that will accept values from the supplied adapter
     */
    public static <T> LazyFutureStream<T> futureStream(Adapter<T> adapter, LazyReact react) {

        return react.fromStream(adapter.stream());
    }

    /**
     * Create a pushable JDK 8 Stream
     * @return a Tuple2 with a Queue&lt;T&gt; and Stream&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public <T> PushableStream<T> stream() {
        Queue<T> q = createQueue();
        return new PushableStream<T>(
                                     q, (Stream) q.stream());

    }
    /**
     * Create a pushable Flux
     * 
     * @return a Tuple2 with a Queue&lt;T&gt; and Fliux&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public <T> PushableFlux<T> flux() {
        Queue<T> q = createQueue();
        return new PushableFlux<T>(
                                          q, Flux.from(q.stream()));
    }
    /**
     * Create a pushable ReactiveSeq
     * 
     * @return a Tuple2 with a Queue&lt;T&gt; and Seq&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public <T> PushableReactiveSeq<T> reactiveSeq() {
        Queue<T> q = createQueue();
        return new PushableReactiveSeq<T>(
                                          q, q.stream());
    }

    /**
     * Create a JDK 8 Stream from the supplied Adapter
     * 
     * @param adapter Adapter to create a Steam from
     * @return Stream that will accept input from supplied adapter
     */
    public static <T> Stream<T> stream(Adapter<T> adapter) {

        return adapter.stream();
    }

    /**
     * Create a pushable ReactiveSeq
     * 
     * @param adapter Adapter to create a Seq from
     * @return A Seq that will accept input from a supplied adapter
     */
    public static <T> ReactiveSeq<T> reactiveSeq(Adapter<T> adapter) {

        return adapter.stream();
    }
    /**
     * Create a pushable ReactiveSeq
     * 
     * @param adapter Adapter to create a Flux from
     * @return A Flux that will accept input from a supplied adapter
     */
    public static <T> Flux<T> flux(Adapter<T> adapter) {

        return Flux.from(adapter.stream());
    }

}
