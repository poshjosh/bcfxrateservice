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
import com.bc.fxrateservice.FxDataFromBaseCurrency;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static org.junit.Assert.*;
import com.bc.fxrateservice.FxRate;
import com.bc.fxrateservice.util.UrlReader;
import java.util.function.Function;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @author Josh
 */
public class UrlParserImplTest extends TestBase {
    
    private final OkHttpClient httpClient;
    
    public UrlParserImplTest() { 
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS).build();
    }

    @Test
    public void testAll() {
        System.out.println("testAll");
        
        final long intervalMillis = TimeUnit.HOURS.toMillis(24);
        
        this.test(new ECBFxRateService(intervalMillis), (response) -> new ECBResponseXml(response));
        
        this.test(new FixerFxRateService(intervalMillis), (response) -> new FixerResponseJson(response));
    }
    
    public void test(
            FxRateServiceImpl svc, 
            Function<String, FxDataFromBaseCurrency> converter) {
        System.out.println("test_" + converter.getClass().getSimpleName());
        
        try{
            final EndpointSupplier endpoint = svc.getEndpointSupplier();
            final String FROM = "USD";
            final String TO = "JPY";
            final String url = endpoint.get(FROM, TO);
            
//            final UrlReader urlReader = new GetUrlContent();
            final UrlReader urlReader = (String url1) -> {
                final okhttp3.Request request = new okhttp3.Request.Builder().url(url1).build();
                try(final Response response = httpClient.newCall(request).execute()) {
                    return response.body().string();
                }
            };

            final String raw = urlReader.read(url);
            
            final UrlParserImpl instance = new UrlParserImpl(urlReader, converter, UrlParserImpl.DATA_REFRESH_INTERVAL);

            final FxRate result1 = raw == null ? FxRate.NONE : instance.parse(FROM, TO, url);
            System.out.println("Result: " + result1);
            assertNotEquals(result1, FxRate.NONE);

            final FxDataFromBaseCurrency data = converter.apply(raw);
            assertFalse(instance.isExpired(data));

            final long ratesAge = System.currentTimeMillis() - data.getDate().getTime();
            System.out.println("Rates age: " + ratesAge + 
                    " in minutes: " + TimeUnit.MILLISECONDS.toMinutes(ratesAge) +
                    " in hours: " + TimeUnit.MILLISECONDS.toHours(ratesAge));
    //        assertTrue(!instance.isExpired(data));
            final FxRate result2 = instance.parseData(FROM, TO, data);
            System.out.println("Result: " + result2);
            assertNotEquals(result2, FxRate.NONE);

            assertEquals(result1, result2);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
