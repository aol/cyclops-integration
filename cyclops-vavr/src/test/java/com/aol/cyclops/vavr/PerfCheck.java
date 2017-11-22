package com.aol.cyclops.vavr;

import org.junit.Ignore;
import org.junit.Test;
import org.pcollections.ConsPersistentList;
import com.oath.cyclops.types.persistent.PersistentList;
import com.oath.cyclops.types.persistent.PersistentList;
import org.pcollections.TreePersistentList;

import io.vavr.collection.Array;
import io.vavr.collection.List;

@Ignore
public class PerfCheck {

    @Test
    public void listInsert() {
        long start = System.currentTimeMillis();
        List<Integer> list = List.empty();
        for (int i = 0; i < 1_000_000; i++) {
            list = list.prepend(1);
        }
        System.out.println("Vavr List took " + (System.currentTimeMillis() - start));
        System.out.println(list.size());

    }

    @Test
    public void listMap() {

        List<Integer> list = List.empty();
        for (int i = 0; i < 1_000_000; i++) {
            list = list.prepend(1);
        }
        long start = System.currentTimeMillis();
        list = list.map(i -> i + 1);
        System.out.println("Vavr List map took " + (System.currentTimeMillis() - start));
        System.out.println(list.size());

    }

    @Test
    public void pstackInsert() {
        long start = System.currentTimeMillis();
        PersistentList<Integer> list = ConsPersistentList.empty();
        for (int i = 0; i < 1_000_000; i++) {
            list = list.plus(1);
        }
        System.out.println("PCollections PersistentList took " + (System.currentTimeMillis() - start));
        System.out.println(list.size());

    }

    @Test
    public void arrayInsert() {
        long start = System.currentTimeMillis();
        Array<Integer> list = Array.empty();
        for (int i = 0; i < 1_000_000; i++) {
            list = list.append(1);
        }
        System.out.println("Vavr Array took " + (System.currentTimeMillis() - start));
        System.out.println(list.size());

    }

    @Test
    public void pvectorInsert() {

        long start = System.currentTimeMillis();
        PersistentList<Integer> list = TreePersistentList.empty();
        for (int i = 0; i < 1_00_000; i++) {
            list = list.plus(1);
        }
        System.out.println("PCollections PersistentList took " + (System.currentTimeMillis() - start));
        System.out.println(list.size());

    }

}
