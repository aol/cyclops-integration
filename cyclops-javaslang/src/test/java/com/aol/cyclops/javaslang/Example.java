package com.aol.cyclops.javaslang;



import com.aol.cyclops.data.collections.extensions.standard.SetX;
import com.aol.cyclops.functionaljava.FJ;

import fj.data.Stream;
import javaslang.collection.Array;
import reactor.core.publisher.Flux;



public class Example {

	public void flatMap(){
		
		FJ.stream(Stream.stream(1,2))
						.flatMap(i->Javaslang.traversable(Array.range(i,4)));
		  
		
	}
	public void amb(){
		
		SetX.fromPublisher(Flux.amb(Javaslang.traversable(Array.of(1,2,3)),
									FJ.stream(Stream.stream(10,20,30))));
		
		
	}
	public void stream(){
		
	/**	
		
		ReactiveSeq<Integer> seq = ReactiveSeq.fromStream(Stream.of(1,2,3));
		Stream<Integer> stream = seq;
		Functor<Integer> functor = seq;
		
		
		
		
		
		
		stream.map(null);
		functor.map(null);
		**/
		
	}
}
