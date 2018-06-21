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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josh
 */
public class FxRateServiceCompositeTest {
    
    private static LocaleProvider localeProvider;
    
    private static FxRateServiceComposite instance;
    
    public FxRateServiceCompositeTest() { }
    
    @BeforeClass
    public static void setUpClass() { 
        localeProvider = new LocaleProvider();
        instance = new DefaultFxRateService();
        System.out.println("----------------------------------------------------");
        System.out.println("Instance: " + instance);
        System.out.println("----------------------------------------------------");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getRate method, of class FxRateServiceComposite
     */
    @Test
    public void testGetRate_Locale_Locale() {
        System.out.println("\ngetRate(Locale, Locale)");
        System.out.println("------------ x ------------");
        final Map.Entry<Locale, Locale> pair = localeProvider.getRandomLocalePair();
        final Locale fromLocale = pair.getKey();
        final Locale toLocale = pair.getValue();
        final FxRate result = instance.getRate(fromLocale, toLocale);
        System.out.println("From: "+fromLocale+", To: "+toLocale+", Result: "+result);
    }

    /**
     * Test of getRates method, of class FxRateServiceComposite
     */
    @Test
    public void testGetRates_LocaleArr_LocaleArr() {
        System.out.println("\ngetRates(Locale[], Locale[])");
        System.out.println("------------ x ------------");
        final Map<Locale, Locale> map = localeProvider.getLocalePairs();
        final Locale[] fromLocales = new ArrayList<>(map.keySet()).toArray(new Locale[0]);
        final Locale[] toLocales = new ArrayList<>(map.values()).toArray(new Locale[0]);
        for(int i=0; i<fromLocales.length; i++) {
            final FxRate result = instance.getRate(fromLocales[i], toLocales[i]);
            System.out.println("From: "+fromLocales[i]+", To: "+toLocales[i]+", Result: "+result);
        }
    }

    /**
     * Test of getRate method, of class FxRateServiceComposite
     */
    @Test
    public void testGetRate_String_String() {
        System.out.println("\ngetRate(String, String)");
        System.out.println("------------ x ------------");
        final Map.Entry<String, String> pair = localeProvider.getRandomCurrCodePair();
        final String fromCode = pair.getKey();
        final String toCode = pair.getValue();
        final FxRate result = instance.getRate(fromCode, toCode);
        System.out.println("From: "+fromCode+", To: "+toCode+", Result: "+result);
    }

    /**
     * Test of getRates method, of class FxRateServiceComposite
     */
    @Test
    public void testGetRates_StringArr_StringArr() {
        System.out.println("\ngetRates(String[], String[])");
        System.out.println("------------ x ------------");
        final Map<String, String> map = localeProvider.getCurrCodePairs();
        final String[] fromCodes = new ArrayList<>(map.keySet()).toArray(new String[0]);
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        for(int i=0; i<fromCodes.length; i++) {
            final FxRate result = instance.getRate(fromCodes[i], toCodes[i]);
            System.out.println("From: "+fromCodes[i]+", To: "+toCodes[i]+", Result: "+result);
        }
    }

    /**
     * Test of loadRate method, of class FxRateServiceComposite
     */
    @Test
    public void testLoadRate() {
        System.out.println("\nloadRate");
        System.out.println("------------ x ------------");
        final Map.Entry<String, String> pair = localeProvider.getRandomCurrCodePair();
        final String fromCode = pair.getKey();
        final String toCode = pair.getValue();
        final FxRate result = instance.loadRate(fromCode, toCode);
        System.out.println("From: "+fromCode+", To: "+toCode+", Result: "+result);
    }

    /**
     * Test of loadRates method, of class FxRateServiceComposite
     */
    @Test
    public void testLoadRates() {
        System.out.println("\nloadRates");
        System.out.println("------------ x ------------");
        final Map<String, String> map = localeProvider.getCurrCodePairs();
        final String[] fromCodes = new ArrayList<>(map.keySet()).toArray(new String[0]);
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        for(int i=0; i<fromCodes.length; i++) {
            final FxRate result = instance.getRate(fromCodes[i], toCodes[i]);
            System.out.println("From: "+fromCodes[i]+", To: "+toCodes[i]+", Result: "+result);
        }
    }

    /**
     * Test of getServiceList method, of class FxRateServiceComposite.
     */
    @Test
    public void testGetServiceList() {
        System.out.println("getServiceList");
        final List<FxRateService> result = instance.getServiceList();
        try{
            result.add(instance);
            fail("Returned Service List is modifiable");
        }catch(Exception ignored) { }
    }

    /**
     * Test of getActiveFxRateService method, of class FxRateServiceComposite.
     */
    @Test
    public void testGetActiveFxRateService() {
        System.out.println("getActiveFxRateService");
        final FxRateService result = instance.getActiveFxRateService();
        assertNotNull(result);
    }
}
