package com.bc.fxrateservice.impl;

import com.bc.fxrateservice.EndpointSupplier;
import com.bc.fxrateservice.ServiceDescriptor;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.fxrateservice.Parser;
import com.bc.fxrateservice.FxRateService;
import com.bc.fxrateservice.FxRate;

/**
 * @(#)AbstractCurrencyrateService.java   20-Oct-2014 09:35:44
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class FxRateServiceImpl implements FxRateService {

    private static final Logger LOG = Logger.getLogger(FxRateServiceImpl.class.getName());

    private final ServiceDescriptor descriptor;
    
    private final long updateIntervalMillis;
    
    private final EndpointSupplier endpointSupplier;
    
    private final Parser<String> urlParser;
    
    private final Map<String, FxRate> cache;
    
    public FxRateServiceImpl(
            ServiceDescriptor descriptor,
            EndpointSupplier endpointSupplier, 
            Parser<String> responseParser, 
            long updateIntervalMillis) {
        this.descriptor = Objects.requireNonNull(descriptor);
        this.endpointSupplier = Objects.requireNonNull(endpointSupplier);
        this.urlParser = Objects.requireNonNull(responseParser);
        this.updateIntervalMillis = updateIntervalMillis;
        this.cache = new HashMap<>();
    }

    @Override
    public ServiceDescriptor getDescriptor() {
        return this.descriptor;
    }
    
    @Override
    public FxRate getRate(Locale fromLocale, Locale toLocale) {

        if(fromLocale == null || toLocale == null) {
            throw new NullPointerException();
        }
        
        if(fromLocale.equals(toLocale)) {
            throw new IllegalArgumentException("No conversion required for the input locales");
        }

        String fromCode = this.getCurrencyCode(fromLocale);
        String toCode = this.getCurrencyCode(toLocale);
        
        if(fromCode == null || toCode == null) {
            throw new NullPointerException();
        }

        return getRate(fromCode, toCode);
    }

//    @Override
    public FxRate [] getRates(Locale [] fromLocales, Locale [] toLocales) {

        if(fromLocales.length != toLocales.length) {
            throw new IllegalArgumentException();
        }

        String [] fromCodes = new String[fromLocales.length];
        String [] toCodes = new String[toLocales.length];
        
        for(int i=0; i<fromLocales.length; i++) {
            
            fromCodes[i] = this.getCurrencyCode(fromLocales[i]);
            
            toCodes[i] = this.getCurrencyCode(toLocales[i]);
            
            if(fromCodes[i] == null) {
                throw new IllegalArgumentException("Currency code could not be ascertained for locale: "+fromLocales[i]);
            }
            if(toCodes[i] == null) {
                throw new IllegalArgumentException("Currency code could not be ascertained for locale: "+toLocales[i]);
            }
        }

        return getRates(fromCodes, toCodes);
    }

    @Override
    public void clearCache() {
        this.cache.clear();
    }
    
//    @Override
    public FxRate clearCachedRate(final String fromCode, final String toCode) {
        
        final String KEY = getKey(fromCode, toCode);
        
        return this.cache.remove(KEY);
    }

//    @Override
    public FxRate getCachedRate(final String fromCode, final String toCode) {
        
        final String KEY = getKey(fromCode, toCode);
        
        // Return last known rate, if available
        //
        return this.cache.get(KEY);
    }
    
//    @Override
    public FxRate [] getCachedRates(final String [] fromCodes, final String [] toCodes) {

        if(fromCodes.length != toCodes.length) {
            throw new IllegalArgumentException();
        }
        
        FxRate [] rates = new FxRate[fromCodes.length];
        
        for(int i=0; i<fromCodes.length; i++) {
            
            rates[i] = this.getCachedRate(fromCodes[i], toCodes[i]);
        }
        
        return rates;
    }
    
    @Override
    public FxRate getRate(final String fromCode, final String toCode) {
        
        FxRate currencyRate = this.getCachedRate(fromCode, toCode);
        
        if(currencyRate != null && !this.isExpired(currencyRate)) {
            return currencyRate;
        }
        
        return this.loadRate(fromCode, toCode);
    }

//    @Override
    public FxRate [] getRates(final String [] fromCodes, final String [] toCodes) {
        
        FxRate [] cachedRates = this.getCachedRates(fromCodes, toCodes);
        
        List<String> fromCodesToLoad_list = null;
        List<String> toCodesToLoad_list = null;
            
        if(cachedRates != null) {

            for(int i=0; i<fromCodes.length; i++) {
                if(cachedRates[i] == null || this.isExpired(cachedRates[i])) {
                    if(fromCodesToLoad_list == null) {
                        fromCodesToLoad_list = new LinkedList<>();
                    }
                    fromCodesToLoad_list.add(fromCodes[i]);
                    if(toCodesToLoad_list == null) {
                        toCodesToLoad_list = new LinkedList<>();
                    }
                    toCodesToLoad_list.add(toCodes[i]);
                }
            }
            
            if(fromCodesToLoad_list == null || toCodesToLoad_list == null) {
//////////////////////////// NOTE THIS //////////////////////////////                
                return cachedRates;
            }
        }

        String [] fromCodesToLoad;
        String [] toCodesToLoad;
        if(fromCodesToLoad_list == null || toCodesToLoad_list == null) {
            fromCodesToLoad = fromCodes;
            toCodesToLoad = toCodes;
        }else{
            fromCodesToLoad = fromCodesToLoad_list.toArray(new String[fromCodesToLoad_list.size()]);
            toCodesToLoad = toCodesToLoad_list.toArray(new String[toCodesToLoad_list.size()]);
        }
        
        FxRate [] loadedRates = this.loadRates(fromCodesToLoad, toCodesToLoad);
        
        if(loadedRates == null) {

            LOG.log(Level.WARNING, 
            "Returning last known rate. Reason: Failed to load currency rate from: {0}", 
            endpointSupplier.get(fromCodesToLoad, toCodesToLoad));           

            // Return the last known rate if available
            //    
            return cachedRates;
            
        }else{
            
            if(fromCodesToLoad_list != null) {

// Update those expired rates in the cachedRates with those loaded                
                for(int i=0; i<cachedRates.length; i++) {
                    
                    FxRate cachedRate = cachedRates[i];
                    
                    if(cachedRate == null) {
                        continue;
                    }
                    
                    if(this.isExpired(cachedRate)) {
                        for(FxRate loadedRate:loadedRates) {
                            if(this.matches(cachedRate, loadedRate)) {
                                cachedRates[i] = loadedRate;
                                break;
                            }
                        }
                    }
                }
                
                return cachedRates;
                
            }else{
                
                return loadedRates;
            }
        }
    }
    
    private boolean matches(FxRate a, FxRate b) {
        return a.getFromCode().equals(b.getFromCode()) && a.getToCode().equals(b.getToCode());
    }

    @Override
    public FxRate loadRate(String fromCode, String toCode) {
    
        final FxRate output;
        
        if(fromCode.equalsIgnoreCase(toCode)) {
            
            output = new FxRateImpl(new Date(), fromCode, toCode, 1.0f);
            
        }else{
            
            final String urlStr = endpointSupplier.get(fromCode, toCode);

            output = this.urlParser.parse(fromCode, toCode, urlStr);
        }
        
        if(output != null) {

            this.cache.put(this.getKey(fromCode, toCode), output);
        }

        return output == null ? FxRate.NONE : output;
    }
    
//    @Override
    public FxRate [] loadRates(String [] fromCodes, String [] toCodes) {
        
        final String urlStr = this.endpointSupplier.get(fromCodes, toCodes);

        LOG.log(Level.FINE, "Endpoint URL: {0}", urlStr);                   

        final FxRate [] update = this.urlParser.parse(fromCodes, toCodes, urlStr);

        if(update != null) {

            for(FxRate rate : update) {

                if(rate == null) {
                    continue;
                }
                
                cache.put(this.getKey(rate.getFromCode(), rate.getToCode()), rate);
            }
        }
        
        return update == null ? new FxRate[0] : update;
    }

    public boolean isExpired(FxRate currencyRate) {
        return this.isExpired(currencyRate.getDate());
    }
    
    public boolean isExpired(Date date) {
        long age = System.currentTimeMillis() - date.getTime();
        return age > this.updateIntervalMillis;
    }
    
    public String getKey(final String fromCode, final String toCode) {
        return fromCode.toUpperCase()+toCode.toUpperCase();
    }
    
    public Map<String, FxRate> getCache() {
        return this.cache;
    }

    private String getCurrencyCode(Locale locale) {
        try {
            Currency curr = Currency.getInstance(locale);
            return curr == null ? null : curr.getCurrencyCode();
        }catch(Exception e) {
            // Currency.getInstance(Locale) may throw java.lang.IllegalArgumentException for older versions
            LOG.warning(e.toString());
            return null;
        }
    }

    public EndpointSupplier getEndpointSupplier() {
        return endpointSupplier;
    }

    public Parser<String> getUrlParser() {
        return urlParser;
    }

    @Override
    public long getUpdateIntervalMillis(long outputIfNone) {
        return updateIntervalMillis <= 0 ? outputIfNone : updateIntervalMillis;
    }

    @Override
    public String toString() {
        return descriptor.toString();
    }
}//END
