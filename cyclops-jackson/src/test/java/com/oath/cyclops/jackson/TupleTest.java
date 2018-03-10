package com.oath.cyclops.jackson;

import cyclops.data.Seq;
import cyclops.data.Vector;
import cyclops.data.tuple.Tuple;
import org.junit.Test;

public class TupleTest {

  @Test
  public void t(){
    System.out.println(JacksonUtil.serializeToJson(Tuple.tuple("hello")));
  }

  @Test
  public void vec(){
    System.out.println(JacksonUtil.serializeToJson(Seq.of("hello")));
  }

  @Test
  public void seq(){
    System.out.println(JacksonUtil.serializeToJson(Seq.of(1,2,3)));
  }
}
