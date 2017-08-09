package cyclops;

import cyclops.companion.vavr.Streams;
import io.vavr.collection.Stream;
import org.junit.Test;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by johnmcclean on 08/08/2017.
 */
public class StreamsTest {

    @Test
    public void simpleFoldRight(){
        assertThat(Streams.foldRight(Stream.of(1,2,3),0,(a,b)->a+b),equalTo(6));
    }
    @Test
    public void stackBuster(){
        assertThat(Streams.foldRight(Stream.range(0,100_000),0,(a,b)->a+b),equalTo(704982704));
    }
}
