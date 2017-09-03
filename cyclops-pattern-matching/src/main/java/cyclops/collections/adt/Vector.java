package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Vector<T> {

    private final BAMT.Node<T> vector;
    private final int size;

    public static <T> Vector<T> of(T... values){
        BAMT.Node<T> tree = BAMT.empty();
        for(int i=0;i<values.length;i++){
            tree = tree.put(i,i,values[i]);
        }
        return new Vector<T>(tree,values.length);
    }
    public Vector<T> plus(T value){
        return new Vector<>(vector.put(size,size,value),size+1);
    }

    int calcSize(){
        return vector.size();
    }
    public int size(){
        return size;
    }
}

