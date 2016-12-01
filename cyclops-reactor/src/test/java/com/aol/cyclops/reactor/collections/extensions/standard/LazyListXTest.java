package com.aol.cyclops.reactor.collections.extensions.standard;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.aol.cyclops.control.LazyReact;
import com.aol.cyclops.data.collections.extensions.standard.ListX;

public class LazyListXTest {

    @Test
    public void test(){
      LazyListX<Integer> ll = LazyListX.fromIterable(Arrays.asList(1,2,3));
      ListX<Integer> list = ll.map(i->i*2);
      list.printOut();
    }
    @Test
    public void multiGrouped(){
      LazyListX<Integer> ll = LazyListX.fromIterable(Arrays.asList(1,2,3));
      ListX<ListX<Integer>> list = ll.grouped(2);
      list.printOut();
      list.map(i->i.size()).printOut();
    }
    AtomicInteger executing = new AtomicInteger(-1);
    @Test
    public void threadSpinLockTest(){
        LazyReact react = new LazyReact(20,20);
        for (int x = 0; x < 100; x++) {
            executing.set(-1);
            System.out.println("------------------------------");
            final int run = x;
            LazyListX<Integer> list = LazyListX.of(1, 2, 3)
                                                   .map(i -> i + 2)
                                                   .limit(1)
                                                   .peek(c->{
                                                       if(executing.get()!=-1)
                                                           fail("already set! " + executing.get() + " run is " + run);
                                                       executing.set(run);
                                                   });
            
            react.ofAsync(()->list)
                    .cycle(20)
                    .map(s -> Thread.currentThread()
                                  .getId()
                          + " " + s.size())
                  .forEach(System.out::println);
            System.out.println("------------------------------");
        }
    }
}
