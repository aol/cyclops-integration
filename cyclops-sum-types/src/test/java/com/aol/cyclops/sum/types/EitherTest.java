package com.aol.cyclops.sum.types;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import org.junit.Test;

import com.aol.cyclops.control.Maybe;

public class EitherTest {
    boolean lazy = true;
    @Test
    public void lazyTest() {
        Either.right(10)
             .flatMap(i -> { lazy=false; return  Either.right(15);})
             .map(i -> { lazy=false; return  Either.right(15);})
             .map(i -> Maybe.of(20));
             
        
        assertTrue(lazy);
            
    }
    @Test
    public void mapFlatMapTest(){
        assertThat(Either.right(10)
               .map(i->i*2)
               .flatMap(i->Either.right(i*4))
               .get(),equalTo(80));
    }
}
