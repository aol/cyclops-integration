package cyclops.collections.adt;



import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;



public class VectorTest {

    @Test
    public void testSize() {
        assertThat(Vector.of(1,2,3).size(),equalTo(3));
        assertThat(Vector.of(1,2,3).plus(1).size(),equalTo(4));
    }
    @Test
    public void testCalcSize() {
        assertThat(Vector.of(1,2,3).calcSize(),equalTo(3));
        assertThat(Vector.of(1,2,3).plus(1).calcSize(),equalTo(4));
    }



}