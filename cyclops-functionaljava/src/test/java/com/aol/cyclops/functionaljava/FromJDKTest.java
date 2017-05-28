package com.aol.cyclops.functionaljava;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import cyclops.conversion.functionaljava.FromJDK;
import org.junit.Test;


public class FromJDKTest {


    @Test
    public void testJDKλ2() {
        assertThat(FromJDK.f2((Integer a, Integer b) -> a * b)
                          .f(100, 5),
                   is(500));
    }

    @Test
    public void testJDKOption() {
        assertThat(FromJDK.option(Optional.of(1))
                          .some(),
                   is(1));
    }

    @Test
    public void testJDKOptionNull() {
        assertThat(FromJDK.option(Optional.ofNullable(null))
                          .orSome(100),
                   is(100));
    }

}
