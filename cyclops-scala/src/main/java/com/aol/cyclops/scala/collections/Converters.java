package com.aol.cyclops.scala.collections;

import java.util.Comparator;

import scala.math.Ordering;
import scala.math.Ordering$;

public class Converters {
    public static <T>  Ordering<T> ordering(Comparator<T> cmp){
        return  Ordering$.MODULE$.comparatorToOrdering(cmp);
        
    }
}
