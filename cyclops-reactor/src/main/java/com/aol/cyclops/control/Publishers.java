package com.aol.cyclops.control;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.reactivestreams.Publisher;

import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;

import reactor.core.publisher.Flux;

/**
 * Utilities for working with reactive-streams Publishers
 * 
 * @author johnmcclean
 *
 */
public class Publishers {

    /**
     * Construct an AnyM type from a Publisher. This allows the Publisher to be manipulated according to a standard interface
     * along with a vast array of other Java Monad implementations
     * 
     * <pre>
     * {@code 
     *    
     *    AnyMSeq<Integer> publisher = Publishers.anyM(Flux.just(1,2,3));
     *    AnyMSeq<Integer> transformedPublisher = myGenericOperation(flux);
     *    
     *    public AnyMSeq<Integer> myGenericOperation(AnyMSeq<Integer> monad);
     * }
     * </pre>
     * 
     * @param flux To wrap inside an AnyM
     * @return AnyMSeq wrapping a flux
     */
    public static <T> AnyMSeq<T> anyM(Publisher<T> flux) {
        return AnyM.ofSeq(flux);
    }

    /**
     * Convert a reactive-streams Publisher to a cyclops-react ReactiveSeq extended Stream type
     * 
     * @param pub Publisher to convert to a Stream
     * @return ReactiveSeq
     */
    public static <T> ReactiveSeq<T> stream(Publisher<T> pub) {
        return ReactiveSeq.fromStream(jdkStream(pub));
    }

    /**
     * Convert a reactive-streams Publisher to a plain java.util.Stream
     * 
     * @param pub Publisher to convert to a Stream
     * @return Stream
     */
    public static <T> Stream<T> jdkStream(Publisher<T> pub) {
        SeqSubscriber<T> sub = SeqSubscriber.subscriber();
        pub.subscribe(sub);
        return StreamSupport.stream(sub.spliterator(), false);
    }
}
