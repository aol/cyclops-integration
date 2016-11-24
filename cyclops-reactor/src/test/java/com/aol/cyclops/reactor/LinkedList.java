package com.aol.cyclops.reactor;

import java.util.function.BinaryOperator;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.control.Xor;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
public class LinkedList<T> {
    public static void main(String[] args){
        Maybe<Node<Integer>> just = Maybe.just(new InnerNode<>(10,new EndNode<>(5)));
        LinkedList<Integer> list = new LinkedList<>(just);
        System.out.println(list.reduce(0, (a,b)->a+b));
        LinkedList<Integer> list2 = new LinkedList<>(Maybe.none());
        System.out.println(list2.reduce(0, (a,b)->a+b));
    }

    private Maybe<Node<T>> head;

    // [constructors]
    // [list mutation by replacing nodes with new ones]

    public T reduce(T seed, BinaryOperator<T> operator) {
       return reduce(head,seed,operator).get();
    }
    
    public Maybe<T> reduce(Maybe<Node<T>> head,T value,BinaryOperator<T> operator) {

        return head.flatMap(node-> reduce(node.match()
                                              .visit(endNode-> Maybe.none(), innerNode-> Maybe.of(innerNode.next)),
                                          operator.apply(value, node.getValue()),
                                          operator))
                   .recover(()->value);

    }

    private interface Node<T> {
        T getValue();
        Xor<EndNode<T>,InnerNode<T>> match();
    }

    
    @Value
    private final static class InnerNode<T> implements Node<T>  { 
        T value; 
        Node<T> next;
        @Override
        public Xor<EndNode<T>, InnerNode<T>> match() {
            return Xor.primary(this);
        }
        
     }
    
    @Value
    private final static class EndNode<T> implements Node<T>  {   
        T value;
        @Override
        public Xor<EndNode<T>, InnerNode<T>> match() {
            return Xor.secondary(this);
        } 
   }

}
