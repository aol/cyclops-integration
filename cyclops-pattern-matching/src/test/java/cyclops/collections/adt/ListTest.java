package cyclops.collections.adt;

import cyclops.companion.Monoids;
import cyclops.stream.ReactiveSeq;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;


public class ListTest {

    @Test
    public void testMap(){
        assertThat(ImmutableList.of(1,2,3).map(i->1*2),equalTo(ImmutableList.of(2,4,6)));
        assertThat(ImmutableList.empty().map(i->1*2),equalTo(ImmutableList.empty()));
    }
    @Test
    public void testFlatMap(){
        assertThat(ImmutableList.of(1,2,3).flatMap(i-> ImmutableList.of(1*2)),equalTo(ImmutableList.of(2,4,6)));
        assertThat(ImmutableList.empty().flatMap(i-> ImmutableList.of(1*2)),equalTo(ImmutableList.empty()));
    }

    @Test
    public void foldRight(){
        ImmutableList.fromStream(ReactiveSeq.range(0,100_000)).foldRight(Monoids.intSum);
    }
}
