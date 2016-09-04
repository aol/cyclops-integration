package com.aol.cyclops.functionaljava;

import java.util.List;

import org.junit.Test;

import fj.control.Trampoline;

public class TrampolineDemo {
    @Test
    public void trampolineDemo() {

        List<String> list = FJ.trampoline(FJ.Trampoline8.suspend(() -> Trampoline.pure("hello world")))
                              .map(String::toUpperCase)
                              .toList();

        System.out.println(list);

    }

}
