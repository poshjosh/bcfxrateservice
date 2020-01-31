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

import com.bc.xml.XmlUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Node;

/**
 *
 * @author Josh
 */
public class ECBResponseXmlTest extends TestBase {
    
    private static ECBResponseXml instance;
    
    public ECBResponseXmlTest() { }
    
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
     * Test of getRateString method, of class ECBResponseXml.
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testGetRateString() throws IOException, URISyntaxException {
        System.out.println("getRateString");
        final String toCode = "JPY";
        final String outputIfNone = null;
//        System.out.println(new XmlUtil().toString(instance.getDocument()));
        final String result = instance.getRateString(toCode, outputIfNone);
        System.out.println(instance.getBaseCurrencyCode()+':'+toCode+" = " + result);
        assertNotNull(result);

        final Map<String, String> map = new LocaleProvider().getCurrCodePairs();
        final String baseCurrCode = instance.getBaseCurrencyCode();
        for(String currCode : map.keySet()) {
            if(baseCurrCode.equalsIgnoreCase(currCode)) {
                continue;
            }
            final String rateStr = instance.getRateString(currCode, null);
            if(rateStr == null) {
                System.out.println(baseCurrCode + ':' + currCode + " = NONE");
                continue;
            }
            final float rate = new BigDecimal(rateStr).floatValue();
            if(rate == 1) {
                throw new AssertionError(baseCurrCode + ':' + currCode + " cannot be equal to One");
            }else{
                System.out.println(baseCurrCode + ':' + currCode + " = " + rate);
            }
        }
    }

    /**
     * Test of getRatesParentNode method, of class ECBResponseXml.
     */
    @Test
    public void testGetRatesParentNode() {
        System.out.println("getRatesParentNode");
        final Node result = instance.getRatesParentNode();
        System.out.println(new XmlUtil().toString(result));
        assertNotNull(result);
    }

    /**
     * Test of getRatesDate method, of class ECBResponseXml.
     * @throws java.text.ParseException
     */
    @Test
    public void testGetRatesDate() throws ParseException {
        System.out.println("getRatesDate");
        final Date expResult = new SimpleDateFormat("yyyy-MM-dd").parse("2018-03-20");
        final Date result = instance.getDate();
        System.out.println(result);
        assertEquals(expResult, result);
    }

    public static ECBResponseXml getInstance() {
        try{
            final String fname = "eurofxref-daily.xml";
            final URL url = Thread.currentThread().getContextClassLoader().getResource(fname);
            final File file = new File(url.getFile());
//            final File file = Paths.get(System.getProperty("user.home"), "Documents", "NetBeansProjects", "bcfxrateservice", "src", "test", "resources", fname).toFile();
            if(!file.exists()) {
                throw new FileNotFoundException(file.toString());
            }
            System.out.println(file);
            Objects.requireNonNull(file);
            final byte [] bytes = Files.readAllBytes(file.toPath());
            final String contents = bytes == null ? null : new String(Files.readAllBytes(file.toPath()));
            System.out.println("Content length: " + (contents == null ? null : contents.length()));
            Objects.requireNonNull(contents);
            return new ECBResponseXml(contents);
        }catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception creating instance of: " + ECBResponseXml.class.getName());
        }
    }
}
