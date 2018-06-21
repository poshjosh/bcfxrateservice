/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bc.fxrateservice.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2018 6:35:36 PM
 */
public class PairProvider<T> implements Function<Collection<T>, Map<T, T>> {

    @Override
    public Map<T, T> apply(Collection<T> input) {
        final List<T> left = new LinkedList<>(input);
        final List<T> right = new LinkedList<>(left);
        Collections.rotate(right, 1);
//        Collections.reverse(right);  This may not always work as the element in the middle remains the same
        return apply(left, right);
    }

    public Map<T, T> apply(List<T> listA, List<T> listB) {
        final Iterator<T> iterA = listA.iterator();
        final Iterator<T> iterB = listB.iterator();
        while(iterA.hasNext() & iterB.hasNext()) {
            final T valA = iterA.next();
            final T valB = iterB.next();
            if(valA == null || valB == null || valA.equals(valB)) {
                iterA.remove();
                iterB.remove();
            }
        }
        final int len = Math.min(listA.size(), listB.size());
        final Map<T, T> output = new LinkedHashMap<>(len, 1.0f);
        for(int i=0; i<len; i++) {
            output.put(listA.get(i), listB.get(i));
        }
        return output;
    }
}
