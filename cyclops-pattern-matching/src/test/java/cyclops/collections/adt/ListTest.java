package cyclops.collections.adt;

import cyclops.companion.Monoids;
import cyclops.stream.ReactiveSeq;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class ListTest {

    @Test
    public void testMap(){
        assertThat(SafeList.of(1,2,3).map(i->1*2),equalTo(SafeList.of(2,4,6)));
        assertThat(SafeList.empty().map(i->1*2),equalTo(SafeList.empty()));
    }
    @Test
    public void testFlatMap(){
        assertThat(SafeList.of(1,2,3).flatMap(i-> SafeList.of(1*2)),equalTo(SafeList.of(2,4,6)));
        assertThat(SafeList.empty().flatMap(i-> SafeList.of(1*2)),equalTo(SafeList.empty()));
    }

    @Test
    public void foldRight(){
        SafeList.fromStream(ReactiveSeq.range(0,100_000)).foldRight(Monoids.intSum);
    }
}
