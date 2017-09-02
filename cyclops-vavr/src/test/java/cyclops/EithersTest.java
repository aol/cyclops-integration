package cyclops;

import com.aol.cyclops.vavr.hkt.EitherKind;
import com.aol.cyclops.vavr.hkt.ListKind;
import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.hkt.Higher;
import cyclops.collections.mutable.ListX;
import cyclops.companion.vavr.Eithers;

import cyclops.companion.vavr.Lists;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.list;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.Nested;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.junit.Test;

import java.util.stream.Stream;

import static com.aol.cyclops2.internal.adapters.StreamAdapter.stream;

/**
 * Created by johnmcclean on 01/09/2017.
 */
public class EithersTest {
    @Test
    public void sequence(){
        CollectionX<Either<Integer, Integer>> stream = ListX.of(Either.right(1),Either.left(10), Either.right(3));
        Either<Integer, ListX<Integer>> res = Eithers.sequencePresent(stream);
        System.out.println(res.map(s->s.toList()));
    }
    @Test
    public void sequenceViaTraverse(){
        List<Either<Integer, Integer>> nestedList = List.of(Either.right(1), Either.right(3));

        Active<list, EitherKind<Integer, Integer>> listHKT = Lists.allTypeclasses(nestedList.map(EitherKind::widen));
        Nested<Higher<VavrWitness.either, Integer>, list, Integer> sequenced = Nested.of(listHKT, Eithers.Instances.definitions()).sequence();
        Higher<Higher<VavrWitness.either, Integer>, Higher<list, Integer>> nested = sequenced.nested;

        Either<Integer, List<Integer>> either = EitherKind.narrowK(sequenced.nested).map(l -> ListKind.narrow(l));
        System.out.println("Either is " + either);

        //Nested<VavrWitness.list, Higher<VavrWitness.either, Integer>, Integer> nested = Nested.of(Lists.allTypeclasses(list), Eithers.Instances.definitions());
       /**
        ListX.Instances.traverse().sequenceA(ListX.Instances.zippingApplicative(),
        CollectionX<Either<Integer, Integer>> stream = ListX.of(Either.right(1), Either.right(3));
        Either<Integer, ListX<Integer>> res = Eithers.sequence(stream);
        System.out.println(res.map(s->s.toList()));
        **/
    }
}
