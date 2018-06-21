package com.bc.fxrateservice.impl;

import com.bc.fxrateservice.util.GetUrlContents;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.bc.fxrateservice.FxRateService;
import com.bc.fxrateservice.FxRate;

/**
 * @author Josh
 */
public class FxRateServiceImplTest {

    private static LocaleProvider localeProvider;
    
    private static FxRateServiceImpl instance;
    
    public FxRateServiceImplTest() { }
    
    @BeforeClass
    public static void setUpClass() { 
        localeProvider = new LocaleProvider();
        final Class type = FxRateServiceImpl.class;
        instance = (FxRateServiceImpl)FxRateService
                .getAvailable().stream().filter(
                (crs) -> type.isInstance(crs))
                .findFirst().orElseThrow(
                () -> new RuntimeException("Could not find any instance of: " + type));
//        instance = new FixerExchangeRateService();
//        instance = new ECBExchangeRateService();
        System.out.println("----------------------------------------------------");
        System.out.println("Instance: " + instance);
        System.out.println("----------------------------------------------------");
    }
    
    @AfterClass
    public static void tearDownClass() { }
    
    @Before
    public void setUp() { }
    
    @After
    public void tearDown() { }

    /**
     * Test of getRates method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetRates_Locale_Locale() {
        System.out.println("\ngetRate(Locale, Locale)");
        System.out.println("------------ x ------------");
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
        final Map<Locale, Locale> map = localeProvider.getLocalePairs();
        final Locale[] fromLocales = new ArrayList<>(map.keySet()).toArray(new Locale[0]);
        final Locale[] toLocales = new ArrayList<>(map.values()).toArray(new Locale[0]);
        FxRate[] result = instance.getRates(fromLocales, toLocales);
        System.out.println("From: "+Arrays.toString(fromLocales));
        System.out.println("  To: "+Arrays.toString(toLocales));
        System.out.println("Result: "+Arrays.toString(result));
    }

    /**
     * Test of clearCache method, of class FxRateServiceImpl.
     */
    @Test
    public void testClearCache() {
        System.out.println("\nclearCache");
        System.out.println("------------ x ------------");
        System.out.println("BEFORE clearing, cache size: "+instance.getCache().size());
        instance.clearCache();
        System.out.println("AFTER clearing, cache size: "+instance.getCache().size());
    }

    /**
     * Test of clearCachedRate method, of class FxRateServiceImpl.
     */
    @Test
    public void testClearCachedRate() {
        
        System.out.println("\nclearCachedRate");
        System.out.println("------------ x ------------");
        
        this.loadRates(false);
        
        System.out.println("BEFORE clearing, cache size: "+instance.getCache().size());
        
        final FxRate rate = (FxRate)instance.getCache().values().iterator().next();
        String fromCode = rate.getFromCode();
        String toCode = rate.getToCode();
        FxRate expResult = rate;
        FxRate result = instance.clearCachedRate(fromCode, toCode);
        System.out.println("AFTER clearing, cache size: "+instance.getCache().size());
        assertEquals(expResult, result);
    }

    /**
     * Test of getCachedRate method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetCachedRate() {
        System.out.println("\ngetCachedRate");
        System.out.println("------------ x ------------");
        final Map.Entry<String, String> pair = localeProvider.getRandomCurrCodePair();
        final String fromCode = pair.getKey();
        final String toCode = pair.getValue();
        FxRate result = instance.getCachedRate(fromCode, toCode);
        System.out.println("From: "+fromCode+", To: "+toCode);
        System.out.println("Result: "+result);
    }

    /**
     * Test of getCachedRates method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetCachedRates() {
        System.out.println("\ngetCachedRates");
        System.out.println("------------ x ------------");
        final Map<String, String> map = localeProvider.getCurrCodePairs();
        final String[] fromCodes = new ArrayList<>(map.keySet()).toArray(new String[0]);
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        FxRate[] result = instance.getCachedRates(fromCodes, toCodes);
        System.out.println("From: "+Arrays.toString(fromCodes));
        System.out.println("  To: "+Arrays.toString(toCodes));
        System.out.println("Result: "+Arrays.toString(result));
    }

    /**
     * Test of getRate method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetRate_String_String() {
        System.out.println("\ngetRate");
        System.out.println("------------ x ------------");
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
        final Map<String, String> map = localeProvider.getCurrCodePairs();
        final String[] fromCodes = new ArrayList<>(map.keySet()).toArray(new String[0]);
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        FxRate[] result = instance.getRates(fromCodes, toCodes);
        System.out.println("From: "+Arrays.toString(fromCodes));
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
        final Map<String, String> map = localeProvider.getCurrCodePairs();
        final String[] fromCodes = new ArrayList<>(map.keySet()).toArray(new String[0]);
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        FxRate[] result = instance.loadRates(fromCodes, toCodes);
        System.out.println("From: "+Arrays.toString(fromCodes));
        System.out.println("  To: "+Arrays.toString(toCodes));
        System.out.println("Result: "+Arrays.toString(result));
    }

    /**
     * Test of isExpired method, of class FxRateServiceImpl.
     */
    @Test
    public void testIsExpired() {
        System.out.println("\nisExpired");
        System.out.println("------------ x ------------");
        Map<String, FxRate> cachedRates = instance.getCache();
        Collection<FxRate> values = cachedRates.values();
        for(FxRate value:values) {
            boolean expired = instance.isExpired(value);
System.out.println("Expired: "+expired+", value: "+value);            
        }
    }

    /**
     * Test of readUrlContents method, of class FxRateServiceImpl.
     */
    @Test
    public void testLoad() {
        System.out.println("\nload");
        System.out.println("------------ x ------------");
        final Map<String, String> map = localeProvider.getCurrCodePairs();
        final String[] fromCodes = new ArrayList<>(map.keySet()).toArray(new String[0]);
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        String urlString = instance.getEndpointSupplier().get(fromCodes, toCodes);
        Object result = new GetUrlContents().apply(urlString);
        System.out.println("From: "+Arrays.toString(fromCodes));
        System.out.println("  To: "+Arrays.toString(toCodes));
        System.out.println("Result: "+result);
    }

    /**
     * Test of getKey method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetKey() {
        System.out.println("\ngetKey");
        System.out.println("------------ x ------------");
        final Map.Entry<String, String> pair = localeProvider.getRandomCurrCodePair();
        final String fromCode = pair.getKey();
        final String toCode = pair.getValue();
        final String result = instance.getKey(fromCode, toCode);
        this.requireNotNullOrEmptyString("Key", result);
    }

    /**
     * Test of getCache method, of class FxRateServiceImpl.
     */
    @Test
    public void testGetCache() {
        System.out.println("\ngetCache");
        System.out.println("------------ x ------------");
        Map<String, FxRate> result = instance.getCache();
        this.requireNotNullOrEmptyString("Cache", result);
    }

    protected void loadRates(boolean clearCached) {
        final Map<Locale, Locale> map = localeProvider.getLocalePairs();
        final Locale[] fromLocales = new ArrayList<>(map.keySet()).toArray(new Locale[0]);
        final Locale[] toLocales = new ArrayList<>(map.values()).toArray(new Locale[0]);
        if(clearCached) {
            instance.clearCache();
        }
        instance.getRates(fromLocales, toLocales);
    }
    
    protected void requireNotNullOrEmptyString(String name, Object value) {
        if(value == null) {
            fail(name+" cannot be null");
        }
        if(value instanceof String && value.toString().isEmpty()) {
            fail(name+" cannot be an empty String");
        }
    }
}
