package com.aol.cyclops.functionaljava.hkt;

import java.util.stream.IntStream;

import com.aol.cyclops.hkt.alias.Higher;

import fj.F;
import fj.data.LazyString;
import fj.data.Option;
import fj.data.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for LazyString's
 * 
 * LazyStringType is a LazyString and a Higher Kinded Type (LazyStringType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the LazyString
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class LazyStringType implements Higher<LazyStringType.µ, Character>, CharSequence {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Convert a LazyString to a simulated HigherKindedType that captures LazyString nature
     * and LazyString element data type separately. Recover via @see LazyStringType#narrow
     * 
     * If the supplied LazyString implements LazyStringType it is returned already, otherwise it
     * is wrapped into a LazyString implementation that does implement LazyStringType
     * 
     * @param list LazyString to widen to a LazyStringType
     * @return LazyStringType encoding HKT info about LazyStrings
     */
    public static <T> LazyStringType widen(final LazyString list) {
        
        return new LazyStringType(list);
    }
    /**
     * Widen a LazyStringType nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a LazyString to widen
     * @return HKT encoded type with a widened LazyString
     */
    public static <C2> Higher<C2, Higher<LazyStringType.µ,Character>> widen2(Higher<C2, LazyStringType> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<LazyStringType.µ,T> must be a LazyStringType
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for LazyString types into the LazyStringType type definition class
     * 
     * @param list HKT encoded list into a LazyStringType
     * @return LazyStringType
     */
    public static  LazyStringType narrowK(final Higher<LazyStringType.µ, Character> list) {
       return (LazyStringType)list;
    }
    /**
     * Convert the HigherKindedType definition for a LazyString into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return LazyString from Higher Kinded Type
     */
    public static <T> LazyString narrow(final Higher<LazyStringType.µ, T> list) {
        return ((LazyStringType)list).narrow();
       
    }


    private final LazyString boxed;

    /**
     * @return This back as a LazyStringX
     */
    public LazyString narrow() {
        return (LazyString) (boxed);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return boxed.hashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return boxed.equals(obj);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LazyStringType [" + boxed + "]";
    }
    /**
     * @return
     * @see fj.data.LazyString#toStream()
     */
    public Stream<Character> toStream() {
        return boxed.toStream();
    }
    /**
     * @return
     * @see fj.data.LazyString#length()
     */
    public int length() {
        return boxed.length();
    }
    /**
     * @param index
     * @return
     * @see fj.data.LazyString#charAt(int)
     */
    public char charAt(int index) {
        return boxed.charAt(index);
    }
    /**
     * @param start
     * @param end
     * @return
     * @see fj.data.LazyString#subSequence(int, int)
     */
    public CharSequence subSequence(int start, int end) {
        return boxed.subSequence(start, end);
    }
    /**
     * @return
     * @see fj.data.LazyString#toStringEager()
     */
    public String toStringEager() {
        return boxed.toStringEager();
    }
    /**
     * @return
     * @see fj.data.LazyString#toStringLazy()
     */
    public String toStringLazy() {
        return boxed.toStringLazy();
    }
    /**
     * @return
     * @see fj.data.LazyString#eval()
     */
    public String eval() {
        return boxed.eval();
    }
    /**
     * @param cs
     * @return
     * @see fj.data.LazyString#append(fj.data.LazyString)
     */
    public LazyString append(LazyString cs) {
        return boxed.append(cs);
    }
    /**
     * @return
     * @see java.lang.CharSequence#chars()
     */
    public IntStream chars() {
        return boxed.chars();
    }
    /**
     * @param s
     * @return
     * @see fj.data.LazyString#append(java.lang.String)
     */
    public LazyString append(String s) {
        return boxed.append(s);
    }
    /**
     * @param cs
     * @return
     * @see fj.data.LazyString#contains(fj.data.LazyString)
     */
    public boolean contains(LazyString cs) {
        return boxed.contains(cs);
    }
    /**
     * @param cs
     * @return
     * @see fj.data.LazyString#endsWith(fj.data.LazyString)
     */
    public boolean endsWith(LazyString cs) {
        return boxed.endsWith(cs);
    }
    /**
     * @param cs
     * @return
     * @see fj.data.LazyString#startsWith(fj.data.LazyString)
     */
    public boolean startsWith(LazyString cs) {
        return boxed.startsWith(cs);
    }
    /**
     * @return
     * @see java.lang.CharSequence#codePoints()
     */
    public IntStream codePoints() {
        return boxed.codePoints();
    }
    /**
     * @return
     * @see fj.data.LazyString#head()
     */
    public char head() {
        return boxed.head();
    }
    /**
     * @return
     * @see fj.data.LazyString#tail()
     */
    public LazyString tail() {
        return boxed.tail();
    }
    /**
     * @return
     * @see fj.data.LazyString#isEmpty()
     */
    public boolean isEmpty() {
        return boxed.isEmpty();
    }
    /**
     * @return
     * @see fj.data.LazyString#reverse()
     */
    public LazyString reverse() {
        return boxed.reverse();
    }
    /**
     * @param c
     * @return
     * @see fj.data.LazyString#indexOf(char)
     */
    public Option<Integer> indexOf(char c) {
        return boxed.indexOf(c);
    }
    /**
     * @param cs
     * @return
     * @see fj.data.LazyString#indexOf(fj.data.LazyString)
     */
    public Option<Integer> indexOf(LazyString cs) {
        return boxed.indexOf(cs);
    }
    /**
     * @param regex
     * @return
     * @see fj.data.LazyString#matches(java.lang.String)
     */
    public boolean matches(String regex) {
        return boxed.matches(regex);
    }
    /**
     * @param p
     * @return
     * @see fj.data.LazyString#split(fj.F)
     */
    public Stream<LazyString> split(F<Character, Boolean> p) {
        return boxed.split(p);
    }
    /**
     * @param f
     * @return
     * @see fj.data.LazyString#map(fj.F)
     */
    public LazyString map(F<Character, Character> f) {
        return boxed.map(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.LazyString#bind(fj.F)
     */
    public LazyString bind(F<Character, LazyString> f) {
        return boxed.bind(f);
    }
    /**
     * @param c
     * @return
     * @see fj.data.LazyString#split(char)
     */
    public Stream<LazyString> split(char c) {
        return boxed.split(c);
    }
    /**
     * @return
     * @see fj.data.LazyString#words()
     */
    public Stream<LazyString> words() {
        return boxed.words();
    }
    /**
     * @return
     * @see fj.data.LazyString#lines()
     */
    public Stream<LazyString> lines() {
        return boxed.lines();
    }

      
}
