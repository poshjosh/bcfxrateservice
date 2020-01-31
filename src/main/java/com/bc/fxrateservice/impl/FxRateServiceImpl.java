package com.bc.fxrateservice.impl;

import com.bc.fxrateservice.EndpointSupplier;
import com.bc.fxrateservice.ServiceDescriptor;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.fxrateservice.Parser;
import com.bc.fxrateservice.FxRateService;
import com.bc.fxrateservice.FxRate;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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
    
    public static final long DEFAULT_UPDATE_INTERVAL_MILLIS = TimeUnit.HOURS.toMillis(12);

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
        this.cache = updateIntervalMillis < 1 ? Collections.EMPTY_MAP : new HashMap<>();
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

        final String fromCode = this.getCurrencyCode(fromLocale, null);
        final String toCode = this.getCurrencyCode(toLocale, null);
        
        if(fromCode == null || toCode == null) {
            throw new NullPointerException();
        }

        return getRate(fromCode, toCode);
    }

    @Override
    public FxRate [] getRates(Locale fromLocale, Locale [] toLocales) {

        final String fromCode = this.requireCurrencyCode(fromLocale);
        
        final String [] toCodes = new String[toLocales.length];
        
        for(int i=0; i<toLocales.length; i++) {
            toCodes[i] = this.requireCurrencyCode(toLocales[i]);
        }

        return getRates(fromCode, toCodes);
    }
    
    public String requireCurrencyCode(Locale locale) {
        final String code = this.getCurrencyCode(locale, null);
        if(code == null) {
            throw new IllegalArgumentException(
                    "Currency code could not be ascertained for locale: " + locale);
        }
        return code;
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
    public FxRate [] getCachedRates(final String fromCode, final String [] toCodes) {

        final FxRate [] rates = new FxRate[toCodes.length];
        
        for(int i=0; i<toCodes.length; i++) {
            
            rates[i] = this.getCachedRate(fromCode, toCodes[i]);
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

    @Override
    public FxRate [] getRates(final String fromCode, final String [] toCodes) {
        final FxRate [] result = new FxRate[toCodes.length];
        for(int i=0; i<toCodes.length; i++) {
            result[i] = this.getRate(fromCode, toCodes[i]);
        }
        return result;
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
        
        if(output != null && this.isUseCache()) {

            this.cache.put(this.getKey(fromCode, toCode), output);
        }

        return output == null ? FxRate.NONE : output;
    }
    
    @Override
    public FxRate [] loadRates(String fromCode, String [] toCodes) {
        
        final String urlStr = this.endpointSupplier.get(fromCode, toCodes);

        LOG.log(Level.FINE, "Endpoint URL: {0}", urlStr);                   

        final FxRate [] update = this.urlParser.parse(fromCode, toCodes, urlStr);

        if(update != null) {

            for(FxRate rate : update) {

                if(rate == null) {
                    continue;
                }
                
                if(this.isUseCache()) {
                    cache.put(this.getKey(rate.getFromCode(), rate.getToCode()), rate);
                }
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
    
    public final Map<String, FxRate> getCache() {
        return Collections.unmodifiableMap(this.cache);
    }

    private String getCurrencyCode(Locale locale, String outputIfNone) {
        String result = null;
        try {
            Currency curr = Currency.getInstance(locale);
            result = curr == null ? null : curr.getCurrencyCode();
        }catch(Exception e) {
            // Currency.getInstance(Locale) may throw java.lang.IllegalArgumentException for older versions
            LOG.warning(e.toString());
        }
        return result == null ? outputIfNone : result;
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
    
    public boolean isUseCache() {
        return updateIntervalMillis > 0;
    }

    @Override
    public String toString() {
        return descriptor.toString();
    }
}//END
