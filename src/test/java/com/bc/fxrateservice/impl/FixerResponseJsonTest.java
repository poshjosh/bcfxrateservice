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

import com.bc.fxrateservice.util.CharReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;
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
public class FixerResponseJsonTest {
    
    private static FixerResponseJson instance;
    
    public FixerResponseJsonTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        instance = getInstance();
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
     * Test of getRate method, of class FixerResponseJson.
     */
    @Test
    public void testGetRate() {
        System.out.println("getRate");
        final String toCode = "AUD";
        final Float outputIfNone = null;
        final Float result = instance.getRate(toCode, outputIfNone);
        System.out.println(instance.getBaseCurrencyCode()+':'+toCode+" = " + result);
        assertNotNull(result);
    }

    /**
     * Test of isSuccess method, of class FixerResponseJson.
     */
    @Test
    public void testIsSuccess() {
        System.out.println("isSuccess");
        final boolean expResult = true;
        final boolean result = instance.isSuccess();
        System.out.println("Is success: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimestamp method, of class FixerResponseJson.
     */
    @Test
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        final long expResult = 1521657590;
        final long result = instance.getTimestamp();
        System.out.println("Timestamp: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getBaseCurrencyCode method, of class FixerResponseJson.
     */
    @Test
    public void testGetBase() {
        System.out.println("getBase");
        final String result = instance.getBaseCurrencyCode();
        System.out.println("Base currency code: " + result);
        assertNotNull(result);
    }

    /**
     * Test of getDate method, of class FixerResponseJson.
     * @throws java.text.ParseException
     */
    @Test
    public void testGetDate() throws ParseException {
        System.out.println("getDate");
        final Date expResult = new SimpleDateFormat("yyyy-MM-dd").parse("2018-03-21");
        final Date result = instance.getDate();
        System.out.println("Date: " + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRates method, of class FixerResponseJson.
     */
    @Test
    public void testGetRates() {
        System.out.println("getRates");

        final JSONObject result = instance.getRates();
        System.out.println(result);
        assertNotNull(result);

        final Set<Object> keySet = result.keySet();
        for(Object key : keySet) {
            final Number number = (Number)result.getOrDefault(key, null);
            if(number == null) {
                continue;
            }
            final float x = number.floatValue();
            final float y = instance.getRate(key.toString(), null); 
            if(x != y) {
                throw new AssertionError("" + x + " != " + y);
            }
        }
        
        final Map<String, String> map = new LocaleProvider().getCurrCodePairs();
        final String baseCurrCode = instance.getBaseCurrencyCode();
        final float NONE = -1.0f;
        for(String currCode : map.keySet()) {
            if(baseCurrCode.equalsIgnoreCase(currCode)) {
                continue;
            }
            final float rate = instance.getRate(currCode, NONE);
            if(rate == NONE) {
                System.out.println(baseCurrCode + ':' + currCode + " = NONE");
            }else if(rate == 1) {    
                throw new AssertionError(baseCurrCode + ':' + currCode + " cannot be equal to One");
            }else{
                System.out.println(baseCurrCode + ':' + currCode + " = " + rate);
            }
        }
    }
    
    public static FixerResponseJson getInstance() {
        try{
            final URL url = Thread.currentThread().getContextClassLoader().getResource("fixer_io_latest.json");
            final File file =  new File(url.getFile());
            if(!file.exists()) {
                throw new FileNotFoundException(file.toString());
            }
            final String contents = new CharReader().readChars(file).toString();
            return new FixerResponseJson(contents);
        }catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception creating instance of: " + FixerResponseJson.class.getName());
        }
    }
}
