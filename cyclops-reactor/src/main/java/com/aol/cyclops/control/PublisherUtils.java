package com.aol.cyclops.control;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.reactivestreams.Publisher;

import com.aol.cyclops.types.stream.reactive.SeqSubscriber;


/**
 * Utilities for working with reactive-streams Publishers
 * 
 * @author johnmcclean
 *
 */
public class PublisherUtils {

   
   
    /**
     * Convert a reactive-streams Publisher to a cyclops-react ReactiveSeq extended Stream type
     * 
     * @param pub Publisher to convert to a Stream
     * @return ReactiveSeq
     */
    public static<T> ReactiveSeq<T> stream(Publisher<T> pub){
        return ReactiveSeq.fromStream(jdkStream(pub));
    }
    
    /**
     * Convert a reactive-streams Publisher to a plain java.util.Stream
     * 
     * @param pub Publisher to convert to a Stream
     * @return Stream
     */
    public static<T> Stream<T> jdkStream(Publisher<T> pub){
        SeqSubscriber<T> sub = SeqSubscriber.subscriber();
        pub.subscribe(sub);
        return StreamSupport.stream(sub.spliterator(),false);
    }
}
