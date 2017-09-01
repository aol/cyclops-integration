package cyclops.collections.adt;

import org.junit.Test;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by johnmcclean on 01/09/2017.
 */
public class BankersQueueTest {

    @Test
    public void enqueue(){
        BankersQueue<Integer> q = BankersQueue.cons(1);

        assertThat(q.match(c->c.dequeue().v1,nil->-1),equalTo(1));

        assertThat(q.match(c->c.dequeue().v2,nil->null),equalTo(BankersQueue.Nil.Instance));

    }
    @Test
    public void enqueue2(){
        BankersQueue<Integer> q = BankersQueue.cons(1).enqueue(10);
        System.out.println("Q is " + q);

        assertThat(q.match(c->c.dequeue().v2.dequeue(-1).v1,nil->-1),equalTo(10));

        assertThat(q.match(c->c.dequeue().v2.dequeue(-1).v2,nil->null),equalTo(BankersQueue.Nil.Instance));

    }

    @Test
    public void get(){
        BankersQueue<Integer> q = BankersQueue.of(1,2,3);

        assertThat(q.get(0).get(),equalTo(1));
        assertThat(q.get(1).get(),equalTo(2));
        assertThat(q.get(2).get(),equalTo(3));
        assertThat(q.get(3).isPresent(),equalTo(false));
        assertThat(q.get(-1).isPresent(),equalTo(false));
    }
}
