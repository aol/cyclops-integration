package cyclops;

import cyclops.collections.immutable.LinkedListX;
import cyclops.collections.scala.ScalaListX;
import org.junit.Test;
import scala.collection.immutable.List;

import static org.testng.Assert.*;

/**
 * Created by johnmcclean on 25/05/2017.
 */
public class ScalaConvertersTest {

    @Test
    public void list(){
        List<Integer> list = LinkedListX.of(1,2,3)
                                        .type(ScalaListX.toPStack())
                                        .map(i->i*2)
                                        .to(ScalaConverters::List);

        System.out.println(list);
    }

}