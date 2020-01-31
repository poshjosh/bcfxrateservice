package com.bc.fxrateservice.impl;

import com.bc.fxrateservice.FxRate;
import com.bc.fxrateservice.util.GetUrlContent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Josh
 */
public class FxRateServiceImplTest extends FxRateServiceTest {

    public FxRateServiceImplTest() { }

    @Override
    public FxRateServiceImpl getInstance() {
        return (FxRateServiceImpl)super.getInstance();
    }


    /**
     * Test of clearCache method, of class FxRateServiceImpl.
     */
    @Test
    public void testClearCache() {
        System.out.println("\nclearCache");
        System.out.println("------------ x ------------");
        final FxRateServiceImpl instance = getInstance();
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
        final FxRateServiceImpl instance = getInstance();
        
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
        final FxRateServiceImpl instance = getInstance();
        final Map.Entry<String, String> pair = getLocaleProvider().getRandomCurrCodePair();
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
        final FxRateServiceImpl instance = getInstance();
        final Map<String, String> map = getLocaleProvider().getCurrCodePairs();
        final String fromCode = getLocaleProvider().getRandomCurrCodePair().getKey();
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        FxRate[] result = instance.getCachedRates(fromCode, toCodes);
        System.out.println("From: "+fromCode);
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
        final FxRateServiceImpl instance = getInstance();
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
        final FxRateServiceImpl instance = getInstance();
        final Map<String, String> map = getLocaleProvider().getCurrCodePairs();
        final String fromCode = "USD";
        final String[] toCodes = new ArrayList<>(map.values()).toArray(new String[0]);
        String urlString = instance.getEndpointSupplier().get(fromCode, toCodes);
        Object result = new GetUrlContent().apply(urlString, null);
        System.out.println("From: "+fromCode);
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
        final FxRateServiceImpl instance = getInstance();
        final Map.Entry<String, String> pair = getLocaleProvider().getRandomCurrCodePair();
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
        final FxRateServiceImpl instance = getInstance();
        Map<String, FxRate> result = instance.getCache();
        this.requireNotNullOrEmptyString("Cache", result);
    }
}
