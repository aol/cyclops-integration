package cyclops.monads.transformers;

import cyclops.monads.AnyM;
import cyclops.monads.Witness.optional;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;


@Ignore
public class FluxTSeqTraversableTest extends AbstractTraversableTest {
    @Test
    public void zipInOrder(){

        FluxT<optional,Integer> flux1 = of(1, 2, 3, 4, 5, 6);
        FluxT<optional,Integer> flux2 = of(100, 200, 300, 400);
        FluxT<optional, Tuple2<Integer, Integer>> p = flux1.zipP(flux2);
        //flux1.zip(flux2).toListOfLists().printOut();
        List<Tuple2<Integer,Integer>> list = flux1.zipP(flux2)
                .stream()
                .toListX();;

        assertThat(asList(1,2,3,4,5,6),hasItem(list.get(0).v1));
        assertThat(asList(100,200,300,400),hasItem(list.get(0).v2));



    }
    @Test
    public void iterate(){
        System.out.println(of(1,2,3).stream().toListX());
    }
    @Override
    public <T> FluxT<optional,T> of(T... elements) {
        return FluxT.of(AnyM.ofNullable(Flux.just(elements)));
    }

    @Override
    public <T> FluxT<optional,T> empty() {

        return FluxT.of(AnyM.ofNullable(Flux.empty()));
    }

}
