package com.aol.cyclops.control;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.Getter;

public class PushingSpliterator<T> implements Spliterator<T> {

   
    public static void main(String[] args) throws InterruptedException{
        PushingSpliterator<String> push =new PushingSpliterator<String>();
        Stream<String> s = StreamSupport.stream(push,false);
       // s.parallel().map(str->str.length()).forEach(System.out::println);
        List<String> str=s.collect(Collectors.toList());
        Thread.sleep(1000);
        push.action.accept("hello");
        push.action.accept("world");
    }
    @Getter
    Consumer<? super T> action;
    
    /* (non-Javadoc)
     * @see java.util.Spliterator#forEachRemaining(java.util.function.Consumer)
     */
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        this.action = action;/**
        while(true){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }**/
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long estimateSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int characteristics() {
        // TODO Auto-generated method stub
        return 0;
    }

}
