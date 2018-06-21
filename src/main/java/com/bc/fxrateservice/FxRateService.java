package com.bc.fxrateservice;

import com.bc.fxrateservice.impl.ECBFxRateService;
import com.bc.fxrateservice.impl.FixerFxRateService;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @(#)CurrencyrateService.java   20-Oct-2014 09:28:34
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
public interface FxRateService {
    
    public static List<FxRateService> getAvailable() {
        return Arrays.asList(
                new FixerFxRateService(),
                new ECBFxRateService()
        );
    }
    
    ServiceDescriptor getDescriptor();
    
    FxRate getRate(Locale fromLocale, Locale toLocale);
    
//    FxRate [] getRates(Locale [] fromLocales, Locale [] toLocales);

    FxRate getRate(final String fromCode, final String toCode);
    
//    FxRate [] getRates(final String [] fromCodes, final String [] toCodes);
    
    FxRate loadRate(String fromCode, String toCode);

//    FxRate [] loadRates(String [] fromCodes, String [] toCodes);
    
//    FxRate getCachedRate(final String fromCode, final String toCode);
    
//    FxRate [] getCachedRates(final String [] fromCodes, final String [] toCodes);
    
//    FxRate clearCachedRate(final String fromCode, final String toCode);
    
    void clearCache();

    long getUpdateIntervalMillis(long outputIfNone);
}
