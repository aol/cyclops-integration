package cyclops;

import com.oath.cyclops.data.collections.extensions.IndexedSequenceX;
import cyclops.collections.immutable.VectorX;
import cyclops.collections.vavr.VavrVectorX;
import cyclops.monads.AnyM;
import cyclops.monads.Vavr;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.option;
import cyclops.monads.Witness;
import cyclops.monads.transformers.ListT;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


import static com.oath.cyclops.types.foldable.Evaluation.LAZY;
import static cyclops.monads.VavrWitness.option;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class ListTTest {

    @Test
    public void vector(){

        ListT<option,Integer> vectorInOption = ListT.ofList(Vavr.option(Option.some(VavrVectorX.of(10))));


        ListT<option,Integer> doubled = vectorInOption.map(i->i*2);
        ListT<option,Integer> repeated = doubled.cycle(3);

        System.out.println(repeated);


        Option<Vector<Integer>> list = option(vectorInOption.unwrap()).map(s -> s.to()
                                                                      .vectorX(LAZY)
                                                                      .to(VavrConverters::Vector));
    }
}
