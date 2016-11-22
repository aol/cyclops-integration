package com.aol.cyclops.sum.types;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.aol.cyclops.control.Maybe;

public class Either4Test {
    boolean lazy = true;
    @Test
    public void lazyTest() {
        Either4.right(10)
             .flatMap(i -> { lazy=false; return  Either4.right(15);})
             .map(i -> { lazy=false; return  Either4.right(15);})
             .map(i -> Maybe.of(20));
             
        
        assertTrue(lazy);
            
    }
    
    @Test
    public void mapFlatMapTest(){
        assertThat(Either4.right(10)
               .map(i->i*2)
               .flatMap(i->Either4.right(i*4))
               .get(),equalTo(80));
    }
    @Test
    public void odd() {
        System.out.println(even(Either4.right(200000)).get());
    }

    public Either4<String,String,String,String> odd(Either4<String,String,String,Integer> n) {

        return n.flatMap(x -> even(Either4.right(x - 1)));
    }

    public Either4<String,String,String,String> even(Either4<String,String,String,Integer> n) {
        return n.flatMap(x -> {
            return x <= 0 ? Either4.right("done") : odd(Either4.right(x - 1));
        });
    }
  
}
