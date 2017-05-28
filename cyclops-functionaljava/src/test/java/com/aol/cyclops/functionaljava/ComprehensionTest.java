package com.aol.cyclops.functionaljava;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.*;

import cyclops.companion.functionaljava.Options;
import org.junit.Test;


import fj.data.Option;

public class ComprehensionTest {



    private String loadData() {
        return "loaded";
    }

    @Test
    public void optionTest() {
        assertFalse(Options.forEach2(Option.some(10), a -> Option.none(), (a, b) -> "failed")
                                .isSome());
    }

    @Test
    public void option2Test() {

        assertThat(Options.forEach2(Option.some(10), a -> Option.<Integer> some(a + 20), (a, b) -> a + b)
                               .option(-1, f -> f),
                   equalTo(40));
    }


}
