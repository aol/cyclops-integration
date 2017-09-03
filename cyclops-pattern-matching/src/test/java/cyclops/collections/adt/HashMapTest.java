package cyclops.collections.adt;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.testng.Assert.*;

public class HashMapTest {

    @Test
    public void plusSize(){
        assertThat(HashMap.empty().plus("hello","world").size(),equalTo(1));
    }

}