package com.aol.cyclops.sum.types;

import org.junit.Test;

import com.aol.cyclops.control.Maybe;

public class MaybeTest {

    @Test
    public void flatMap() {
        Maybe.of(10)
             .flatMap(i -> { System.out.println("Not lazy!"); return  Maybe.of(15);})
             .map(i -> { System.out.println("Not lazy!"); return  Maybe.of(15);})
             .map(i -> Maybe.of(20));
            
    }
}
