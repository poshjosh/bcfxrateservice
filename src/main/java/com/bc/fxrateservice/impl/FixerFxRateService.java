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

import com.bc.fxrateservice.Parser;
import com.bc.fxrateservice.util.GetUrlContent;
import com.bc.fxrateservice.util.UrlReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 21, 2018 7:54:19 PM
 */
public class FixerFxRateService extends FxRateServiceImpl {

    private static final Date versionDate;
    static{
        final Calendar cal = Calendar.getInstance();
        cal.set(2018, 2, 21, 21, 22, 0);
        versionDate = cal.getTime();
    }
    
    private static final class ApiKey {
        public String get() {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try(InputStream in = cl.getResourceAsStream("META-INF/com.bc.fxrateservice.properties")) {
                final Properties props = new Properties();
                props.load(in);
                final String result = Objects.requireNonNull(props.getProperty("fixer.io.apikey", null));
                return result;
            }catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public FixerFxRateService() {
        this(DEFAULT_UPDATE_INTERVAL_MILLIS);
    }
    
    public FixerFxRateService(long updateIntervalMillis) {
        this(new ApiKey().get(), updateIntervalMillis);
    }

    public FixerFxRateService(String apiKey, long updateIntervalMillis) {
        this(new GetUrlContent(), apiKey, updateIntervalMillis);
    }
    
    public FixerFxRateService(UrlReader urlReader, long updateIntervalMillis) {
        this(urlReader, new ApiKey().get(), updateIntervalMillis);
    }
    
    public FixerFxRateService(UrlReader urlReader, String apiKey, long updateIntervalMillis) {
        this(
                new UrlParserImpl(urlReader, (response) -> new FixerResponseJson(response), UrlParserImpl.DATA_REFRESH_INTERVAL), 
                apiKey,
                updateIntervalMillis
        );
    }

    public FixerFxRateService(Parser<String> parser, String apiKey, long updateIntervalMillis) {
        super(
                new ServiceDescriptorImpl(
                        "Fixer FX Rate Service", 
                        "https://fixer.io", 
                        versionDate, 
                        "1.0"
                ), 
                new SingleEndpointSupplier("http://data.fixer.io/api/latest?access_key=" + apiKey), 
                parser, 
                updateIntervalMillis
        );
    }
}
