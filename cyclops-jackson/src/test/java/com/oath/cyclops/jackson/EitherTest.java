package com.oath.cyclops.jackson;

import cyclops.control.Either;
import cyclops.control.Option;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class EitherTest {

   @Test
  public void left(){
      assertThat(JacksonUtil.serializeToJson(Either.left(10)),equalTo("{\"left\":10}"));

   }

  @Test
  public void right(){
    assertThat(JacksonUtil.serializeToJson(Either.right(10)),equalTo("{\"right\":10}"));

  }

}
