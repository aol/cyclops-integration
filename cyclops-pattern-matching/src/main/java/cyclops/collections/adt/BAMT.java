package cyclops.collections.adt;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

import static cyclops.collections.adt.BAMT.ArrayUtils.last;
import static cyclops.collections.adt.BAMT.Two.two;

public class BAMT<T> {

    public interface NestedArray<T>{
        static final int BITS_IN_INDEX = 5;
        static final int SIZE = (int) StrictMath.pow(2, BITS_IN_INDEX);

        public NestedArray<T> append(ActiveTail<T> tail);

        static int bitpos(int hash, int shift){
            return 1 << mask(hash, shift);
        }
        static int mask(int hash, int shift){
            return (hash >>> shift) & (SIZE-1);
        }
        static int mask(int hash){
            return (hash) & (SIZE-1);
        }

    }
    public interface PopulatedArray<T> extends NestedArray<T>{

        public Optional<T> get(int pos);
        public T[] getNestedArrayAt(int pos);
        public PopulatedArray<T> set(int pos, T value);

    }

    static class ArrayUtils{

        public static <T> T[] append(T[] array, T value) {
            T[] newArray = (T[])new Object[array.length + 1];
            System.arraycopy(array, 0, newArray, 0, array.length);
            newArray[array.length] = value;
            return newArray;
        }
        public static Object[] append2(Object[][] array, Object value) {
            Object[] newArray = new Object[array.length + 1];
            System.arraycopy(array, 0, newArray, 0, array.length);
            newArray[array.length] = value;
            return newArray;
        }
        public static <T> T last(T[] array){
            return array[array.length-1];
        }
    }

    @AllArgsConstructor
    public static class ActiveTail<T> implements PopulatedArray<T>{
        private final int bitShiftDepth =0;
        private final T[] array;

        public <T> ActiveTail<T> tail(T[] array){
            return new ActiveTail<>(array);
        }
        public static <T> ActiveTail<T> tail(T value){
            return new ActiveTail<>((T[])new Object[]{value});
        }
        public static <T> ActiveTail<T> emptyTail(){
            return new ActiveTail<>((T[])new Object[0]);
        }

        public ActiveTail<T> append(T t) {
            if(array.length<32){
                return  tail(ArrayUtils.append(array,t));
            }
            return this;
        }

        @Override
        public Optional<T> get(int pos) {
            int indx = pos & 0x01f;
            if(indx<array.length)
                return Optional.of((T)array[indx]);
            return Optional.empty();
        }

        @Override
        public T[] getNestedArrayAt(int pos) {
            return array;
        }

        @Override
        public PopulatedArray<T> set(int pos, T value) {
            T[] updatedNodes =  Arrays.copyOf(array, array.length);
            updatedNodes[pos] = value;
            return new ActiveTail<>(updatedNodes);
        }

        public int size(){
            return array.length;
        }
        @Override
        public NestedArray<T> append(ActiveTail<T> tail) {
            return tail;
        }
    }

    public static class Zero<T> implements NestedArray<T>{
        @Override
        public NestedArray<T> append(ActiveTail<T> tail) {
            return new One(tail.array);
        }
    }

    @AllArgsConstructor
    public static class One<T> implements PopulatedArray<T> {

        private final int bitShiftDepth =0;
        private final T[] array;

        public static <T> One<T> one(T[] array){
            return new One<>(array);
        }
        public static <T> One<T> one(T value){
            return new One<>((T[])new Object[]{value});
        }

        public NestedArray<T> append(ActiveTail<T> t) {
            return two(new  Object[][]{array,t.array});
        }

        @Override
        public Optional<T> get(int pos) {
            int indx = pos & 0x01f;
            if(indx<array.length)
                return Optional.of((T)array[indx]);
            return Optional.empty();
        }

        @Override
        public T[] getNestedArrayAt(int pos) {
            return array;
        }

        @Override
        public PopulatedArray<T> set(int pos, T t) {
            Object[] updatedNodes = Arrays.copyOf(array, array.length);
            updatedNodes[pos] = t;
            return new One(updatedNodes);
        }


    }
    @AllArgsConstructor
    public static class Two<T> implements PopulatedArray<T>{
        public static final int bitShiftDepth =5;
        private final Object[][] array;

        public static <T> Two<T> two(Object[][] array){
            return new Two<T>(array);
        }

        @Override
        public PopulatedArray<T> set(int pos, T t) {
            Object[][] updatedNodes = Arrays.copyOf(array, array.length);
            int indx = NestedArray.mask(pos,bitShiftDepth);
            Object[] e = updatedNodes[indx];
            Object[] newNode = Arrays.copyOf(e,e.length);
            updatedNodes[indx] = newNode;
            newNode[NestedArray.mask(pos)]=t;
            return two(updatedNodes);

        }

        @Override
        public Optional<T> get(int pos) {
            T[] local = getNestedArrayAt(pos);
            int resolved = NestedArray.bitpos(pos,bitShiftDepth);
            int indx = pos & 0x01f;
            if(indx<local.length){
                return Optional.of(local[indx]);
            }
            return Optional.empty();
        }

        @Override
        public T[] getNestedArrayAt(int pos) {
            int indx = NestedArray.mask(pos, bitShiftDepth);
            if(indx<array.length)
                return  (T[])array[indx];
            return (T[])new Object[0];

        }


        @Override
        public NestedArray<T> append(ActiveTail<T> tail) {
            if(array.length<32){
                Object[][] updatedNodes = Arrays.copyOf(array, array.length+1,Object[][].class);
                updatedNodes[array.length]=tail.array;
                return two(updatedNodes);
            }
            return Three.three(new Object[][][]{array,new Object[][]{tail.array}});


        }
    }
    @AllArgsConstructor
    public static class Three<T> implements PopulatedArray<T>{
        public static final int bitShiftDepth = 10;
        private final Object[][][] array;

        public static <T> Three<T> three(Object[][][] array){
            return new Three<T>(array);
        }

        @Override
        public PopulatedArray<T> set(int pos, T t) {
            Object[][][] updatedNodes = Arrays.copyOf(array, array.length);
            int indx = NestedArray.mask(pos,bitShiftDepth);
            Object[][] e = updatedNodes[indx];
            Object[][] newNode = Arrays.copyOf(e,e.length);
            updatedNodes[indx] = newNode;
            int indx2 = NestedArray.mask(pos,Two.bitShiftDepth);
            Object[] f = e[indx2];
            Object[] newNode2 = Arrays.copyOf(f,f.length);
            newNode[indx2]=f;
            f[NestedArray.mask(pos)]=t;
            return three(updatedNodes);

        }

        @Override
        public Optional<T> get(int pos) {
            T[] local = getNestedArrayAt(pos);
            int resolved = NestedArray.bitpos(pos,bitShiftDepth);
            int indx = pos & 0x01f;
            if(indx<local.length){
                return Optional.of(local[indx]);
            }
            return Optional.empty();
        }

        @Override
        public T[] getNestedArrayAt(int pos) {
            int indx = NestedArray.mask(pos, bitShiftDepth);
            if(indx<array.length){
               Object[][] twoArray = array[indx];
               int indx2 = NestedArray.mask(pos,Two.bitShiftDepth);
               if(indx2<twoArray.length){
                   return  (T[])twoArray[indx2];
               }
            }

            return (T[])new Object[0];

        }


        @Override
        public NestedArray<T> append(ActiveTail<T> tail) {
            if(array[array.length-1].length<32){
                Object[][][] updatedNodes = Arrays.copyOf(array, array.length,Object[][][].class);
                updatedNodes[updatedNodes.length-1]=Arrays.copyOf(last(updatedNodes), last(updatedNodes).length+1,Object[][].class);
                last(updatedNodes)[last(array).length] = tail.array;
                return three(updatedNodes);
            }
            if(array.length<32){
                Object[][][] updatedNodes = Arrays.copyOf(array, array.length+1,Object[][][].class);
                updatedNodes[array.length] = new Object[][]{tail.array};
                return three(updatedNodes);



            }
            return null;
        }
    }

}
