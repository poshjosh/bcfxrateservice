/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bc.fxrateservice.impl;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 22, 2018 7:02:25 PM
 */
public class LocaleProvider {

    public List<String> getCurrencyCodes(List<Locale> locales) {
        final List<String> codes = new LinkedList<>();
        for(Locale locale : locales) {
            String code = this.getCurrencyCode(locale);
            if(code == null) {
                continue;
            }
            codes.add(code);
        }
        return codes;
    }
    
    public String getCurrencyCode(Locale locale) {
        try {
            Currency curr = Currency.getInstance(locale);
            return curr == null ? null : curr.getCurrencyCode();
        }catch(Exception e) {
            // Currency.getInstance(Locale) may throw java.lang.IllegalArgumentException for older versions
//            Logx.log(this.getClass(), e);
            return null;
        }
    }
    
    public Map<String, String> getCurrCodePairs() {
        final List<String> fromCodes = this.getCurrencyCodes(getAvailableLocales(true));
        return new PairProvider<String>().apply(fromCodes);
    }
    
    public Map.Entry<String, String> getRandomCurrCodePair() {
        String from = this.getCurrencyCode(this.getRandomLocale());
        while(from == null) {
            from = this.getCurrencyCode(this.getRandomLocale()); 
        }
        String to;
        do{
            to = this.getCurrencyCode(this.getRandomLocale());
        }while(to == null || from.equals(to));
        return new SimpleImmutableEntry<>(from, to);
    }

    public Map<Locale, Locale> getLocalePairs() {
        final List<Locale> locales = getAvailableLocales(true);
        Iterator<Locale> iter = locales.iterator();
        while(iter.hasNext()) {
            Locale locale = iter.next();
            if(locale == null || this.getCurrencyCode(locale) == null) {
                iter.remove();
            }
        }
        return new PairProvider<Locale>().apply(locales);
    }

    public Map.Entry<Locale, Locale> getRandomLocalePair() {
        Locale from = this.getRandomLocale();
        Locale to;
        do{
            to = this.getRandomLocale();
        }while(to.equals(from));
        return new SimpleImmutableEntry(from, to);
    }
    
    public Locale getRandomLocale() {
        Locale locale = null;
        String code;
        do{
            int off = this.randomInt(getAvailableLocalesSize());
            locale = getAvailableLocales(false).get(off);
            code = this.getCurrencyCode(locale);
        }while(code == null);
        return locale;
    }

    public int randomInt(int size) {
        
        double random = Math.random();

        double numbr = (random * size);

        return (int)Math.floor(numbr);
    }

    private static final List<Locale> _al = Arrays.asList(Locale.getAvailableLocales());
    public int getAvailableLocalesSize() {
        return _al.size();
    }
    public List<Locale> getAvailableLocales(boolean shuffle) {
        final List<Locale> list = new ArrayList(_al);
        if(shuffle) {
            Collections.shuffle(list);
        }
        return list;
    }
}
