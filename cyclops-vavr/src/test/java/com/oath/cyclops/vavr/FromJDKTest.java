package com.oath.cyclops.vavr;

import cyclops.conversion.vavr.FromJDK;
import org.junit.Test;

import java.util.Optional;

import static cyclops.conversion.vavr.FromJDK.f2;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by johnmcclean on 4/8/15.
 */
public class FromJDKTest {

    @Test
    public void testJDKf() {
        assertThat(FromJDK.f1((Integer a) -> a * 100)
                          .apply(2),
                   is(200));

    }

    @Test
    public void testJDKf2() {
        assertThat(f2((Integer a, Integer b) -> a * b).curried()
                                                      .apply(100)
                                                      .apply(5),
                   is(500));
    }

    @Test
    public void testJDKOption() {
        assertThat(FromJDK.option(Optional.of(1))
                          .get(),
                   is(1));
    }

    @Test
    public void testJDKOptionNull() {
        assertThat(FromJDK.option(Optional.ofNullable(null))
                          .getOrElse(100),
                   is(100));
    }

}
