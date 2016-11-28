package com.aol.cyclops.scala.collections;

import java.util.Collection;
import java.util.function.Function;

import scala.collection.GenTraversableOnce;
import scala.collection.JavaConverters;
import scala.collection.generic.CanBuildFrom;

public interface HasScalaCollection<T> {

    public GenTraversableOnce<T> traversable();
    public CanBuildFrom canBuildFrom();
    
    public static <T> GenTraversableOnce<T> traversable(Collection<T> col){
        if(col instanceof HasScalaCollection)
           return ((HasScalaCollection)col).traversable();
        return JavaConverters.collectionAsScalaIterable(col);
     }
    
    public static <T,R> R visit(Collection<T> col,Function<HasScalaCollection<T>,R> scala, Function<Collection<T>,R> java){
        if(col instanceof HasScalaCollection){
            return (R)scala.apply((HasScalaCollection)col);
        }
        return java.apply(col);
    }
    
    public static <T> Collection<T> narrow(Collection<? extends T> col){
        return (Collection<T>)col;
    }
    
}
