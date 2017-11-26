package cyclops;

import cyclops.reactive.ReactiveSeq;
import org.testng.annotations.Test;

import java.util.stream.Stream;

/**
 * Created by johnmcclean on 24/07/2017.
 */
public class StreamTest {

    @Test
    public void flatMap(){

        System.out.println();

        ReactiveSeq.of(1, 2, 3)
                    .flatMap(i -> Stream.of(i - 1, i, i + 1))
                    .flatMap(i -> Stream.of(i - 1, i, i + 1))
                    .filter(i -> { System.out.println(i); return true; })
                    .findFirst();





    }
}
