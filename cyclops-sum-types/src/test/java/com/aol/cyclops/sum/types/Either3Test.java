package com.aol.cyclops.sum.types;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import com.aol.cyclops.control.Maybe;

public class Either3Test {
    boolean lazy = true;
    @Test
    public void lazyTest() {
        Either3.right(10)
             .flatMap(i -> { lazy=false; return  Either3.right(15);})
             .map(i -> { lazy=false; return  Either3.right(15);})
             .map(i -> Maybe.of(20));
             
        
        assertTrue(lazy);
            
    }
    
    @Test
    public void mapFlatMapTest(){
        assertThat(Either3.right(10)
               .map(i->i*2)
               .flatMap(i->Either3.right(i*4))
               .get(),equalTo(80));
    }
    @Test
    public void odd() {
        System.out.println(even(Either3.right(200000)).get());
    }

    public Either3<String,String,String> odd(Either3<String,String,Integer> n) {

        return n.flatMap(x -> even(Either3.right(x - 1)));
    }

    public Either3<String,String,String> even(Either3<String,String,Integer> n) {
        return n.flatMap(x -> {
            return x <= 0 ? Either3.right("done") : odd(Either3.right(x - 1));
        });
    }
  
}
