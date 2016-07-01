package com.aol.cyclops.functionaljava;

import org.junit.Test;

import com.aol.cyclops.types.anyM.AnyMSeq;

import fj.data.Stream;
import javaslang.collection.Array;

public class FlatMapFail {
	@Test
    public void jsFlatMap(){
		AnyMSeq<Integer> stream = FJ.stream(Stream.stream(1,2));
		
		AnyMSeq<Integer> array = Javaslang.array(Array.of(1, 2));
    	Stream.stream(1,2)
        	  .flatMap(i->Array.range(i,4));

    	
    	
    }
}
