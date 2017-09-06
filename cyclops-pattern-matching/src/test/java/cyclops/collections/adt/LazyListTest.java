package cyclops.collections.adt;


import cyclops.control.Eval;
import cyclops.control.Maybe;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class LazyListTest {


    @Test
    public void prependAllTest(){
        assertThat(LazyList.of(1,2,3).prependAll(LazyList.of(4,5,6)),equalTo(LazyList.of(4,5,6,1,2,3)));
    }





}
