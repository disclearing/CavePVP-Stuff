package net.frozenorb.foxtrot.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedList<E> extends AbstractList<E> {

    private ArrayList<E> internalList = new ArrayList<E>();
    private Comparator<E> comparator;

    public SortedList(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    // Note that add(E e) in AbstractList is calling this one
    @Override 
    public void add(int position, E e) {
        internalList.add(e);
        Collections.sort(internalList, comparator);
    }

    @Override
    public E get(int i) {
        return internalList.get(i);
    }

    @Override
    public int size() {
        return internalList.size();
    }

}