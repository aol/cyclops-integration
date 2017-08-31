package cyclops;



import cyclops.async.Future;

import cyclops.control.Reader;
import cyclops.monads.Witness.future;
import cyclops.typeclasses.Kleisli;
import org.junit.Test;


/**
 * Created by johnmcclean on 11/08/2017.
 */
public class KleisliExample {

    interface DAO {
         Future<String> load(long id);
         Future<Boolean> save(long id,String data);
    }
    class TestDAO implements DAO {
        public Future<String> load(long id){
            return Future.ofResult("Loading "+id);
        }
        public Future<Boolean> save(long id,String data){
            System.out.println("Saving " + id  + " data: " + data);
            return Future.ofResult(true);
        }

    }

    @Test
    public void chain(){


        DAO testDAO = new TestDAO();
        Kleisli<future, DAO, Boolean> lazy = process(10);

        Future<Boolean> task = lazy.apply(testDAO)
                                   .convert(Future::narrowK);

        System.out.println("Result is " + task.get());

        System.out.println("Task completed successfully? " + task.visit(s->s.booleanValue(),e->false));


    }
    public Reader<DAO,String> loadName(long id){
        return null;
    }
    public Reader<DAO,Boolean> updateName(long id, String name){
        return null;
    }
    public boolean logIfFail(long id, String name, boolean success){
        return null;
    }
    @Test
    public void forComp(){

        Reader<DAO, Boolean> r2 = loadName(10).forEach2(s -> updateName(10, s),
                                                            (name, success) -> logIfFail(10, name, success));

        Reader<DAO,Boolean>  r= loadName(10).flatMap(s->updateName(10,s));
    }



    public Kleisli<future,DAO,String> load(long id){
        return Kleisli.of(Future.Instances.monad(), DAO -> DAO.load(id));
    }
    public Kleisli<future,DAO,Boolean> save(long id, String data){
        return Kleisli.of(Future.Instances.monad(), DAO -> DAO.save(id,data));
    }
    public Kleisli<future,DAO,Boolean> process(long id){
        return load(id).flatMapK(s -> save(id + 1, s));
    }
}
