package cyclops;

import cyclops.async.Future;
import cyclops.control.Reader;
import org.junit.Test;

/**
 * Created by johnmcclean on 11/08/2017.
 */
public class FutureReader {
    interface DAO {
        Future<String> load(long id);
        Future<Boolean> save(long id,String data);
    }
    public Reader<DAO,Future<String>> loadName(long id){
        return null;
    }
    public Reader<DAO,Future<Boolean>> updateName(long id, String name){
        return null;
    }
    public Boolean logIfFail(long id, String name, boolean success){
        return true;
    }
    public void forTest(){
        /**
        Reader<DAO, Future<Boolean>> load10 =
                loadName(10l).forEach2(nameF ->  dao -> {
                            Future<Boolean> r = nameF.flatMap(name -> updateName(10, name + "suffix").apply(dao));
                            return r;
                        }
                        ,(nameF, successF) -> successF.flatMap(success -> nameF.flatMap(name -> logIfFail(10, name, success))));

         }**/
    }
}
