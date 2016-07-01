package com.aol.cyclops.reactor;

import static com.aol.cyclops.control.For.Publishers.each2;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jooq.lambda.tuple.Tuple;
import org.reactivestreams.Publisher;

import com.aol.cyclops.control.For;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.types.Functor;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
@AllArgsConstructor


public class FutureW2<T> implements Functor2<T> {
	
	private final CompletableFuture<T> future;
	
	@Override
	public <R> Functor2<R> map(Function<? super T, ? extends R> fn) {
		return new FutureW2<R>(future.thenApply(fn));
	}

	public void nestedStream(){
		
		
		
		Publisher<Integer> source1 = ReactiveSeq.of(1,2,3);
		Publisher<Integer> source2 = Flux.just(10,20,30);
 		
		Publisher<Integer> fastest= Flux.amb(source1,source2);
		
	
		
		fastest.toString();
		
		
		ReactiveSeq.of(1,2,3)
			  	   .flatMap(a-> ReactiveSeq.range(0,a)
					  				       .map(b->Tuple.tuple(a,b)));
		
		
		
		
		
		
		
			 
	    each2(ReactiveSeq.of(1,2,3), a->ReactiveSeq.range(0,a), Tuple::tuple);
		   
		
	    
	    
	    
	    
		For.stream(Stream.of(1,2,3))
		   .stream(a->IntStream.range(0, a))
		   .yield(a->b->Tuple.tuple(a,b));
		
		
	}
	public void transform(){
		
		
		Functor<Integer> func1 = FutureW.ofResult(1);
		
		Functor<Integer> doubled = func1.map(i->i*2);
		
		
		
		doubled.map(i->i+2);
		
		
		
	}
}
