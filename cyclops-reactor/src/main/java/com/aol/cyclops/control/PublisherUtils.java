package com.aol.cyclops.control;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.reactivestreams.Publisher;

import com.aol.cyclops.types.stream.reactive.SeqSubscriber;
import com.aol.cyclops.types.stream.reactive.ValueSubscriber;


public class PublisherUtils {

   
   
    public static<T> ReactiveSeq<T> stream(Publisher<T> pub){
        return ReactiveSeq.fromStream(jdkStream(pub));
    }
    public static<T> Stream<T> jdkStream(Publisher<T> pub){
        SeqSubscriber<T> sub = SeqSubscriber.subscriber();
        pub.subscribe(sub);
        return StreamSupport.stream(sub.spliterator(),false);
    }
}
