/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.fxrateservice.impl;

import com.bc.fxrateservice.FxRate;
import com.bc.fxrateservice.FxRateService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 4, 2018 10:04:42 PM
 */
public class FxRateServiceTest extends TestBase {

    private static LocaleProvider localeProvider;
    
    public FxRateServiceTest() { }
    
    @BeforeClass
    public static void setUpClass() { 
        localeProvider = new LocaleProvider();
    }
    
    /**
     * Test of getRates method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetRates_Locale_Locale() {
        System.out.println("\ngetRate(Locale, Locale)");
        System.out.println("------------ x ------------");
        final FxRateService instance = getInstance();
        final Map.Entry<Locale, Locale> pair = localeProvider.getRandomLocalePair();
        final Locale fromLocale = pair.getKey();
        final Locale toLocale = pair.getValue();
        FxRate result = instance.getRate(fromLocale, toLocale);
        System.out.println("From: "+fromLocale+", To: "+toLocale);
        System.out.println("Result: "+result);
    }

    /**
     * Test of getRates method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetRates_LocaleArr_LocaleArr() {
        System.out.println("\ngetRates");
        System.out.println("------------ x ------------");
        final FxRateService instance = getInstance();
        final Map<Locale, Locale> map = localeProvider.getLocalePairs();
        final Locale fromLocale = localeProvider.getRandomLocale();
        final Locale[] toLocales = new ArrayList<>(map.values()).toArray(new Locale[0]);
        FxRate[] result = instance.getRates(fromLocale, toLocales);
        System.out.println("From: "+fromLocale);
        System.out.println("  To: "+Arrays.toString(toLocales));
        System.out.println("Result: "+Arrays.toString(result));
    }

    /**
     * Test of getRate method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetRate_String_String() {
        System.out.println("\ngetRate");
        System.out.println("------------ x ------------");
        final FxRateService instance = getInstance();
        final Map.Entry<String, String> pair = localeProvider.getRandomCurrCodePair();
        final String fromCode = pair.getKey();
        final String toCode = pair.getValue();
        FxRate result = instance.getRate(fromCode, toCode);
        System.out.println("From: "+fromCode+", To: "+toCode);
        System.out.println("Result: "+result);
    }

    /**
     * Test of getRates method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetRates_StringArr_StringArr() {
        System.out.println("\ngetRates");
        System.out.println("------------ x ------------");
        final FxRateService instance = getInstance();
        final Map<String, String> map = localeProvider.getCurrCodePairs();
        final String fromCode = localeProvider.getRandomCurrCodePair().getKey();
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        FxRate[] result = instance.getRates(fromCode, toCodes);
        System.out.println("From: "+fromCode);
        System.out.println("  To: "+Arrays.toString(toCodes));
        System.out.println("Result: "+Arrays.toString(result));
    }

    /**
     * Test of loadRate method, of class FxRateServiceImpl.
     */
    @Test
    public void testLoadRate() {
        System.out.println("\ngetRate");
        System.out.println("------------ x ------------");
        final FxRateService instance = getInstance();
        final Map.Entry<String, String> pair = localeProvider.getRandomCurrCodePair();
        final String fromCode = pair.getKey();
        final String toCode = pair.getValue();
        final FxRate result = instance.loadRate(fromCode, toCode);
        System.out.println("From: "+fromCode+", To: "+toCode);
        System.out.println("Result: "+result);
    }

    /**
     * Test of loadRates method, of class FxRateServiceImpl.
     */
    @Test
    public void testLoadRates() {
        System.out.println("\ngetRates");
        System.out.println("------------ x ------------");
        final FxRateService instance = getInstance();
        final Map<String, String> map = localeProvider.getCurrCodePairs();
        final String fromCode = localeProvider.getRandomCurrCodePair().getKey();
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        FxRate[] result = instance.loadRates(fromCode, toCodes);
        System.out.println("From: "+fromCode);
        System.out.println("  To: "+Arrays.toString(toCodes));
        System.out.println("Result: "+Arrays.toString(result));
    }

    protected void loadRates(boolean clearCached) {
        final Map<Locale, Locale> map = localeProvider.getLocalePairs();
        final Locale fromLocale = map.keySet().iterator().next();
        final Locale[] toLocales = new ArrayList<>(map.values()).toArray(new Locale[0]);
        final FxRateService instance = getInstance();
        if(clearCached) {
            instance.clearCache();
        }
        instance.getRates(fromLocale, toLocales);
    }
    
    protected void requireNotNullOrEmptyString(String name, Object value) {
        if(value == null) {
            fail(name+" cannot be null");
        }
        if(value instanceof String && value.toString().isEmpty()) {
            fail(name+" cannot be an empty String");
        }
    }

    private static FxRateService _i;
    public FxRateService getInstance() {
        if(_i == null) {
            _i = this.createInstance();
        }
        return _i;
    }
    
    public LocaleProvider getLocaleProvider() {
        return localeProvider;
    }
    
    public FxRateService createInstance() {
        final Class type = FxRateServiceImpl.class;
        final FxRateService result = (FxRateService)FxRateService
                .getAvailable().stream().filter(
                (crs) -> type.isInstance(crs))
                .findFirst().orElseThrow(
                () -> new RuntimeException("Could not find any instance of: " + type));
//        instance = new FixerExchangeRateService();
//        instance = new ECBExchangeRateService();
        System.out.println("----------------------------------------------------");
        System.out.println("Instance: " + result);
        System.out.println("----------------------------------------------------");
        return result;
    }
}
