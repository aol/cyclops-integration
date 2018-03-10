package com.oath.cyclops.jackson;

import cyclops.data.Seq;
import cyclops.data.Vector;
import cyclops.data.tuple.Tuple;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TupleTest {

  @Test
  public void t1(){
    assertThat(JacksonUtil.serializeToJson(Tuple.tuple("hello")),equalTo("{\"first\":\"hello\"}"));
  }
  @Test
  public void t2(){
    assertThat(JacksonUtil.serializeToJson(Tuple.tuple("hello","world")),equalTo("{\"first\":\"hello\",\"second\":\"world\"}"));
  }
  @Test
  public void t3(){
    assertThat(JacksonUtil.serializeToJson(Tuple.tuple("hello","world","x")),equalTo("{\"first\":\"hello\",\"second\":\"world\",\"third\":\"x\"}"));
  }
  @Test
  public void t4(){
    assertThat(JacksonUtil.serializeToJson(Tuple.tuple("hello","world","x","a")),equalTo("{\"first\":\"hello\",\"second\":\"world\",\"third\":\"x\",\"fourth\":\"a\"}"));
  }
  @Test
  public void t5(){
    assertThat(JacksonUtil.serializeToJson(Tuple.tuple("hello","world","x","a","b")),equalTo("{\"first\":\"hello\",\"second\":\"world\",\"third\":\"x\",\"fourth\":\"a\",\"fifth\":\"b\"}"));
  }

  @Test
  public void vec(){
    System.out.println(JacksonUtil.serializeToJson(Seq.of("hello")));
  }


}
