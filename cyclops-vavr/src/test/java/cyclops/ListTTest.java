package cyclops;

import cyclops.collections.vavr.VavrVectorX;
import cyclops.monads.Vavr;
import cyclops.monads.VavrWitness.option;
import cyclops.monads.transformers.ListT;
import io.vavr.collection.Vector;
import io.vavr.control.Option;
import org.junit.Test;

import static com.oath.cyclops.types.foldable.Evaluation.LAZY;
import static cyclops.monads.VavrWitness.option;


public class ListTTest {

    @Test
    public void vector(){

        ListT<option,Integer> vectorInOption = ListT.of(Vavr.option(Option.some(VavrVectorX.of(10))));


        ListT<option,Integer> doubled = vectorInOption.map(i->i*2);
        ListT<option,Integer> repeated = doubled.cycle(3);

        System.out.println(repeated);


        Option<Vector<Integer>> list = option(vectorInOption.unwrap()).map(s -> s.to()
                                                                      .vectorX(LAZY)
                                                                      .to(VavrConverters::Vector));
    }
}
