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

import com.bc.fxrateservice.EndpointSupplier;
import com.bc.fxrateservice.util.GetUrlContents;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.bc.fxrateservice.FxRate;

/**
 *
 * @author Josh
 */
public class FixerUrlParserTest {
    
    public FixerUrlParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
     * Test of isExpired method, of class FixerUrlParser.
     */
    @Test
    public void testAll() {
        System.out.println("testAll");
        
        final FixerFxRateService svc = new FixerFxRateService();
        final EndpointSupplier endpoint = svc.getEndpointSupplier();
        final String FROM = "USD";
        final String TO = "JPY";
        final String url = endpoint.get(FROM, TO);

        final FixerUrlParser instance = new FixerUrlParser();
        
        final String raw = new GetUrlContents().apply(url);
        final FxRate result1 = instance.parse(FROM, TO, raw);
        System.out.println("Result: " + result1);
        assertNotEquals(result1, FxRate.NONE);
        
        final FixerResponseJson json = new FixerResponseJson(raw);
        assertFalse(instance.isExpired(json));
        final FxRate result2 = instance.parseData(FROM, TO, json);
        System.out.println("Result: " + result2);
        assertNotEquals(result2, FxRate.NONE);
        
        assertEquals(result1, result2);
    }
}