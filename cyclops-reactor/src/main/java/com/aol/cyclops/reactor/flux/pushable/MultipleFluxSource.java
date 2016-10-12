package com.aol.cyclops.reactor.flux.pushable;

import java.util.stream.Stream;

import com.aol.cyclops.control.LazyReact;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.async.Queue;
import com.aol.cyclops.data.async.Topic;
import com.aol.cyclops.types.futurestream.LazyFutureStream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MultipleFluxSource<T> {

    private final Topic<T> topic;

    public MultipleFluxSource(Queue<T> q) {
        topic = new Topic(
                          q);
    }

    /**
     * Create a pushable LazyFutureStream using the supplied ReactPool
     * 
     * @param s React builder to use to create the Stream
     * @return a Tuple2 with a Topic&lt;T&gt; and LazyFutureStream&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public LazyFutureStream<T> futureStream(LazyReact s) {

        return s.fromStream(topic.stream());

    }

    /**
     * Create a pushable JDK 8 Stream
     * @return a Tuple2 with a Topic&lt;T&gt; and Stream&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public Stream<T> stream() {

        return (Stream) topic.stream();

    }

    /**
     * Create a pushable ReactiveSeq
     * 
     * @return a Tuple2 with a Topic&lt;T&gt; and Seq&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public ReactiveSeq<T> reactiveSeq() {

        return topic.stream();
    }

    /**
     * Create a pushable Flux
     * 
     * @return a Tuple2 with a Topic&lt;T&gt; and Flux&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public Flux<T> flux() {

        return Flux.from(topic.stream());
    }

    /**
     * Create a pushable Mono
     * 
     * @return a Tuple2 with a Topic&lt;T&gt; and Mono&lt;T&gt; - add data to the Queue
     * to push it to the Stream
     */
    public Mono<T> mono() {

        return Mono.from(flux());
    }

    /**
     * @return Topic used as input for any generated Streams
     */
    public Topic<T> getInput() {
        return topic;
    }

}