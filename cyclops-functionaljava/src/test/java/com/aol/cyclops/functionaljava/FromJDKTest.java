package com.aol.cyclops.functionaljava;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.aol.cyclops.control.monads.transformers.ListT;

import fj.data.List;

/**
 * Created by johnmcclean on 4/8/15.
 */
public class FromJDKTest {

    @Test
    public void testJDKλ() {
        ListT.of(FJ.list(List.list(1, 2, 3)
                             .map(a -> Arrays.asList(a))))
             .map(a -> a * 2);

        assertThat(FromJDK.f1((Integer a) -> a * 100)
                          .f(2),
                   is(200));

    }

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
