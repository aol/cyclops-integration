package com.aol.cyclops.reactor.collections.extensions;

import java.util.Arrays;

import org.junit.Test;

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
}
