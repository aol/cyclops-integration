package com.aol.cyclops.vavr.hkt.typeclesses.instances;

import com.aol.cyclops.vavr.hkt.ListKind;
import com.aol.cyclops.vavr.hkt.OptionKind;
import com.aol.cyclops.vavr.hkt.StreamKind;
import com.oath.cyclops.hkt.Higher;
import com.google.common.collect.FluentIterable;
import cyclops.companion.Monoids;
import cyclops.companion.vavr.Lists;
import cyclops.companion.vavr.Lists.ListNested;
import cyclops.companion.vavr.Options;
import cyclops.companion.vavr.Options.OptionNested;
import cyclops.companion.vavr.Streams;
import cyclops.companion.vavr.Streams.StreamNested;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.list;

import cyclops.monads.VavrWitness.option;
import cyclops.monads.VavrWitness.stream;
import cyclops.typeclasses.Nested;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class NestedTest {

    @Test
    public void listOption(){
        Higher<list, Integer> res = ListNested
                                         .option(List.of(Option.some(1)))
                                         .map(i -> i * 20)
                                         .foldLeft(Monoids.intMax);
        List<Integer> fi = ListKind.narrow(res);
        assertThat(fi.get(0),equalTo(20));
    }
    @Test
    public void streamEither(){
        Higher<stream, Integer> res = StreamNested.either(Stream.of(Either.right(1)))
                                                  .map(i -> i * 20)
                                                  .foldLeft(Monoids.intMax);
        Stream<Integer> fi = StreamKind.narrow(res);
        assertThat(fi.get(0),equalTo(20));
    }

    @Test
    public void optionList(){
        Nested<option,list,Integer> optList  = OptionNested.list(Option.some(List.ofAll(1,10,2,3)))
                                                                       .map(i -> i * 20);

        Option<Integer> opt  = optList.foldLeft(Monoids.intMax)
                                      .convert(OptionKind::narrowK);

        assertThat(opt,equalTo(Option.some(200)));
    }
}

